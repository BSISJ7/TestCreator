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
    private final ObservableList<CheckBox> checkBoxList = FXCollections.observableArrayList();

    @Override
    public float getPointsScored() {
        for (CheckBox checkBox : checkBoxList) {
            checkBox.setDisable(true);
            System.out.println(checkBox.getText() + " " + checkBox.isSelected() + " " + question.isCorrect(checkBox.getText()));
            if (question.isCorrect(checkBox.getText()) && checkBox.isSelected()) {
                pointsScored++;
                checkBox.setStyle("-fx-text-fill: green");
            }
            else
                checkBox.setStyle("-fx-text-fill: red");
        }
        return pointsScored;
    }

    @Override
    public void setupQuestion(MultipleCheckBox question) {
        this.question = question;

        questionTextArea.setText(question.getQuestionText());

        for (MultipleCheckBox.CheckBoxAnswer choice : question.getChoicesCopy()) {
            CheckBox radioBtn = new CheckBox(choice.getAnswer());
            radioBtn.setStyle("-fx-font-size: 15; -fx-padding: 15");

            checkBoxList.add(radioBtn);
        }

        FXCollections.shuffle(checkBoxList);
        for (CheckBox checkBox : checkBoxList) {
            answerContainerVBox.getChildren().add(checkBox);
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    public void cleanUp() {
        checkBoxList.clear();
        answerContainerVBox.getChildren().clear();
        pointsScored = 0;
    }
}
