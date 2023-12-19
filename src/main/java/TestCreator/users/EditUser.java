package TestCreator.users;

import TestCreator.login.CreateNewPassword;
import TestCreator.login.EmailVerifier;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import software.amazon.awssdk.services.ses.model.SesException;

import java.io.IOException;


public class EditUser {
    @FXML
    public StackPane rootNode;
    @FXML
    public Label usernameLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button updateButton;
    private final UserManager userManager = new UserManager();
    private User user;

    public void initialize() {
        emailTextField.textProperty().addListener((_, _, _) -> validateInputs());
    }

    public void setUser(String username) {
        user = userManager.getUser(username);
        usernameLabel.setText(username);
        emailTextField.setText(user.getEmail());
    }

    private void validateInputs() {
        boolean isEmailValid = emailTextField.getText().trim().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                || emailTextField.getText().isEmpty();
        updateButton.setDisable(!isEmailValid);
    }

    @FXML
    private void updateEmail() {
        try {
            if (userManager.emailDoesNotExist(emailTextField.getText())) {
                EmailVerifier.verifyEmail(emailTextField.getText());
                user.setEmail(emailTextField.getText());
                new StackPaneAlert(rootNode, "Email has been updated successfully.  A verification email has been" +
                        "sent to the address you have given.").showAndWait().thenAccept(_ -> returnToMainMenu());
            } else
                new StackPaneAlert(rootNode, "This email is already associated with an account.").show();
        } catch (SesException e) {
            new StackPaneAlert(rootNode, STR."Email verification error: \{e.getMessage()}").show();
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void returnToMainMenu() {
        try {
            StageManager.setScene("MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

/**
 * This method is used to change the password of a user.
 * It sets the scene to the ChangePassword.fxml file, which is the UI for changing the password.
 * It also sets the UserManager of the CreateNewPassword controller to the current UserManager.
 * After setting the scene and the UserManager, it clears the stage controller.
 * If there is an IOException while loading the ChangePassword.fxml file, it shows an alert with the error message
 * and throws a RuntimeException.
 *
 * @throws IOException If there is an error loading the ChangePassword.fxml file.
 */
public void changePassword() {
    try {
        StageManager.setScene("/users/ChangePassword.fxml");
        ((CreateNewPassword) StageManager.getStageController()).setUserManager(userManager);
        StageManager.clearStageController();
    } catch (IOException e) {
        new StackPaneAlert(rootNode, "Error loading ChangePassword.fxml").show();
        throw new RuntimeException(e);
    }
}
}
