package TestCreator.questions.quickEditors;

import TestCreator.questions.FlashCard;
import TestCreator.questions.editorPanels.QuestionEditor;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashCardQuickEditor {

    @FXML
    public TextArea questionTextArea;
    @FXML
    public TextField questionName;
    @FXML
    public Button acceptButton;

    private FlashCard question;

    @FXML
    public void initialize() {
        question = new FlashCard();
        questionTextArea.setWrapText(true);
        questionName.setText(question.getName());

        questionTextArea.textProperty().addListener((_, _, _) ->
            acceptButton.setDisable(questionTextArea.getText().trim().lines().count() < 3));
    }

    public void acceptQuestion() {
        questionName.setText(question.getName());
        List<String> sentencesList = new ArrayList<>(questionTextArea.getText().trim().lines().toList());
        question.setFlashQuestion(sentencesList.getFirst());
        sentencesList.removeFirst();
        question.setFlashAnswer(sentencesList.getFirst());

        try {
            StageManager.setScene("/questions/editorPanels/FlashCardEditor.fxml");
            ((QuestionEditor) StageManager.getStageController()).setupQuestion(question, false);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading FlashCardEditor.fxml");
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        returnToEditor();
    }

    @FXML
    private void returnToEditor() {
        try {
            StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
            throw new RuntimeException(e);
        }
    }
}
