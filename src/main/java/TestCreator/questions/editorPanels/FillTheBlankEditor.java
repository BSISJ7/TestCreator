package TestCreator.questions.editorPanels;

import TestCreator.questions.FillTheBlank;
import TestCreator.utilities.SelectionManager;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.fxmisc.richtext.InlineCssTextArea;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static TestCreator.utilities.WordAtCaretFinder.getPositionStart;
import static TestCreator.utilities.WordAtCaretFinder.getWordAtCaret;


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
    ScrollPane questionScrollPane;
    private int prevWordIndex = -1;
    private String prevWord = "";
    private boolean addingWord = false;
    private boolean removingWord = false;
    private boolean mouseHeld = false;
    @FXML
    private ListView<String> wordBankListView;
    @FXML
    private TextField questionName;
    @FXML
    private Button removeWordBtn;
    @FXML
    private Button addWordBtn;

    private final ArrayList<Integer> answerOffsetsList = new ArrayList<>();

    private SelectionManager selectedWordManager = new SelectionManager();

    public static final String NO_ALPHANUM = "[^a-zA-Z0-9-'_]";
    public static final String SEARCH_REGEX = "[a-zA-Z0-9-'_]";

    public void initialize() {
        StageManager.setTitle("Fill The Blank Editor");

        questionTextArea.setOnMouseClicked(event -> {
            if(!removingWord && !addingWord) return;

            double mouseX = event.getX();
            double mouseY = event.getY();
            int caretPosition = questionTextArea.hit(mouseX, mouseY).getInsertionIndex();
            String word = getWordAtCaret(questionTextArea.getText(), caretPosition).replaceAll("[.!?]+(?=\\s|$)", "");;
            int wordStartIndex = getPositionStart(questionTextArea.getText(), caretPosition);
            int wordEndIndex = wordStartIndex + word.length();

            if(removingWord && selectedWordManager.containsWord(word, wordStartIndex, wordEndIndex)) {
                questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.DEFAULT.getStyle());
                wordBankListView.getItems().remove(word);

                selectedWordManager.removeWord(word);
                toggleRemoveWord();
            } else if(addingWord && !selectedWordManager.containsWord(word, wordStartIndex, wordEndIndex) && !word.trim().equalsIgnoreCase("")) {
                wordBankListView.getItems().add(word);
                questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.ANSWER.getStyle());
                selectedWordManager.addSelectedWord(word, wordStartIndex, wordEndIndex, SelectionManager.STYLE.ANSWER);
                toggleAddWord();
            }
        });

        questionTextArea.setOnMouseMoved(event -> {
            if(removingWord || addingWord) {
                //This is required to keep the caretPosition accurate when the mouse is hovering on over the last line of text
                questionTextArea.appendText("\n\n");

                int caretPosition = questionTextArea.hit(event.getX(), event.getY()).getInsertionIndex();
                String word = getWordAtCaret(questionTextArea.getText(), caretPosition).replaceAll("[.!?]+(?=\\s|$)", "");
                int wordStartIndex = getPositionStart(questionTextArea.getText(), caretPosition);
                int wordEndIndex = wordStartIndex + word.length();

                boolean isNewWord = prevWordIndex != wordStartIndex;
                boolean isAnswer = selectedWordManager.isAnswer(wordStartIndex);
                boolean badIndex = wordStartIndex < 0 || wordStartIndex > questionTextArea.getLength();

                if(badIndex) return;
                else if (isNewWord && !isAnswer){
                    questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.HOVER.getStyle());
                }else if(isNewWord && removingWord) {
                    questionTextArea.setStyle(wordStartIndex, wordEndIndex, SelectionManager.STYLE.REMOVAL.getStyle());
                }

                boolean prevIsAnswer = selectedWordManager.isAnswer(prevWordIndex);
                boolean prevBadIndex = prevWordIndex < 0 || prevWordIndex > questionTextArea.getLength();
                if(prevIsAnswer && isNewWord && !prevBadIndex)
                    questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.ANSWER.getStyle());
                else if (isNewWord && !prevBadIndex) {
                    questionTextArea.setStyle(prevWordIndex, prevWordIndex + prevWord.length(), SelectionManager.STYLE.DEFAULT.getStyle());
                }

                prevWordIndex = wordStartIndex;
                prevWord = word;

                //This is required to keep the caretPosition accurate when the mouse is hovering on over the last line of text
                int finalNewLineIndex = questionTextArea.getText().lastIndexOf("\n\n");
                questionTextArea.replaceText(finalNewLineIndex, questionTextArea.getText().length(),"");
            }
        });

        questionTextArea.setOnKeyTyped(_ -> {
            wordBankListView.getItems().clear();
            answerOffsetsList.clear();
            selectedWordManager.clearList();

            Pattern pattern = Pattern.compile("\\w+");
            Matcher matcher = pattern.matcher(questionTextArea.getText());
            while (matcher.find()) {
                System.out.println("start: " + matcher.start() + " end: " + matcher.end());
                System.out.println("word: "+ questionTextArea.getText().substring(matcher.start(), matcher.end()));
                if (hasStyle(questionTextArea, matcher.start(), matcher.end())) {
                    String word = questionTextArea.getText().substring(matcher.start(), matcher.end());
                    wordBankListView.getItems().add(word);
                    answerOffsetsList.add(matcher.start());
                    selectedWordManager.addSelectedWord(word, matcher.start(), matcher.end(), SelectionManager.STYLE.ANSWER);
                }
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


    public boolean hasStyle(InlineCssTextArea textArea, int start, int end) {
        String style = SelectionManager.STYLE.ANSWER.getStyle();
        for (int i = start; i < end; i++) {
            String currentStyle = textArea.getStyleOfChar(i);
            if (!currentStyle.equals(style)) {
                return false;
            }
        }
    return true;
}



    @FXML
    public void toggleRemoveWord() {
        Platform.runLater(() ->{
            if (removingWord) {
                questionTextArea.setEditable(true);
                removeWordBtn.setText("Remove Word");
                removingWord = false;
            } else {
                questionTextArea.setEditable(false);
                questionTextArea.deselect();
                removeWordBtn.setText("Cancel Remove");
                removingWord = true;
                if (addingWord)
                    toggleAddWord();
            }
        });
    }

    @FXML
    public void toggleAddWord() {
        Platform.runLater(() -> {
            if (addingWord) {
                questionTextArea.setEditable(true);
                addWordBtn.setText("Add Word");
                addingWord = false;
            } else {
                questionTextArea.setEditable(false);
                questionTextArea.deselect();
                addWordBtn.setText("Cancel Add");
                addingWord = true;
                if (removingWord)
                    toggleRemoveWord();
            }
        });
    }

    private void replaceSelection(int startIndex, int endIndex, SelectionManager.STYLE style){
        if(selectedWordManager.isAnswer(startIndex)) return;
        questionTextArea.setStyle(startIndex, endIndex, style.getStyle());
    }

    private void removeStyle(int startIndex, int endIndex){
        questionTextArea.setStyle(startIndex, endIndex, SelectionManager.STYLE.DEFAULT.getStyle());
    }

    @Override
    public void setupQuestion(FillTheBlank question) {
        this.question = question;
        wordBankListView.setItems(question.getWordBankCopy());
        System.out.println("wordBank: " + wordBankListView.getItems());
        questionTextArea.replaceText(question.getFillQuestion().replace("/\n/g", ",").replace("\r", ""));
        answerOffsetsList.addAll(question.getAnswerOffsetsCopy());
        questionName.setText(question.getName());
        displayAnswersCheckBox.setSelected(question.hintsDisplayed());
//        questionTextArea.replaceText(DictionaryManager.getDictionary().getRandomWords(10," "));
    }

    @Override
    public void updateQuestion() {
        question.setWordBank(wordBankListView.getItems());
        question.setFillTheBlankQuestion(questionTextArea.getText());
        question.setName(questionName.getText());
        question.setWordIndexes(answerOffsetsList);
        question.setDisplayAnswers(displayAnswersCheckBox.isSelected());
    }
}
