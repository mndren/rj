package com.rj.handlers;

import com.rj.business.html.FilterField;
import com.rj.business.html.HtmlBuilder;
import com.rj.constants.RjConstants;
import com.rj.models.Clienti;
import com.rj.utility.RjLogger;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.Map;

import static com.rj.constants.RjConstants.SSR.DEFAULT_TARGET;
import static com.rj.constants.RjConstants.SSR.SSR_CLIENTI;
import static com.rj.utility.RjUtility.queryParam;
import static io.undertow.util.HttpString.tryFromString;

public class ClientiHandler {

    public void list(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String ragioneSociale = queryParam(e, "ragione_sociale");
        String email = queryParam(e, "email");
        String partitaIva = queryParam(e, "partita_iva");

        Map<String, String> filters = Map.of(
                "ragione_sociale", ragioneSociale,
                "email", email,
                "partita_iva", partitaIva
        );
        List<Clienti> rows = new Clienti().listAllFiltered(filters);

        String html = HtmlBuilder.page(
                "Clienti",
                rows.size() + " record trovati",
                HtmlBuilder.filters(SSR_CLIENTI, DEFAULT_TARGET,
                        new FilterField("ragione_sociale", "Ragione sociale", ragioneSociale),
                        new FilterField("email", "Email", email),
                        new FilterField("partita_iva", "Partita Iva", partitaIva)
                ),
                HtmlBuilder.toolbar(SSR_CLIENTI + "/new", DEFAULT_TARGET, "Nuovo Cliente"),
                HtmlBuilder.table(rows, SSR_CLIENTI, DEFAULT_TARGET)
        );

        RjUtility.sendHtml(e, 200, html);
    }


    public void view(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Clienti c = new Clienti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Cliente #" + id, "",
                HtmlBuilder.toolbarBackToList(SSR_CLIENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(c, Clienti.class, SSR_CLIENTI + "/" + id + "/edit", "get", true, DEFAULT_TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void edit(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Clienti c = new Clienti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Modifica Cliente #" + id, "",
                HtmlBuilder.toolbarBackToList(SSR_CLIENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(c, Clienti.class, SSR_CLIENTI + "/" + id, "put", false, DEFAULT_TARGET)

        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void newForm(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String html = HtmlBuilder.page(
                "Nuovo Cliente", "",
                HtmlBuilder.toolbarBackToList(SSR_CLIENTI, DEFAULT_TARGET, "Ritorna alla lista"),
                HtmlBuilder.form(null, Clienti.class, SSR_CLIENTI, "post", false, DEFAULT_TARGET)
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

            Clienti c = new Clienti();
            c.setRagione_sociale(params.get("ragione_sociale"));
            c.setPartita_iva(params.get("partita_iva"));
            c.setCodice_fiscale(params.get("codice_fiscale"));
            c.setEmail(params.get("email"));
            c.setTelefono(params.get("telefono"));
            c.setIndirizzo(params.get("indirizzo"));

            boolean ok = c.insert();

            if (ok) {
                exchange.getResponseHeaders().put(
                        tryFromString("HX-Redirect"), SSR_CLIENTI
                );
                RjUtility.sendHtml(exchange, 204, "");
                RjLogger.info("Inserito cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.insert");
            } else {
                RjLogger.info("Errore inserendo cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.insert");
                RjUtility.sendHtml(exchange, 500, "<p>Errore durante il salvataggio.</p>");
            }
        });
    }

    public void update(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        e.getRequestReceiver().receiveFullString((exchange, body) -> {
            Map<String, String> params = RjUtility.parseForm(body);

            Clienti c = new Clienti().findById(id).orElseThrow();
            c.setRagione_sociale(params.get("ragione_sociale"));
            c.setPartita_iva(params.get("partita_iva"));
            c.setCodice_fiscale(params.get("codice_fiscale"));
            c.setEmail(params.get("email"));
            c.setTelefono(params.get("telefono"));
            c.setIndirizzo(params.get("indirizzo"));

            boolean ok = c.update();

            if (ok) {
                exchange.getResponseHeaders().put(
                        tryFromString("HX-Redirect"), SSR_CLIENTI
                );
                RjLogger.info("Modificato cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.update");
                RjUtility.sendHtml(exchange, 204, "");
            } else {
                RjLogger.error("Errore modificando il cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.update");
                RjUtility.sendHtml(exchange, 500, "<p>Errore durante l'aggiornamento.</p>");
            }
        });
    }

    public void delete(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Clienti c = new Clienti().findById(id).orElseThrow();
        boolean ok = c.delete();
        if (ok) {
            e.getResponseHeaders().put(
                    tryFromString("HX-Redirect"), SSR_CLIENTI
            );
            RjLogger.info("Eliminato il cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.delete");
            RjUtility.sendHtml(e, 204, "");
        } else {
            RjLogger.error("Errore eliminando il cliente con ragione sociale: " + c.ragione_sociale, "ClientiHandler.delete");
            RjUtility.sendHtml(e, 500, "<p>Errore durante l'eliminazione.</p>");
        }

    }
}