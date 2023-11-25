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

    private ObservableList<RadioButton> radioButtons;

    private MultipleChoice question;

    private ToggleGroup choiceToggle;

    private String selectedAnswer;
    private int selectedIndex;

    @FXML
    public void initialize() {
        choiceToggle = new ToggleGroup();
        radioButtons = FXCollections.observableArrayList();
    }

    @Override
    public void setupQuestion(MultipleChoice question) {
        this.question = question;

        questionTextArea.setText(question.getQuestionText());
        for (String choice : question.getChoicesCopy()) {
            RadioButton radioBtn = new RadioButton(choice);
            radioBtn.setToggleGroup(choiceToggle);
            radioBtn.setStyle("-fx-font-size: 15; -fx-padding: 15");

            radioButtons.add(radioBtn);
            radioBtn.selectedProperty().addListener((_, _, _) -> {
                if (radioBtn.isSelected()) {
                    selectedIndex = radioButtons.indexOf(radioBtn);
                    selectedAnswer = radioBtn.getText();
                }
            });
        }

        int column = 0;
        int row = 0;
        FXCollections.shuffle(radioButtons);
        for (RadioButton radioButton : radioButtons) {
            gridPane.add(radioButton, column, row);
            row = column == 1 ? row + 1 : row;
            column = column == 1 ? 0 : 1;
        }
        radioButtons.get(0).setSelected(true);
        selectedAnswer = radioButtons.get(0).getText();
    }

    @Override
    public String getFXMLName() {
        return "MultChoiceTestPanel";
    }

    @Override
    public void disableAnswerChanges() {
        radioButtons.forEach(radioBtn -> radioBtn.setDisable(true));
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public float getPointsScored() {
        radioButtons.forEach(radioButton -> {
            if (radioButton.getText().equals(question.getAnswer()))
                radioButton.setStyle("-fx-text-fill: rgb(0,150,0);-fx-font-size: 15; -fx-padding: 15");
        });
        if (question.getAnswer().equals(selectedAnswer)) {
            return 1;
        } else {
            radioButtons.get(selectedIndex).setStyle("-fx-text-fill: rgba(220,34,0,0.64);-fx-font-size: 15; -fx-padding: 15");
            return 0.0f;
        }
    }
}