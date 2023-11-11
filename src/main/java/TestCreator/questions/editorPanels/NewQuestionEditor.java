package TestCreator.questions.editorPanels;

import TestCreator.Main;
import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewQuestionEditor {

    @FXML
    ChoiceBox<String> typesChoiceBox;
    @FXML
    TextField name;
    @FXML
    DialogPane newQuestionDialog;

    private Node okButton;

    private String questionType;

    @FXML
    public void initialize() {
        StageManager.setTitle("New Question");

        questionType = Question.getQuestionTypesList().get(0);
        typesChoiceBox.setItems(FXCollections.observableArrayList(Question.getQuestionTypesList()));

        typesChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) ->
                questionType = Question.getQuestionTypesList().get(newValue.intValue()));
        typesChoiceBox.getSelectionModel().select(0);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().equals(""))
                okButton.setDisable(true);
            else
                okButton.setDisable(false);
        });
    }

    public void setOkButton(Node okButton) {
        this.okButton = okButton;
    }

    public void updateQuestion(Question question) {
        question.setName(name.getText());
        question.setQuestionType(questionType);
    }


    public Question getNewQuestion(Test test) {
        return Question.getQuestionInstance(name.getText(), questionType, test);
    }
}
