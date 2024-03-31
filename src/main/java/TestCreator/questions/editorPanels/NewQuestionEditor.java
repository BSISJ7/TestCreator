package TestCreator.questions.editorPanels;

import TestCreator.questions.MultipleChoice;
import TestCreator.questions.Question;
import TestCreator.utilities.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class NewQuestionEditor {
    @FXML
    public ListView<String> questionListView;
    @FXML
    public Slider questionCountSlider;
    @FXML
    public Label questionCountLabel;
    @FXML
    public Tab triviaTab;
    @FXML
    public Button quickMultiAcceptBtn;
    @FXML
    public TextField quickMultiQuestionName;
    @FXML
    public ChoiceBox<String> quickMultiTypesChoiceBox;
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private ComboBox<Integer> questionCountComboBox;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button quickAcceptBtn;
    @FXML
    private TextField quickQuestionName;
    @FXML
    private ChoiceBox<String> quickTypesChoiceBox;
    @FXML
    private VBox containerVBox;
    @FXML
    private Button acceptBtn;
    @FXML
    private ChoiceBox<String> typesChoiceBox;
    @FXML
    private TextField questionName;

    private Question question = new MultipleChoice();

    private Question.QuestionTypes questionType = Question.QuestionTypes.MULTIPLE_CHOICE;

    public static final List<MultipleChoice> triviaQuestionsList = new ArrayList<>();

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
        quickQuestionName.textProperty().addListener((_, _, _) ->
                quickAcceptBtn.disableProperty().bind(quickQuestionName.textProperty().isEmpty()
                        .or(quickTypesChoiceBox.getSelectionModel().selectedItemProperty().isNull())));


        quickMultiQuestionName.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));
        quickMultiTypesChoiceBox.prefWidthProperty().bind(containerVBox.widthProperty().multiply(0.8));
        quickMultiQuestionName.setText(question.getName());

        List<String> quickMultiEditorNames = classFinder.getFilesInDirectory("TestCreator/questions/quickEditors/multiEditors");
        quickMultiEditorNames.stream().map(name -> name.substring(0, name.indexOf("QuickMultiEditor")))
                .map(name -> name.charAt(0) + name.substring(1).replaceAll("(?=[A-Z])", " "))
                .forEach(quickMultiTypesChoiceBox.getItems()::add);
        quickMultiTypesChoiceBox.getSelectionModel().selectFirst();
        quickMultiTypesChoiceBox.getSelectionModel().selectedItemProperty().addListener((_, _, newQuestionType) ->{
            questionType = Question.QuestionTypes.valueOf(newQuestionType.replace(" ", "_").toUpperCase());
            question = Question.createQuestion(questionType);
            quickMultiQuestionName.setText(question.getName());
        });
        quickMultiQuestionName.textProperty().addListener((_, _, _) ->
                quickMultiAcceptBtn.disableProperty().bind(quickMultiQuestionName.textProperty().isEmpty()
                        .or(quickMultiTypesChoiceBox.getSelectionModel().selectedItemProperty().isNull())));


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
            } else if(tabPane.getSelectionModel().getSelectedIndex() == 1){
                quickTypesChoiceBox.getSelectionModel().selectFirst();
                if (!quickTypesChoiceBox.getSelectionModel().isEmpty())
                    questionType = Question.QuestionTypes.valueOf(quickTypesChoiceBox.getSelectionModel()
                            .getSelectedItem().replace(" ", "_").toUpperCase());
                question = Question.createQuestion(questionType);
                quickQuestionName.setText(question.getName());
                quickAcceptBtn.disableProperty().bind(quickQuestionName.textProperty().isEmpty()
                        .or(quickTypesChoiceBox.getSelectionModel().selectedItemProperty().isNull()));
            } else if (tabPane.getSelectionModel().getSelectedIndex() == 2){
                quickMultiTypesChoiceBox.getSelectionModel().selectFirst();
                if(!quickMultiTypesChoiceBox.getSelectionModel().isEmpty())
                    questionType = Question.QuestionTypes.valueOf(quickTypesChoiceBox.getSelectionModel()
                            .getSelectedItem().replace(" ","_").toUpperCase());
                quickMultiQuestionName.setText("Flash Card");
                quickMultiAcceptBtn.disableProperty().bind(quickMultiQuestionName.textProperty().isEmpty()
                        .or(quickMultiTypesChoiceBox.getSelectionModel().selectedItemProperty().isNull()));
            }
        });



        //TODO: Change the TDBQuestion interface
        tabPane.getTabs().remove(triviaTab);  //remove the triviaTab from the tabPane

        questionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        categoryComboBox.getItems().addAll(TDBQuestion.Categories.getCategoryNames());
        categoryComboBox.getSelectionModel().select(0);

        questionCountLabel.textProperty().bind(questionCountSlider.valueProperty().asString("%.0f"));
    }

    public void returnToMainMenu() {
        try {
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
            throw new RuntimeException(e);
        }
    }

    public void addNewQuestion() {
        try {
            StageManager.setScene(STR."/questions/editorPanels/\{questionType.getQuestionType()}Editor.fxml");
            ((QuestionEditor) StageManager.getStageController()).setupQuestion(question, false);
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert(STR."Error loading \{questionType.getQuestionType()}Editor.fxml");
            throw new RuntimeException(e);
        }
    }

    public void addNewQuickQuestion() {
        try {
            StageManager.setScene(STR."/questions/quickEditors/\{questionType.getQuestionType()}QuickEditor.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert(STR."Error loading \{questionType.getQuestionType()}QuickEditor.fxml");
            throw new RuntimeException(e);
        }
    }

    public void addNewQuickMultiQuestion() {
        try {
            StageManager.setScene(STR."/questions/quickEditors/multiEditors/\{questionType.getQuestionType()}QuickMultiEditor.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert(STR."Error loading \{questionType.getQuestionType()}QuickEditor.fxml");
            throw new RuntimeException(e);
        }
    }

    public void acceptOpenTrivia() {
        try {
            TestManager.getInstance().addQuestions(triviaQuestionsList);
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading MainMenu.fxml");
            throw new RuntimeException(e);
        }
    }

    public void getQuestions() {
        OpenTriviaDownloader downloader = new OpenTriviaDownloader();
        try {
            List<TDBQuestion> questions = downloader.downloadQuestions(categoryComboBox.getValue(), (int) questionCountSlider.getValue());
            questionListView.getItems().clear();
            questions.forEach(question -> {
                triviaQuestionsList.add(new MultipleChoice(question));
                questionListView.getItems().add(question.getQuestion());
            });
        } catch (IOException | InterruptedException e) {
            StageManager.showAlert("Error downloading questions");
            throw new RuntimeException(e);
        }
    }

    public void addAllQuestions() {
        triviaQuestionsList.forEach(question -> {
            TestManager.getInstance().addQuestion(question);
            questionListView.getItems().remove(question.getQuestionText());
        });
    }

    public void addQuestion() {
        triviaQuestionsList.stream().filter(question -> questionListView.getSelectionModel().getSelectedItems().contains(question.getQuestionText()))
                .forEach(question -> {
                    TestManager.getInstance().addQuestion(question);
                    questionListView.getItems().remove(question.getQuestionText());
                });
    }

}
