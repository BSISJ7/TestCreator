package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.Question;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.stream.Stream;

public class NewQuestionEditor {

    @FXML
    private Button acceptBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private StackPane rootNode;
    @FXML
    private ChoiceBox<String> typesChoiceBox;
    @FXML
    private TextField questionName;

    private Question question = new MultipleChoice();

    private Question.QuestionTypes questionType = Question.QuestionTypes.MULTIPLE_CHOICE;

    @FXML
    public void initialize() {
        StageManager.setTitle("New Question");

        questionName.setText(question.getName());

        ObservableList<String> observableList = FXCollections.observableArrayList();
        Stream.of(Question.QuestionTypes.values()).forEach(type -> observableList.add(type.getDisplayName()));
        typesChoiceBox.setItems(observableList);
        typesChoiceBox.getSelectionModel().select(0);
        typesChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newQuestionType) ->{
            questionType = Question.QuestionTypes.valueOf(newQuestionType.replace(" ", "_").toUpperCase());
            question = Question.createQuestion(questionType);
            questionName.setText(question.getName());
        });
        questionName.textProperty().addListener((_, _, _) -> acceptBtn.disableProperty().bind(questionName.textProperty().isEmpty()));
    }

    public void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void addNewQuestion() {
        try {
            System.out.println(questionType.getQuestionType());
            StageManager.setScene(STR."/questions/editorPanels/\{questionType.getQuestionType()}Editor.fxml");
        } catch (IOException e) {
            new StackPaneAlert(rootNode, STR."Error loading \{questionType.getQuestionType()}Editor.fxml").show();
            throw new RuntimeException(e);
        }
        ((QuestionEditor) StageManager.getStageController()).setupQuestion(question, false, rootNode);
    }
}
