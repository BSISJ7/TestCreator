package TestCreator.questions.testPanels;

import TestCreator.questions.MultipleChoice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class MultipleChoiceTestPanel implements TestPanel<MultipleChoice> {

    @FXML
    private TextArea questionTextArea;
    @FXML
    private GridPane gridPane;
    @FXML
    private BorderPane rootNode;

    private final ObservableList<RadioButton> radioButtonList = FXCollections.observableArrayList();;

    private MultipleChoice question;

    private ToggleGroup choiceToggle;

    private String selectedAnswer;
    private int selectedIndex;

    @FXML
    public void initialize() {
        choiceToggle = new ToggleGroup();
    }

    @Override
    public void setupQuestion(MultipleChoice question) {
        this.question = question;

        questionTextArea.setText(question.getQuestionText());
        for (String choice : question.getChoicesCopy()) {
            RadioButton radioBtn = new RadioButton(choice);
            radioBtn.setToggleGroup(choiceToggle);
            radioBtn.setStyle("-fx-font-size: 15; -fx-padding: 15");

            radioButtonList.add(radioBtn);
            radioBtn.selectedProperty().addListener((_, _, _) -> {
                if (radioBtn.isSelected()) {
                    selectedIndex = radioButtonList.indexOf(radioBtn);
                    selectedAnswer = radioBtn.getText();
                }
            });
        }

        int column = 0;
        int row = 0;
        FXCollections.shuffle(radioButtonList);
        for (RadioButton radioButton : radioButtonList) {
            gridPane.add(radioButton, column, row);
            row = column == 1 ? row + 1 : row;
            column = column == 1 ? 0 : 1;
        }
        radioButtonList.get(0).setSelected(true);
        selectedAnswer = radioButtonList.get(0).getText();
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public float getPointsScored() {
        radioButtonList.forEach(radioButton -> {
            if (radioButton.getText().equals(question.getAnswer())) {
                radioButton.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 15; -fx-padding: 15");
                radioButton.setDisable(true);
            }
        });
        if (question.getAnswer().equals(selectedAnswer)) {
            return 1;
        } else {
            radioButtonList.get(selectedIndex).setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 15; -fx-padding: 15");
            return 0;
        }
    }

    public void cleanUp() {
        radioButtonList.forEach(radioButton -> {
            radioButton.setStyle("-fx-font-size: 15; -fx-padding: 15");
            radioButton.setDisable(false);
        });
    }
}