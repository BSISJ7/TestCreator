package TestCreator.questions.quickEditors;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.editorPanels.QuestionEditor;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//
public class MultipleChoiceQuickEditor {

    @FXML
    public TextArea questionTextArea;
    @FXML
    public TextField questionName;
    @FXML
    public Button acceptButton;
    @FXML
    private StackPane rootNode;

    private MultipleChoice question;

    @FXML
    public void initialize() {
        question = new MultipleChoice();
        questionTextArea.setWrapText(true);
        questionName.setText(question.getName());

        questionTextArea.textProperty().addListener((_, _, _) ->
                acceptButton.setDisable(questionTextArea.getText().trim().lines().count() < 3));
    }

    public void acceptQuestion() {
        questionName.setText(question.getName());
        List<String> sentencesList = new ArrayList<>(questionTextArea.getText().trim().lines().toList());
        question.setQuestionText(sentencesList.getFirst());
        sentencesList.removeFirst();
        question.setChoices(FXCollections.observableList(sentencesList));
        question.setAnswerIndex(0);

        try {
            StageManager.setRootPane("/questions/editorPanels/MultipleChoiceEditor.fxml");
            ((QuestionEditor) StageManager.getStageController()).setupQuestion(question, false, rootNode);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MultipleChoiceEditor.fxml");
            throw new RuntimeException(e);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        returnToEditor();
    }

    private void returnToEditor() {
        try {
            StageManager.setRootPane("/questions/editorPanels/NewQuestionEditor.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
            throw new RuntimeException(e);
        }
    }
}
