package TestCreator.questions.testPanels;

import TestCreator.questions.MultipleCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class MultipleCheckBoxTestPanel implements TestPanel<MultipleCheckBox>{

    public TextArea questionTextArea;
    public FlowPane answerContainerVBox;
    public BorderPane rootNode;
    MultipleCheckBox question;
    private float pointsScored;
    private final ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();

    @Override
    public float getPointsScored() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setDisable(true);
            if (question.isCorrectAt(checkBoxes.indexOf(checkBox))) {
                checkBox.setStyle("-fx-text-fill: green");
            } else {
                checkBox.setStyle("-fx-text-fill: red");
            }
        }

        for (int i = 0; i < question.getNumChoices(); i++) {
            if (question.isCorrectAt(i))
                pointsScored += 1;
        }
        return pointsScored / question.getNumChoices();
    }

    @Override
    public void setupQuestion(MultipleCheckBox question) {
        this.question = question;

        questionTextArea.setText(question.getQuestionText());

        for (MultipleCheckBox.CheckBoxAnswer choice : question.getChoicesCopy()) {
            CheckBox radioBtn = new CheckBox(choice.getAnswer());
            radioBtn.setStyle("-fx-font-size: 15; -fx-padding: 15");

            checkBoxes.add(radioBtn);
        }

        FXCollections.shuffle(checkBoxes);
        for (CheckBox checkBox : checkBoxes) {
            answerContainerVBox.getChildren().add(checkBox);
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }
}
