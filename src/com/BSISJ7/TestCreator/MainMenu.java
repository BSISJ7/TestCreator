package com.BSISJ7.TestCreator;

import com.BSISJ7.TestCreator.questions.Question;
import com.BSISJ7.TestCreator.questions.editorPanels.EditorPanel;
import com.BSISJ7.TestCreator.questions.editorPanels.NewQuestionEditor;
import com.BSISJ7.TestCreator.questions.testPanels.TestingPanel;
import com.BSISJ7.TestCreator.testCreation.TestEditDialog;
import com.BSISJ7.TestCreator.testIO.TestData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

import static java.lang.System.nanoTime;

public class MainMenu {

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

    public static final String MAIN_MENU_LOCATION = "/com/BSISJ7/TestCreator/MainMenu.fxml";
    public static final String OS_TYPE = System.getProperty("os.name");

    @FXML
    public void initialize() {
        ContextMenu testContextMenu = new ContextMenu();

        MenuItem loginItem = new MenuItem("Login");
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

        if(true) {
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("New Test Dialog");
        dialog.setTitle("Create New Test");
        dialog.initOwner(testMenuPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/BSISJ7/TestCreator/testCreation/TestEditDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        TestEditDialog testEditor = fxmlLoader.getController();
        testEditor.setTest(new Test());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Test newTest = testEditor.getTest();
            TestData.getInstance().addTest(newTest);
            testListView.getSelectionModel().select(newTest);
            TestData.getInstance().saveTests();
        }
    }

    @FXML
    public void editTest() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("Edit Test Dialog");
        dialog.setTitle("Edit New Test");
        dialog.initOwner(testMenuPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/BSISJ7/TestCreator/testCreation/TestEditDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        TestEditDialog testEditor = fxmlLoader.getController();
        testEditor.setTest(selectedTest());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TestData.getInstance().getTests().set(testListView.getSelectionModel().getSelectedIndex(),
                    testEditor.getTest());
            testDescription.setText(selectedTest().getDescription());
//            reviewDate.setText(selectedTest().getReviewDate().format(shortDateFormat));
            TestData.getInstance().saveTests();
        }
    }

    @FXML
    public void runTest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BSISJ7/TestCreator/questions/testPanels/" +
                    "TestingPanel.fxml"));
            Parent runTestPanel = loader.load();
            TestingPanel testController = loader.getController();
            testController.setupTest(selectedTest());
            Stage stage = (Stage) testMenuPane.getScene().getWindow();
            stage.getScene().setRoot(runTestPanel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void exitProgram() {
        Platform.exit();
    }

    @FXML
    public void loadMainMenu(ActionEvent event) {
        try {
            Parent testMenuPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/BSISJ7/TestCreator/" +
                    "MainMenu.fxml")));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(testMenuPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void createNewQuestion() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setHeaderText("New Question Menu");
        dialog.setTitle("Create a new question.");
        dialog.initOwner(testMenuPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/com/BSISJ7/TestCreator/questions/editorPanels/" +
                "NewQuestionEditor.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);
        NewQuestionEditor questionEditor = fxmlLoader.getController();
        questionEditor.setOkButton(dialog.getDialogPane().lookupButton(ButtonType.OK));
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Question newQuestion = questionEditor.getNewQuestion(selectedTest());
            selectedTest().addQuestion(newQuestion);
            questionListView.setItems(FXCollections.observableArrayList(selectedTest().getQuestionList()));
            testListView.refresh();
            questionListView.refresh();
            TestData.getInstance().saveTests();
        }
    }

//    public void setReviewDate() {
//
//        if (selectedTest().getReviewDate().isBefore(LocalDateTime.now())) {
//            reviewDate.setText("");
//        } else {
//            reviewDate.setText(selectedTest().getReviewDate().format(shortDateFormat));
//        }
//    }

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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Question Editor");
        dialog.initOwner(testMenuPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(Question.EDITOR_PANELS_LOCATION +
                selectedQuestion().getClass().getSimpleName() + "Editor.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        EditorPanel questionDialog = loader.getController();
        questionDialog.setupQuestion(selectedQuestion());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedTest().getQuestionList().set(questionListView.getSelectionModel().getSelectedIndex(), questionDialog.getQuestion());
            questionListView.getItems().set(questionListView.getSelectionModel().getSelectedIndex(), questionDialog.getQuestion());
            TestData.getInstance().saveTests();
            questionListView.refresh();
            testListView.refresh();
        }
    }

    private void autoFillTests(){
        while (TestData.getInstance().size() < 4) {
            TestData.getInstance().addTest(new Test("Test #" + new Random(nanoTime()).nextInt(999)));
        }

        TestData.getInstance().getTests().forEach(test -> {
            for(int x = 0; test.getQuestionList().size() < 4; x++){
                String qName = Question.getQuestionTypesList().get(x) + " #" + new Random().nextInt(200);
                Question newQuestion = Question.getQuestionInstance(qName, Question.getQuestionTypesList().get(x), test);
                newQuestion.autofillData();
                if(newQuestion.readyToRun()) {
                    test.addQuestion(newQuestion);
                }
            }
        });
        testListView.setItems(TestData.getInstance().getTests());
        TestData.getInstance().saveTests();
    }

    private void resetTests(){
        TestData.getInstance().getTests().clear();
        questionListView.getItems().clear();
        autoFillTests();
    }

    @FXML
    private void openLoginPanel() {
        try {
            Stage stage = (Stage) testMenuPane.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(getClass()
                    .getResource("/com/BSISJ7/TestCreator/loginPanel.fxml"))), 800, 500));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
