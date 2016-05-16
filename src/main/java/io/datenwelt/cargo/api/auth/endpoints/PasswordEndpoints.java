package io.datenwelt.cargo.api.auth.endpoints;

import io.datenwelt.cargo.api.auth.model.PasswordReset;
import io.datenwelt.cargo.api.auth.model.User;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.cargo.rest.Endpoint;
import io.datenwelt.cargo.rest.Request;
import io.datenwelt.cargo.rest.headers.Header;
import io.datenwelt.cargo.rest.query.Query;
import io.datenwelt.cargo.rest.response.APIError;
import io.datenwelt.cargo.rest.response.APIException;
import io.datenwelt.cargo.rest.response.BadRequest;
import io.datenwelt.cargo.rest.response.Gone;
import io.datenwelt.cargo.rest.response.InternalServerError;
import io.datenwelt.cargo.rest.response.NotFound;
import io.datenwelt.cargo.rest.response.OK;
import io.datenwelt.cargo.rest.response.UnprocessableEntity;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import io.datenwelt.sql.SqlConnection;
import io.datenwelt.sql.SqlDatabase;
import io.datenwelt.sql.SqlQueryResult;
import io.datenwelt.sql.SqlRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author job
 */
public class PasswordEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(PasswordEndpoints.class);

    public static final Endpoint GET = (Request request) -> {
        Optional<Query> query = request.query("username");
        if (!query.isPresent() || !query.get().getValue().isPresent()) {
            return new BadRequest(new APIError(601, "QUERY_USERNAME_IS_MISSING"));
        }
        if (!request.header("Referer").isPresent()) {
            return new BadRequest(new APIError(610, "HEADER_REFERER_IS_MISSING"));
        }
        Header referer = request.header("Referer").get();
        String link = referer.get();
        int linkEndPos = link.indexOf("password.html");
        if (linkEndPos == -1) {
            return new BadRequest(new APIError(611, "HEADER_REFERER_IS_MISSING"));
        }
        link = link.substring(0, linkEndPos) + "password.html";
        String username = query.get().getValue().get();
        SqlDatabase db = Database.get();
        try (SqlConnection conn = db.connect()) {
            String sql = "SELECT * FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=? AND active=1";
            try (SqlQueryResult rs = conn.query(sql, username)) {
                if (!rs.first()) {
                    LOG.debug("Password reset link requested but user unknown or not active: " + username);
                    return new OK();
                }
                User user = rs.as(User.class);
                String fullname = user.getFirstname() + " " + user.getLastname();
                PasswordReset reset = PasswordReset.createFor(username);
                SqlRow<PasswordReset> row = db.getRowFor(reset);
                row.insert();
                reset.send(fullname, link);
                LOG.info("Created new password reset link for user #{} / {}.", user.getId(), username);
                return new OK();
            }
        } catch (SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };

    public static class PostInput {
        public String token;
        public String password;
    };
    
    public static final Endpoint POST = (Request request) -> {
        PostInput input = request.getBodyAs(PostInput.class).orElseThrow(() -> (new APIException(new UnprocessableEntity(new APIError(600, "REQUEST_ENTITY_MISSING")))));
        String token = input.token == null ? "" : input.token.trim();
        if (token.isEmpty()) {
            return new BadRequest(new APIError(601, "REQUEST_ENTITY_FIELD_TOKEN_MISSING"));
        }
        String password = input.password == null ? "" : input.password.trim();
        if (password.isEmpty()) {
            return new BadRequest(new APIError(602, "REQUEST_ENTITY_FIELD_PASSWORD_MISSING"));
        }
        SqlDatabase db = Database.get();
        try {
            SqlRow<PasswordReset> row = db.getNewRowFor(PasswordReset.class);
            try {
                row.load(token);
            } catch (NoSuchElementException ex) {
                return new NotFound(new APIError(701, "TOKEN_UNKNOWN"));
            }
            PasswordReset reset = row.get();
            if (reset.getValidUntil().isBeforeNow()) {
                return new Gone(new APIError(702, "TOKEN_EXPIRED"));
            }
            String sql = "SELECT * FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=?";
            try (SqlConnection conn = db.connect()) {
                try (SqlQueryResult rs = conn.query(sql, reset.getEmail())) {
                    if (!rs.first()) {
                        LOG.error("Tried to reset password for user from password reset token {}, but user {} was missing in database.", token, reset.getEmail());
                        return new InternalServerError();
                    }
                    User user = rs.as(User.class);
                    user.setPassword(password);
                    user.setActive(1);
                    db.getRowFor(user).update();
                    row.delete();
                    return new OK();
                }
            }
        } catch (SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };

}
