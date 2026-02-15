package com.rj;

import com.rj.json.Health;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import org.jboss.logging.Logger;

public class App {

   private static final Logger logger = Logger.getLogger(App.class);

    public static void start()
    {
        logger.info("sto partendo.....");

        RoutingHandler routes = new RoutingHandler()
                .get("/", exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                    exchange.getResponseSender().send("<html lang=\"it\">\n" +
                            "<head>\n" +
                            "    <title>RJ</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "  <h1>ciao sono rj entra</h1>\n" +
                            "<hr />\n" +
                            "<a href=\"/health\">health</a>\n" +
                            "</body>\n" +
                            "</html>");
                })

                .get("/health", exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(Health.toJson("ok", "renè", "1,0"));
                });

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(routes)
                .build();

        server.start();


        logger.info("ascolto su http://localhost:8080");
    }
}
