package com.rj.utility;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.List;
import java.util.Properties;

public class RjMailer {

    private static final Session SESSION;
    private static final String FROM;

    static {
        RjProperties rp = new RjProperties();
        FROM = rp.getProp("mail.from");
        String password = rp.getProp("mail.password");
        String host = rp.getProp("mail.host");
        String port = rp.getProp("mail.port");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        SESSION = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, password);
            }
        });
    }

    public static void send(String to, String subject, String body) {
        Thread.ofVirtual().start(() -> {
            try {
                Message msg = new MimeMessage(SESSION);
                msg.setFrom(new InternetAddress(FROM));
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                msg.setSubject(subject);
                msg.setContent(body, "text/html; charset=utf-8");
                Transport.send(msg);
                RjLogger.info("Mail inviata a " + to, "RjMailer");
            } catch (Exception e) {
                RjLogger.error(e, "RjMailer.send");
            }
        });
    }

    public static void send(List<String> recipients, String subject, String body) {
        recipients.forEach(to -> send(to, subject, body));
    }
}