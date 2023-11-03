package com.BSISJ7.TestCreator.questions.testPanels;

import com.BSISJ7.TestCreator.questions.Question;
import com.BSISJ7.TestCreator.questions.TrueFalse;
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
    private ToggleGroup toggleGroup;
    @FXML
    private BorderPane mainWindow;

    @FXML
    public void initialize() {
        toggleGroup = new ToggleGroup();
        trueBtn.setToggleGroup(toggleGroup);
        falseBtn.setToggleGroup(toggleGroup);

        trueBtn.setSelected(true);

        trueBtn.setStyle("-fx-font-size: 20; -fx-padding: 15");
        falseBtn.setStyle("-fx-font-size: 20; -fx-padding: 15");
    }

    @Override
    public void setupQuestion(TrueFalse question) {
        this.question = question;
        questionText.setText(this.question.getTrueFalseQuestion());
        trueBtn.setSelected(this.question.trueSelected());
    }

    @Override
    public String getFXMLName() {
        return location.toString();
    }

    @Override
    public Node getQuestionScene() {
        return mainWindow;
    }

    @Override
    public void disableAnswerChanges() {
        falseBtn.setDisable(true);
        trueBtn.setDisable(true);
        System.out.println("Disabled");
    }

    @Override
    public float getPointsScored() {

        if(question.trueSelected()){
            trueBtn.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 20; -fx-padding: 15");
        } else if(!question.trueSelected()){
            falseBtn.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 20; -fx-padding: 15");
        }



        if(trueBtn.isSelected() && !question.trueSelected()){
            trueBtn.setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 20; -fx-padding: 15");
            return 0;
        }else if (falseBtn.isSelected() && question.trueSelected()) {
            falseBtn.setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 20; -fx-padding: 15");
            return 0;
        }
        return 1;
    }
}

