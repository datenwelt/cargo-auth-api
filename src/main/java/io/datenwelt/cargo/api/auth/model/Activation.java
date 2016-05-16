/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.datenwelt.cargo.api.auth.model;

import io.datenwelt.cargo.api.auth.utils.Mail;
import io.datenwelt.utils.Strings;
import io.datenwelt.sql.SqlStorable;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author job
 */
public class Activation implements SqlStorable {

    private static final Logger LOG = LoggerFactory.getLogger(Activation.class);
    
    private String token;
    private String email;
    private DateTime validUntil;

    public Activation() {
    }

    public static Activation createFor(String email) {
        Activation activation = new Activation();
        activation.email = email;
        activation.token = Strings.token();
        activation.validUntil = DateTime.now().plus(Duration.standardHours(24));
        return activation;
    }

    public void send(String fullname, String link) {
        link += "#" + token;
        String subject = "Your registration at datenwelt.io";
        String text = "Thanks for registering at datenwelt.io. To activate your account please follow this link: " + link;
        Runnable mailer = () -> {
            try {
                Mail.sendmail(fullname, email, subject, text);
            } catch (Exception ex) {
                LOG.error("Unable to send activation link to " + email + ": " + ex.getMessage(), ex);
            }
        };
        new Thread(mailer).start();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public DateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(DateTime validUntil) {
        this.validUntil = validUntil;
    }

}
