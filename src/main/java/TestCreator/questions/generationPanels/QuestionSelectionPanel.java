package TestCreator.questions.generationPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.Question;
import TestCreator.utilities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionSelectionPanel {
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private StackPane rootNode;
    @FXML
    private ListView<Question> questionListView;
    @FXML
    private Button addQuestionButton;
    @FXML
    private ComboBox<Integer> questionCountComboBox;

    public static final List<MultipleChoice> questionsList = new ArrayList<>();

    @FXML
    private void initialize() {
        categoryComboBox.getItems().addAll(TDBQuestion.Categories.getCategoryNames());
        categoryComboBox.getSelectionModel().select(0);
        questionCountComboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        questionCountComboBox.getSelectionModel().select(0);
    }

    private void retrieveQuestions() {
        OpenTriviaDownloader downloader = new OpenTriviaDownloader();
        try {
            List<TDBQuestion> questions = downloader.downloadQuestions(categoryComboBox.getValue(), questionCountComboBox.getValue());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void acceptQuestion(ActionEvent actionEvent) {
        try {
            TestManager.getInstance().addQuestions(questionsList);
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void cancel(ActionEvent actionEvent) {
        try {
            StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading NewQuestionEditor.fxml").show();
            throw new RuntimeException(e);
        }
    }
}
