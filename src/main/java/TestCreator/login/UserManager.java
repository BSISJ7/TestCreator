package TestCreator.login;

import TestCreator.testIO.UserDBIO;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static final List<User> USER_LIST = new ArrayList<>();

    private static User currentUser;
    private static final UserDBIO userDBIO = new UserDBIO();

    private UserManager(){
//        if(Main.TESTING_MODE) GenerateUsers();
    }

    public static void initialize() {
        try {
            userDBIO.setupUserTable();
            USER_LIST.addAll(userDBIO.getAllUsers());
        }catch (SQLException e){
            System.out.println("Connection to database failed: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Connection Error");
            alert.setHeaderText("Database Connection Error");
            alert.setContentText("Connection to database failed: " + e.getMessage());
            alert.showAndWait();
        }
    }

    public static int size() {
        return USER_LIST.size();
    }

    public static boolean isAuthorized(String username, String password) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)
                    && UserAuthenticator.authenticate(user.getHashedPassword(), password)) return true;
        }
        return false;
    }

    public static List<User> getUserList() {
        return USER_LIST;
    }

    public static void addUser(String username, String hashedPassword) throws SQLException {
        USER_LIST.add(new User(username, hashedPassword));
        userDBIO.addUser(new User(username, hashedPassword));
    }

    public static void addUser(String username) {
        if(!userExists(username)) {
            USER_LIST.add(new User(username));
        }
    }

    public static boolean userExists(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    public static User getUser(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public static void setCurrentUser(String username) {
        try {
            currentUser = getUser(username);
        } catch (NullPointerException e) {
            System.out.println("User does not exist");
        }
    }

    public static boolean setUserByEmail(String email){
        for (User user : USER_LIST) {
            if (user.getEmail().equals(email)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public static boolean emailExists(String email){
        for (User user : USER_LIST) {
            if (user.getEmail().equals(email)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public static String getCurrentUsername() {
        return currentUser.getUsername();
    }

    public static void setCurrentUserPassword(String newPassword) {
        currentUser.setHashedPassword(UserAuthenticator.generateStrongPasswordHash(newPassword));
    }
}
