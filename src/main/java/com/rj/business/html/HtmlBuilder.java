package com.rj.business.html;


import com.rj.business.annotations.Column;
import com.rj.business.annotations.Form;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HtmlBuilder {

    public static String esc(Object o) {
        if (o == null) return "";
        return o.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }


    private static List<Field> tableFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        for (Field f : clazz.getFields()) {
            Form lbl = f.getAnnotation(Form.class);
            if (lbl != null && lbl.visible()) result.add(f);
        }
        return result;
    }


    private static List<Field> allLabeledFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        for (Field f : clazz.getFields()) {
            if (f.getAnnotation(Form.class) != null && f.getAnnotation(Form.class).visible()) result.add(f);
        }
        return result;
    }

    private static Field idField(Class<?> clazz) {
        for (Field f : clazz.getFields()) {
            Column col = f.getAnnotation(Column.class);
            if ((col != null && col.id()) || f.getName().equals("id")) return f;
        }
        throw new RuntimeException("Nessun id in " + clazz.getSimpleName());
    }

    public static <T> String table(List<T> rows, String baseRoute, String hxTarget) {
        if (rows.isEmpty()) return "<p class=\"empty-state\">Nessun risultato trovato.</p>";

        Class<?> clazz = rows.get(0).getClass();
        List<Field> fields = tableFields(clazz);
        Field id = idField(clazz);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"table-wrapper\">");
        sb.append("<table>");


        sb.append("<thead><tr>");
        sb.append("<th class=\"actions-col\">Azioni</th>");
        for (Field f : fields) {
            sb.append("<th>").append(esc(f.getAnnotation(Form.class).value())).append("</th>");
        }

        sb.append("</tr></thead>");

        sb.append("<tbody>");

        for (T row : rows) {
            String rowId = getFieldValue(row, id);
            sb.append("<tr>");

            sb.append("<td class=\"actions-cell\">");
            sb.append(btnView(baseRoute + "/" + rowId, hxTarget));
            sb.append(btnEdit(baseRoute + "/" + rowId + "/edit", hxTarget));
            sb.append(btnDelete(baseRoute + "/" + rowId, hxTarget));
            sb.append("</td>");

            for (Field f : fields) {
                sb.append("<td>").append(esc(getFieldValue(row, f))).append("</td>");
            }

            sb.append("</tr>");
        }
        sb.append("</tbody></table></div>");

        return sb.toString();
    }


    public static <T> String form(T obj, Class<?> clazz, String action,
                                  String method, boolean readonly, String hxTarget) {
        List<Field> fields = allLabeledFields(clazz);
        Field id = idField(clazz);
        StringBuilder sb = new StringBuilder();

        sb.append("<form ")
                .append("hx-").append(method).append("=\"").append(action).append("\" ")
                .append("hx-target=\"").append(hxTarget).append("\" ")
                .append("hx-swap=\"innerHTML\">");

        for (Field f : fields) {
            Form frm = f.getAnnotation(Form.class);
            Column col = f.getAnnotation(Column.class);
            boolean isId = (col != null && col.id()) || f.getName().equals("id");
            boolean isReadonly = readonly || isId;

            String value = obj != null ? esc(getFieldValue(obj, f)) : "";
            String name = f.getName();

            sb.append("<div class=\"field-group\">");
            sb.append("<label for=\"").append(name).append("\">").append(esc(frm.value())).append("</label>");
            sb.append("<input ")
                    .append("id=\"").append(name).append("\" ")
                    .append("name=\"").append(name).append("\" ")
                    .append("value=\"").append(value).append("\" ")
                    .append("type=\"").append(frm.type()).append("\" ")
                    .append("maxlength=\"").append(frm.maxlength()).append("\" ")
                    .append("minlength=\"").append(frm.minlength()).append("\" ");

            if (frm.placeholder() != null && !frm.placeholder().trim().isEmpty()) {
                sb.append("placeholder=\"").append(frm.placeholder()).append("\" ");
            }

            if (frm.pattern() != null && !frm.pattern().trim().isEmpty()) {
                sb.append("pattern=\"").append(frm.pattern()).append("\" ");
            }

            sb.append(frm.required() ? "required" : "")
                    .append(isReadonly ? "readonly " : "")
                    .append(frm.autofocus() ? "autofocus" : "")
                    .append("/>");

            sb.append("</div>");
        }

        if (!readonly) {
            sb.append("<div class=\"form-actions\">");
            sb.append("<button type=\"submit\" class=\"btn btn-primary\">Salva</button>");
            sb.append("</div>");
        }

        sb.append("</form>");
        return sb.toString();
    }


    public static String page(String title, String subtitle, String... blocks) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"page\">");
        sb.append("<div class=\"page-header\">");
        sb.append("<h2 class=\"page-title\">").append(esc(title)).append("</h2>");
        if (subtitle != null && !subtitle.isEmpty())
            sb.append("<p class=\"page-subtitle\">").append(esc(subtitle)).append("</p>");
        sb.append("</div>");
        sb.append("<div class=\"page-body\">");
        for (String block : blocks) sb.append(block);
        sb.append("</div></div>");
        return sb.toString();
    }

    public static String filters(String action, String hxTarget, FilterField... fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"filters\">");

        for (FilterField f : fields) {
            sb.append("<input type=\"text\" ")
                    .append("name=\"").append(f.name()).append("\" ")
                    .append("placeholder=\"").append(esc(f.placeholder())).append("\" ")
                    .append("value=\"").append(esc(f.value())).append("\" ")
                    .append("hx-get=\"").append(action).append("\" ")
                    .append("hx-target=\"").append(hxTarget).append("\" ")
                    .append("hx-swap=\"innerHTML\" ")
                    .append("hx-include=\"closest div\" ")
                    .append("/>");
        }

        sb.append("</div>");
        return sb.toString();
    }


    public static String toolbar(String newRoute, String hxTarget, String label) {
        return "<div class=\"toolbar\">" +
                "<button class=\"btn btn-primary\" " +
                "hx-get=\"" + newRoute + "\" " +
                "hx-target=\"" + hxTarget + "\" " +
                "hx-swap=\"innerHTML\">" +
                "+ " + esc(label) +
                "</button></div>";
    }

    public static String toolbarBackToList(String newRoute, String hxTarget, String label) {
        return "<div class=\"toolbar\">" +
                "<button class=\"btn btn-primary\" " +
                "hx-get=\"" + newRoute + "\" " +
                "hx-target=\"" + hxTarget + "\" " +
                "hx-push-url=\"true\" " +
                "hx-swap=\"innerHTML\">" +
                esc(label) +
                "</button></div>";
    }


    public static String btnView(String route, String hxTarget) {
        return "<button class=\"btn btn-sm btn-ghost\" " +
                "hx-get=\"" + route + "\" " +
                "hx-target=\"" + hxTarget + "\" " +
                "hx-swap=\"innerHTML\" " +
                "hx-push-url=\"true\" " +
                "title=\"Visualizza\">◈</button>";
    }

    public static String btnEdit(String route, String hxTarget) {
        return "<button class=\"btn btn-sm btn-outline\" " +
                "hx-get=\"" + route + "\" " +
                "hx-target=\"" + hxTarget + "\" " +
                "hx-swap=\"innerHTML\" " +
                "hx-push-url=\"true\" " +
                "title=\"Modifica\">✎</button>";
    }

    public static String btnDelete(String route, String hxTarget) {
        return "<button class=\"btn btn-sm btn-danger\" " +
                "hx-delete=\"" + route + "\" " +
                "hx-target=\"" + hxTarget + "\" " +
                "hx-swap=\"outerHTML\" " +
                "hx-confirm=\"Eliminare questo record?\" " +
                "title=\"Elimina\">✕</button>";
    }


    private static String getFieldValue(Object obj, Field f) {
        try {
            f.setAccessible(true);
            Object val = f.get(obj);
            return val != null ? val.toString() : "";
        } catch (Exception e) {
            return "";
        }
    }
}
