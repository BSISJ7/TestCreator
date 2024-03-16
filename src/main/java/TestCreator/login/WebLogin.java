package TestCreator.login;

import TestCreator.MainMenu;
import TestCreator.users.CreateUser;
import TestCreator.users.UserManager;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import com.google.api.client.auth.oauth2.Credential;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
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
    private  StackPane rootNode;

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
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        } catch (NullPointerException e){
            new StackPaneAlert(rootNode, STR."Error loading users. \{e.getMessage()}").show();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void login() {
        if(loginAttempts >= 3){
            new StackPaneAlert(rootNode, "Too many failed login attempts. Please reset your password.").show();
            resetPassword();
            return;
        }
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (userManager.isAuthorized(username, password)) {
            loadMainMenu(username);
        } else {
            new StackPaneAlert(rootNode, "Incorrect username or password.").show();
            loginAttempts++;
        }
    }

    public void resetPassword() {
        try{
            StageManager.setScene("/login/PasswordResetPanel.fxml");
            ((PasswordResetPanel) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading PasswordResetPanel").show();
            throw new RuntimeException(e);
        }
    }

    public void createUser() {
        try {
            StageManager.setScene("/users/CreateUser.fxml");
            ((CreateUser) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading CreateUser.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void loginWithGoogle() {
        try {
            Credential credential = GoogleAuthenticator.authorize();
            if (credential != null) {
                loadMainMenu("Google User");
            } else {
                new StackPaneAlert(rootNode, "Google authorization failed.").show();
            }
        } catch (IOException | GeneralSecurityException e) {
            new StackPaneAlert(rootNode, STR."Error during Google authorization: \{e.getMessage()}").show();
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
        userManager.initialize(rootNode);
    }
}