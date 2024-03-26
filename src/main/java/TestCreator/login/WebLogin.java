package TestCreator.login;

import TestCreator.MainMenu;
import TestCreator.users.CreateUser;
import TestCreator.users.UserManager;
import TestCreator.utilities.StageManager;
import com.google.api.client.auth.oauth2.Credential;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class WebLogin {
    @FXML
    public VBox loginPane;
    public Hyperlink resetPasswordBtn;
    public Hyperlink noLoginBtn;
    public Hyperlink newUserBtn;
    @FXML
    public Button googleLoginBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    private int loginAttempts = 0;

    private UserManager userManager;

    public void initialize() {
        loginBtn.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty()));

        passwordField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) login();
        });
        usernameField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) login();
        });
    }

    @FXML
    void loadMainMenu(String username) {
        try{
            StageManager.setScene("/MainMenu.fxml");
            ((MainMenu) StageManager.getStageController()).setUsername(username);
            StageManager.clearStageController();
            userManager.closeConnection();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            StageManager.showAlert(STR."Error loading users. \{e.getMessage()}");
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void login() {
        if(loginAttempts >= 3){
            StageManager.showAlert("Too many failed login attempts. Please reset your password.");
            resetPassword();
            return;
        }
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userManager.isAuthorized(username, password)) {
            loadMainMenu(username);
        } else {
            StageManager.showAlert("Incorrect username or password.");
            loginAttempts++;
        }
    }

    public void resetPassword() {
        try{
            StageManager.setScene("/login/PasswordResetPanel.fxml");
            ((PasswordResetPanel) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading PasswordResetPanel");
            throw new RuntimeException(e);
        }
    }

    public void createUser() {
        try {
            StageManager.setScene("/users/CreateUser.fxml");
            ((CreateUser) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading CreateUser.fxml");
            throw new RuntimeException(e);
        }
    }

    public void loginWithGoogle() {
        try {
            Credential credential = GoogleAuthenticator.authorize();
            if (credential != null) {
                loadMainMenu("Google User");
            } else {
                StageManager.showAlert("Google authorization failed.");
            }
        } catch (IOException | GeneralSecurityException e) {
            StageManager.showAlert(STR."Error during Google authorization: \{e.getMessage()}");
        }
    }

    public void guestLogin() {
        loadMainMenu("Guest");
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setupUserManager() {
        userManager = new UserManager();
        userManager.initialize();
    }
}