package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbConnection {
    private static final String DATABASE_PROPERTIES = "database.properties";

    private final DatabaseConfig databaseConfig;

    public DbConnection() {
        this.databaseConfig = loadDatabaseConfig();
        loadDriver(databaseConfig.getDriver());
    }

    public DbConnection(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                databaseConfig.getUrl(),
                databaseConfig.getUsername(),
                databaseConfig.getPassword()
        );
    }

    private DatabaseConfig loadDatabaseConfig() {
        Properties properties = new Properties();

        try (InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(DATABASE_PROPERTIES)) {

            if (inputStream == null) {
                throw new IllegalStateException("Database configuration file not found: " + DATABASE_PROPERTIES);
            }

            properties.load(inputStream);

            return new DatabaseConfig(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"),
                    properties.getProperty("db.driver")
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load database configuration", e);
        }
    }

    private void loadDriver(String driver) {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Database driver not found: " + driver, e);
        }
    }
}