package TestCreator.login;

import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;
import static TestCreator.utilities.PasswordChecker.*;


public class CreateUser {
    @FXML
    public CheckBox passVisibleCheckBox;
    public TextField passwordFieldVisible;
    public Label minLengthReqLabel;
    public Label maxLengthReqLabel;
    public Label specialCharReqLabel;
    public Label numberReqLabel;
    public Label lowerCaseReqLabel;
    public Label upperCaseReqLabel;
    @FXML
    public VBox requirementsVBox;
    public TextField emailTextField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createUserButton;

    public void initialize() {
        usernameField.textProperty().addListener((_, _, _) -> validateInputs());
        passwordField.textProperty().addListener((_, _, _) -> validateInputs());
        emailTextField.textProperty().addListener((_, _, _) -> validateInputs());

        passwordFieldVisible.visibleProperty().bind(passwordField.visibleProperty().not());
        passwordFieldVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordFieldVisible.managedProperty().bind(passwordField.visibleProperty().not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordField.visibleProperty().bind(passVisibleCheckBox.selectedProperty().not());

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
        boolean isUsernameValid = usernameField.getText().length() <= 50 && !usernameField.getText().isEmpty();
        usernameField.setStyle(isUsernameValid ? "-fx-border-color: green" : "-fx-border-color: red");

        boolean isPasswordValid = PasswordChecker.isPasswordCorrect(passwordField.getText());
        passwordField.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");
        passwordFieldVisible.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");

        boolean isEmailValid = emailTextField.getText().trim().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
                || emailTextField.getText().isEmpty();

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

        createUserButton.setDisable(!(isUsernameValid && isPasswordValid && isEmailValid));
    }

    @FXML
    private void addUser() {
        if (!createUserButton.isDisabled()) {
            try {
                UserManager.addUser(usernameField.getText(), UserAuthenticator.generateStrongPasswordHash(passwordField.getText()));
                goToLoginPage();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("New User Created");
                alert.setContentText("New user created successfully.");
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("New User Error");
                alert.setContentText("Error creating new user: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void goToLoginPage() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
