package TestCreator.questions.testPanels;

import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public class RunTestPanel {

    private final List<TestPanel> testPanels = new ArrayList<>();
    private final Timer timer = new Timer();
    private final List<Integer> flaggedList = new ArrayList<>();
    private final List<Button> questionBtnList = new ArrayList<>();
    @FXML
    public Button flagQuestionBtn;
    @FXML
    private VBox shortcutBtnsPane;
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
    private int totalParts = 0;
    private int questionIndex;
    private int numberCorrect;
    private List<Question> questionList;

    public void initialize() {

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
        questionList = new ArrayList<>(test.getQuestionList());

        for (int x = 0; x < questionList.size(); x++) {
            if (questionList.get(x).getTestPanel() == null || !questionList.get(x).readyToRun())
                questionList.remove(questionList.get(x));
            else {
                try {
                    testPanels.add(questionList.get(x).getTestPanel());
                    Button button = new Button(x + 1 + "");
                    button.setId("cirlce-button");
                    button.minHeight(40);
                    button.setMinWidth(40);
                    button.setOnAction(event -> {
                        questionIndex = Integer.parseInt(button.getText()) - 1;
                        loadTestPane();
                    });
                    questionBtnList.add(button);
                    shortcutBtnsPane.getChildren().add(button);
                    totalParts += questionList.get(x).getGradableParts();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    questionList.remove(questionList.get(x));
                }
            }

        }

        testNameLbl.setText(test.getName());
        questionIndex = 0;
        loadTestPane();
    }

    public void prevQuestion() {
        if (--questionIndex < 0)
            questionIndex = questionList.size() - 1;
        loadTestPane();
    }

    public void nextQuestion() {
        if (++questionIndex >= questionList.size())
            questionIndex = 0;
        loadTestPane();
    }

    private void loadTestPane() {
        try {
            String panelName = questionList.get(questionIndex).getTestPanel().getClass().getSimpleName();
            StageManager.setScene(("/TestCreator/questions/testPanels/" + panelName + ".fxml"));
            testNameLbl.setText("Question Name: " + test.getQuestionAtIndex((questionIndex)).getName());
            questionDisplay.setCenter(testPanels.get(questionIndex).getQuestionScene());
            setFlagText();
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void checkCorrectAnswers() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Grade Test");
        alert.setHeaderText("Finish test and grade questions.");
        alert.setContentText("Are you sure you want to end your test? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            finishTestBtn.setDisable(true);
            timer.cancel();
            numberCorrect = 0;

            testPanels.forEach(testPanel -> {
                numberCorrect += testPanel.getPointsScored();
                testPanel.disableAnswerChanges();
            });

            float percentCorrect = ((float) numberCorrect / (float) totalParts) * 100;
            percentCorrectLbl.setText("Percent Correct: " + (int) percentCorrect + "%");
            numCorrectLbl.setText("Correct Answers: " + numberCorrect + "/" + totalParts);

            if (percentCorrect >= 90) {
                numCorrectLbl.setStyle("-fx-background-color: rgb(173, 255, 47)");
                percentCorrectLbl.setStyle("-fx-background-color: rgb(173, 255, 47)");
            } else if (percentCorrect >= 80 && percentCorrect < 90) {
                numCorrectLbl.setStyle("-fx-background-color: rgb(240, 255, 0)");
                percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 255, 0)");
            } else if (percentCorrect >= 70 && percentCorrect < 80) {
                numCorrectLbl.setStyle("-fx-background-color: rgb(240, 215, 0)");
                percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 215, 0)");
            } else if (percentCorrect >= 65 && percentCorrect < 70) {
                numCorrectLbl.setStyle("-fx-background-color: rgb(240, 140, 0)");
                percentCorrectLbl.setStyle("-fx-background-color: rgb(240, 140, 0)");
            } else {
                numCorrectLbl.setStyle("-fx-background-color: rgba(220,0,0,0.64)");
                percentCorrectLbl.setStyle("-fx-background-color: rgba(220,0,0,0.64)");
            }
        }
    }

    @FXML
    public void returnToTestMenu(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Test Menu");
        alert.setHeaderText("Exit test and return to the test menu.");
        alert.setContentText("Progress will be lost, are you sure? Press OK to confirm, or cancel to back out");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            try{
                StageManager.setScene("/MainMenu.fxml");
            } catch (IOException e) {
                FXML_ALERT.showAndWait();
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void flagQuestion() {
        if (!flaggedList.contains(questionIndex)) {
            flaggedList.add(questionIndex);
            questionBtnList.get(questionIndex).setId("flagged-button");
        } else {
            flaggedList.remove(Integer.valueOf(questionIndex));
            questionBtnList.get(questionIndex).setId("cirlce-button");
        }
        setFlagText();
    }

    private void setFlagText() {
        flagQuestionBtn.getText();
        flagQuestionBtn.setText((flaggedList.contains(questionIndex)) ? "Unflag" : "Flag");
    }
}
