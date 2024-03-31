package TestCreator.login;

import TestCreator.users.UserManager;
import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static TestCreator.utilities.PasswordChecker.*;

public class CreateNewPassword {

    @FXML
    private Label maxLengthReqLabel;
    @FXML
    private Button changePassButton;
    @FXML
    private PasswordField passwordField;
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
    private PasswordField passwordConfirmField;

    private UserManager userManager;

    public void initialize() {
        StageManager.setTitle("Create New Password");
        passwordField.textProperty().addListener((_, _, _) -> validateInputs());
        passwordConfirmField.textProperty().addListener((_, _, _) -> validateInputs());

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
        boolean isPasswordValid = PasswordChecker.isPasswordCorrect(passwordField.getText());
        boolean isPasswordConfirmValid = passwordField.getText().equals(passwordConfirmField.getText());

        passwordField.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");
        passwordConfirmField.setStyle(isPasswordConfirmValid ? "-fx-border-color: green" : "-fx-border-color: red");

        matchReqLabel.setStyle(isPasswordConfirmValid ? "-fx-text-fill: black" : "-fx-text-fill: red");
        minLengthReqLabel.setStyle(PasswordChecker.checkMinLength(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        maxLengthReqLabel.setStyle(PasswordChecker.checkMaxLength(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        upperCaseReqLabel.setStyle(PasswordChecker.checkUpperCase(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        lowerCaseReqLabel.setStyle(PasswordChecker.checkLowerCase(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        numberReqLabel.setStyle(PasswordChecker.checkDigit(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");
        specialCharReqLabel.setStyle(PasswordChecker.checkSpecialChar(passwordField.getText())
                ? "-fx-text-fill: black" : "-fx-text-fill: red");

        changePassButton.setDisable(!(isPasswordValid) || !(isPasswordConfirmValid));
    }

    public void goToLoginPage() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
            ((WebLogin) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading WebLogin.fxml");
            throw new RuntimeException(e);
        }
    }

    public void changePassword() {
        userManager.setCurrentUserPassword(passwordField.getText());
        StageManager.showAlert("Your password has been changed.");
        goToLoginPage();
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
