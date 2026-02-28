package com.rj.handlers;

import com.rj.db.DataSource;
import com.rj.utility.RjAuthUtility;
import com.rj.utility.RjLogger;
import com.rj.utility.RjUtility;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class LoginHandler {

    public void loginPage(HttpServerExchange e) {
        // Se già loggato manda a home
        if (RjAuthUtility.getUtenteId(e) != null) {
            e.setStatusCode(302);
            e.getResponseHeaders().put(Headers.LOCATION, "/");
            e.endExchange();
            return;
        }

        RjUtility.sendHtml(e, 200, loginForm(""));
    }

    public void login(HttpServerExchange e) {
        e.getRequestReceiver().receiveFullString((exchange, body) -> {
            Map<String, String> params = RjUtility.parseForm(body);
            String username = params.get("username");
            String password = RjAuthUtility.hashPassword(params.get("password"));

            try (Connection c = DataSource.getConnection();
                 PreparedStatement pst = c.prepareStatement(
                         "SELECT id FROM utenti WHERE username = ? AND password_hash = ?")) {
                pst.setString(1, username);
                pst.setString(2, password);

                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        RjAuthUtility.createSession(exchange, rs.getLong(1), username);
                        //RjMailer.send("mail", "Benvenuto", "Ciao, il tuo account è stato creato.");
                        exchange.getResponseHeaders().put(Headers.LOCATION, "/");
                        exchange.setStatusCode(302);
                        exchange.endExchange();
                    } else {
                        RjUtility.sendHtml(exchange, 401, loginForm("Credenziali non valide."));
                    }
                }
            } catch (Exception ex) {
                RjLogger.error(ex, "LoginHandler.login");
                RjUtility.sendHtml(exchange, 500, loginForm("Errore del server."));
            }
        });
    }

    public void logout(HttpServerExchange e) {
        RjAuthUtility.destroySession(e);
        e.setStatusCode(302);
        e.getResponseHeaders().put(Headers.LOCATION, "/login");
        e.endExchange();
    }

    private String loginForm(String error) {
        return """
                <html><head>
                    <title>RJ · Login</title>
                    <link rel="stylesheet" href="/style.css"/>
                </head><body>
                <div class="login-wrap">
                    <div class="login-box">
                        <div class="logo">RJ</div>
                        %s
                        <form method="post" action="/login">
                            <div class="field">
                                <label>Username</label>
                                <input type="text" name="username" autofocus/>
                            </div>
                            <div class="field">
                                <label>Password</label>
                                <input type="password" name="password"/>
                            </div>
                            <div class="form-actions">
                                <button class="btn btn-primary" type="submit">Accedi</button>
                            </div>
                        </form>
                    </div>
                </div>
                </body></html>
                """.formatted(error.isBlank() ? "" : "<p class=\"error\">" + error + "</p>");
    }
}
