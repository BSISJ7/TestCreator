package TestCreator.questions.editorPanels;

import TestCreator.questions.MatchingWord;
import TestCreator.utilities.StageManager;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

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
    public Button acceptBtn;
    public Button cancelBtn;

    @FXML
    public void initialize() {
        StageManager.setTitle("Matching Word Editor");

        questionListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (!questionListView.getSelectionModel().isEmpty()) {
                answerListView.getSelectionModel().select(questionListView.getSelectionModel().getSelectedIndex());
                questionTextArea.setText(questionListView.getItems().get(questionListView.getSelectionModel().getSelectedIndex()));
                questionTextArea.setText(questionListView.getItems().get(questionListView.getSelectionModel().getSelectedIndex()));
                answerTextArea.setText(answerListView.getItems().get(answerListView.getSelectionModel().getSelectedIndex()));
            }
        });
        answerListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (!answerListView.getSelectionModel().isEmpty()) {
                questionListView.getSelectionModel().select(answerListView.getSelectionModel().getSelectedIndex());
                questionTextArea.setText(questionListView.getItems().get(questionListView.getSelectionModel().getSelectedIndex()));
                questionTextArea.setText(questionListView.getItems().get(questionListView.getSelectionModel().getSelectedIndex()));
                answerTextArea.setText(answerListView.getItems().get(answerListView.getSelectionModel().getSelectedIndex()));
            }
        });
        removePairBtn.disableProperty().bind(questionListView.getSelectionModel().selectedItemProperty().isNull()
                .or(answerListView.getSelectionModel().selectedItemProperty().isNull()));

        ChangeListener<String> newPairListener = (_, _, _) -> {
            addPairBtn.setDisable(
                    questionTextArea.getText().trim().isEmpty()
                            || answerTextArea.getText().trim().isEmpty()
                            || questionListView.getItems().contains(questionTextArea.getText())
                            || answerListView.getItems().contains(answerTextArea.getText())
            );

            updateBtn.setDisable(
                    containsExceptAt(questionListView.getItems(), questionTextArea.getText(),
                            questionListView.getSelectionModel().getSelectedIndex())
                            || containsExceptAt(answerListView.getItems(), answerTextArea.getText(),
                            answerListView.getSelectionModel().getSelectedIndex())
                            || answerTextArea.getText().trim().isEmpty()
                            || questionTextArea.getText().trim().isEmpty()
                            || questionListView.getSelectionModel().isEmpty()
                            || answerListView.getSelectionModel().isEmpty()
            );
        };

        questionTextArea.textProperty().addListener(newPairListener);
        answerTextArea.textProperty().addListener(newPairListener);

        ContextMenu pairContextMenu = new ContextMenu();
        MenuItem removePair = new MenuItem("Remove Pair");
        removePair.setOnAction(_ -> removePair());
        pairContextMenu.getItems().addAll(removePair);
    }

    @FXML
    public void addPair() {
        if(questionListView.getItems().size() >= MatchingWord.MAX_PAIRS || answerListView.getItems().size() >= MatchingWord.MAX_PAIRS) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Too many pairs");
            alert.setContentText("You can only have 10 pairs of matching questions and answers");
            alert.showAndWait();
            return;
        }
        if (!questionListView.getItems().contains(questionTextArea.getText()) &&
                !answerListView.getItems().contains(answerTextArea.getText())) {
            questionListView.getItems().add(questionTextArea.getText());
            answerListView.getItems().add(answerTextArea.getText());
            questionTextArea.setText("");
            answerTextArea.setText("");
            updateBtn.setDisable(true);
            addPairBtn.setDisable(true);
            questionListView.getSelectionModel().selectLast();
            answerListView.getSelectionModel().select(questionListView.getSelectionModel().getSelectedIndex());
        }
    }

    @FXML
    public void removePair() {
        if (!questionListView.getSelectionModel().isEmpty()) {
            questionListView.getItems().remove(questionListView.getSelectionModel().getSelectedIndex());
            answerListView.getItems().remove(answerListView.getSelectionModel().getSelectedIndex());
            if (!questionListView.getItems().isEmpty()) {
                questionListView.getSelectionModel().clearSelection();
                questionListView.getSelectionModel().selectLast();
                answerListView.getSelectionModel().select(questionListView.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @Override
    public void setupQuestion(MatchingWord question) {
        this.question = question;
        questionListView.setItems(question.getKeyListCopy());
        answerListView.setItems(question.getValueListCopy());
        questionName.setText(question.getName());
    }

    @Override
    public void updateQuestion() {
        question.setName(questionName.getText());
        question.setKeys(questionListView.getItems());
        question.setValues(answerListView.getItems());
    }

    @FXML
    public void updatePair() {
        questionListView.getItems().set(questionListView.getSelectionModel().getSelectedIndex(), questionTextArea.getText());
        answerListView.getItems().set(answerListView.getSelectionModel().getSelectedIndex(), answerTextArea.getText());
        addPairBtn.setDisable(true);
    }

    private boolean containsExceptAt(List<String> list, String value, int selectedIndex) {
        if (selectedIndex < 0)
            return false;
        for (int i = 0; i < list.size(); i++) {
            if (i != selectedIndex && list.get(i).equals(value)) {
                return true;
            }
        }
        return false;
    }
}
