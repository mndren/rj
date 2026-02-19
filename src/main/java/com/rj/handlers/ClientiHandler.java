package com.rj.handlers;

import com.rj.constants.RjConstants;
import com.rj.models.Clienti;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

import java.util.List;

public class ClientiHandler {

    public void getPage(HttpServerExchange e) {

        StringBuilder sb = new StringBuilder();
        List<Clienti> clientiList = Clienti.listAll();

        sb.append("<h1>Lista Clienti</h1>");

        if (clientiList.isEmpty()) {
            sb.append("<p>Nessun cliente trovato.</p>");
        } else {
            sb.append("<table>");
            sb.append("<thead>");
            sb.append("<tr>");
            sb.append("<th></th>");
            sb.append("<th>ID</th>");
            sb.append("<th>Ragione Sociale</th>");
            sb.append("<th>Partita IVA</th>");
            sb.append("<th>Email</th>");
            sb.append("<th>Telefono</th>");
            sb.append("<th>Indirizzo</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            sb.append("<tbody>");

            for (Clienti client : clientiList) {
                sb.append("<tr>");
                sb.append("<td>").append("<button>Modifica</button>").append("</td>");
                sb.append("<td>").append(client.getId()).append("</td>");
                sb.append("<td>").append(escape(client.getRagione_sociale())).append("</td>");
                sb.append("<td>").append(escape(client.getPartita_iva())).append("</td>");
                sb.append("<td>").append(escape(client.getEmail())).append("</td>");
                sb.append("<td>").append(escape(client.getTelefono())).append("</td>");
                sb.append("<td>").append(escape(client.getIndirizzo())).append("</td>");
                sb.append("</tr>");
            }

            sb.append("</tbody>");
            sb.append("</table>");
        }


        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, sb.toString());

    }

    private String escape(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}