package com.rj.handlers;

import com.rj.constants.RjConstants;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

public class FallbackHandler {

    public void getPage(HttpServerExchange e) {
        var sb = new StringBuilder();

        sb.append("<h1>Pagina non trovata</h1>");
        sb.append("<div class='container'>");
        sb.append("<p> Pagina non trovata per: ").append(e.getRequestURI()).append("</p>");
        sb.append("</div>");

        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, sb.toString());
    }
}
