package io.datenwelt.auth.api.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.datenwelt.cargo.api.auth.model.User;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.config.InvalidConfigurationException;
import io.datenwelt.sql.SqlConfiguration;
import io.datenwelt.sql.SqlConnection;
import io.datenwelt.sql.SqlDatabase;
import io.datenwelt.sql.SqlRow;
import io.datenwelt.sql.mysql.MysqlConfiguration;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author job
 */
public class RegisterEndpointsIT {
    
    public RegisterEndpointsIT() {
    }
    
    @BeforeClass
    public static void setUpClass() throws SQLException, NoSuchElementException, InvalidConfigurationException {
        Database.init();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSuccessfulPOST() throws SQLException, JsonProcessingException, IOException {
        // Prepare the test.
        SqlDatabase db = Database.get();
        SqlConnection conn = db.connect();
        User testUser = User.createSample();  
        conn.execute("DELETE FROM " + db.getTable(User.class).getQuotedTableName() + " WHERE email=?", testUser.getEmail());

        ObjectMapper om = new ObjectMapper();
        ObjectNode json = om.createObjectNode();
        json.put("firstname", testUser.getFirstname());
        json.put("lastname", testUser.getLastname());
        json.put("email", testUser.getEmail());
        json.put("password", testUser.getPassword());
        String content = om.writeValueAsString(json);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), content);                

        Request.Builder builder = new Request.Builder();
        builder.url("http://localhost:8000/datenwelt-auth-api/api/v1/register");
        builder.header("Origin", "localhost:8080");
        builder.header("Referer", "http://localhost:8080/register.html");
        builder.post(body);
        Request request = builder.build();
        
        OkHttpClient client = new OkHttpClient();
        Call apiCall = client.newCall(request);
        
        Response response = apiCall.execute();
       // assertTrue("API call is successful", response.isSuccessful());
        

    }
    
}
