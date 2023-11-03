package com.BSISJ7.TestCreator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static com.BSISJ7.TestCreator.MainMenu.MAIN_MENU_LOCATION;

public class LoginPanel {

    @FXML
    public VBox loginPane;
    public Button resetPasswordBtn;
    public Button noLoginBtn;
    public Button newUserBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginBtn;

    @FXML
    void loadMainMenu(String username) {
        try {
            Stage stage = (Stage) loginBtn.getScene().getWindow();
            stage.setTitle("Main Menu - %s".formatted(username));
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource(MAIN_MENU_LOCATION))), 800, 500));
        } catch (IOException e) {
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
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect username or password.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize() {
        loginBtn.disableProperty().bind(usernameField.textProperty().isEmpty()
                .or(passwordField.textProperty().isEmpty()));

        //when enter is pressed in the password field or username field call the authenticate method if the login button is not disabled
        passwordField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) authenticate();
        });
        usernameField.setOnAction(_ -> {
            if (!loginBtn.isDisabled()) authenticate();
        });
    }

    public void resetPassword() {
        try {
            Stage stage = (Stage) resetPasswordBtn.getScene().getWindow();
            stage.setTitle("Password Reset");
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/com/BSISJ7/TestCreator/PasswordResetPanel.fxml"))), 800, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createUser() {
        try {
            Stage stage = (Stage) resetPasswordBtn.getScene().getWindow();
            stage.setTitle("New User");
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/com/BSISJ7/TestCreator/CreateUser.fxml"))), 800, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}