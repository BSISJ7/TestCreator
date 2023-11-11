package TestCreator.login;

import TestCreator.Main;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.UserManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;


public class CreateUser {

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,50}$";
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createUserButton;
    


    public void initialize() {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateInputs());
    }

    private void validateInputs() {
        boolean usernameValid = usernameField.getText().length() <= 50;
        usernameField.setStyle(usernameValid ? "-fx-border-color: green" : "-fx-border-color: red");

        boolean passwordValid = passwordField.getText().matches(PASSWORD_PATTERN);
        passwordField.setStyle(passwordValid ? "-fx-border-color: green" : "-fx-border-color: red");

        createUserButton.setDisable(!(usernameValid && passwordValid));
    }

    @FXML
    private void createUser() {
        if (!createUserButton.isDisabled()) {
            UserManager.addUser(usernameField.getText(), passwordField.getText());
            goToLoginPage();
        }
    }

    @FXML
    private void goToLoginPage() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
