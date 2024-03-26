package TestCreator.login;

import TestCreator.users.EditUser;
import TestCreator.users.UserAuthenticator;
import TestCreator.users.UserManager;
import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

import static TestCreator.utilities.PasswordChecker.*;

public class ChangePassword {

    @FXML
    public PasswordField oldPasswordField;
    @FXML
    private Label maxLengthReqLabel;
    @FXML
    private Button changePassButton;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Label specialCharReqLabel;
    @FXML
    private Label numberReqLabel;
    @FXML
    private Label lowerCaseReqLabel;
    @FXML
    private Label upperCaseReqLabel;
    @FXML
    private VBox requirementsVBox;
    @FXML
    private Label minLengthReqLabel;
    @FXML
    private Label matchReqLabel;
    @FXML
    private PasswordField confirmPassField;
    @FXML
    private StackPane rootNode;
    private UserManager userManager;

    public void initialize() {
        StageManager.setTitle("Create New Password");
        newPasswordField.textProperty().addListener((_, _, _) -> validateInputs());
        confirmPassField.textProperty().addListener((_, _, _) -> validateInputs());

        minLengthReqLabel.setText(STR."Password must be at least \{MIN_LENGTH} characters long.");
        maxLengthReqLabel.setText(STR."Password must be at most \{MAX_LENGTH} characters long.");

        upperCaseReqLabel = new Label("Password must contain at least one uppercase letter.");
        if(REQUIRE_UPPER_CASE) requirementsVBox.getChildren().add(upperCaseReqLabel);

        lowerCaseReqLabel = new Label("Password must contain at least one lowercase letter.");
        if (REQUIRE_LOWER_CASE) requirementsVBox.getChildren().add(lowerCaseReqLabel);

        numberReqLabel = new Label("Password must contain at least one digit.");
        if(REQUIRE_DIGIT) requirementsVBox.getChildren().add(numberReqLabel);

        specialCharReqLabel = new Label("Password must contain at least one special character.");
        if(REQUIRE_SPECIAL_CHAR) requirementsVBox.getChildren().add(specialCharReqLabel);
    }

    private void validateInputs() {
        boolean isPasswordValid = PasswordChecker.isPasswordCorrect(newPasswordField.getText());
        boolean isPasswordConfirmValid = newPasswordField.getText().equals(confirmPassField.getText());

        newPasswordField.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");
        confirmPassField.setStyle(isPasswordConfirmValid ? "-fx-border-color: green" : "-fx-border-color: red");

        matchReqLabel.setStyle(isPasswordConfirmValid ? "-fx-text-fill: black" : "-fx-text-fill: red");
        minLengthReqLabel.setStyle(PasswordChecker.checkMinLength(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        maxLengthReqLabel.setStyle(PasswordChecker.checkMaxLength(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        upperCaseReqLabel.setStyle(PasswordChecker.checkUpperCase(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        lowerCaseReqLabel.setStyle(PasswordChecker.checkLowerCase(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        numberReqLabel.setStyle(PasswordChecker.checkDigit(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        specialCharReqLabel.setStyle(PasswordChecker.checkSpecialChar(newPasswordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");

        changePassButton.setDisable(!(isPasswordValid) || !(isPasswordConfirmValid));
    }

    public void openUserEditor() {
        try{
            StageManager.setScene("/users/EditUser.fxml");
            ((EditUser) StageManager.getStageController()).setUser(userManager.getUsername());
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading EditUser.fxml");
            throw new RuntimeException(e);
        }
    }

    public void changePassword() {
        if(UserAuthenticator.authenticate(userManager.getHashedPassword(), oldPasswordField.getText())) {
            try {
            userManager.setCurrentUserPassword(newPasswordField.getText());
                userManager.updateUser();
                StageManager.showDialog("Your password has been changed.").thenAccept(_ -> openUserEditor());
            } catch (SQLException e) {
                StageManager.showAlert(STR."Error updating user password.\n\{e.getMessage()}");
            }
        }else
            StageManager.showAlert("The previous password is incorrect.");
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
