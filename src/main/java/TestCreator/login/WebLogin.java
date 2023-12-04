package TestCreator.login;

import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class WebLogin {
    @FXML
    public VBox loginPane;
    public Hyperlink resetPasswordBtn;
    public Hyperlink noLoginBtn;
    public Hyperlink newUserBtn;
    @FXML
    private  StackPane rootNode;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    private int loginAttempts = 0;


    public void initialize() {

        UserManager.initialize(rootNode);
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
            System.out.println("Loading main menu");
            UserManager.setCurrentUser(username);
            StageManager.setScene("/MainMenu.fxml");
            System.out.println("Main menu loaded");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
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

        if (UserManager.isAuthorized(username, password)) {
            loadMainMenu(username);
        } else {
            new StackPaneAlert(rootNode, "Incorrect username or password.").show();
            loginAttempts++;
        }
    }

    public void resetPassword() {
        try{
            StageManager.setScene("/login/PasswordResetPanel.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading PasswordResetPanel").show();
            throw new RuntimeException(e);
        }
    }

    public void createUser() {
        try {
            StageManager.setScene("/login/CreateUser.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading CreateUser.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void guestLogin() {
        loadMainMenu("Guest");
    }
}