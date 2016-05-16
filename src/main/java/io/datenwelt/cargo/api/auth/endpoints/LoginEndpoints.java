package io.datenwelt.cargo.api.auth.endpoints;

import io.datenwelt.cargo.api.auth.endpoints.data.LoginInput;
import io.datenwelt.cargo.api.auth.model.User;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.cargo.rest.Endpoint;
import io.datenwelt.cargo.rest.Request;
import io.datenwelt.cargo.rest.response.APIError;
import io.datenwelt.cargo.rest.response.APIException;
import io.datenwelt.cargo.rest.response.BadRequest;
import io.datenwelt.cargo.rest.response.InternalServerError;
import io.datenwelt.cargo.rest.response.LoginFailed;
import io.datenwelt.cargo.rest.response.OK;
import io.datenwelt.cargo.rest.response.UnprocessableEntity;
import java.sql.SQLException;
import io.datenwelt.sql.SqlConnection;
import io.datenwelt.sql.SqlDatabase;
import io.datenwelt.sql.SqlQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author job
 */
public class LoginEndpoints {

    private static final Logger LOG = LoggerFactory.getLogger(LoginEndpoints.class);

    public static final Endpoint POST = (Request request) -> {
        LoginInput input = request.getBodyAs(LoginInput.class).orElseThrow(() -> (new APIException(new UnprocessableEntity(new APIError(600, "REQUEST_ENTITY_MISSING")))));
        String username = input.username;
        String password = input.password;
        username = username == null ? "" : username.trim();
        password = password == null ? "" : password.trim();
        if (username.isEmpty()) {
            return new BadRequest(new APIError(601, "REQUEST_ENTITY_FIELD_USERNAME_MISSING"));
        }
        if (password.isEmpty()) {
            return new BadRequest(new APIError(602, "REQUEST_ENTITY_FIELD_PASSWORD_MISSING"));
        }
        SqlDatabase db = Database.get();
        try (SqlConnection conn = db.connect()) {
            String sql = "SELECT * FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=? AND password=? AND active=1";
            try ( SqlQueryResult rs = conn.query(sql, username, password) ) {
                if ( rs.first() ) {
                    User user = rs.as(User.class);
                    LOG.info("Login succesful for user #{} / {}.", user.getId(), user.getEmail());
                    return new OK();
                } else {
                    LOG.info("Login failed for user {}.", username);
                    return new LoginFailed();   
                }
            }
        } catch (SQLException ex) {
            LOG.error("Database error: {}", ex.getMessage(), ex);
            return new InternalServerError();
        }
    };

}
