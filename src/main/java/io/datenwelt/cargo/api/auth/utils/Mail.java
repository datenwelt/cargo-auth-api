package io.datenwelt.cargo.api.auth.utils;

import java.util.NoSuchElementException;
import javax.mail.Message;
import io.datenwelt.config.Configuration;
import io.datenwelt.config.InvalidConfigurationException;
import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;

/**
 *
 * @author job
 */
public class Mail {
    
    static private String hostname;
    static private int port;
    static private TransportStrategy protocol;
    static private String username;
    static private String password;
    static private String sender;
    
    public static void init() {
        if ( hostname == null ) {
            try {
                Configuration config = Configuration.withDirectory("/etc/datenwelt");
                hostname = config.get("mail.auth", "smtp_hostname");
                port = Integer.parseInt(config.get("mail.auth", "smtp_port"));
                username = config.get("mail.auth", "smtp_username");
                password = config.get("mail.auth", "smtp_password");
                sender = config.get("mail.auth", "smtp_sender");
                String proto = config.get("mail.auth", "smtp_protocol", "ssl").toLowerCase();
                if ( "ssl".equals(proto) ) {
                    protocol = TransportStrategy.SMTP_SSL;
                } else if ( "tls".equals(proto) ) {
                    protocol = TransportStrategy.SMTP_TLS;
                } else {
                    protocol = TransportStrategy.SMTP_PLAIN;
                }
            } catch (NoSuchElementException | InvalidConfigurationException ex) {
                throw new RuntimeException("Unable to load mail configuration: " + ex.getMessage(), ex);
            } 
        }
        
    }
    
    public static void sendmail(String name, String recipient, String subject, String text) {
        Email email = new Email();
        email.setText(text);
        email.setSubject(subject);
        email.setFromAddress("datenwelt it service AG", sender);
        email.addRecipient(name, recipient, Message.RecipientType.TO);
        
        Mailer mailer = new Mailer(hostname, port, username, password, protocol);
        mailer.sendMail(email);
    }
    
}
