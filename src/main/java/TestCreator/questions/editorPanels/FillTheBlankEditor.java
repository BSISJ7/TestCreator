package TestCreator.questions.editorPanels;

import TestCreator.questions.FillTheBlank;
import TestCreator.utilities.CaretUtilities;
import TestCreator.utilities.SelectionManager;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static TestCreator.utilities.CaretUtilities.getWordAtCaret;


public class FillTheBlankEditor extends QuestionEditor<FillTheBlank> {
    @FXML
    public Button acceptBtn;
    @FXML
    public Button cancelBtn;
    @FXML
    public CheckBox displayAnswersCheckBox;
    @FXML
    public InlineCssTextArea questionTextArea;
    @FXML
    public CheckBox multiSelectCheckBox;
    @FXML
    ScrollPane questionScrollPane;
    private int prevWordIndex = -1;
    private String prevWord = "";
    private boolean addingWord = false;
    private boolean removingWord = false;
    @FXML
    private ListView<String> wordBankListView;
    @FXML
    private TextField questionName;
    @FXML
    private Button removeWordBtn;
    @FXML
    private Button addWordBtn;

    private final SelectionManager selectedWordManager = new SelectionManager();

    public static final String NON_ALPHANUMERIC = "[^a-zA-Z0-9-'_]";
    public static final String ALPHANUMERIC = "[a-zA-Z0-9-'_]+";

