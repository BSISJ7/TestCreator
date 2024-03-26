package TestCreator.testIO;

import TestCreator.testIO.awsIO.DatabaseConnectionManager;
import TestCreator.users.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDBIO {
    private static final String RDS_DB_USERNAME = System.getenv("RDS_USERNAME");
    private static final String RDS_DB_PASSWORD = System.getenv("RDS_PASSWORD");

    public static final String USER_DATABASE_INFO
            = "jdbc:mysql://testcreator-db.cyqisi4dgbgt.us-east-2.rds.amazonaws.com:1433/testcreatordb?user" +
            STR."=\{System.getenv("RDS_USERNAME")}&hashedPassword=\{System.getenv("RDS_PASSWORD")}";

    private final DatabaseConnectionManager dbConnManager = new DatabaseConnectionManager(USER_DATABASE_INFO, RDS_DB_USERNAME, RDS_DB_PASSWORD);


    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, hashedPassword, firstName, lastName, email, phoneNumber)" +
                " VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getHashedPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPhoneNumber());
//            stmt.setBoolean(7, user.isEmailVerified());

            stmt.executeUpdate();
        }
    }

    public User getUser(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setHashedPassword(rs.getString("hashedPassword"));
                    user.setFirstName(rs.getString("firstName"));
                    user.setLastName(rs.getString("lastName"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(rs.getString("phoneNumber"));
//                    user.setEmailVerified(rs.getBoolean("emailVerified"));

                    return user;
                } else {
                    return null;
                }
            }
        }
    }

    public void setupUserTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "username VARCHAR(50) PRIMARY KEY, " +
                "hashedPassword VARCHAR(200), " +
                "firstName VARCHAR(50), " +
                "lastName VARCHAR(50), " +
                "email VARCHAR(50), " +
                "phoneNumber VARCHAR(15)" +
//                "emailVerified BOOLEAN" +
                ")";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.execute();
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET hashedPassword = ?, firstName = ?, lastName = ?, email = ?, phoneNumber = ? WHERE username = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getHashedPassword());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getUsername());

            stmt.executeUpdate();
        }
    }

    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            stmt.executeUpdate();
        }
    }

    public boolean userExists(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean passwordMatches(String username, String hashedPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND hashedPassword = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean phoneNumberExists(String phoneNumber) throws SQLException {
        String sql = "SELECT * FROM users WHERE phoneNumber = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phoneNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean emailMatches(String username, String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND email = ?";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = dbConnManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setHashedPassword(rs.getString("hashedPassword"));
                user.setFirstName(rs.getString("firstName"));
                user.setLastName(rs.getString("lastName"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phoneNumber"));

                users.add(user);
            }
        }
        return users;
    }

    public void closeConnection() {
        dbConnManager.closeConnection();
    }
}