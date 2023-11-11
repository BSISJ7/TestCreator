package TestCreator.questions.editorPanels;

import TestCreator.Main;
import TestCreator.questions.Question;
import TestCreator.questions.TrueFalse;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class TrueFalseEditor implements EditorPanel {


    @FXML
    public TextField questionName;
    @FXML
    public TextArea trueFalseTextArea;
    @FXML
    public RadioButton trueBtn;
    @FXML
    public RadioButton falseBtn;

    private TrueFalse question;

    @FXML
    public void initialize() {
        StageManager.setTitle("True False Editor");
        ToggleGroup trueFalseToggle = new ToggleGroup();
        trueBtn.setToggleGroup(trueFalseToggle);
        falseBtn.setToggleGroup(trueFalseToggle);

        questionName.textProperty().addListener((observable, oldValue, newValue) ->
                question.setName(questionName.getText()));

        trueFalseTextArea.textProperty().addListener((observable, oldValue, newValue) ->
                question.setTrueFalseQuestion(trueFalseTextArea.getText()));
    }

    @Override
    public void setupQuestion(Question question) {
        this.question = (TrueFalse) question.getCopy();
        trueFalseTextArea.setText(((TrueFalse) question).getTrueFalseQuestion());
        questionName.setText(question.getName());

        if (this.question.trueSelected())
            trueBtn.setSelected(true);
        else
            falseBtn.setSelected(true);
    }

    @Override
    public TrueFalse getQuestion() {
        return question;
    }
}
