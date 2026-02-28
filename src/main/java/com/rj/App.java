package com.rj;

import com.rj.db.DataSource;
import com.rj.handlers.*;
import com.rj.net.AuthHandler;
import com.rj.net.ReqLogging;
import com.rj.utility.RjProperties;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import org.jboss.logging.Logger;

import static com.rj.constants.RjConstants.API.API_V1_HEALTH;
import static com.rj.constants.RjConstants.SSR.*;

public class App {

    private static final Logger logger = Logger.getLogger(App.class);

    public static void start() {

        try {
            DataSource.init();
        } catch (Exception e) {
            logger.error("errore inizializzazione DB. Arresto.", e);
            System.exit(1);
        }
        var hh = new HealthHandler();
        var fh = new FallbackHandler();
        var ih = new IndexHandler();
        var rp = new RjProperties();
        var ch = new ClientiHandler();
        var lh = new LogHandler();
        var uh = new UtentiHandler();
        var lgh = new LoginHandler();

        RoutingHandler routes = new RoutingHandler()
                // fallback
                .setFallbackHandler(fh::getPage)

                .get("/login", lgh::loginPage)
                .post("/login", lgh::login)
                .get("/logout", lgh::logout)
                // logs
                .get(SSR_LOGS, lh::list)
                .get(SSR_LOGS_TABLE, lh::table)

                // index
                .get("/", ih::getPage)
                .get("/htmx.min.js", ih::getHtmx)
                .get("/style.css", ih::getCss)

                // health page
                .get(SSR_HEALTH, hh::getPage)
                // health api
                .get(API_V1_HEALTH, hh::getInfo)

                // clienti page
                .get(SSR_CLIENTI, ch::list)
                .get(SSR_CLIENTI_NEW, ch::newForm)
                .get(SSR_CLIENTI_EDIT, ch::edit)

                // clienti api
                .post(SSR_CLIENTI, ch::insert)
                .put(SSR_CLIENTI_ID, ch::update)
                .get(SSR_CLIENTI_ID, ch::view)
                .delete(SSR_CLIENTI_ID, ch::delete)

                // utenti page
                .get(SSR_UTENTI, uh::list)
                .get(SSR_UTENTI_NEW, uh::newForm)
                .get(SSR_UTENTI_EDIT, uh::edit)

                // utenti api
                .post(SSR_UTENTI, uh::insert)
                .put(SSR_UTENTI_ID, uh::update)
                .get(SSR_UTENTI_ID, uh::view)
                .delete(SSR_UTENTI_ID, uh::delete);

        // logging for all request
        var withLogging = new ReqLogging(routes);
        var withAuth = new AuthHandler(withLogging);

        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler("true".equalsIgnoreCase(rp.getProp("req.logging")) ? withLogging : new AuthHandler(routes))
                .build();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("shutdown hook attivato...");
            server.stop();
            DataSource.shutdown();
        }));

        server.start();


        logger.info("ascolto su http://localhost:8080");
    }
}
