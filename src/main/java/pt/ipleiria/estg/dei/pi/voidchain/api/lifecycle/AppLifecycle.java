package pt.ipleiria.estg.dei.pi.voidchain.api.lifecycle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ipleiria.estg.dei.pi.voidchain.api.APIConfiguration;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.BlockchainManager;
import pt.ipleiria.estg.dei.pi.voidchain.api.managers.NetworkProxyManager;
import pt.ipleiria.estg.dei.pi.voidchain.node.Node;
import pt.ipleiria.estg.dei.pi.voidchain.util.Keys;
import pt.ipleiria.estg.dei.pi.voidchain.util.Storage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import java.io.IOException;
import java.security.Security;

/**
 * The App lifecycle class has code that should run before the start up and shutdown of quarkus.
 */
@ApplicationScoped
public class AppLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(AppLifecycle.class);

    private Node node;

    /**
     * On application start.
     *
     * @param ev the ev
     * @throws IOException the io exception
     */
    void onStart(@Observes StartupEvent ev) throws IOException {
        logger.info("The application is starting...");

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());

        APIConfiguration.createDefaultConfigFiles();
        Storage.createDefaultConfigFiles();

        APIConfiguration apiConfig = APIConfiguration.getInstance();

        Keys.generatePubAndPrivKeys(apiConfig.getId());
        Keys.generateSSLKey(apiConfig.getId());

        if (apiConfig.hasNode()) {
            node = new Node(apiConfig.getId(), apiConfig.hasSync());
            NetworkProxyManager.createInstance(node.getMessengerProxy());
        } else
            NetworkProxyManager.createInstance(apiConfig.getId());

        BlockchainManager.getInstance();
    }

    /**
     * On application stop.
     *
     * @param ev the ev
     */
    void onStop(@Observes ShutdownEvent ev) {
        logger.info("The application is stopping...");
        APIConfiguration apiConfig = APIConfiguration.getInstance();

        if (apiConfig.hasNode())
            node.close();

        NetworkProxyManager.getInstance().close();
        BlockchainManager.getInstance().close();
    }
}
