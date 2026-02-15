package com.rj;

import com.rj.handlers.HealthHandler;
import com.rj.utility.RjUtility;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.jboss.logging.Logger;

import static com.rj.constants.RjConstants.API_V1_HEALTH;

public class App {

   private static final Logger logger = Logger.getLogger(App.class);

    public static void start() {
        HealthHandler hh = new HealthHandler();
        RjUtility ru = new RjUtility();

        logger.info("sto partendo.....");

        RoutingHandler routes = new RoutingHandler()
                .get("/", exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    String index = ru.getIndex();
                    if (index.isBlank()) {
                        exchange.setStatusCode(404);
                        exchange.getResponseSender().send("<html><h1>Pagina non trovata</h1></html>");
                    }
                    exchange.getResponseSender().send(index);
                })

                .get(API_V1_HEALTH, exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(hh.getInfo());
                });

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(routes)
                .build();

        server.start();


        logger.info("ascolto su http://localhost:8080");
    }
}
