package TestCreator.questions.testPanels;

import TestCreator.Test;
import TestCreator.audio.textToSpeech.TTSManager;
import TestCreator.audio.transcription.TranscrptionManager;
import TestCreator.questions.*;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private float playbackSpeed;

    private final TTSManager TTS_MANAGER = new TTSManager();

    private boolean firstRun = true;

    public enum TEST_COMMANDS {
        NEXT("next"),
        BACK("back"),
        GRADE_TEST("grade"),
        MAIN_MENU("main"),
        FLAG("flag"),
        ONE("one"),
        TWO("two"),
        THREE("three"),
        FOUR("four"),
        FIVE("five"),
        SIX("six"),
        SEVEN("seven"),
        EIGHT("eight"),
        READ("read"),
        STOP("stop"),
        FLIP("flip");

        private final String command;

        TEST_COMMANDS(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }
    }

    public static final String TEST_AUDIO_COMMANDS;
    static {
        TEST_AUDIO_COMMANDS = STR."[\{
                Arrays.stream(TEST_COMMANDS.values())
                        .map(command -> STR."\"\{command.getCommand()}\"")
                        .collect(Collectors.joining(","))
                }]";
    }

    public TestDisplay() {
    }

    public void initialize() {
        setTitle();

        LocalTime tempTimer = LocalTime.of(0, 0, 0);
        timerLbl.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(tempTimer));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final LocalTime currentTime = LocalTime.parse(timerLbl.getText(), DateTimeFormatter.ofPattern("HH:mm:ss")).plusSeconds(1);
                Platform.runLater(() -> timerLbl.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime)));
            }
        }, 1000, 1000);
        setupTest(TestManager.getInstance().getSelectedTest());

        try {
            listenForCommands();
        } catch (IOException e) {
            StageManager.showAlert(STR."Error listening for commands: \{e.getMessage()}");
        }
    }

    private void listenForCommands() throws IOException{
        Thread transcriptionThread = new Thread(() -> {
            TranscrptionManager transcrptionManager;
            try {
                transcrptionManager = new TranscrptionManager();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                String command;
                while ((command = transcrptionManager.listenForCommands(TEST_AUDIO_COMMANDS)) != null) {
                    switch (command) {
                        case "next":
                            Platform.runLater(this::nextQuestion);
                            break;
                        case "back":
                            Platform.runLater(this::prevQuestion);
                            break;
                        case "grade test":
                            Platform.runLater(this::endTest);
                            break;
                        case "main menu":
                            Platform.runLater(this::returnToMainMenu);
                            break;
                        case "flag":
                            Platform.runLater(this::flagQuestion);
                            break;
                        case "one":
                            setVoiceAnswer(0);
                            break;
                        case "two":
                            setVoiceAnswer(1);
                            break;
                        case "three":
                            setVoiceAnswer(2);
                            break;
                        case "four":
                            setVoiceAnswer(3);
                            break;
                        case "five":
                            setVoiceAnswer(4);
                            break;
                        case "six":
                            setVoiceAnswer(5);
                            break;
                        case "seven":
                            setVoiceAnswer(6);
                            break;
                        case "eight":
                            setVoiceAnswer(7);
                            break;
                        case "flip":
                            if(questionList.get(questionIndex).getType().equals("FlashCard")) {
                                Platform.runLater(() -> {
                                    FlashCardTestPanel flashCardTestPanel = (FlashCardTestPanel) testPanels.get(questionIndex);
                                    flashCardTestPanel.flipCard();
                                    playQuestionAudio();
                                });
                            }
                            break;
                        case "read":
                            playQuestionAudio();
                            break;
                        case "stop":
                            TTS_MANAGER.stopSpeaking();
                            break;

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        transcriptionThread.start();
    }

    private void setVoiceAnswer(int answerIndex){
        Platform.runLater(() -> {
            switch (questionList.get(questionIndex).getType()) {
                case "MultipleChoice":
                    MultipleChoiceTestPanel multTestPanel = (MultipleChoiceTestPanel) testPanels.get(questionIndex);
                    multTestPanel.selectVoiceAnswer(answerIndex);

                    break;
                case "MultipleCheckBox":
                    MultipleCheckBoxTestPanel multiCheckBoxTestPanel = (MultipleCheckBoxTestPanel) testPanels.get(questionIndex);
                    multiCheckBoxTestPanel.selectVoiceAnswer(answerIndex);
                    break;
                case "TrueFalse":
                    TrueFalseTestPanel trueFalseTestPanel = (TrueFalseTestPanel) testPanels.get(questionIndex);
                    trueFalseTestPanel.selectVoiceAnswer(answerIndex);
                    break;
            }
        });
    }

    public void setupTest(Test test) {
        this.test = test;
        playbackSpeed = 1.35f;
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

        if(questionList.isEmpty()) {
            returnToMainMenu();
            return;
        }

        for (int x = 0; x < questionList.size(); x++) {
            try {
                testPanels.add(questionList.get(x).getTestPanel());
                testPanels.get(x).setupQuestion(questionList.get(x));
                Button loadQuestionBtn = new Button(STR."\{x + 1}");
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

        TestManager.getInstance().selectQuestion(questionList.getFirst());
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
        setTitle();
    }

    public void nextQuestion() {
        prevQuestionIndex = questionIndex;
        if (++questionIndex >= questionList.size())
            questionIndex = 0;
        loadQuestionPane();
        updateQuestionIndex();
        setTitle();
    }

    private void updateQuestionIndex() {
        questionBtnList.get(prevQuestionIndex).setStyle((questionBtnList.get(prevQuestionIndex).getStyleClass()
                .contains("flagQuestionBtn")) ? UNSELECTED_FLAG_STYLE : UNSELECTED_Question_STYLE);
        questionBtnList.get(questionIndex).setStyle((questionBtnList.get(questionIndex).getStyleClass()
                .contains("flagQuestionBtn")) ? SELECTED_FLAG_STYLE : SELECTED_QUESTION_STYLE);
    }

    private void loadQuestionPane() {
        if (questionIndex < 0 || questionIndex >= questionList.size() || testPanels.isEmpty()) return;
        TestManager.getInstance().selectQuestion(questionList.get(questionIndex));
        testNameLbl.setText(test.getQuestionAtIndex((questionIndex)).getName());
        questionDisplay.setCenter(testPanels.get(questionIndex).getRootNode());
        setFlagText();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(firstRun ? 600 : 250), _ -> playQuestionAudio()));
        timeline.play();
        firstRun = false;
    }

    private void playQuestionAudio(){
        TTS_MANAGER.stopSpeaking();

        switch (test.getQuestionAtIndex(questionIndex).getType()) {
            case "MultipleChoice":
                TTS_MANAGER.speak(((MultipleChoice) test.getQuestionAtIndex(questionIndex)).getQuestionText(), playbackSpeed);
//                for(int x = 0; x < ((MultipleChoice) test.getQuestionAtIndex(questionIndex)).getChoicesCopy().size(); x++) {
//                    TTS_MANAGER.speak(STR."\{x + 1}. \{((MultipleChoice) test.getQuestionAtIndex(questionIndex))
//                            .getChoicesCopy().get(x)}", playbackSpeed);
//                }
                break;
            case "TrueFalse":
                TTS_MANAGER.speak(((TrueFalse) test.getQuestionAtIndex(questionIndex)).getTrueFalseQuestion(), playbackSpeed);
                break;
            case "FillInTheBlank":
                TTS_MANAGER.speak(((FillTheBlank) test.getQuestionAtIndex(questionIndex)).getFillQuestion(), playbackSpeed);
                break;
            case "Matching":
                //TODO: implement matching question type speech
                break;
            case "MultipleCheckBox":
                TTS_MANAGER.speak(((MultipleCheckBox) test.getQuestionAtIndex(questionIndex)).getQuestionText(), playbackSpeed);
                break;
            case "FlashCard":
                FlashCardTestPanel flashCardTestPanel = (FlashCardTestPanel) testPanels.get(questionIndex);
                TTS_MANAGER.speak(flashCardTestPanel.getVisibleText(), playbackSpeed);
                break;
        }
    }

    private void setTitle(){
        StageManager.setTitle(STR. "[\{ TestManager.getInstance().getSelectedTestName() }]: Question \{ (questionIndex + 1) }" );
    }

    public void endTest() {
        StageManager.showDialog("Are you sure you want to end your test? Press OK to confirm, or cancel to back out.")
                .thenAccept(okayClicked -> {
                    if (okayClicked) {
                        TTS_MANAGER.stopSpeaking();
                        finishTestBtn.setDisable(true);
                        timer.cancel();
                        int numberCorrect = 0;

                        for (int i = 0; i < questionList.size(); i++) {
                            int pointsScored = (int) testPanels.get(i).getPointsScored();
                            int maxPoints = questionList.get(i).getMaxScore();
                            if (pointsScored == maxPoints)
                                questionBtnList.get(i).getStyleClass().add("correctAnswer");
                            else if (pointsScored > 0)
                                questionBtnList.get(i).getStyleClass().add("partialCorrectAnswer");
                            else
                                questionBtnList.get(i).getStyleClass().add("wrongAnswer");

                            numberCorrect += pointsScored;
                        }

                        float percentCorrect = ((float) numberCorrect / (float) maximumScore) * 100;
                        percentCorrectLbl.setText(STR."Percent Correct: \{(int) percentCorrect}%");
                        numCorrectLbl.setText(STR."Correct Answers: \{numberCorrect}/\{maximumScore}");

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
        StageManager.showDialog("Are you sure you want to return to the main menu? Press OK to confirm, or stopRecording to back out.").thenAccept(okayClicked -> {
            if (okayClicked) {
                TTS_MANAGER.stopSpeaking();
                try {
                    cleanup();
                    StageManager.setScene("/MainMenu.fxml");
                    StageManager.clearStageController();
                } catch (IOException e) {
                    e.printStackTrace();
                    StageManager.showAlert("Error loading MainMenu.fxml");
                    throw new RuntimeException(e);
                }
            }
        });
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