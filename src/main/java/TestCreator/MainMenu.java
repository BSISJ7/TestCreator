package TestCreator;

import TestCreator.login.UserManager;
import TestCreator.questions.Question;
import TestCreator.questions.editorPanels.QuestionEditor;
import TestCreator.testCreation.TestEditor;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Optional;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public class MainMenu {
    @FXML
    public Button newTestBtn;
    @FXML
    public VBox menuBarVBox;
    @FXML
    public Button editTestBtn;
    @FXML
    public Button deleteTestBtn;
    @FXML
    public Button editQuestionBtn;
    public Button deleteQuestionBtn;
    @FXML
    Button newQuestionBtn;
    @FXML
    Button beginTestBtn;
    @FXML
    private ListView<Test> testListView;
    @FXML
    private ListView<Question> questionListView;
    private ContextMenu questionContextMenu;

    public void initialize() {
        testListView.setItems(TestManager.getInstance().getObservableTestList());
        if (TestManager.getInstance().getSelectedTest() != null) {
            testListView.getSelectionModel().select(TestManager.getInstance().getSelectedTest());
            questionListView.setItems(FXCollections.observableArrayList(selectedTest().getQuestionList()));

            if (TestManager.getInstance().getSelectedQuestion() != null)
                questionListView.getSelectionModel().select(TestManager.getInstance().getSelectedQuestion());
            else{
                questionListView.getSelectionModel().select(0);
                TestManager.getInstance().setSelectedQuestion(questionListView.getSelectionModel().getSelectedItem());
            }
            beginTestBtn.setDisable(TestManager.getInstance().getSelectedTest().notReadyToRun());
        }

        newQuestionBtn.disableProperty().bind(testListView.getSelectionModel().selectedItemProperty().isNull());
        deleteTestBtn.disableProperty().bind(testListView.getSelectionModel().selectedItemProperty().isNull());
        editTestBtn.disableProperty().bind(testListView.getSelectionModel().selectedItemProperty().isNull());
        editQuestionBtn.disableProperty().bind(questionListView.getSelectionModel().selectedItemProperty().isNull());
        deleteQuestionBtn.disableProperty().bind(questionListView.getSelectionModel().selectedItemProperty().isNull());

        StageManager.setTitle("Main Menu - %s".formatted(UserManager.getCurrentUsername()));
        ContextMenu testContextMenu = new ContextMenu();

        MenuItem loginItem = new MenuItem("login");
        loginItem.setOnAction(_ -> openLoginPanel());

        MenuItem runTestItem = new MenuItem("Run");
        runTestItem.setOnAction(_ -> beginTest());

        MenuItem deleteTestItem = new MenuItem("Delete");
        deleteTestItem.setOnAction(_ -> deleteTest());

        MenuItem editTestItem = new MenuItem("Edit");
        editTestItem.setOnAction(_ -> editTest());

        MenuItem createQuestionItem = new MenuItem("New Question");
        createQuestionItem.setOnAction(_ -> createNewQuestion());
        testContextMenu.getItems().addAll(createQuestionItem, editTestItem, deleteTestItem);

        questionContextMenu = new ContextMenu();

        MenuItem deleteQuestionItem = new MenuItem("Delete Question");
        deleteQuestionItem.setOnAction(_ -> deleteQuestion());

        MenuItem editQuestionItem = new MenuItem("Edit Question");
        editQuestionItem.setOnAction(_ -> editQuestion());
        questionContextMenu.getItems().addAll(editQuestionItem, deleteQuestionItem);

        Callback<ListView<Test>, ListCell<Test>> testCellFactory = new Callback<>() {
            @Override
            public TextFieldListCell<Test> call(ListView<Test> param) {
                TextFieldListCell<Test> shortDescCell = new TextFieldListCell<Test>() {
                    public void updateItem(Test test, boolean empty) {
                        super.updateItem(test, empty);

                        if (test != null && !empty) {
                            if (test.notReadyToRun())
                                setTextFill(Color.TOMATO);
                            else
                                setTextFill(Color.BLACK);
                        }
                    }
                };
                shortDescCell.setConverter(new StringConverter<Test>() {
                    @Override
                    public String toString(Test test) {
                        if (test != null)
                            return test.getName();
                        else
                            return null;
                    }

                    @Override
                    public Test fromString(String name) {
                        if (testListView.getSelectionModel().getSelectedIndex() >= 0) {
                            return selectedTest();
                        } else
                            return null;
                    }
                });

                shortDescCell.emptyProperty().addListener((_, _, isNowEmpty) -> {
                    if (isNowEmpty) {
                        shortDescCell.setContextMenu(null);
                    } else {
                        shortDescCell.setContextMenu(testContextMenu);
                    }
                });

                return shortDescCell;
            }
        };

        testListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (!testListView.getSelectionModel().isEmpty()) {
                TestManager.getInstance().setSelectedTest(testListView.getSelectionModel().getSelectedItem());
                questionListView.setItems(FXCollections.observableArrayList(selectedTest().getQuestionList()));
                if (questionListView.getItems().contains(TestManager.getInstance().getSelectedQuestion())) {
                    questionListView.getSelectionModel().select(TestManager.getInstance().getSelectedQuestion());
                }
                beginTestBtn.setDisable(TestManager.getInstance().getSelectedTest().notReadyToRun());
            }else beginTestBtn.setDisable(true);
        });

        testListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                editTest();
            }
        });

        testListView.setCellFactory(testCellFactory);
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Callback<ListView<Question>, ListCell<Question>> questionCellFactory = new Callback<>() {
            @Override
            public TextFieldListCell<Question> call(ListView<Question> param) {
                TextFieldListCell<Question> shortDescCell = new TextFieldListCell<Question>() {
                    public void updateItem(Question question, boolean empty) {
                        super.updateItem(question, empty);

                        if (question != null && !empty) {
                            if (!question.readyToRun())
                                setTextFill(Color.TOMATO);
                            else
                                setTextFill(Color.BLACK);
                        }
                    }
                };
                shortDescCell.setConverter(new StringConverter<Question>() {
                    @Override
                    public String toString(Question question) {
                        if (question != null) {
                            return question.getName();
                        } else
                            return null;
                    }

                    @Override
                    public Question fromString(String name) {
                        if (questionListView.getSelectionModel().getSelectedIndex() >= 0) {
                            return questionListView.getSelectionModel().getSelectedItem();
                        } else
                            return null;
                    }
                });

                shortDescCell.emptyProperty().addListener((_, _, isNowEmpty) -> {
                    if (isNowEmpty) {
                        shortDescCell.setContextMenu(null);
                    } else {
                        shortDescCell.setContextMenu(questionContextMenu);
                    }
                });

                return shortDescCell;
            }
        };

        questionListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2 && questionListView.getSelectionModel().getSelectedIndex() >= 0) {
                editQuestion();
            }
        });

        questionListView.setCellFactory(questionCellFactory);
        questionListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        questionListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE && questionListView.getSelectionModel().getSelectedIndex() >= 0)
                deleteQuestion();
            else if (event.getCode() == KeyCode.ENTER && questionListView.getSelectionModel().getSelectedIndex() >= 0)
                editQuestion();
        });

        questionListView.getSelectionModel().selectedItemProperty().addListener((_, _, _) -> {
            if (questionListView.getSelectionModel().getSelectedIndex() >= 0)
                TestManager.getInstance().setSelectedQuestion(questionListView.getSelectionModel().getSelectedItem());
        });
    }

    @FXML
    private void deleteQuestion() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Delete Question: " + selectedQuestion().getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedTest().removeQuestion(selectedQuestion());
            questionListView.getItems().remove(selectedQuestion());
            questionListView.refresh();
            testListView.refresh();
            IOManager.getInstance().saveTests();
        }
    }

    @FXML
    public void checkKeyPress(KeyEvent keyEvent) {
        if (testListView.getSelectionModel().getSelectedIndex() >= 0 && keyEvent.getCode().equals(KeyCode.DELETE))
            deleteTest();
        else if (testListView.getSelectionModel().getSelectedIndex() >= 0 && keyEvent.getCode().equals(KeyCode.ENTER))
            editTest();
    }

    @FXML
    private void deleteTest() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Test");
        alert.setHeaderText("Delete Test: " + selectedTest().getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            questionListView.getItems().clear();
            questionListView.refresh();
            IOManager.getInstance().deleteTest(selectedTest());
            testListView.getItems().remove(selectedTest());
            testListView.refresh();
            IOManager.getInstance().saveTests();
        }
    }

    @FXML
    public void createTest() {
        try{
            StageManager.setScene("/testCreation/TestEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
        ((TestEditor) StageManager.getStageController()).setTest(new Test());
    }

    @FXML
    public void editTest() throws RuntimeException {
        try{
            StageManager.setScene("/testCreation/TestEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
        ((TestEditor) StageManager.getStageController()).setTest(TestManager.getInstance().getSelectedTest());
    }

    @FXML
    public void beginTest() {
        try{
            StageManager.setScene("/questions/testPanels/TestingPanel.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void exitProgram() {
        Platform.exit();
    }

    @FXML
    public void createNewQuestion() {
        try {
            StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    /**
     * @return Returns the selected test from testListView
     */
    private Test selectedTest() {
        return testListView.getSelectionModel().getSelectedItem();
    }

    private Question selectedQuestion() {
        return questionListView.getSelectionModel().getSelectedItem();
    }

    @FXML
    private void editQuestion() {
        TestManager.getInstance().setSelectedQuestion(questionListView.getSelectionModel().getSelectedItem());
        try {
            StageManager.setScene("/questions/editorPanels/" +
                    selectedQuestion().getClass().getSimpleName() + "Editor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
        ((QuestionEditor) StageManager.getStageController()).setupQuestion(TestManager.getInstance()
                .getSelectedQuestion(), true);
    }


    private void resetTests() {
        TestManager.getInstance().getObservableTestList().clear();
        questionListView.getItems().clear();
        TestManager.getInstance().autoFillTests();
    }

    @FXML
    private void openLoginPanel() {
        try{
            StageManager.setScene("/login/WebLogin.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void openOptionsMenu() {
        try {
            StageManager.setScene("/options/OptionsMenu.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
