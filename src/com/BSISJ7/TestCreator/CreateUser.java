package com.BSISJ7.TestCreator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CreateUser {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button createUserButton;

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,50}$";

    @FXML
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
            // Create user logic here
            User newUser = new User(usernameField.getText(), passwordField.getText());
            goToLoginPage();
        }
    }

    @FXML
    private void goToLoginPage() {
        try {
            Stage stage = (Stage) createUserButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/com/BSISJ7/TestCreator/loginPanel.fxml"))), 800, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
