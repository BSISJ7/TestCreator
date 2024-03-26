package TestCreator.dataIO.awsIO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionManager {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseConnectionManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void closeConnection() {
        try {
            DriverManager.getConnection(jdbcUrl, username, password).close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}