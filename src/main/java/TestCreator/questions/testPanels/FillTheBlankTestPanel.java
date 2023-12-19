package TestCreator.questions.testPanels;

import TestCreator.questions.FillTheBlank;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static TestCreator.utilities.WordAtCaretFinder.getWordAtCaret;


public class FillTheBlankTestPanel implements TestPanel<FillTheBlank> {

    @FXML
    public InlineCssTextArea questionTextArea;
    @FXML
    public ListView<String> wordBankListView;
    public CheckBox displayAnswersCheckBox;
    @FXML
    public ScrollPane questionScrollPane;
    @FXML
    private BorderPane rootNode;
    @FXML
    private RadioButton displayCorrectBtn;
    @FXML
    private RadioButton displayAnswersBtn;
    private FillTheBlank question;

    private List<String> wordBank;
    private List<Integer> wordOffsets;
    private List<Integer> origOffsetsList;

    private int caretPosition = -1;

    private boolean isShowingAnswer = false;
    private boolean showingCorrectAnswers = false;
    private boolean isTestGraded = false;
    private String answerBlank = "";
    private int prevIndex;


    @FXML
    public void initialize() {
        wordBankListView.visibleProperty().bind(displayAnswersCheckBox.selectedProperty());

        questionTextArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        questionTextArea.prefWidthProperty().bind(questionScrollPane.widthProperty());
        questionTextArea.prefHeightProperty().bind(questionScrollPane.heightProperty());
        questionTextArea.setWrapText(true);

        questionTextArea.addEventFilter(MouseEvent.MOUSE_DRAGGED, MouseEvent::consume);
        questionTextArea.addEventFilter(MouseEvent.MOUSE_RELEASED, MouseEvent::consume);

        questionTextArea.setOnMouseClicked(event -> {
            caretPosition = questionTextArea.getCaretPosition();
            int wordStartIndex = isInsideAnswer(caretPosition);
            if (event.getClickCount() == 1 && wordStartIndex != -1) {
                double mouseX = event.getX();
                double mouseY = event.getY();
                caretPosition = questionTextArea.hit(mouseX, mouseY).getInsertionIndex();

                if (prevIndex != -1 && prevIndex < questionTextArea.getLength() && prevIndex != wordStartIndex) {
                    questionTextArea.setStyle(prevIndex, prevIndex + answerBlank.length(), "-fx-fill: black;");
                }
                questionTextArea.setStyle(wordStartIndex, wordStartIndex + answerBlank.length(), "-fx-fill: blue;");
                prevIndex = wordStartIndex;
            }
        });

        wordBankListView.setOnMouseClicked(_ -> {
            int startIndex = isInsideAnswer(prevIndex);
            if (startIndex != -1 && !isTestGraded) {
                StringBuilder selectedWord = new StringBuilder(wordBankListView.getSelectionModel().getSelectedItem());
                while (selectedWord.length() < answerBlank.length())
                    selectedWord.append("_");
                questionTextArea.replaceText(startIndex, startIndex + answerBlank.length(), selectedWord.toString());
                questionTextArea.setStyle(startIndex, startIndex + answerBlank.length(), FillTheBlank.ANSWER);
            }
        });

        questionTextArea.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            int startIndex = isInsideAnswer(prevIndex);
            int caretPosition = questionTextArea.getCaretPosition();
            if (event.getCharacter().matches(FillTheBlank.ALLOWED_CHARS) && startIndex != -1 && isInsideAnswer(caretPosition) != -1
                && caretPosition < startIndex + answerBlank.length()) {
                questionTextArea.replaceText(caretPosition, caretPosition+1, event.getCharacter());
                questionTextArea.setStyle(caretPosition, caretPosition, STR."\{FillTheBlank.ANSWER}-fx-underline: true;");
                questionTextArea.moveTo(questionTextArea.getCaretPosition());
            }
            event.consume();
            prevIndex = isInsideAnswer(caretPosition);
            setStyleIfSelected(caretPosition);
        });
        questionTextArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            int caretPosition = questionTextArea.getCaretPosition();
            KeyCode keyCode = event.getCode();
            if (keyCode == KeyCode.UP || keyCode == KeyCode.DOWN || keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
                return;
            } else if (keyCode == KeyCode.DELETE && isInsideAnswer(caretPosition) != -1 && isAfterAnswer(caretPosition) == -1) {
                questionTextArea.replaceText(caretPosition, caretPosition+1, "_");
                questionTextArea.setStyle(caretPosition, caretPosition+1, STR."\{FillTheBlank.ANSWER}-fx-underline: true;");
            }else if (keyCode == KeyCode.BACK_SPACE && (isInsideAnswer(prevIndex) > -1 || isAfterAnswer(caretPosition) > -1)
                && isStartOfAnswer(caretPosition) == -1 && caretPosition > 1) {
                if (questionTextArea.getSelection().getLength() > 1)
                    questionTextArea.selectRange(caretPosition, caretPosition);
                questionTextArea.replaceText(caretPosition - 1, caretPosition, "_");
                questionTextArea.setStyle(caretPosition - 1, caretPosition - 1, STR."\{FillTheBlank.ANSWER}-fx-underline: true;");
                questionTextArea.moveTo(questionTextArea.getCaretPosition() - 1);
            }
            event.consume();
            prevIndex = isInsideAnswer(caretPosition);
            setStyleIfSelected(caretPosition);
        });
        questionTextArea.addEventFilter(KeyEvent.KEY_RELEASED, KeyEvent::consume);
    }

    public void setupQuestion(FillTheBlank question) {
        this.question = question;
        long randNum = System.nanoTime();
        showingCorrectAnswers = question.hintsDisplayed();

        origOffsetsList = new ArrayList<>(question.getAnswerOffsetsCopy());
        List<String> origWordBank = question.getWordBankCopy();

        wordBank = question.getWordBankCopy();

        questionTextArea.replaceText(question.getFillQuestion());

        for (String word : wordBank) {
            if (word.length() > answerBlank.length())
                answerBlank = "_".repeat(word.length());
        }

        int offsetAdjustment = 0;
        for (int x = 0; x < origOffsetsList.size(); x++) {
            origOffsetsList.set(x, origOffsetsList.get(x) + offsetAdjustment);
            String answer = origWordBank.get(x);
            int startOffset = origOffsetsList.get(x);
            int endOffset = startOffset + answer.length();

            questionTextArea.replaceText(startOffset, endOffset, "");
            questionTextArea.insertText(startOffset, answerBlank);

            offsetAdjustment += answerBlank.length() - answer.length();
        }

        ToggleGroup toggle = new ToggleGroup();
        displayAnswersBtn.setToggleGroup(toggle);
        displayCorrectBtn.setToggleGroup(toggle);

        displayAnswersCheckBox.setSelected(question.hintsDisplayed());

        wordOffsets = new ArrayList<>(origOffsetsList);
        Collections.shuffle(wordOffsets, new Random(randNum));
        Collections.shuffle(wordBank, new Random(randNum));
        wordBankListView.setItems(FXCollections.observableArrayList(wordBank));
    }

    private void setStyleIfSelected(int position) {
        if (prevIndex != -1 && prevIndex < questionTextArea.getLength() && prevIndex != position) {
            questionTextArea.setStyle(prevIndex, prevIndex + answerBlank.length(), FillTheBlank.DEFAULT);
        }
        if (isInsideAnswer(position) != -1) {
            questionTextArea.setStyle(position, position, STR."\{FillTheBlank.ANSWER}-fx-underline: true;");
        }
    }

    private int isStartOfAnswer(int position) {
        for (int x = 0; x < wordOffsets.size(); x++) {
            if (position == wordOffsets.get(x)) {
                return wordOffsets.get(x);
            }
        }
        return -1;
    }

    private int isAfterAnswer(int position) {
        for (int x = 0; x < wordOffsets.size(); x++) {
            int endOffset = wordBank.get(x).length() + wordOffsets.get(x);
            if (position == endOffset + 1) {
                return wordOffsets.get(x);
            }
        }
        return -1;
    }

    private int isInsideAnswer(int position) {
        for (int x = 0; x < wordOffsets.size(); x++) {
            int endOffset = wordBank.get(x).length() + wordOffsets.get(x);
            if (position >= wordOffsets.get(x) && position <= endOffset) {
                return wordOffsets.get(x);
            }
        }
        return -1;
    }

    /**
     * Displays the correct answers in the corresponding blanks.
     */
    private void displayCorrectAnswers() {
        if (isTestGraded && !showingCorrectAnswers) {
            showingCorrectAnswers = true;
        }
    }

    /**
     * Displays the given user answers in the corresponding blanks.
     */
    private void displayAnswers() {
        if (isTestGraded && showingCorrectAnswers) {
            showingCorrectAnswers = false;
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void cleanUp() {
        questionTextArea.clear();
        wordBankListView.getItems().clear();
        wordBank.clear();
        wordOffsets.clear();
        origOffsetsList.clear();
    }

    @Override
    public float getPointsScored() {
        float score = 0.0f;
        isTestGraded = true;
        displayAnswersBtn.setSelected(true);
        displayAnswersBtn.setDisable(false);
        displayCorrectBtn.setDisable(false);
        questionTextArea.setDisable(true);

        for (int x = 0; x < wordOffsets.size(); x++) {
            int startOffset = wordOffsets.get(x);
            int endOffset = startOffset + answerBlank.length();
            String answer = getWordAtCaret(questionTextArea.getText(), startOffset).replaceAll("_+$", "");

            StringBuilder correctAnswer = new StringBuilder(wordBank.get(x));
            while(correctAnswer.length() < answerBlank.length()) correctAnswer.append("_");
            questionTextArea.replaceText(startOffset, endOffset, correctAnswer.toString());

            if (answer.equals(wordBank.get(x))) {
                score += 1.0f;
                questionTextArea.setStyle(startOffset, endOffset, FillTheBlank.CORRECT);
            } else {
                questionTextArea.setStyle(startOffset, endOffset, FillTheBlank.INCORRECT);
            }
        }
        return score;
    }

    @FXML
    private void dispCorrect() {
        displayCorrectAnswers();
    }

    @FXML
    private void dispAnswers() {
        displayAnswers();
    }
}