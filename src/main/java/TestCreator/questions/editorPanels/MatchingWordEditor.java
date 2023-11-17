package TestCreator.questions.editorPanels;

import TestCreator.questions.MatchingWord;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MatchingWordEditor extends QuestionEditor<MatchingWord> {


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

    private boolean updating;

    @FXML
    public void initialize() {
        StageManager.setTitle("Matching Word Editor");

        updating = false;

        checkForChanges(questionListView, answerListView);

        checkForChanges(answerListView, questionListView);

        ChangeListener<String> newPairListener = (_, _, _) -> {
            addPairBtn.setDisable(questionTextArea.getText().trim().equalsIgnoreCase("")
                    || answerTextArea.getText().trim().equalsIgnoreCase("")
                    || question.getKeyList().contains(questionTextArea.getText())
                    || question.getValueList().contains(answerTextArea.getText()));


            updateBtn.setDisable((answerListView.getItems().contains(answerTextArea.getText())
                    && questionListView.getItems().contains(questionTextArea.getText()))
                    || answerTextArea.getText().trim().equalsIgnoreCase("")
                    || questionTextArea.getText().trim().equalsIgnoreCase("")
                    || questionListView.getSelectionModel().isEmpty());
        };

        questionTextArea.textProperty().addListener(newPairListener);
        answerTextArea.textProperty().addListener(newPairListener);

        ContextMenu pairContextMenu = new ContextMenu();
        MenuItem removePair = new MenuItem("Remove Pair");
        removePair.setOnAction(_ -> removePair());
        pairContextMenu.getItems().addAll(removePair);

        questionName.textProperty().addListener((_, _, _) ->
                question.setName(questionName.getText()));
    }

    private void checkForChanges(ListView<String> questionListView, ListView<String> answerListView) {
        questionListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
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
        }
    }

    @Override
    public void setupQuestion(MatchingWord question) {
        this.question = (MatchingWord) question.getCopy();
        questionListView.setItems(this.question.getKeyList());
        answerListView.setItems(this.question.getValueList());
        questionName.setText(question.getName());
    }

    @Override
    public void setupQuestion() {
        setupQuestion(new MatchingWord(STR."Question \{ TestManager.getInstance().getNumOfQuestions()}"));
    }

    @FXML
    public void updatePair() {
        updating = true;

        question.setQuestionAt(questionListView.getSelectionModel().getSelectedIndex(),
                questionTextArea.getText());
        question.setAnswerAt(answerListView.getSelectionModel().getSelectedIndex(),
                answerTextArea.getText());

        updateBtn.setDisable(true);
        addPairBtn.setDisable(true);
        updating = false;
    }
}
