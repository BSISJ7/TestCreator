package TestCreator.questions.testPanels;

import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StackPaneDialogue;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class TestDisplay {

    private final List<TestPanel> testPanels = new ArrayList<>();
    private final Timer timer = new Timer();
    private final List<Integer> flaggedList = new ArrayList<>();
    private List<Button> questionBtnList = new ArrayList<>();
    @FXML
    public Button flagQuestionBtn;
    public Button mainMenuBtn;
    public Button prevQuestionBtn;
    public Button nextQuestionBtn;
    public HBox reviewDisplayVBox;
    @FXML
    public StackPane rootNode;
    @FXML
    public BorderPane contentPane;
    @FXML
    private VBox shortcutBtnsVBox;
    @FXML
    private BorderPane questionDisplay;
    @FXML
    private Label testNameLbl;
    @FXML
    private Label numCorrectLbl;
    @FXML
    private Label percentCorrectLbl;
    @FXML
    private Label timerLbl;
    @FXML
    private Button finishTestBtn;
    private Test test;
    private int maximumScore = 0;
    private int questionIndex = 0;
    private int prevQuestionIndex = 0;
    private List<Question> questionList;
    public static final String SELECTED_QUESTION_STYLE = "-fx-border-color: #00bfff";
    public static final String UNSELECTED_Question_STYLE = "-fx-border-color: transparent";
    public static final String SELECTED_FLAG_STYLE = "-fx-border-color: white";
    public static final String UNSELECTED_FLAG_STYLE = "-fx-border-color: red";

    public void initialize() {
        StageManager.setTitle(STR. "[\{ testNameLbl.getText() }]: Question \{ (questionIndex + 1) }" );
        setupTest(TestManager.getInstance().getSelectedTest());

        LocalTime tempTimer = LocalTime.of(0, 0, 0);
        timerLbl.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(tempTimer));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final LocalTime currentTime = LocalTime.parse(timerLbl.getText(), DateTimeFormatter.ofPattern("HH:mm:ss")).plusSeconds(1);
                Platform.runLater(() -> timerLbl.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime)));
            }
        }, 1000, 1000);
    }

    public void setupTest(Test test) {
        this.test = test;
        questionList = test.getQuestionListCopy();

        questionList = questionList.stream()
                .filter(question -> {
                    try {
                        return Objects.nonNull(question.getTestPanel()) && question.readyToRun();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }).collect(Collectors.toList());

        for (int x = 0; x < questionList.size(); x++) {
            try {
                testPanels.add(questionList.get(x).getTestPanel());
                testPanels.get(x).setupQuestion(questionList.get(x));
                Button loadQuestionBtn = new Button(x + 1 + "");
                loadQuestionBtn.minHeight(40);
                loadQuestionBtn.setMinWidth(40);
                loadQuestionBtn.focusedProperty().addListener((_, _, _) -> {
                    if (loadQuestionBtn.isFocused())
                        loadQuestionBtn.getStyleClass().add("selectedQuestionBtn");
                    else
                        loadQuestionBtn.getStyleClass().remove("selectedQuestionBtn");
                });
                loadQuestionBtn.setOnAction(_ -> {
                    questionBtnList.get(questionIndex).setStyle("-fx-border-color: transparent");
                    prevQuestionIndex = questionIndex;
                    questionIndex = Integer.parseInt(loadQuestionBtn.getText()) - 1;
                    loadQuestionPane();
                    updateQuestionIndex();
                });
                questionBtnList.add(loadQuestionBtn);
                shortcutBtnsVBox.getChildren().add(loadQuestionBtn);
                maximumScore += questionList.get(x).getMaxScore();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                questionList.remove(questionList.get(x));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        TestManager.getInstance().setSelectedQuestion(questionList.get(0));
        Platform.runLater(() -> testNameLbl.setText(test.getName()));
        questionIndex = 0;
        questionBtnList.get(questionIndex).setStyle("-fx-border-color: #00bfff");
        loadQuestionPane();
    }

    public void prevQuestion() {
        prevQuestionIndex = questionIndex;
        if (--questionIndex < 0)
            questionIndex = questionList.size() - 1;
        loadQuestionPane();
        updateQuestionIndex();
    }

    public void nextQuestion() {
        prevQuestionIndex = questionIndex;
        if (++questionIndex >= questionList.size())
            questionIndex = 0;
        loadQuestionPane();
        updateQuestionIndex();
    }

    private void updateQuestionIndex() {
        questionBtnList.get(prevQuestionIndex).setStyle((questionBtnList.get(prevQuestionIndex).getStyleClass()
                .contains("flagQuestionBtn")) ? UNSELECTED_FLAG_STYLE : UNSELECTED_Question_STYLE);
        questionBtnList.get(questionIndex).setStyle((questionBtnList.get(questionIndex).getStyleClass()
                .contains("flagQuestionBtn")) ? SELECTED_FLAG_STYLE : SELECTED_QUESTION_STYLE);
    }

    private void loadQuestionPane() {
        if (questionIndex < 0 || questionIndex >= questionList.size() || testPanels.isEmpty()) return;
        TestManager.getInstance().setSelectedQuestion(questionList.get(questionIndex));
        Platform.runLater(() -> {
            testNameLbl.setText(test.getQuestionAtIndex((questionIndex)).getName());
            questionDisplay.setCenter(testPanels.get(questionIndex).getRootNode());
        });
        setFlagText();
    }

    public void checkCorrectAnswers() {
        new StackPaneDialogue(rootNode, "Are you sure you want to end your test? Press OK to confirm, or cancel to back out.")
                .showAndWait().thenAccept(okayClicked -> {
                    if (okayClicked) {
                        finishTestBtn.setDisable(true);
                        timer.cancel();
                        int numberCorrect = 0;

                        for (int i = 0; i < questionList.size(); i++) {
                            int pointsScored = (int) testPanels.get(i).getPointsScored();
                            int maxPoints = questionList.get(i).getMaxScore();
//                            System.out.println("Question Name: " + questionList.get(i).getName());
//                            System.out.println("Points scored: " + pointsScored + " out of " + maxPoints);
                            if (pointsScored == maxPoints)
                                questionBtnList.get(i).getStyleClass().add("correctAnswer");
                            else if (pointsScored > 0)
                                questionBtnList.get(i).getStyleClass().add("partialCorrectAnswer");
                            else
                                questionBtnList.get(i).getStyleClass().add("wrongAnswer");

                            numberCorrect += pointsScored;
                        }

                        float percentCorrect = ((float) numberCorrect / (float) maximumScore) * 100;
                        percentCorrectLbl.setText("Percent Correct: " + (int) percentCorrect + "%");
                        numCorrectLbl.setText("Correct Answers: " + numberCorrect + "/" + maximumScore);

                        if (percentCorrect >= 90) {
                            numCorrectLbl.setStyle("-fx-background-color: rgb(173, 255, 47)");
                            percentCorrectLbl.setStyle("-fx-background-color: rgb(173, 255, 47)");
                        } else if (percentCorrect >= 80) {
                            numCorrectLbl.setStyle("-fx-background-color: rgb(240, 255, 0)");
                            percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 255, 0)");
                        } else if (percentCorrect >= 70) {
                            numCorrectLbl.setStyle("-fx-background-color: rgb(240, 215, 0)");
                            percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 215, 0)");
                        } else if (percentCorrect >= 65) {
                            numCorrectLbl.setStyle("-fx-background-color: rgb(240, 140, 0)");
                            percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 140, 0)");
                        } else {
                            numCorrectLbl.setStyle("-fx-background-color: rgba(220,0,0,0.64)");
                            percentCorrectLbl.setStyle("-fx-background-color: rgba(220,0,0,0.64)");
                        }
                    }
                });
    }

    @FXML
    public void returnToMainMenu() {
        try {
            cleanup();
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            e.printStackTrace();
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }

//        new StackPaneDialogue(rootNode, "Are you sure? Press OK to confirm, or cancel to back out.")
//                .showAndWait().thenAccept(okayClicked -> {
//            if (okayClicked) {
//                try {
//                    cleanup();
//                    StageManager.setScene("/MainMenu.fxml");
//                    StageManager.clearStageController();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
//                    throw new RuntimeException(e);
//                }
//            }
//        });
    }

    public void cleanup() {
        testPanels.forEach(TestPanel::cleanUp);
        questionBtnList.forEach(button -> button.setOnAction(null));
        timer.cancel();
        testPanels.clear();
        flaggedList.clear();
        questionBtnList.clear();
        if (Objects.nonNull(questionList)) questionList.clear();
        prevQuestionBtn.setOnAction(null);
        nextQuestionBtn.setOnAction(null);
        finishTestBtn.setOnAction(null);
        mainMenuBtn.setOnAction(null);
        questionList = null;
        questionBtnList = null;
    }

    @FXML
    private void flagQuestion() {
        if (!flaggedList.contains(questionIndex)) {
            flaggedList.add(questionIndex);
            questionBtnList.get(questionIndex).getStyleClass().add("flagQuestionBtn");
            questionBtnList.get(questionIndex).requestFocus();
        } else {
            flaggedList.remove(Integer.valueOf(questionIndex));
            questionBtnList.get(questionIndex).getStyleClass().remove("flagQuestionBtn");
            questionBtnList.get(questionIndex).requestFocus();
        }
        setFlagText();
    }

    private void setFlagText() {
        flagQuestionBtn.setText((flaggedList.contains(questionIndex)) ? "Unflag" : "Flag");
        questionBtnList.get(questionIndex).setStyle((questionBtnList.get(questionIndex).getStyleClass()
                .contains("flagQuestionBtn")) ? SELECTED_FLAG_STYLE : SELECTED_QUESTION_STYLE);
    }
}
