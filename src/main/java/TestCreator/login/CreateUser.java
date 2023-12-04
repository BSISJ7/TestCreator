package TestCreator.login;

import TestCreator.utilities.PasswordChecker;
import TestCreator.utilities.StackPaneAlert;
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

import static TestCreator.utilities.PasswordChecker.*;


public class CreateUser {
    public StackPane rootNode;
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

        if(UserManager.emailDoesNotExist(emailTextField.getText())){
            verifyEmail(emailTextField.getText());
        }


        if (!createUserButton.isDisabled()) {
            try {
                UserManager.addUser(usernameField.getText(), UserAuthenticator.generateStrongPasswordHash(passwordField.getText()));
                new StackPaneAlert(rootNode, "New user created successfully.").show();
                goToLoginPage();
            } catch (Exception e) {
                new StackPaneAlert(rootNode, "Error creating new user: " + e.getMessage()).show();
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void goToLoginPage() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading WebLogin.fxml").show();
            throw new RuntimeException(e);
        }
    }


    public static void verifyEmail(String email) {
        System.setProperty("aws.accessKeyId", System.getenv("AWS_SES_ACCESS_KEY"));
        System.setProperty("aws.secretAccessKey", System.getenv("AWS_SES_SECRET_ACCESS_KEY"));

        SesClient client = SesClient.builder()
                .region(Region.US_EAST_2)
                .build();

        try {
            VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
                    .emailAddress(email)
                    .build();

            client.verifyEmailIdentity(request);
            System.out.println("A verification email has been sent to " + email);

        } catch (
                SesException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        client.close();
    }
}
