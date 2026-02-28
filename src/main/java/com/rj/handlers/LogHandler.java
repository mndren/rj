package com.rj.handlers;

import com.rj.business.html.FilterField;
import com.rj.business.html.HtmlBuilder;
import com.rj.models.RjLog;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static com.rj.constants.RjConstants.SSR.DEFAULT_TARGET;
import static com.rj.constants.RjConstants.SSR.SSR_LOGS;
import static com.rj.utility.RjUtility.queryParam;

public class LogHandler {

    public void list(HttpServerExchange e) {
        if (RjUtility.redirectIfDirect(e)) return;
        String context = queryParam(e, "context");
        String error = queryParam(e, "error");

        Map<String, String> filters = Map.of(
                "", context,
                "error", error
        );

        String html = HtmlBuilder.page("Log", "",
                HtmlBuilder.filters(SSR_LOGS, DEFAULT_TARGET,
                        new FilterField("context", "Contesto", context),
                        new FilterField("error", "Messaggio", error)
                ),
                "<div hx-get=\"/logs/table\" hx-trigger=\"every 5s\" hx-target=\"#log-table\" hx-swap=\"outerHTML\">" +
                        buildLogTable(new RjLog().listAllFiltered(filters)) +
                        "</div>"
        );

        RjUtility.sendHtml(e, 200, html);
    }

    public void table(HttpServerExchange e) {
        RjUtility.sendHtml(e, 200, buildLogTable(new RjLog().listAll()));
    }

    private String buildLogTable(List<RjLog> logs) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table id=\"log-table\" hx-get=\"/logs/table\" hx-trigger=\"every 5s\" hx-swap=\"outerHTML\">");
        sb.append("<thead><tr><th>Time</th><th>Level</th><th>Contesto</th><th>Messaggio</th></tr></thead><tbody>");
        for (RjLog log : logs) {
            String color = switch (log.level) {
                case "ERROR" -> "color:#c00";
                case "WARN" -> "color:#a60";
                default -> "color:#888";
            };
            String parsedDate = log.date != null ? log.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : null;

            sb.append("<tr>")
                    .append("<td>").append(parsedDate).append("</td>")
                    .append("<td style=\"").append(color).append(";font-weight:600\">").append(log.level).append("</td>")
                    .append("<td>").append(RjUtility.escape(log.context)).append("</td>")
                    .append("<td>").append(RjUtility.escape(log.error)).append("</td>")
                    .append("</tr>");
        }

        sb.append("</tbody></table>");
        return sb.toString();
    }
}
