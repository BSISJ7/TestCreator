package TestCreator.users;

import TestCreator.login.WebLogin;
import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

import java.io.IOException;
import java.sql.SQLException;

import static TestCreator.utilities.PasswordChecker.*;


public class CreateUser {
    @FXML
    public StackPane rootNode;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    private CheckBox passVisibleCheckBox;
    @FXML
    private TextField passwordFieldVisible;
    @FXML
    private Label minLengthReqLabel;
    @FXML
    private Label maxLengthReqLabel;
    private Label specialCharReqLabel;
    private Label numberReqLabel;
    private Label lowerCaseReqLabel;
    private Label upperCaseReqLabel;
    private Label passwordsMatchLabel;
    @FXML
    private VBox requirementsVBox;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button createUserButton;
    private UserManager userManager;

    public void initialize() {
        usernameField.textProperty().addListener((_, _, _) -> validateInputs());
        passwordField.textProperty().addListener((_, _, _) -> validateInputs());
        emailTextField.textProperty().addListener((_, _, _) -> validateInputs());

        passwordFieldVisible.visibleProperty().bind(passwordField.visibleProperty().not());
        passwordFieldVisible.textProperty().bindBidirectional(passwordField.textProperty());
        passwordFieldVisible.managedProperty().bind(passwordField.visibleProperty().not());
        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordField.visibleProperty().bind(passVisibleCheckBox.selectedProperty().not());

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

        passwordsMatchLabel = new Label("Passwords must match.");
        confirmPasswordField.textProperty().addListener((_, _, _) -> {
            if (!confirmPasswordField.getText().equals(passwordField.getText())) {
                passwordsMatchLabel.setStyle("-fx-text-fill: red");
                passwordsMatchLabel.setText("Passwords must match.");
            } else {
                passwordsMatchLabel.setStyle("-fx-text-fill: black");
                passwordsMatchLabel.setText("Passwords match.");
            }
        });
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    private void validateInputs() {
        boolean isUsernameValid = usernameField.getText().length() <= 50 && !usernameField.getText().isEmpty();
        usernameField.setStyle(isUsernameValid ? "-fx-border-color: green" : "-fx-border-color: red");

        boolean isPasswordValid = PasswordChecker.isPasswordCorrect(passwordField.getText());
        passwordField.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");
        passwordFieldVisible.setStyle(isPasswordValid ? "-fx-border-color: green" : "-fx-border-color: red");

        boolean isEmailValid = emailTextField.getText().trim().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
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
        if (userManager.userExists(usernameField.getText())) {
            StageManager.showDialog("Username already exists.").thenAccept(_ -> goToLoginPage());
        }
        else if (!createUserButton.isDisabled()) {
            try {
                if(emailTextField.getText().isEmpty()) {
                    this.userManager.addUser(usernameField.getText(), UserAuthenticator
                            .generateStrongPasswordHash(passwordField.getText()));
                    StageManager.showDialog("New user created successfully.")
                            .thenAccept(_ -> goToLoginPage());
                }
                else if(userManager.emailDoesNotExist(emailTextField.getText())) {
                    verifyEmail(emailTextField.getText());
                    this.userManager.addUser(usernameField.getText(),
                            UserAuthenticator.generateStrongPasswordHash(passwordField.getText()),
                            emailTextField.getText());
                    StageManager.showDialog("New user created successfully.  A verification email has been" +
                            "sent to the address you have given.").thenAccept(_ -> goToLoginPage());
                }else
                    StageManager.showAlert("This email is already associated with an account.");
            } catch (SesException e) {
                StageManager.showAlert(STR."Email verification error: \{e.getMessage()}");
                throw new RuntimeException(e);
            } catch (SQLException e) {
                StageManager.showAlert(STR."Database error, could not create new user: \{e.getMessage()}");
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void goToLoginPage() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
            ((WebLogin) StageManager.getStageController()).setUserManager(userManager);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading WebLogin.fxml");
            throw new RuntimeException(e);
        }
    }


    public void verifyEmail(String email) throws SesException {
        SesClient client = SesClient.builder()
                .region(Region.US_EAST_2)
                .build();

        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder().emailAddress(email).build();
            client.verifyEmailIdentity(request);
        } catch (SesException e) {
            throw new RuntimeException(e);
        }
        client.close();
    }
}
