package io.datenwelt.cargo.api.auth.endpoints;

import io.datenwelt.cargo.api.auth.endpoints.data.RegistrationInput;
import io.datenwelt.cargo.api.auth.model.Activation;
import io.datenwelt.cargo.api.auth.model.User;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.cargo.rest.Endpoint;
import io.datenwelt.cargo.rest.Request;
import io.datenwelt.cargo.rest.headers.Header;
import io.datenwelt.cargo.rest.response.APIError;
import io.datenwelt.cargo.rest.response.BadRequest;
import io.datenwelt.cargo.rest.response.InternalServerError;
import io.datenwelt.cargo.rest.response.OK;
import io.datenwelt.cargo.rest.response.UnprocessableEntity;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import io.datenwelt.sql.SqlConnection;
import io.datenwelt.sql.SqlDatabase;
import io.datenwelt.sql.SqlQueryResult;
import io.datenwelt.sql.SqlRow;
import io.datenwelt.sql.SqlTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author job
 */
public class RegisterEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(RegisterEndpoints.class);

    public static final Endpoint POST = (Request request) -> {
        Optional<RegistrationInput> body = request.getBodyAs(RegistrationInput.class);
        if (!body.isPresent()) {
            return new UnprocessableEntity(new APIError(600, "REQUEST_ENTITY_MISSING"));
        }
        RegistrationInput registration = body.get();
        String firstname = registration.getFirstname().trim();
        String lastname = registration.getLastname().trim();
        String email = registration.getEmail().trim();
        String password = registration.getPassword().trim();

        if (firstname.isEmpty()) {
            return new BadRequest(new APIError(601, "REQUEST_ENTITY_FIELD_FIRSTNAME_MISSING"));
        }
        if (lastname.isEmpty()) {
            return new BadRequest(new APIError(602, "REQUEST_ENTITY_FIELD_LASTNAME_MISSING"));
        }
        if (email.isEmpty()) {
            return new BadRequest(new APIError(603, "REQUEST_ENTITY_FIELD_EMAIL_MISSING"));
        }
        if (password.isEmpty()) {
            return new BadRequest(new APIError(604, "REQUEST_ENTITY_FIELD_PASSWORD_MISSING"));
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

        SqlDatabase database = Database.get();
        try (SqlConnection connection = database.connect()) {
            SqlTable userTable = database.getTable(User.class);
            String sql = "SELECT count(*) AS num FROM " + userTable.getQuotedTableName() + " WHERE email=?";
            Map<String, Object> row;
            try (SqlQueryResult result = connection.query(sql, email)) {
                row = result.asMap();
            } catch (SQLException ex) {
                throw new SQLException(ex);
            }
            Long num = (Long) row.get("num");
            if (num > 0) {
                return new BadRequest(new APIError(701, "USER_ALREADY_REGISTERED"));
            }
            User user = new User();
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setEmail(email);
            user.setPassword(password);
            user.setActive(0);
            SqlRow<User> userRow = database.getRowFor(user);
            userRow.insert("id");

            Activation activation = Activation.createFor(email);
            SqlRow<Activation> activationRow = database.getRowFor(activation);
            activationRow.insert();
            activation.send(firstname + " " + lastname, link);
            LOG.info("Created new user #{} / {}.", user.getId(), email);
            return new OK();
        } catch (SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };
}
