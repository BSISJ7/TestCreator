package TestCreator.login;

import TestCreator.users.UserManager;
import TestCreator.utilities.DictionaryManager;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import javax.mail.MessagingException;
import java.io.IOException;

import static TestCreator.login.EmailVerifier.isEmailVerified;


public class PasswordResetPanel {

    @FXML
    private Button sendButton;
    @FXML
    private TextField emailTextField;
    @FXML
    private Label resetPassphraseLabel;
    @FXML
    private TextField resetPassphraseTextField;
    @FXML
    private Button resetButton;
    private String resetPassphraseString;
    @FXML
    private StackPane rootNode;
    private UserManager userManager;

    @FXML
    public void initialize() {
        StageManager.setTitle("Password Reset");
        emailTextField.setOnAction(_ -> sendPassResetEmail());
        emailTextField.setOnKeyTyped(_ -> {
            if (emailTextField.getText().trim().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                emailTextField.setStyle("-fx-border-color: green");
                sendButton.setDisable(false);
            } else {
                emailTextField.setStyle("-fx-border-color: red");
                sendButton.setDisable(!resetPassphraseTextField.getText().isEmpty());
            }
        });
        resetPassphraseTextField.setOnKeyTyped(_ -> resetButton.setDisable(resetPassphraseTextField.getText().isEmpty() || resetPassphraseString.isEmpty()));
    }

    public void sendPassResetEmail() {
        if (!emailTextField.getText().trim().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            StageManager.showAlert("Invalid email syntax.");
        }
        else if(userManager.emailDoesNotExist(emailTextField.getText())){
            StageManager.showAlert("The email address provided is not associated with an account.");
        }
        else if(!isEmailVerified(emailTextField.getText())){
            EmailVerifier.verifyEmail(emailTextField.getText());
            StageManager.showAlert("The email address provided has not been verified yet. A verification email" +
                    " has been sent to your email.");
        } else {
            try{
                resetPassphraseString = DictionaryManager.getDictionary().getRandomWords(3, "-");
                resetPassphraseLabel.setVisible(true);
                resetPassphraseTextField.setVisible(true);
                StageManager.showAlert("An email with a reset passphrase been sent to the address provided.");
                EmailSender.sendEmail(emailTextField.getText(), emailTextField.getText(), "email-smtp.us-east-2.amazonaws.com",
                        "Password Reset", STR."Enter the passphrase \{resetPassphraseString} to reset your password.", EmailSender.SMTP_USER, EmailSender.SMTP_PASS);
            } catch (MessagingException mex) {
                mex.printStackTrace();
                StageManager.showAlert(STR."Email failed to send: \{mex.getMessage()}");
            }
        }
    }

    public void loadCreateNewPassword() {
        try {
            StageManager.setScene("/login/CreateNewPassword.fxml");
            ((CreateNewPassword) StageManager.getStageController()).setUserManager(userManager);

            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading CreateNewPassword.fxml");
            throw new RuntimeException(e);
        }
    }

    public void resetPassword() {
        if(resetPassphraseTextField.getText().equals(resetPassphraseString)){
            loadCreateNewPassword();
        } else {
            StageManager.showAlert("The passphrase you entered is incorrect.");
        }
    }

    public void returnToLogin() {
        try {
            StageManager.setScene("/login/WebLogin.fxml");
            ((WebLogin) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading WebLogin.fxml");
            throw new RuntimeException(e);
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
