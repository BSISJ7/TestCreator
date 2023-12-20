package TestCreator.users;

import TestCreator.login.EmailVerifier;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StackPaneDialogue;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;

public class ChangeEmail {

    @FXML
    public Label emailLabel;
    @FXML
    public StackPane rootNode;
    @FXML
    public TextField emailTextField;
    @FXML
    public Button changeEmailButton;
    private UserManager userManager;

    public void initialize() {
        emailTextField.textProperty().addListener((_, _, _) -> validateInputs());
        emailTextField.prefWidthProperty().bind(rootNode.widthProperty().multiply(0.8));
    }

    private void validateInputs() {
        boolean isEmailValid = emailTextField.getText().trim().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                && !emailTextField.getText().isEmpty() && !emailTextField.getText().trim().equalsIgnoreCase(userManager.getEmail());
        changeEmailButton.setDisable(!isEmailValid);
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
        emailLabel.setText(userManager.getEmail());
    }

    public void changeEmail() {
        userManager.setEmail(emailTextField.getText().trim());
        try {
            userManager.updateUser();
            changeEmailButton.setDisable(true);
            if (EmailVerifier.isEmailVerified(userManager.getEmail()))
                new StackPaneAlert(rootNode, "Email changed successfully!").showAndWait().thenAccept(_ -> returnToUserEditor());
            else
                new StackPaneDialogue(rootNode, "Email changed successfully! Do you want to verify it?")
                        .showAndWait().thenAccept(result -> {
                            if (result) {
                                EmailVerifier.verifyEmail(userManager.getEmail());
                                new StackPaneAlert(rootNode, "Verification email sent!").showAndWait().thenAccept(_ -> returnToUserEditor());
                            } else
                                returnToUserEditor();
                        });
        }catch (SQLException e) {
            new StackPaneAlert(rootNode, "Error loading EditUser.fxml").show();
            throw new RuntimeException(e);
        } catch (Exception e) {
            new StackPaneAlert(rootNode, STR."Error updating user: \n\{e.getMessage()}").show();
        }
    }

    public void returnToUserEditor() {
        try{
            StageManager.setScene("/users/EditUser.fxml");
            ((EditUser) StageManager.getStageController()).setUser(userManager.getUsername());
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading EditUser.fxml").show();
            throw new RuntimeException(e);
        }
    }
}
