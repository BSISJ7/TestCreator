package TestCreator.login;

import TestCreator.utilities.DictionaryManager;
import TestCreator.utilities.StackPaneAlert;
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
        if (!emailTextField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            new StackPaneAlert(rootNode, "Invalid email syntax.").show();
        }
        else if(userManager.emailDoesNotExist(emailTextField.getText())){
            new StackPaneAlert(rootNode, "The email address provided is not associated with an account.").show();
        }
        else if(!isEmailVerified(emailTextField.getText())){
            EmailVerifier.verifyEmail(emailTextField.getText());
            new StackPaneAlert(rootNode, "The email address provided has not been verified yet. A verification email" +
                    " has been sent to your email.").show();
        } else {
            try{
                resetPassphraseString = DictionaryManager.getDictionary().getRandomWords(3, "-");
                resetPassphraseLabel.setVisible(true);
                resetPassphraseTextField.setVisible(true);
                new StackPaneAlert(rootNode, "An email with a reset passphrase been sent to the address provided.").show();
                EmailSender.sendEmail(emailTextField.getText(), emailTextField.getText(), "email-smtp.us-east-2.amazonaws.com",
                        "Password Reset", STR."Enter the passphrase \{resetPassphraseString} to reset your password.", EmailSender.SMTP_USER, EmailSender.SMTP_PASS);
            } catch (MessagingException mex) {
                mex.printStackTrace();
                new StackPaneAlert(rootNode, "Email failed to send: " + mex.getMessage()).show();
            }
        }
    }

    public void loadCreateNewPassword() {
        try {
            StageManager.setScene("/login/CreateNewPassword.fxml");
            ((CreateNewPassword) StageManager.getStageController()).setUserManager(userManager);

            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading CreateNewPassword.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void resetPassword() {
        if(resetPassphraseTextField.getText().equals(resetPassphraseString)){
            loadCreateNewPassword();
        } else {
            new StackPaneAlert(rootNode, "The passphrase you entered is incorrect.").show();
        }
    }

    public void returnToLogin() {
        try {
            StageManager.setScene("/login/WebLogin.fxml");
            ((WebLogin) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading WebLogin.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
