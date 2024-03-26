package TestCreator.users;

import TestCreator.MainMenu;
import TestCreator.login.ChangePassword;
import TestCreator.login.EmailVerifier;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;


public class EditUser {
    @FXML
    public StackPane rootNode;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label emailLabel;
    @FXML
    public Button verifyEmailButton;
    private final UserManager userManager = new UserManager();
    @FXML
    public Button changeEmailButton;

    public void initialize() {
        userManager.initialize();
    }

    public void setUser(String username) {
        StageManager.setTitle(STR."Profile: \{username}");
        userManager.setCurrentUser(username);
        usernameLabel.setText(STR."Username: \{username}");
        emailLabel.setText(userManager.getEmail() == null ? "No email associated with this account." : userManager.getEmail());
        boolean isVerified = username.equalsIgnoreCase("guest") || EmailVerifier.isEmailVerified(userManager.getEmail());
        verifyEmailButton.setDisable(username.equalsIgnoreCase("guest") || isVerified);
        changeEmailButton.setDisable(username.equalsIgnoreCase("guest"));
    }

    @FXML
    private void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
            ((MainMenu) StageManager.getStageController()).setUsername(userManager.getUsername());
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
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
 */
public void changePassword() {
    try {
        StageManager.setScene("/login/ChangePassword.fxml");
        ((ChangePassword) StageManager.getStageController()).setUserManager(userManager);
        StageManager.clearStageController();
    } catch (IOException e) {
        StageManager.showAlert("Error loading ChangePassword.fxml");
        throw new RuntimeException(e);
    }
}

    public void changeEmail() {
        try {
            StageManager.setScene("/users/ChangeEmail.fxml");
            ((ChangeEmail) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading ChangeEmail.fxml");
            throw new RuntimeException(e);
        }
    }

    public void verifyEmail() {
        verifyEmailButton.setDisable(true);
        EmailVerifier.verifyEmail(userManager.getEmail());
        StageManager.showAlert("A verification email has been sent to your email address.");
    }
}
