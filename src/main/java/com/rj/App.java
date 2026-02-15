package com.rj;

import com.rj.handlers.FallbackHandler;
import com.rj.handlers.HealthHandler;
import com.rj.handlers.IndexHandler;
import com.rj.utility.RjUtility;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.jboss.logging.Logger;

import static com.rj.constants.RjConstants.API.API_V1_HEALTH;
import static com.rj.constants.RjConstants.SSR.SSR_HEALTH;

public class App {

   private static final Logger logger = Logger.getLogger(App.class);

    public static void start() {
        var hh = new HealthHandler();
        var fh = new FallbackHandler();
        var ih = new IndexHandler();

        RoutingHandler routes = new RoutingHandler()
                // fallback
                .setFallbackHandler(fh::getPage)

                // index page
                .get("/", ih::getPage)

                // health page
                .get(SSR_HEALTH , hh::getPage)

                // health api
                .get(API_V1_HEALTH,hh::getInfo);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(routes)
                .build();

        server.start();


        logger.info("ascolto su http://localhost:8080");
    }
}
