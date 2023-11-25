package TestCreator.login;

import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StageManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;
import static TestCreator.utilities.PasswordChecker.*;

public class CreateNewPassword {

    public Label maxLengthReqLabel;
    public Button changePassButton;
    public PasswordField passwordField;
    public Label specialCharReqLabel;
    public Label numberReqLabel;
    public Label lowerCaseReqLabel;
    public Label upperCaseReqLabel;
    public VBox requirementsVBox;
    public Label minLengthReqLabel;
    public Label matchReqLabel;
    public PasswordField passwordConfirmField;

    public void initialize() {
        StageManager.setTitle("Create New Password");
        passwordField.textProperty().addListener((_, _, _) -> validateInputs());
        passwordConfirmField.textProperty().addListener((_, _, _) -> validateInputs());

        minLengthReqLabel.setText("Password must be at least " + MIN_LENGTH + " characters long.");
        maxLengthReqLabel.setText("Password must be at most " + MAX_LENGTH + " characters long.");

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
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void changePassword() {
        UserManager.setCurrentUserPassword(passwordField.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Password Changed");
        alert.setHeaderText("Password Changed");
        alert.setContentText("Your password has been changed.");
        alert.showAndWait();
        goToLoginPage();
    }
}
