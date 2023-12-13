package TestCreator.testCreation;

import TestCreator.Test;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TestEditor {

    @FXML
    private Button acceptBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private VBox testInfoVBox;
    @FXML
    private StackPane rootNode;
    @FXML
    TextField testName;
    @FXML
    TextArea testDescription;
    @FXML
    private Test test;

    public void initialize() {
        testName.maxWidthProperty().bind(testInfoVBox.widthProperty().multiply(0.8));
        testDescription.maxWidthProperty().bind(testInfoVBox.widthProperty().multiply(0.8));
    }

    public void setTest(Test test) {
        this.test = test;
        testName.setText(test.getName());
        testDescription.setText(test.getDescription());
    }

    public Test getTest() {
        test.setName(testName.getText());
        test.setDescription(testDescription.getText());
        return test;
    }

    public void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void acceptTest() {
        test.setName(testName.getText());
        test.setDescription(testDescription.getText());
        TestManager.getInstance().addTest(test);
        returnToMainMenu();
        IOManager.getInstance().saveTests();
    }
}
