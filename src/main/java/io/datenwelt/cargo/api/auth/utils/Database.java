/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.datenwelt.cargo.api.auth.utils;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import io.datenwelt.config.Configuration;
import io.datenwelt.config.InvalidConfigurationException;
import io.datenwelt.sql.SqlConfiguration;
import io.datenwelt.sql.SqlDatabase;
import io.datenwelt.sql.mysql.MysqlConfigurationFactory;

/**
 *
 * @author job
 */
public class Database {

    private static SqlDatabase database;

    public static void init() throws SQLException, NoSuchElementException, InvalidConfigurationException {
        if (database == null) {
            Configuration config = Configuration.withDirectory("/etc/datenwelt");
            SqlConfiguration sqlConfiguration = new MysqlConfigurationFactory(config, "auth").create();
            database = sqlConfiguration.loadDatabase();
        }
    }

    public static SqlDatabase get() {
        if ( database == null ) {
            throw new IllegalStateException("Database must be initialized first.");
        }
        return database;
    }

}
