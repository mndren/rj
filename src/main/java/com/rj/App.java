package com.rj;

import com.rj.handlers.HealthHandler;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.jboss.logging.Logger;

public class App {

   private static final Logger logger = Logger.getLogger(App.class);

    public static void start() {
        HealthHandler hh = new HealthHandler();

        logger.info("sto partendo.....");

        RoutingHandler routes = new RoutingHandler()
                .get("/", exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    exchange.getResponseSender().send(
                            """
                                    <html lang="it">
                                    <head>
                                        <title>RJ</title>
                                    </head>
                                    <body>
                                      <h1>ciao sono rj entra</h1>
                                    <hr>
                                    <a href="/health">health</a>
                                    </body>
                                    </html>
                               """
                    );
                })

                .get("/health", exchange -> {
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
