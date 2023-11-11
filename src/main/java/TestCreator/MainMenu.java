package TestCreator;

import TestCreator.questions.Question;
import TestCreator.testIO.TestData;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.UserManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

import static java.lang.System.nanoTime;

public class MainMenu {

    public static final String MAIN_MENU_LOCATION = "/MainMenu.fxml";
    public static final String OS_TYPE = System.getProperty("os.name");
    @FXML
    BorderPane testMenuPane;
    @FXML
    TextArea testDescription;
    @FXML
    Label reviewDate;
    @FXML
    Button newQuestionBtn;
    @FXML
    Button beginTestBtn;
    @FXML
    private ListView<Test> testListView;
    @FXML
    private ListView<Question> questionListView;
    private ContextMenu questionContextMenu;
    @FXML
    private HBox menuBar;
    


    public void initialize() {
        StageManager.setTitle("Main Menu - %s".formatted(UserManager.getCurrentUsername()));
        ContextMenu testContextMenu = new ContextMenu();

        MenuItem loginItem = new MenuItem("login");
        loginItem.setOnAction(_ -> openLoginPanel());

        MenuItem runTestItem = new MenuItem("Run");
        runTestItem.setOnAction(_ -> runTest());

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
        editQuestionItem.setOnAction(event -> editQuestion());
        questionContextMenu.getItems().addAll(editQuestionItem, deleteQuestionItem);

        Callback testCellFactory = new Callback<ListView<Test>, TextFieldListCell<Test>>() {
            @Override
            public TextFieldListCell<Test> call(ListView<Test> param) {
                TextFieldListCell<Test> shortDescCell = new TextFieldListCell<Test>() {
                    public void updateItem(Test test, boolean empty) {
                        super.updateItem(test, empty);

                        if (test != null && !empty) {
                            if (!test.readyToRun())
//                                setStyle("-fx-background-color: tomato");
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
                            testDescription.setText(selectedTest().getDescription());
                            return selectedTest();
                        } else
                            return null;
                    }
                });

                shortDescCell.emptyProperty().addListener((observable, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        shortDescCell.setContextMenu(null);
                    } else {
                        shortDescCell.setContextMenu(testContextMenu);
                    }
                });

                return shortDescCell;
            }
        };

        testListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (testListView.getSelectionModel().getSelectedIndex() >= 0) {
                newQuestionBtn.setDisable(false);
                beginTestBtn.setDisable(false);
                testDescription.setText(selectedTest().getDescription());
                questionListView.setItems(FXCollections.observableArrayList(selectedTest().getQuestionList()));
            } else {
                newQuestionBtn.setDisable(true);
                beginTestBtn.setDisable(true);
//                reviewDate.setText("");
                testDescription.setText("");
            }
        });

        testListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                editTest();
            }
        });

        testListView.setCellFactory(testCellFactory);
        testListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        autoFillTests();

        Callback questionCellFactory = new Callback<ListView<Question>, TextFieldListCell<Question>>() {
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

                shortDescCell.emptyProperty().addListener((observable, wasEmpty, isNowEmpty) -> {
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

        if (Main.TESTING_MODE) {
            Button resetTestBtn = new Button("Reset Tests");
            resetTestBtn.setOnAction(event -> resetTests());
            menuBar.getChildren().addAll(resetTestBtn);
        }
    }

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
            TestData.getInstance().saveTests();
        }
    }

    @FXML
    public void deleteTestKeyPress(KeyEvent keyEvent) {
        if (testListView.getSelectionModel().getSelectedIndex() >= 0 && keyEvent.getCode().equals(KeyCode.DELETE))
            deleteTest();
        else if (testListView.getSelectionModel().getSelectedIndex() >= 0 && keyEvent.getCode().equals(KeyCode.ENTER))
            editTest();
    }

    @FXML
    public void deleteTest(ActionEvent event) {
        if (testListView.getSelectionModel().getSelectedIndex() >= 0) {
            deleteTest();
        }
    }

    private void deleteTest() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Test");
        alert.setHeaderText("Delete Test: " + selectedTest().getName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            questionListView.getItems().clear();
            TestData.getInstance().removeTest(selectedTest());
            TestData.getInstance().saveTests();
            testListView.refresh();
            questionListView.refresh();
        }
    }

    @FXML
    public void createTest() {
        try{
            StageManager.setScene("/testCreation/TestEditDialog.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void editTest() throws RuntimeException {
        try{
            StageManager.setScene("/testCreation/TestEditDialog.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void runTest() {
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

    private void editQuestion() {
        try {
            StageManager.setScene("/questions/editorPanels/" +
                    selectedQuestion().getClass().getSimpleName() + "Editor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    private void autoFillTests() {
        if(Main.TESTING_MODE) {
            while (TestData.getInstance().size() < 4) {
                TestData.getInstance().addTest(new Test("Test #" + new Random(nanoTime()).nextInt(999)));
            }

            System.out.println("Test Data Size: " + TestData.getInstance().size());

            TestData.getInstance().getTests().forEach(test -> {
                for (int x = 0; test.getQuestionList().size() < 4; x++) {
                    String qName = Question.getQuestionTypesList().get(x) + " #" + new Random().nextInt(200);
                    Question newQuestion = Question.getQuestionInstance(qName, Question.getQuestionTypesList().get(x), test);
                    newQuestion.autofillData();
                    if (newQuestion.readyToRun()) {
                        test.addQuestion(newQuestion);
                    }
                }
            });
            testListView.setItems(TestData.getInstance().getTests());
            TestData.getInstance().saveTests();
        }
    }

    private void resetTests() {
        TestData.getInstance().getTests().clear();
        questionListView.getItems().clear();
        autoFillTests();
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

    public void onTestListViewClicked(MouseEvent mouseEvent) {
    }

    public void onQuestionListViewClicked(MouseEvent mouseEvent) {
    }

    public void openOptionMenu(ActionEvent actionEvent) {
        try {
            StageManager.setScene("/options/OptionsMenu.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }
}
