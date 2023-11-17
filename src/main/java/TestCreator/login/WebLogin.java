package TestCreator.login;

import TestCreator.utilities.StageManager;
import TestCreator.utilities.UserManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;


public class WebLogin {
    @FXML
    public VBox loginPane;
    public Hyperlink resetPasswordBtn;
    public Hyperlink noLoginBtn;
    public Hyperlink newUserBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;


    public void initialize() {

        loginBtn.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty()));

        passwordField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) authenticate();
        });
        usernameField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) authenticate();
        });
    }

    @FXML
    void loadMainMenu(String username) {
        try{
            UserManager.addUser(username);
            UserManager.setCurrentUser(username);
            StageManager.setScene("/MainMenu.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    void loadMainMenu() {
        loadMainMenu("Guest");
    }

    @FXML
    public void authenticate() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.equals("admin") && password.equals("pass")) {
            loadMainMenu(username);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("WebLogin Failed");
            alert.setContentText("Incorrect username or password.");
            alert.showAndWait();
        }
    }

    public void resetPassword() {
        try{
            StageManager.setScene("/login/PasswordResetPanel.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void createUser() {
        try {
            StageManager.setScene("/login/CreateUser.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}