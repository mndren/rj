package com.rj;

import com.rj.handlers.FallbackHandler;
import com.rj.handlers.HealthHandler;
import com.rj.handlers.IndexHandler;
import com.rj.net.ReqLogging;
import com.rj.utility.RjProperties;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import org.jboss.logging.Logger;

import static com.rj.constants.RjConstants.API.API_V1_HEALTH;
import static com.rj.constants.RjConstants.SSR.SSR_HEALTH;

public class App {

    private static final Logger logger = Logger.getLogger(App.class);

    public static void start() {
        var hh = new HealthHandler();
        var fh = new FallbackHandler();
        var ih = new IndexHandler();
        var rp = new RjProperties();

        RoutingHandler routes = new RoutingHandler()
                // fallback
                .setFallbackHandler(fh::getPage)

                // index
                .get("/", ih::getPage)

                // health
                // page
                .get(SSR_HEALTH, hh::getPage)
                // api
                .get(API_V1_HEALTH, hh::getInfo);

        // logging for all request
        var withLogging = new ReqLogging(routes);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler("true".equalsIgnoreCase(rp.getProp("req.logging")) ? withLogging : routes)
                .build();

        server.start();


        logger.info("ascolto su http://localhost:8080");
    }
}
