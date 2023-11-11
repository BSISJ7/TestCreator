package TestCreator.questions.editorPanels;

import TestCreator.questions.MatchingWord;
import TestCreator.questions.Question;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class MatchingWordEditor implements EditorPanel {


    @FXML
    public ListView<String> questionListView;
    @FXML
    public ListView<String> answerListView;
    @FXML
    public TextArea questionTextArea;
    @FXML
    public TextArea answerTextArea;
    @FXML
    public TextField questionName;
    @FXML
    public Button addPairBtn;
    @FXML
    public Button removePairBtn;
    @FXML
    public Button updateBtn;

    private MatchingWord question;

    private boolean updating;
    private boolean isNewQuestion = false;
    private boolean isNewAnswer = false;

    @FXML
    public void initialize() {

        updating = false;

        checkForChanges(questionListView, answerListView);

        checkForChanges(answerListView, questionListView);

        ChangeListener newPairListener = (observable, oldValue, newValue) -> {
            if (!questionTextArea.getText().trim().equalsIgnoreCase("")
                    && !answerTextArea.getText().trim().equalsIgnoreCase("")
                    && !question.getKeyList().contains(questionTextArea.getText())
                    && !question.getValueList().contains(answerTextArea.getText())) {
                addPairBtn.setDisable(false);
            } else
                addPairBtn.setDisable(true);

            if ((!answerListView.getItems().contains(answerTextArea.getText())
                    || !questionListView.getItems().contains(questionTextArea.getText()))
                    && !answerTextArea.getText().trim().equalsIgnoreCase("")
                    && !questionTextArea.getText().trim().equalsIgnoreCase("")
                    && !questionListView.getSelectionModel().isEmpty()) {

                updateBtn.setDisable(false);
            } else
                updateBtn.setDisable(true);
        };

        questionTextArea.textProperty().addListener(newPairListener);
        answerTextArea.textProperty().addListener(newPairListener);

        ContextMenu pairContextMenu = new ContextMenu();
        MenuItem removePair = new MenuItem("Remove Pair");
        removePair.setOnAction(event -> removePair());
        pairContextMenu.getItems().addAll(removePair);

        questionName.textProperty().addListener((observable, oldValue, newValue) ->
                question.setName(questionName.getText()));
    }

    private void checkForChanges(ListView<String> questionListView, ListView<String> answerListView) {
        questionListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!updating) {
                if (questionListView.getSelectionModel().getSelectedIndex() >= 0) {
                    removePairBtn.setDisable(false);
                    answerListView.getSelectionModel().select(questionListView.getSelectionModel().getSelectedIndex());
                    questionTextArea.setText(question.getMatchingQuestion(questionListView.getSelectionModel().getSelectedIndex()));
                    answerTextArea.setText(question.getMatchingAnswer(answerListView.getSelectionModel().getSelectedIndex()));
                } else {
                    removePairBtn.setDisable(true);
                    updateBtn.setDisable(true);
                }
            }
        });
    }

    @FXML
    public void addPair() {
        if (!question.getKeyList().contains(questionTextArea.getText()) &&
                !question.getValueList().contains(answerTextArea.getText())) {
            question.addKey(questionTextArea.getText());
            question.addValue(answerTextArea.getText());
            questionTextArea.setText("");
            answerTextArea.setText("");
            updateBtn.setDisable(true);
            isNewAnswer = false;
            isNewQuestion = false;
            questionListView.getSelectionModel().select(questionListView.getItems().size() - 1);
        }
    }

    @FXML
    public void removePair() {
        if (!questionListView.getSelectionModel().isEmpty()) {
            int index = questionListView.getSelectionModel().getSelectedIndex();
            questionListView.getItems().remove(index);
            answerListView.getItems().remove(index);
            question.removeKeyAt(index);
            question.removeValueAt(index);
            isNewAnswer = false;
            isNewQuestion = false;
        }
    }

    @Override
    public void setupQuestion(Question question) {
        this.question = (MatchingWord) question.getCopy();
        questionListView.setItems(this.question.getKeyList());
        answerListView.setItems(this.question.getValueList());
        questionName.setText(question.getName());
    }

    @Override
    public MatchingWord getQuestion() {
        question.getKeyList().forEach(item -> System.out.println(item));
        return question;
    }

    @FXML
    public void updatePair(ActionEvent actionEvent) {
        updating = true;

        question.setQuestionAt(questionListView.getSelectionModel().getSelectedIndex(),
                questionTextArea.getText());
        question.setAnswerAt(answerListView.getSelectionModel().getSelectedIndex(),
                answerTextArea.getText());

        updateBtn.setDisable(true);
        addPairBtn.setDisable(true);
        updating = false;
        isNewAnswer = false;
        isNewQuestion = false;
    }
}
