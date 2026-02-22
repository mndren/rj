package com.rj.handlers;

import com.rj.business.html.FilterField;
import com.rj.business.html.HtmlBuilder;
import com.rj.constants.RjConstants;
import com.rj.models.Utenti;
import com.rj.utility.RjLogger;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.Map;

import static com.rj.constants.RjConstants.SSR.DEFAULT_TARGET;
import static com.rj.constants.RjConstants.SSR.SSR_UTENTI;
import static com.rj.utility.RjUtility.queryParam;
import static io.undertow.util.HttpString.tryFromString;

public class UtentiHandler {
    public void list(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String username = queryParam(e, "username");


        Map<String, String> filters = Map.of(
                "username", username

        );
        List<Utenti> rows = new Utenti().listAllFiltered(filters);

        String html = HtmlBuilder.page(
                "Utenti",
                rows.size() + " record trovati",
                HtmlBuilder.filters(SSR_UTENTI, DEFAULT_TARGET,
                        new FilterField("username", "Username", username)
                ),
                HtmlBuilder.toolbar(SSR_UTENTI + "/new", DEFAULT_TARGET, "Nuovo Utente"),
                HtmlBuilder.table(rows, SSR_UTENTI, DEFAULT_TARGET)
        );

        RjUtility.sendHtml(e, 200, html);
    }


    public void view(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Utenti c = new Utenti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Utente #" + id, "",
                HtmlBuilder.toolbarBackToList(SSR_UTENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(c, Utenti.class, SSR_UTENTI + "/" + id + "/edit", "get", true, DEFAULT_TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void edit(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Utenti c = new Utenti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Modifica Utente #" + id, "",
                HtmlBuilder.toolbarBackToList(SSR_UTENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(c, Utenti.class, SSR_UTENTI + "/" + id, "put", false, DEFAULT_TARGET)

        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void newForm(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String html = HtmlBuilder.page(
                "Nuovo Utente", "",
                HtmlBuilder.toolbarBackToList(SSR_UTENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(null, Utenti.class, SSR_UTENTI, "post", false, DEFAULT_TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    private Long pathId(HttpServerExchange e) {
        return Long.parseLong(e.getQueryParameters().get("id").getFirst());
    }

    public void insert(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        e.getRequestReceiver().receiveFullString((exchange, body) -> {
            Map<String, String> params = RjUtility.parseForm(body);

            Utenti c = new Utenti();
            c.setUsername(params.get("username"));
            c.setPassword_hash(RjUtility.hashPassword(params.get("password_hash")));
            c.setRuolo(params.get("ruolo"));
            c.setAttivo(Boolean.valueOf(params.get("attivo")));

            boolean ok = c.insert();

            if (ok) {
                exchange.getResponseHeaders().put(
                        tryFromString("HX-Redirect"), SSR_UTENTI
                );
                RjUtility.sendHtml(exchange, 204, "");
                RjLogger.info("Inserito utente con username: " + c.username, "UtentiHandler.insert");
            } else {
                RjLogger.info("Errore inserendo utente con username: " + c.username, "UtentiHandler.insert");
                RjUtility.sendHtml(exchange, 500, "<p>Errore durante il salvataggio.</p>");
            }
        });
    }

    public void update(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        e.getRequestReceiver().receiveFullString((exchange, body) -> {
            Map<String, String> params = RjUtility.parseForm(body);

            Utenti c = new Utenti().findById(id).orElseThrow();
            c.setUsername(params.get("username"));
            c.setPassword_hash(RjUtility.hashPassword(params.get("password_hash")));
            c.setRuolo(params.get("ruolo"));
            c.setAttivo(Boolean.valueOf(params.get("attivo")));

            boolean ok = c.update();

            if (ok) {
                exchange.getResponseHeaders().put(
                        tryFromString("HX-Redirect"), SSR_UTENTI
                );
                RjLogger.info("Modificato utente con username: " + c.username, "UtentiHandler.update");
                RjUtility.sendHtml(exchange, 204, "");
            } else {
                RjLogger.error("Errore modificando utente con username: " + c.username, "UtentiHandler.update");
                RjUtility.sendHtml(exchange, 500, "<p>Errore durante l'aggiornamento.</p>");
            }
        });
    }

    public void delete(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Utenti c = new Utenti().findById(id).orElseThrow();
        boolean ok = c.delete();
        if (ok) {
            e.getResponseHeaders().put(
                    tryFromString("HX-Redirect"), SSR_UTENTI
            );
            RjLogger.info("Eliminato utente con username: " + c.username, "UtentiHandler.delete");
            RjUtility.sendHtml(e, 204, "");
        } else {
            RjLogger.error("Errore eliminando utente con username: " + c.username, "UtentiHandler.delete");
            RjUtility.sendHtml(e, 500, "<p>Errore durante l'eliminazione.</p>");
        }

    }
}
