package com.rj.net;

import com.rj.utility.RjUtility;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.jboss.logging.Logger;

public record ReqLogging(HttpHandler next) implements HttpHandler {

    private static final Logger logger = Logger.getLogger(ReqLogging.class);

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        long start = System.nanoTime();

        String method = exchange.getRequestMethod().toString();
        String path = exchange.getRequestPath();
        String query = exchange.getQueryString();
        try {
            next.handleRequest(exchange);

        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            int status = exchange.getStatusCode();
            logger.infof("%s %s%s -> %d (%d ms)%n",
                    method,
                    path,
                    RjUtility.isBlank(query) ? "" : query,
                    status,
                    elapsedMs);

        }
    }
}
