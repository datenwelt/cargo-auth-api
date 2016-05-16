/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.datenwelt.cargo.api.auth;

import io.datenwelt.cargo.api.auth.endpoints.ActivateEndpoints;
import io.datenwelt.cargo.api.auth.endpoints.LoginEndpoints;
import io.datenwelt.cargo.api.auth.endpoints.PasswordEndpoints;
import io.datenwelt.cargo.api.auth.endpoints.RegisterEndpoints;
import io.datenwelt.cargo.api.auth.utils.Database;
import io.datenwelt.cargo.api.auth.utils.Mail;
import io.datenwelt.cargo.rest.Router;
import io.datenwelt.cargo.rest.filters.AccessLog;
import io.datenwelt.cargo.rest.filters.CORSFilter;
import io.datenwelt.cargo.rest.response.OK;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import javax.servlet.ServletException;
import io.datenwelt.config.InvalidConfigurationException;

/**
 *
 * @author job
 */
public class AuthAPI extends Router {
    

    @Override
    public void init() throws ServletException {
        try {
            Database.init();
        } catch (SQLException | NoSuchElementException | InvalidConfigurationException ex) {
            throw new ServletException("Unable to initialize database.", ex);   
        }
        
        try {
            Mail.init();
        } catch (RuntimeException ex) {
            throw new ServletException("Unable to initialize mailer.", ex);   
        }
        
        filter(CORSFilter.class);
        filter(AccessLog.class);
        
        GET("/", (r)->(new OK()));
        POST("/register", RegisterEndpoints.POST);
        GET("/activate", ActivateEndpoints.GET);
        POST("/activate", ActivateEndpoints.POST);
        POST("/login", LoginEndpoints.POST);
        GET("/password", PasswordEndpoints.GET);
        POST("/password", PasswordEndpoints.POST);
    }
    
    
    
}
