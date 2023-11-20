package TestCreator.questions.editorPanels;

import TestCreator.questions.TrueFalse;
import TestCreator.utilities.StageManager;
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
    }

    @Override
    public void setupQuestion(TrueFalse question) {
        this.question = question;
        trueFalseTextArea.setText(question.getTrueFalseQuestion());
        questionName.setText(question.getName());

        if (this.question.trueSelected())
            trueBtn.setSelected(true);
        else
            falseBtn.setSelected(true);
    }

    @Override
    public void updateQuestion() {
        question.setName(questionName.getText());
        question.setTrueFalse(trueBtn.isSelected());
        question.setTrueFalseQuestion(trueFalseTextArea.getText());
    }
}
