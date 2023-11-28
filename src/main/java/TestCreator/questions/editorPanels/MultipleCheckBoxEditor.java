package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleCheckBox;
import TestCreator.utilities.StageManager;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MultipleCheckBoxEditor extends QuestionEditor<MultipleCheckBox>{

    public FlowPane answersContainer;
    public Button acceptBtn;
    public Button cancelBtn;
    public Button addAnswerBtn;
    public TextArea questionTextArea;
    public TextField questionName;
    public ScrollPane answerScroll;
    public VBox rootPane;

    public void initialize() {
        StageManager.setTitle("Multiple Checkbox Editor");
        answerScroll.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.33));
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

            Button removeAnswerBtn = new Button("X");
            removeAnswerBtn.setOnAction(_ -> removeAnswer(removeAnswerBtn));

            answerBox.getChildren().addAll(isCorrectCheckBox, answerField, removeAnswerBtn);

            answersContainer.getChildren().add(answerBox);
        }
    }

    public void addAnswer() {
        if(answersContainer.getChildren().size() > MultipleCheckBox.MAX_CHOICES) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Too many answers");
            alert.setContentText("You can only have " + MultipleCheckBox.MAX_CHOICES + " answers");
            alert.showAndWait();
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
