package TestCreator.users;

import TestCreator.testIO.UserDBIO;
import TestCreator.utilities.StackPaneAlert;
import javafx.scene.layout.StackPane;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final List<User> USER_LIST = new ArrayList<>();

    private User currentUser;
    private final UserDBIO userDBIO = new UserDBIO();


    public void initialize(StackPane rootNode) {
        try {
            userDBIO.setupUserTable();
            USER_LIST.addAll(userDBIO.getAllUsers());
        }catch (SQLException e){
            new StackPaneAlert(rootNode, STR."Connection to database failed: \{e.getMessage()}").show();
        }
    }

    public int size() {
        return USER_LIST.size();
    }

    public boolean isAuthorized(String username, String password) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)
                    && UserAuthenticator.authenticate(user.getHashedPassword(), password)) return true;
        }
        return false;
    }

    public List<User> getUserList() {
        return USER_LIST;
    }

    public void addUser(String username, String hashedPassword) throws SQLException {
        USER_LIST.add(new User(username, hashedPassword));
        userDBIO.addUser(new User(username, hashedPassword));
    }

    public void addUser(String username) {
        if(!userExists(username)) {
            USER_LIST.add(new User(username));
        }
    }

    public boolean userExists(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return true;
        }
        return false;
    }

    public User getUser(String username) {
        for (User user : USER_LIST) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }

    public void setCurrentUser(String username) {
        try {
            currentUser = getUser(username);
        } catch (NullPointerException e) {
            System.out.println("User does not exist");
        }
    }

    public boolean setUserByEmail(String email){
        for (User user : USER_LIST) {
            if (user.getEmail().equals(email)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public boolean emailDoesNotExist(String email){
        for (User user : USER_LIST) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                currentUser = user;
                return false;
            }
        }
        return true;
    }

    public String getCurrentUsername() throws NullPointerException{
        return currentUser.getUsername();
    }

    public void setCurrentUserPassword(String newPassword) {
        currentUser.setHashedPassword(UserAuthenticator.generateStrongPasswordHash(newPassword));
    }

    public void closeConnection() {
        userDBIO.closeConnection();
    }

    public void addUser(String username, String hashedPassword, String email) throws SQLException {
        USER_LIST.add(new User(username, hashedPassword, email));
        userDBIO.addUser(new User(username, hashedPassword, email));
    }

    public String getHashedPassword() {
        return currentUser.getHashedPassword();
    }

    public void setCurrentUserEmail(String text) {
        currentUser.setEmail(text);
    }

    public String getUsername() {
        return currentUser.getUsername();
    }

    public void setEmail(String trim) {
        currentUser.setEmail(trim);
    }

    public String getEmail() {
        return currentUser.getEmail();
    }

    public void updateUser() throws SQLException {
        userDBIO.updateUser(currentUser);
    }
}
