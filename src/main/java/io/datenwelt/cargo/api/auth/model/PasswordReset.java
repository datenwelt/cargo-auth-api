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
public class PasswordReset implements SqlStorable {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordReset.class);
    
    private String token;
    private String email;
    private DateTime validUntil;

    public PasswordReset() {
    }

    public static PasswordReset createFor(String email) {
        PasswordReset reset = new PasswordReset();
        reset.email = email;
        reset.token = Strings.token();
        reset.validUntil = DateTime.now().plus(Duration.standardHours(24));
        return reset;
    }

    public void send(String fullname, String link) {
        link += "#" + token;
        String subject = "Change your password at datenwelt.io";
        String text = "To reset your password please follow this link: " + link;
        Runnable mailer = () -> {
            try {
                Mail.sendmail(fullname, email, subject, text);
            } catch (Exception ex) {
                LOG.error("Unable to send password reset link to " + email + ": " + ex.getMessage(), ex);
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
