package TestCreator.questions.editorPanels;

import TestCreator.questions.FlashCard;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlashCardEditor extends QuestionEditor<FlashCard> {

    @FXML
    public TextField questionName;
    @FXML
    public TextArea flashQuestionTextArea;
    @FXML
    public TextArea flashAnswerTextArea;
    @FXML
    public Button acceptBtn;
    @FXML
    public Button cancelBtn;

    @FXML
    public void initialize() {
        StageManager.setTitle("Flash Card Editor");
    }

    @Override
    public void setupQuestion(FlashCard question) {
        this.question = question;
        flashQuestionTextArea.setText(question.getFlashQuestion());
        flashAnswerTextArea.setText(question.getFlashAnswer());
        questionName.setText(question.getName());
    }

    @Override
    public void updateQuestion() {
        question.setName(questionName.getText());
        question.setFlashQuestion(flashQuestionTextArea.getText());
        question.setFlashAnswer(flashAnswerTextArea.getText());
    }
}