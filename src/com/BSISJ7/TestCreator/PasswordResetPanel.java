package com.BSISJ7.TestCreator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PasswordResetPanel {

    public Button sendButton;
    public Button cancelButton;
    public TextField emailTextField;
    public VBox passwordResetPane;

    public void initialize(){
//        sendButton.disableProperty().bind(emailTextField.textProperty().isEmpty());
        emailTextField.setOnAction(_ -> sendPassResetEmail());
        //when a key is pressed in the email text field, check if the email is valid
        emailTextField.setOnKeyTyped(_ -> {
            if (emailTextField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                emailTextField.setStyle("-fx-border-color: green");
                sendButton.setDisable(false);
            } else {
                emailTextField.setStyle("-fx-border-color: red");
                sendButton.setDisable(true);
            }
        });
    }

    public void sendPassResetEmail() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email Sent");
        alert.setContentText("An email has been sent to the address provided.");
        alert.showAndWait();
        returnToLogin();
    }

    public void returnToLogin() {
        try {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/com/BSISJ7/TestCreator/loginPanel.fxml"))), 800, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
