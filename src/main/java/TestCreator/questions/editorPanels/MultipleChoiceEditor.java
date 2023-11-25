package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MultipleChoiceEditor extends QuestionEditor<MultipleChoice> {

    @FXML
    public Button removeChoiceBtn;
    @FXML
    public Button setCorrectBtn;
    public Button acceptBtn;
    public Button cancelBtn;
    @FXML
    TextArea questionTextArea;
    @FXML
    TextField questionName;
    @FXML
    TextArea choiceTextArea;
    @FXML
    ListView<String> choicesListView;
    private boolean choiceMouseEntered;
    private ObservableList<String> choiceObsList;

    private int answerIndex = -1;
    

    public void initialize() {
        StageManager.setTitle("Multiple Choice Editor");

        ContextMenu choiceContext = new ContextMenu();
        MenuItem removeChoiceItem = new MenuItem("Remove Choice");
        removeChoiceItem.setOnAction(_ -> removeChoice());
        MenuItem correctChoiceItem = new MenuItem("Set Correct Choice");
        correctChoiceItem.setOnAction(_ -> setCorrectAnswer());
        choiceContext.getItems().addAll(correctChoiceItem, removeChoiceItem);

        Callback<ListView<String>, ListCell<String>> choiceCellFactory = new Callback<>() {
            @Override
            public TextFieldListCell<String> call(ListView<String> param) {
                TextFieldListCell<String> shortDescCell = new TextFieldListCell<>() {
                    public void updateItem(String choice, boolean empty) {
                        setStyle(null);
                        super.updateItem(choice, empty);
                        if (choice != null && !empty) {
                            if (choicesListView.getItems().indexOf(choice) == answerIndex
                                    && choicesListView.getItems().indexOf(choice) == choicesListView.getSelectionModel().getSelectedIndex()) {
                                setStyle("-fx-background-color: #9dbf68");
                            } else if (choicesListView.getItems().indexOf(choice) == answerIndex
                                    && choicesListView.getItems().indexOf(choice) != choicesListView.getSelectionModel().getSelectedIndex()) {
                                setStyle("-fx-background-color: #c4ee81");
                            } else
                                setStyle("");
                        }
                    }
                };

                shortDescCell.emptyProperty().addListener((_, _, isNowEmpty) -> {
                    if (isNowEmpty) {
                        shortDescCell.setContextMenu(null);
                    } else {
                        shortDescCell.setContextMenu(choiceContext);
                    }
                });

                shortDescCell.setConverter(new StringConverter<>() {
                    @Override
                    public String toString(String choice) {
                        if (choice != null) {
                            if (choice.length() >= 20)
                                return choice.substring(0, 20);
                            else
                                return choice;
                        } else
                            return null;
                    }

                    @Override
                    public String fromString(String name) {
                        return name;
                    }
                });
                return shortDescCell;
            }
        };

        choicesListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (choicesListView.getSelectionModel().getSelectedIndex() >= 0 && choiceMouseEntered)
                choiceTextArea.setText(choiceObsList.get(choicesListView.getSelectionModel().getSelectedIndex()));
            choicesListView.refresh();
        });

        choicesListView.setCellFactory(choiceCellFactory);

        if (!choicesListView.getItems().isEmpty())
            choicesListView.getSelectionModel().select(0);

        choicesListView.setOnMouseEntered(_ -> choiceMouseEntered = true);
        choicesListView.setOnMouseExited(_ -> choiceMouseEntered = false);
    }

    public void setupQuestion(MultipleChoice question) {
        this.question = question;
        questionName.setText(this.question.getName());
        questionTextArea.setText(this.question.getQuestionText());
        answerIndex = question.getAnswerIndex();

        choiceObsList = FXCollections.observableList(question.getChoicesCopy());
        choicesListView.setItems(choiceObsList);
        if (!choiceObsList.isEmpty()) {
            choicesListView.getSelectionModel().select(0);
            choiceTextArea.setText(choiceObsList.get(0));
        }

        if (!choicesListView.getSelectionModel().isEmpty())
            choicesListView.getSelectionModel().select(0);

        choiceTextArea.disableProperty().bind(choicesListView.getSelectionModel().selectedItemProperty().isNull());
        setCorrectBtn.disableProperty().bind(choicesListView.getSelectionModel().selectedItemProperty().isNull());
        removeChoiceBtn.disableProperty().bind(choicesListView.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void updateQuestion() {
        question.setAnswerIndex(choicesListView.getSelectionModel().getSelectedIndex());
        question.setQuestionText(questionTextArea.getText());
        question.setAnswerIndex(answerIndex);
        question.setName(questionName.getText());
        question.setChoices(choiceObsList);
    }

    @FXML
    public void setCorrectAnswer() {
        answerIndex = choicesListView.getSelectionModel().getSelectedIndex();
        choicesListView.refresh();
    }

    @FXML
    public void removeChoice() {
        int selectedIndex = choicesListView.getSelectionModel().getSelectedIndex();
        if (answerIndex > selectedIndex) answerIndex--;
        else if (answerIndex == selectedIndex)
            answerIndex = -1;

        choiceObsList.remove(selectedIndex);
        selectedIndex = (selectedIndex != choicesListView.getItems().size()) ? selectedIndex : selectedIndex - 1;
        if (!choicesListView.getSelectionModel().isEmpty()) {
            choicesListView.getSelectionModel().select(selectedIndex);
            choiceTextArea.setText(choicesListView.getSelectionModel().getSelectedItem());
        } else {
            choiceTextArea.setText("");
        }
    }

    @FXML
    public void newChoice() {
        if(choicesListView.getItems().size() >= MultipleChoice.MAX_CHOICES) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Too many choices");
            alert.setContentText("You can only have 10 choices per question");
            alert.showAndWait();
            return;
        }
        choicesListView.setDisable(false);
        choiceObsList.add("");
        choicesListView.getSelectionModel().select(choicesListView.getItems().size() - 1);
        choiceTextArea.setText("");
        choiceTextArea.requestFocus();
    }

    @FXML
    public void choiceKeyTyped() {
        if (choicesListView.getSelectionModel().getSelectedIndex() >= 0) {
            int index = choicesListView.getSelectionModel().getSelectedIndex();
            choiceObsList.set(choicesListView.getSelectionModel().getSelectedIndex(), choiceTextArea.getText());
            choicesListView.getSelectionModel().select(index);
        }
    }

}