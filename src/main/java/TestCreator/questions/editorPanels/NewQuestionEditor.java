package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.Question;
import TestCreator.utilities.ClassFinder;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class NewQuestionEditor {
    @FXML
    private TabPane tabPane;
    @FXML
    private Button fastAcceptBtn;
    @FXML
    private TextField quickQuestionName;
    @FXML
    private ChoiceBox<String> quickTypesChoiceBox;
    @FXML
    private VBox containerVBox;
    @FXML
    private Button acceptBtn;
    @FXML
    private StackPane rootNode;
    @FXML
    private ChoiceBox<String> typesChoiceBox;
    @FXML
    private TextField questionName;

    private Question question = new MultipleChoice();

    private Question.QuestionTypes questionType = Question.QuestionTypes.MULTIPLE_CHOICE;

    @FXML
    public void initialize() {
        StageManager.setTitle("New Question");

        questionName.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));
        typesChoiceBox.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));

        questionName.setText(question.getName());

        ObservableList<String> observableList = FXCollections.observableArrayList();
        Stream.of(Question.QuestionTypes.values()).forEach(type -> observableList.add(type.getDisplayName()));
        typesChoiceBox.setItems(observableList);
        typesChoiceBox.getSelectionModel().select(0);
        typesChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newQuestionType) ->{
            questionType = Question.QuestionTypes.valueOf(newQuestionType.replace(" ", "_").toUpperCase());
            question = Question.createQuestion(questionType);
            questionName.setText(question.getName());
        });
        questionName.textProperty().addListener((_, _, _) ->
                acceptBtn.disableProperty().bind(questionName.textProperty().isEmpty()
                        .or(typesChoiceBox.getSelectionModel().selectedItemProperty().isNull())));




        quickQuestionName.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));
        quickTypesChoiceBox.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));
        quickQuestionName.setText(question.getName());

        ClassFinder classFinder = new ClassFinder();
        List<String> quickEditorNames = classFinder.getFilesInDirectory("TestCreator/questions/quickEditors");
        quickEditorNames.stream().map(name -> name.substring(0, name.indexOf("QuickEditor")))
                .map(name -> name.charAt(0) + name.substring(1).replaceAll("(?=[A-Z])", " "))
                .forEach(quickTypesChoiceBox.getItems()::add);
        quickTypesChoiceBox.getSelectionModel().selectFirst();
        quickTypesChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newQuestionType) ->{
            questionType = Question.QuestionTypes.valueOf(newQuestionType.replace(" ", "_").toUpperCase());
            question = Question.createQuestion(questionType);
            quickQuestionName.setText(question.getName());
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
                typesChoiceBox.getSelectionModel().selectFirst();
                if (!typesChoiceBox.getSelectionModel().isEmpty())
                    questionType = Question.QuestionTypes.valueOf(typesChoiceBox.getSelectionModel()
                            .getSelectedItem().replace(" ", "_").toUpperCase());
                question = Question.createQuestion(questionType);
                questionName.setText(question.getName());
                acceptBtn.disableProperty().bind(questionName.textProperty().isEmpty()
                        .or(typesChoiceBox.getSelectionModel().selectedItemProperty().isNull()));
            } else {
                quickTypesChoiceBox.getSelectionModel().selectFirst();
                if (!quickTypesChoiceBox.getSelectionModel().isEmpty())
                    questionType = Question.QuestionTypes.valueOf(quickTypesChoiceBox.getSelectionModel()
                            .getSelectedItem().replace(" ", "_").toUpperCase());
                question = Question.createQuestion(questionType);
                quickQuestionName.setText(question.getName());
                fastAcceptBtn.disableProperty().bind(quickQuestionName.textProperty().isEmpty()
                        .or(quickTypesChoiceBox.getSelectionModel().selectedItemProperty().isNull()));
            }
        });
    }

    public void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void addNewQuestion() {
        try {
            StageManager.setScene(STR."/questions/editorPanels/\{questionType.getQuestionType()}Editor.fxml");
            ((QuestionEditor) StageManager.getStageController()).setupQuestion(question, false, rootNode);
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, STR."Error loading \{questionType.getQuestionType()}Editor.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void addNewQuickQuestion() {
        try {
            StageManager.setScene(STR."/questions/quickEditors/\{questionType.getQuestionType()}QuickEditor.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, STR."Error loading \{questionType.getQuestionType()}QuickEditor.fxml").show();
            throw new RuntimeException(e);
        }
    }
}