    /**
     * This method initializes the editor.
     */
    public void initialize() {
        StageManager.setTitle("Fill The Blank Editor");

        questionTextArea.setOnMouseClicked(event -> {
            if (!removingWord && !addingWord) return;

            double mouseX = event.getX();
            double mouseY = event.getY();
            int caretPosition = questionTextArea.hit(mouseX, mouseY).getInsertionIndex();
            String word = getWordAtCaret(questionTextArea.getText(), caretPosition, NON_ALPHANUMERIC);

            int wordStartIndex = CaretUtilities.getBeginningIndex(questionTextArea.getText(), caretPosition, NON_ALPHANUMERIC);
            int wordEndIndex = wordStartIndex + word.length();

            if (removingWord && selectedWordManager.containsWord(word)) {
                questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.DEFAULT.getStyle());
                wordBankListView.getItems().remove(word);
                selectedWordManager.removeWord(word);
                if (!multiSelectCheckBox.isSelected()) toggleRemoveWord();
            } else if (addingWord && !selectedWordManager.containsWord(word) && !word.trim().equalsIgnoreCase("")) {
                wordBankListView.getItems().add(word);
                questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.ANSWER.getStyle());
                selectedWordManager.addSelectedWord(word, wordStartIndex, wordEndIndex, SelectionManager.STYLE.ANSWER);
                if (!multiSelectCheckBox.isSelected()) toggleAddWord();
            }
        });

        questionTextArea.setOnMouseMoved(event -> {
            if (removingWord || addingWord) {
                //This is required to keep the caretPosition accurate when the mouse is hovering on over the last line of text
                int originalLength = questionTextArea.getText().length();
                questionTextArea.appendText("\n\n");
                int caretPosition = questionTextArea.hit(event.getX(), event.getY()).getInsertionIndex();
                questionTextArea.replaceText(originalLength, questionTextArea.getText().length(), "");

                String word = getWordAtCaret(questionTextArea.getText(), caretPosition, NON_ALPHANUMERIC);
                int wordStartIndex = CaretUtilities.getBeginningIndex(questionTextArea.getText(), caretPosition, NON_ALPHANUMERIC);
                int wordEndIndex = wordStartIndex + word.length();

                boolean isNewWord = prevWordIndex != wordStartIndex;
                boolean isAnswer = selectedWordManager.isAnswer(wordStartIndex);

                if (wordStartIndex < 0 || wordStartIndex > questionTextArea.getLength()) return;
                else if (isNewWord && !isAnswer && !removingWord) {
                    questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.HOVER.getStyle());
                } else if (isNewWord && removingWord && isAnswer) {
                    questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.REMOVAL.getStyle());
                }

                boolean prevBadIndex = prevWordIndex < 0 || prevWordIndex > questionTextArea.getLength();
                if (selectedWordManager.isAnswer(prevWordIndex) && isNewWord && !prevBadIndex)
                    questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.ANSWER.getStyle());
                else if (isNewWord && !prevBadIndex) {
                    questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.DEFAULT.getStyle());
                }

                prevWordIndex = wordStartIndex;
                prevWord = word;
            }
        });

        questionTextArea.setOnMouseExited(_ -> {
            if (prevWordIndex < 0 || prevWordIndex > questionTextArea.getLength()) return;
            if (selectedWordManager.isAnswer(prevWordIndex)) {
                questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.ANSWER.getStyle());
            } else {
                questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.DEFAULT.getStyle());
            }

            prevWordIndex = -1;
            prevWord = "";
        });

        questionTextArea.setOnKeyTyped(_ -> {
            updateAnswerLists();
            if (questionTextArea.getCaretPosition() > 0) {
                String character = questionTextArea.getText().substring(questionTextArea.getCaretPosition() - 1, questionTextArea.getCaretPosition());
                if (character.matches(NON_ALPHANUMERIC)) {
                    removeStyle(questionTextArea.getCaretPosition() - 1, questionTextArea.getCaretPosition());
                }
            }
        });

        questionTextArea.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown() && event.getCode().toString().equalsIgnoreCase("V")) {
                event.consume();

                String clipboard = Clipboard.getSystemClipboard().getString();
                if(clipboard == null || clipboard.isEmpty()) return;

                int caretPosition = questionTextArea.getCaretPosition();
                Pattern pattern = Pattern.compile(ALPHANUMERIC);
                boolean wordFound = pattern.matcher(clipboard).find();

                if (questionTextArea.getSelectedText().isEmpty()) {
                    int wordStartIndex = CaretUtilities.getBeginningIndex(questionTextArea.getText(), caretPosition, NON_ALPHANUMERIC);
                    boolean isAnswer = selectedWordManager.isAnswer(wordStartIndex);
                    if(isAnswer && wordFound)
                        questionTextArea.insert(caretPosition, clipboard, SelectionManager.STYLE.ANSWER.getStyle());
                    else
                        questionTextArea.insertText(caretPosition, clipboard);
                }else {
                    int selectionStart = questionTextArea.getSelection().getStart();
                    int selectionEnd = questionTextArea.getSelection().getEnd();
                    boolean startInAnswer = selectedWordManager.isBetweenIndexes(selectionStart);
                    boolean endInAnswer = selectedWordManager.isBetweenIndexes(selectionEnd);

                    if(startInAnswer && endInAnswer)
                        questionTextArea.replace(selectionStart, selectionEnd, clipboard, SelectionManager.STYLE.ANSWER.getStyle());
                    else if(startInAnswer){
                        questionTextArea.replace(selectionStart, selectionEnd, clipboard, SelectionManager.STYLE.DEFAULT.getStyle());
                        int newWordStartIndex = CaretUtilities.getBeginningIndex(questionTextArea.getText(), selectionStart, NON_ALPHANUMERIC);
                        int newWordEndIndex = newWordStartIndex + getWordAtCaret(questionTextArea.getText(), newWordStartIndex, NON_ALPHANUMERIC).length();
                        questionTextArea.setStyle(newWordStartIndex, newWordEndIndex, SelectionManager.STYLE.ANSWER.getStyle());
                    }else if(endInAnswer) {
                        questionTextArea.replace(selectionStart, selectionEnd, clipboard, SelectionManager.STYLE.DEFAULT.getStyle());
                        int newWordStartIndex = CaretUtilities.getBeginningIndex(questionTextArea.getText(),
                                selectionStart + clipboard.length(), NON_ALPHANUMERIC);
                        int wordLength = getWordAtCaret(questionTextArea.getText(), newWordStartIndex, NON_ALPHANUMERIC).length();
                        int newWordEndIndex = newWordStartIndex + wordLength;
                        questionTextArea.setStyle(newWordStartIndex, newWordEndIndex, SelectionManager.STYLE.ANSWER.getStyle());
                    }else
                        questionTextArea.replaceText(selectionStart, selectionEnd, clipboard);
                }
                updateAnswerLists();
            }
        });

        questionName.textProperty().addListener((_, _, _) ->
                question.setName(questionName.getText()));

        questionTextArea.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        questionTextArea.prefWidthProperty().bind(questionScrollPane.widthProperty());
        questionTextArea.prefHeightProperty().bind(questionScrollPane.heightProperty());
        questionTextArea.setWrapText(true);

        Platform.runLater(wordBankListView::refresh);
    }

    /**
     * This method updates the word bank and answer offsets list based on the current text in the questionTextArea.
     */
    private void updateAnswerLists() {
        wordBankListView.getItems().clear();
        selectedWordManager.clearList();

        Pattern pattern = Pattern.compile(ALPHANUMERIC);
        Matcher matcher = pattern.matcher(questionTextArea.getText());
        while (matcher.find()) {
            if (hasStyle(questionTextArea, matcher.start(), matcher.end(), SelectionManager.STYLE.ANSWER.getStyle())) {
                String word = questionTextArea.getText().substring(matcher.start(), matcher.end());
                selectedWordManager.addSelectedWord(word, matcher.start(), matcher.end(), SelectionManager.STYLE.ANSWER);
                questionTextArea.setStyle(matcher.start(), matcher.end(), SelectionManager.STYLE.ANSWER.getStyle());
            }
        }
        selectedWordManager.sortList();
        wordBankListView.getItems().addAll(selectedWordManager.getAnswers());
    }


    /**
     * This method checks if the text in the given text area has the given style from the start index to the end index.
     *
     * @param textArea The text area to check.
     * @param start    The start index.
     * @param end      The end index.
     * @param style    The style to check for.
     * @return True if the text has the given style from the start index to the end index, false otherwise.
     */
    public boolean hasStyle(InlineCssTextArea textArea, int start, int end, String style) {
        for (int i = start; i < end; i++) {
            String currentStyle = textArea.getStyleOfChar(i);
            if (!currentStyle.equals(style)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method toggles the removeWord word mode.
     */
    @FXML
    public void toggleRemoveWord() {
        Platform.runLater(() -> {
            removingWord = !removingWord;
            questionTextArea.setEditable(!removingWord);
            removeWordBtn.setText(removingWord ? "Cancel Remove" : "Remove Word");
            if (addingWord && removingWord) toggleAddWord();
        });
    }

    /**
     * This method toggles the addWord word mode.
     */
    @FXML
    public void toggleAddWord() {
        Platform.runLater(() -> {
            addingWord = !addingWord;
            questionTextArea.setEditable(!addingWord);
            addWordBtn.setText(addingWord ? "Cancel Add" : "Add Word");
            if (removingWord && addingWord) toggleRemoveWord();
        });
    }

    /**
     * This method removes the style from the text area from the start index to the end index.
     *
     * @param startIndex The start index.
     * @param endIndex   The end index.
     */
    private void removeStyle(int startIndex, int endIndex) {
        questionTextArea.setStyle(startIndex, endIndex, SelectionManager.STYLE.DEFAULT.getStyle());
    }


    /**
     * This method sets up the question editor with the given question.
     *
     * @param question The question to set up the editor with.
     */
    @Override
    public void setupQuestion(FillTheBlank question) {
        this.question = question;
        wordBankListView.setItems(question.getWordBankCopy());
        questionTextArea.replaceText(question.getFillQuestion().replace("/\n/g", ",").replace("\r", ""));
        questionName.setText(question.getName());
        displayAnswersCheckBox.setSelected(question.hintsDisplayed());

        for (int x = 0; x < question.getWordBankCopy().size(); x++) {
            String word = question.getAnswer(x);
            int startIndex = question.getWordIndex(x);
            int endIndex = startIndex + word.length();

            selectedWordManager.addSelectedWord(word, startIndex, endIndex, SelectionManager.STYLE.ANSWER);
            questionTextArea.setStyle(startIndex, endIndex, SelectionManager.STYLE.ANSWER.getStyle());
        }
        updateAnswerLists();
    }

    /**
     * This method updates the question with the current values in the editor.
     */
    @Override
    public void updateQuestion() {
        question.setAnswerList(selectedWordManager.getAnswerList());
        question.setFillTheBlankQuestion(questionTextArea.getText());
        question.setName(questionName.getText());
        question.setDisplayAnswers(displayAnswersCheckBox.isSelected());
    }
}
