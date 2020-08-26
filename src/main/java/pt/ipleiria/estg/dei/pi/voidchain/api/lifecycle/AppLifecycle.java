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
import pt.ipleiria.estg.dei.pi.voidchain.util.KeyGenerator;
import pt.ipleiria.estg.dei.pi.voidchain.util.Storage;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import java.io.IOException;
import java.security.Security;

@ApplicationScoped
public class AppLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(AppLifecycle.class);

    private Node node;

    void onStart(@Observes StartupEvent ev) throws IOException {
        logger.info("The application is starting...");

        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());

        APIConfiguration.createDefaultConfigFiles();
        Storage.createDefaultConfigFiles();

        APIConfiguration apiConfig = APIConfiguration.getInstance();

        KeyGenerator.generatePubAndPrivKeys(apiConfig.getId());
        KeyGenerator.generateSSLKey(apiConfig.getId());

        if (apiConfig.hasNode()) {
            node = new Node(apiConfig.getId(), apiConfig.hasSync());
            NetworkProxyManager.createInstance(node.getMessengerProxy());
        } else
            NetworkProxyManager.createInstance(apiConfig.getId());

        BlockchainManager.getInstance();
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("The application is stopping...");
        APIConfiguration apiConfig = APIConfiguration.getInstance();

        if (apiConfig.hasNode())
            node.close();

        NetworkProxyManager.getInstance().close();
        BlockchainManager.getInstance().close();
    }
}
