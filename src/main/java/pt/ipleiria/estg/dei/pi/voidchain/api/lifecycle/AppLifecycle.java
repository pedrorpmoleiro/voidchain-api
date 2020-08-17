package pt.ipleiria.estg.dei.pi.voidchain.api.lifecycle;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class AppLifecycle {
    private static final Logger logger = LoggerFactory.getLogger(AppLifecycle.class);

    void onStart(@Observes StartupEvent ev) {
        logger.info("The application is starting...");
    }

    void onStop(@Observes ShutdownEvent ev) {
        logger.info("The application is stopping...");
    }
}
