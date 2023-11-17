package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.Question;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.stream.Stream;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public class NewQuestionEditor {

    public Button acceptBtn;
    public Button cancelBtn;
    @FXML
    ChoiceBox<String> typesChoiceBox;
    @FXML
    TextField questionName;
    @FXML
    VBox newQuestionVBox;

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
        typesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((_, _, _) ->{
            questionType = Question.QuestionTypes.valueOf(typesChoiceBox.getSelectionModel().getSelectedItem()
                    .replace(" ", "_").toUpperCase());
            question = Question.createQuestion(questionType);
            questionName.setText(question.getName());
        });
        questionName.textProperty().addListener((_, _, _) -> acceptBtn.disableProperty().bind(questionName.textProperty().isEmpty()));
    }

    public void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void addNewQuestion() {
        try {
            StageManager.setScene(STR."/questions/editorPanels/\{questionType.getQuestionType()}Editor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
        ((QuestionEditor) StageManager.getStageController()).setupQuestion(question);
    }
}
