package TestCreator.questions.testPanels;

import TestCreator.questions.TrueFalse;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

import java.net.URL;

public class TrueFalseTestPanel implements TestPanel<TrueFalse> {

    @FXML
    public TextArea questionText;
    @FXML
    public RadioButton trueBtn;
    @FXML
    public RadioButton falseBtn;
    TrueFalse question;
    @FXML
    private URL location;
    @FXML
    private BorderPane rootNode;

    @FXML
    public void initialize() {
        ToggleGroup toggleGroup = new ToggleGroup();
        trueBtn.setToggleGroup(toggleGroup);
        falseBtn.setToggleGroup(toggleGroup);

        trueBtn.setSelected(true);

        trueBtn.setStyle("-fx-font-size: 20; -fx-padding: 15");
        falseBtn.setStyle("-fx-font-size: 20; -fx-padding: 15");
    }

    @Override
    public void setupQuestion(TrueFalse question){
        this.question = question;
        questionText.setText(question.getTrueFalseQuestion());
        trueBtn.setSelected(question.trueSelected());
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public float getPointsScored() {
        trueBtn.setDisable(true);
        falseBtn.setDisable(true);

        if (question.trueSelected())
            trueBtn.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 20; -fx-padding: 15");
        else if (!question.trueSelected())
            falseBtn.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 20; -fx-padding: 15");

        if (trueBtn.isSelected() && !question.trueSelected()) {
            trueBtn.setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 20; -fx-padding: 15");
            return 0;
        } else if (falseBtn.isSelected() && question.trueSelected()) {
            falseBtn.setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 20; -fx-padding: 15");
            return 0;
        }
        return 1;
    }
}

