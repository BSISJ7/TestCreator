package TestCreator.login;

import TestCreator.utilities.DictionaryManager;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.mail.MessagingException;
import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;


public class PasswordResetRequest {

    public Button sendButton;
    public Button cancelButton;
    public TextField emailTextField;
    public VBox passwordResetPane;
    public Label resetPassphraseLabel;
    public TextField resetPassphraseTextField;
    public Button resetButton;

    private String resetPassphraseString;

    @FXML
    public void initialize() {
        StageManager.setTitle("Password Reset");
        emailTextField.setOnAction(_ -> sendPassResetEmail());
        emailTextField.setOnKeyTyped(_ -> {
            if (emailTextField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                emailTextField.setStyle("-fx-border-color: green");
                sendButton.setDisable(false);
            } else {
                emailTextField.setStyle("-fx-border-color: red");
                sendButton.setDisable(!resetPassphraseTextField.getText().isEmpty());
            }
        });
        resetPassphraseTextField.setOnKeyTyped(_ -> {
            resetButton.setDisable(resetPassphraseTextField.getText().isEmpty() || resetPassphraseString.isEmpty());
        });
    }

    public void sendPassResetEmail() {
        if(!UserManager.emailExists(emailTextField.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Error");
            alert.setHeaderText("Email Error");
            alert.setContentText("The email address provided is not associated with an account.");
            alert.showAndWait();
            return;
        }

        try{
            resetPassphraseString = DictionaryManager.getDictionary().getRandomWords(3);
            EmailSender.sendEmail(emailTextField.getText(), "BrSanders@tutanota.com", "email-smtp.us-east-2.amazonaws.com",
                    "Password Reset", STR."Enter the passphrase \{resetPassphraseString} to reset your password.", EmailSender.SMTP_USER, EmailSender.SMTP_PASS);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("EmailSender Sent");
            alert.setContentText("An email has been sent to the address provided.");
            alert.showAndWait();
            resetPassphraseLabel.setVisible(true);
            resetPassphraseTextField.setVisible(true);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email Error");
            alert.setHeaderText("Email Error");
            alert.setContentText("Email failed to send: " + mex.getMessage());
            alert.showAndWait();
        }
    }

    public void loadCreateNewPassword() {
        try {
            StageManager.setScene("/login/CreateNewPassword.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void resetPassword() {
        if(resetPassphraseTextField.getText().equals(resetPassphraseString)){
            loadCreateNewPassword();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Password Reset");
            alert.setHeaderText("Password Reset");
            alert.setContentText("The passphrase you entered is incorrect.");
            alert.showAndWait();
        }
    }

    public void returnToLogin() {
        try {
            StageManager.setScene("/login/WebLogin.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
