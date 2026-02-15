package com.rj.handlers;

import com.rj.constants.RjConstants;
import com.rj.json.Health;
import com.rj.utility.RjProperties;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

public class HealthHandler {

    public void getInfo(HttpServerExchange e) {
        RjProperties rp = new RjProperties();
        String json = Health.toJson("ok", rp.getProp("application.name"), rp.getProp("version"));
        RjUtility.sendJson(e, RjConstants.RjResponse.Status.OK, json);
    }

    public void getPage(HttpServerExchange e) {
        RjProperties rp = new RjProperties();
        var sb = new StringBuilder();

        var h = Health.fromJson(Health.toJson("ok", rp.getProp("application.name"), rp.getProp("version")));

        if (h != null) {
            sb.append("<h1>Health</h1>");
            sb.append("<div class='container'>");
            sb.append("<p> Status: ").append(h.getStatus()).append("</p>");
            sb.append("<p> Name: ").append(h.getName()).append("</p>");
            sb.append("<p> Version: ").append(h.getVersion()).append("</p>");
            sb.append("</div>");
        }


        RjUtility.sendHtml(e, RjConstants.RjResponse.Status.OK, sb.toString());
    }
}
