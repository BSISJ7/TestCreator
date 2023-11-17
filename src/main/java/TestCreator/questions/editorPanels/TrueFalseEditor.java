package TestCreator.questions.editorPanels;

import TestCreator.questions.TrueFalse;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TrueFalseEditor extends QuestionEditor<TrueFalse> {

    @FXML
    public TextField questionName;
    @FXML
    public TextArea trueFalseTextArea;
    @FXML
    public RadioButton trueBtn;
    @FXML
    public RadioButton falseBtn;
    @FXML
    public Button acceptBtn;
    @FXML
    public Button cancelBtn;

    @FXML
    public void initialize() {
        StageManager.setTitle("True False Editor");
        ToggleGroup trueFalseToggle = new ToggleGroup();
        trueBtn.setToggleGroup(trueFalseToggle);
        falseBtn.setToggleGroup(trueFalseToggle);

        questionName.textProperty().addListener((_, _, _) ->
                question.setName(questionName.getText()));

        trueFalseTextArea.textProperty().addListener((_, _, _) ->
                question.setTrueFalseQuestion(trueFalseTextArea.getText()));
    }

    @Override
    public void setupQuestion(TrueFalse question) {
        this.question = (TrueFalse) question.getCopy();
        trueFalseTextArea.setText(question.getTrueFalseQuestion());
        questionName.setText(question.getName());

        if (this.question.trueSelected())
            trueBtn.setSelected(true);
        else
            falseBtn.setSelected(true);
    }

    @Override
    public void setupQuestion() {
        setupQuestion(new TrueFalse(STR."Question \{ TestManager.getInstance().getNumOfQuestions()}"));
    }
}
