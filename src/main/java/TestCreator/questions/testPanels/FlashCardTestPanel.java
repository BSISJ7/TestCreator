package TestCreator.questions.testPanels;

import TestCreator.questions.FlashCard;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FlashCardTestPanel implements TestPanel<FlashCard> {

    @FXML
    public TextField questionAnswerField;
    public VBox rootNode;
    public CheckBox correctCheckBox;

    private FlashCard question;

    private boolean questionDisplayed = true;
    private boolean isTestGraded = false;

    @Override
    public void setupQuestion(FlashCard question) {
        this.question = question;
        questionAnswerField.setText(question.getFlashQuestion());
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public float getPointsScored() {
        isTestGraded = true;
        boolean correct = correctCheckBox.isSelected();
        correctCheckBox.setDisable(true);

        if(correct)
            questionAnswerField.setStyle("-fx-text-fill: green");
        else
            questionAnswerField.setStyle("-fx-text-fill: red");

        return correct ? 1 : 0;
    }

    public void questionAnswerClicked() {
        questionAnswerField.setText(questionDisplayed ? question.getFlashAnswer() : question.getFlashQuestion());
        questionDisplayed = !questionDisplayed;
    }
}