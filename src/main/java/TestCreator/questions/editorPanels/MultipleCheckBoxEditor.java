package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleCheckBox;
import TestCreator.utilities.StageManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MultipleCheckBoxEditor extends QuestionEditor<MultipleCheckBox>{

    @FXML
    private StackPane rootNode;
    @FXML
    private FlowPane answersContainer;
    @FXML
    private TextArea questionTextArea;
    @FXML
    private TextField questionName;
    @FXML
    private ScrollPane answerScroll;
    @FXML
    private VBox containerVBox;

    public void initialize() {
        StageManager.setTitle("Multiple Checkbox Editor");
        answerScroll.prefHeightProperty().bind(containerVBox.heightProperty().multiply(0.33));
    }

    public void setupQuestion(MultipleCheckBox question) {
        this.question = question;
        questionName.setText(this.question.getName());
        questionTextArea.setText(this.question.getQuestionText());
        ObservableList<MultipleCheckBox.CheckBoxAnswer> answersList = question.getChoicesCopy();
        for(MultipleCheckBox.CheckBoxAnswer answer : answersList) {
            HBox answerBox = new HBox();

            CheckBox isCorrectCheckBox = new CheckBox();
            isCorrectCheckBox.setSelected(answer.isCorrect());
            isCorrectCheckBox.setTooltip(new Tooltip("Set if the answer is correct."));

            TextField answerField = new TextField(answer.getAnswer());
            //have answerField fill to the size of the window instead of the scroll pane
            answerField.prefWidthProperty().bind(rootNode.widthProperty());

            Button removeAnswerBtn = new Button("X");
            removeAnswerBtn.setOnAction(_ -> removeAnswer(removeAnswerBtn));

            answerBox.getChildren().addAll(isCorrectCheckBox, answerField, removeAnswerBtn);

            answersContainer.getChildren().add(answerBox);
        }
    }

    public void addAnswer() {
        if(answersContainer.getChildren().size() > MultipleCheckBox.MAX_CHOICES) {
            StageManager.showAlert(STR."You can only have \{MultipleCheckBox.MAX_CHOICES} answers");
            return;
        }

        CheckBox isCorrectCheckBox = new CheckBox();
        isCorrectCheckBox.setSelected(false);
        isCorrectCheckBox.setTooltip(new Tooltip("Set if the answer is correct."));

        TextField answerField = new TextField("");
        answerField.prefWidthProperty().bind(answerScroll.widthProperty());
        answerField.setPromptText("Answer");

        Button removeAnswerBtn = new Button("X");
        removeAnswerBtn.setOnAction(_ -> removeAnswer(removeAnswerBtn));
        removeAnswerBtn.setTooltip(new Tooltip("Remove this answer"));

        HBox answerBox = new HBox();
        answerBox.getChildren().addAll(isCorrectCheckBox, answerField, removeAnswerBtn);
        answerBox.prefWidthProperty().bind(answerScroll.widthProperty());
        answerBox.setSpacing(5);

        answersContainer.getChildren().add(answerBox);
    }


    public void removeAnswer(Button removeAnswerBtn) {
        for (int i = 0; i < answersContainer.getChildren().size(); i++) {
            if (answersContainer.getChildren().get(i).equals(removeAnswerBtn.getParent())) {
                answersContainer.getChildren().remove(i);
                break;
            }
        }
    }

    @Override
    public void updateQuestion() {
        ObservableList<MultipleCheckBox.CheckBoxAnswer> answersList = question.getChoicesCopy();
        for (int i = 0; i < answersContainer.getChildren().size(); i++) {
            HBox answerBox = (HBox) answersContainer.getChildren().get(i);
            CheckBox isCorrectCheckBox = (CheckBox) answerBox.getChildren().get(0);
            TextField answerField = (TextField) answerBox.getChildren().get(1);
            answersList.add(new MultipleCheckBox.CheckBoxAnswer(answerField.getText(), isCorrectCheckBox.isSelected()));
        }
        question.setQuestionText(questionTextArea.getText());
        question.setName(questionName.getText());
        question.setAnswers(answersList);
    }
}
