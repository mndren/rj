package com.rj.handlers;

import com.rj.business.html.HtmlBuilder;
import com.rj.constants.RjConstants;
import com.rj.models.Clienti;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.Map;

import static io.undertow.util.HttpString.tryFromString;

public class ClientiHandler {


    private static final String TARGET = "#content";
    private static final String BASE = "/clienti";

    public void list(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        List<Clienti> rows = new Clienti().listAll();

        String html = HtmlBuilder.page(
                "Clienti",
                rows.size() + " record trovati",
                HtmlBuilder.toolbar(BASE + "/new", TARGET, "Nuovo Cliente"),
                HtmlBuilder.table(rows, BASE, TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void view(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Clienti c = new Clienti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Cliente #" + id, "",
                HtmlBuilder.form(c, Clienti.class, BASE + "/" + id + "/edit", "get", true, TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void edit(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        Long id = pathId(e);
        Clienti c = new Clienti().findById(id).orElseThrow();

        String html = HtmlBuilder.page(
                "Modifica Cliente #" + id, "",
                HtmlBuilder.form(c, Clienti.class, BASE + "/" + id, "put", false, TARGET)
        );

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, html);
    }

    public void newForm(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String html = HtmlBuilder.page(
                "Nuovo Cliente", "",
                HtmlBuilder.form(null, Clienti.class, BASE, "post", false, TARGET)
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
                        tryFromString("HX-Redirect"), BASE
                );
                RjUtility.sendHtml(exchange, 204, "");
            } else {
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
                        tryFromString("HX-Redirect"), BASE
                );
                RjUtility.sendHtml(exchange, 204, "");
            } else {
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
                    tryFromString("HX-Redirect"), BASE
            );
            RjUtility.sendHtml(e, 204, "");
        } else {
            RjUtility.sendHtml(e, 500, "<p>Errore durante l'eliminazione.</p>");
        }

    }
}