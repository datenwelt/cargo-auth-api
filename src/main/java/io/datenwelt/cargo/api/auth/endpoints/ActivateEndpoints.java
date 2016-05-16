package io.datenwelt.cargo.api.auth.endpoints;

import io.datenwelt.cargo.api.auth.endpoints.data.ActivationInput;
import io.datenwelt.cargo.api.auth.model.Activation;
import io.datenwelt.cargo.api.auth.model.User;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.cargo.rest.Endpoint;
import io.datenwelt.cargo.rest.Request;
import io.datenwelt.cargo.rest.headers.Header;
import io.datenwelt.cargo.rest.query.Query;
import io.datenwelt.cargo.rest.response.APIError;
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
public class ActivateEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(ActivateEndpoints.class);

    public static final Endpoint GET = (Request request) -> {
        Optional<Query> query = request.query("username");
        if (!query.isPresent() || !query.get().getValue().isPresent()) {
            return new BadRequest(new APIError(601, "QUERY_USERNAME_MISSING"));
        }
        if (!request.header("Referer").isPresent()) {
            return new BadRequest(new APIError(610, "HEADER_REFERER_IS_MISSING"));
        }
        Header referer = request.header("Referer").get();
        String link = referer.get();
        int linkEndPos = link.indexOf("register.html");
        if (linkEndPos == -1) {
            return new BadRequest(new APIError(611, "HEADER_REFERER_WRONG_FORMAT"));
        }
        link = link.substring(0, linkEndPos) + "activate.html";
        String username = query.get().getValue().get();
        SqlDatabase db = Database.get();
        try (SqlConnection conn = db.connect()) {
            String sql = "SELECT * FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=? AND active=0";
            try (SqlQueryResult rs = conn.query(sql, username)) {
                if (!rs.first()) {
                    LOG.debug("Activation link requested but user already activated: " + username);
                    return new OK();
                }
                User user = rs.as(User.class);
                String fullname = user.getFirstname() + " " + user.getLastname();
                Activation activation = Activation.createFor(username);
                SqlRow<Activation> row = db.getRowFor(activation);
                row.insert();
                activation.send(fullname, link);
                LOG.info("Created new activation link for user #{} / {}.", user.getId(), username);
                return new OK();
            }
        } catch (SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };

    public static final Endpoint POST = (Request request) -> {
        Optional<ActivationInput> body = request.getBodyAs(ActivationInput.class);
        if (!body.isPresent()) {
            return new UnprocessableEntity(new APIError(600, "REQUEST_ENTITY_MISSING"));
        }
        ActivationInput input = body.get();    
        String token = input.getToken();
        if ( token.isEmpty() ) {
            return new BadRequest(new APIError(601, "REQUEST_ENTITY_FIELD_TOKEN_MISSING"));
        }
        SqlDatabase db = Database.get();
        try {
            SqlRow<Activation> row =  db.getNewRowFor(Activation.class);
            try {
                row.load(token);
            } catch (NoSuchElementException ex) {
                return new NotFound(new APIError(701, "TOKEN_UNKNOWN"));
            }
            Activation activation = row.get();
            if ( activation.getValidUntil().isBeforeNow() ) {
                return new Gone(new APIError(702, "TOKEN_EXPIRED"));
            }
            String sql = "SELECT * FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=?";
            try (SqlConnection conn = db.connect()) {
                try ( SqlQueryResult rs = conn.query(sql, activation.getEmail())) {
                    if ( !rs.first()) {
                        LOG.error("Tried to activate a user from activation token {}, but user {} was missing in database.", token, activation.getEmail());
                        return new InternalServerError();
                    }
                    User user = rs.as(User.class);
                    user.setActive(1);
                    db.getRowFor(user).update();
                    return new OK();
                }
            }            
        } catch(SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };

}
