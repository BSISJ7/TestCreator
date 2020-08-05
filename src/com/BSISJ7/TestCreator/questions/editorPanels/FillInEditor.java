package com.BSISJ7.TestCreator.questions.editorPanels;

import com.BSISJ7.TestCreator.questions.FillInTheBlank;
import com.BSISJ7.TestCreator.questions.Question;
import com.BSISJ7.TestCreator.utilities.DefaultFillHighlighter;
import com.BSISJ7.TestCreator.utilities.FillTextPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import static com.BSISJ7.TestCreator.utilities.WordAtCaretFinder.getPositionStart;
import static com.BSISJ7.TestCreator.utilities.WordAtCaretFinder.getWordAtCaret;
import static javax.swing.text.Highlighter.Highlight;
import static javax.swing.text.Highlighter.HighlightPainter;

public class FillInEditor implements EditorPanel {

    private final static HighlightPainter NEW_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(196, 238, 129));
    private final static HighlightPainter SELECTED_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(193, 239, 248));
    private final static HighlightPainter DELETE_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(248, 96, 97));


    private FillTextPane fillTextPane = new FillTextPane(this);

    private DefaultFillHighlighter questionHighlighter;

    private int prevWordLocation = -1;

    private String prevWord;

    private boolean addingWord = false;
    private boolean removingWord = false;
    private boolean mouseHeld = false;

    private FillInTheBlank question;

    @FXML
    private ListView<String> wordBankListView;
    @FXML
    private TextField questionName;
    @FXML
    ScrollPane questionScrollPane;
    @FXML
    private Button removeWordBtn;
    @FXML
    private Button addWordBtn;

    @FXML
    public void initialize() {
        fillTextPane.setVisible(true);
        fillTextPane.setPreferredSize(new Dimension(300, 400));

        questionHighlighter = fillTextPane.getHighlighter();

        fillTextPane.addMouseMotionListener(new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {}

            public void mouseMoved(MouseEvent e) {
                int caretPosition = fillTextPane.viewToModel(e.getPoint());
                String hoveredWord = getWordAtCaret(fillTextPane.getText(), caretPosition).replaceAll("[.!?]+(?=[\\s]|$)", "");

                if (!hoveredWord.trim().equalsIgnoreCase("") && addingWord || removingWord) {
                    int wordLocation = getPositionStart(fillTextPane.getText(), caretPosition);

                    if (addingWord && !mouseHeld && prevWordLocation != wordLocation) {
                        removeHighlightAt(prevWordLocation);

                        if (!isHighlighted(wordLocation)) {
                            addHighlight(wordLocation, wordLocation + hoveredWord.length(), NEW_WORD_PAINT);
                        }
                    }else if(addingWord && mouseHeld && prevWordLocation != wordLocation) {
                        if(wordLocation < prevWordLocation){
                            int prevEndOffset = prevWordLocation + prevWord.length();
                            addHighlight(wordLocation, prevEndOffset, NEW_WORD_PAINT);
                        }

                    } else if (removingWord && prevWordLocation != wordLocation) {
                        removeHighlightAt(prevWordLocation);
                        if (isHighlighted(wordLocation) && isInWordBank(hoveredWord)) {
                            removeHighlightAt(prevWordLocation);
                            removeHighlightAt(wordLocation);
                            addHighlight(wordLocation, wordLocation + hoveredWord.length(), DELETE_WORD_PAINT);
                        }
                    }

                    if (isInWordBank(prevWord) && !isHighlighted(prevWordLocation) && question.getWordPositions().contains(prevWordLocation)) {
                        addHighlight(prevWordLocation, prevWordLocation + prevWord.length(), SELECTED_WORD_PAINT);
                    }
                    prevWordLocation = wordLocation;
                    prevWord = hoveredWord;
                }
            }
        });

        fillTextPane.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int caretPosition = fillTextPane.viewToModel(e.getPoint());
                String hoveredWord = getWordAtCaret(fillTextPane.getText(), caretPosition).replaceAll("[.!?]+(?=[\\s]|$)", "");
                int wordLocation = getPositionStart(fillTextPane.getText(), caretPosition);

                if (removingWord && isInWordBank(hoveredWord)) {
                    removeHighlightAt(wordLocation);
                    toggleRemoveWord();
                    updateWordBank();
                } else if (addingWord && !isInWordBank(hoveredWord) && !hoveredWord.trim().equalsIgnoreCase("")) {
                    removeHighlightAt(wordLocation);
                    toggleAddWord();
                    addHighlight(wordLocation, wordLocation + hoveredWord.length(), SELECTED_WORD_PAINT);
                    updateWordBank();
                }
            }
            public void mousePressed(MouseEvent e) {
                mouseHeld = true;
            }
            public void mouseReleased(MouseEvent e) {
                mouseHeld = false;
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {
                if (addingWord && !question.getWordPositions().contains(prevWordLocation))
                    removeHighlightAt(prevWordLocation);
                else if(removingWord && question.getWordPositions().contains(prevWordLocation)) {
                    removeHighlightAt(prevWordLocation);
                    addHighlight(prevWordLocation, prevWordLocation + prevWord.length(), SELECTED_WORD_PAINT);
                }
                prevWordLocation = -1;
                prevWord = "";
            }
        });

        questionName.textProperty().addListener((observable, oldValue, newValue) ->
                question.setName(questionName.getText()));

        SwingNode swNode = new SwingNode();
        swNode.setContent(fillTextPane);
        questionScrollPane.setContent(swNode);
    }

    public void updateWordBank() {
        ArrayList<String> wordBank = new ArrayList<>();
        ArrayList<Integer> wordLocations = new ArrayList<>();

        int selectionStart = fillTextPane.getSelectionStart();
        int selectionEnd = fillTextPane.getSelectionEnd();
        fillTextPane.select(-1, -1);

        String word;
        int textLength =  fillTextPane.getText().length();
        for (Highlight highlightedWords : questionHighlighter.getHighlights()) {
            if(highlightedWords.getEndOffset() <= textLength){
                word = fillTextPane.getText().substring(highlightedWords.getStartOffset(), highlightedWords.getEndOffset());
                if (!word.trim().equalsIgnoreCase("")) {
                    wordLocations.add(highlightedWords.getStartOffset());
                    wordBank.add(word);
                }
            }
        }

        question.setWordBank(wordBank);
        question.setWordPositions(wordLocations);
        question.setFillInQuestion(fillTextPane.getText());

        Platform.runLater(() -> wordBankListView.setItems(FXCollections.observableArrayList(wordBank)));
        wordBankListView.refresh();
        fillTextPane.select(selectionStart, selectionEnd);
    }

    private void removeHighlightAt(int wordLocation) {
        for (Highlight highlight : questionHighlighter.getHighlights()) {
            if (wordLocation == highlight.getStartOffset()) {
                questionHighlighter.removeHighlight(highlight);
            }
        }
    }

    private boolean insideHighlight(int startOffset, int endOffset){
        return !questionHighlighter.getHighlightList().stream()
                .noneMatch(highlight -> !(endOffset < highlight.getStartOffset() || highlight.getEndOffset() < startOffset));
    }

    private void addHighlight(int startOffset, int endOffset, HighlightPainter painter){
        try {
            questionHighlighter.addHighlight(startOffset, endOffset, painter);
        }catch (BadLocationException e){e.printStackTrace();}
    }

    private boolean isHighlighted(int wordLocation) {
        for (Highlight highlight : questionHighlighter.getHighlights()) {
            if (wordLocation == highlight.getStartOffset()) {
                return true;
            }
        }
        return false;
    }

    private boolean isInWordBank(String checkWord) {
        for (String word : wordBankListView.getItems()) {
            if (word.equals(checkWord))
                return true;
        }
        return false;
    }

    @FXML
    public void toggleRemoveWord() {
        if(removingWord) {
            fillTextPane.setEnabled(true);
            Platform.runLater(() -> removeWordBtn.setText("Remove Word"));
            removingWord = false;
        }
        else{
            fillTextPane.setEnabled(false);
            fillTextPane.setSelectionStart(-1);
            fillTextPane.setSelectionEnd(-1);
            removeWordBtn.setText("Cancel Remove");
            removingWord = true;
            if(addingWord)
                toggleAddWord();
        }
    }

    @FXML
    public void toggleAddWord() {
        if(addingWord) {
            fillTextPane.setEnabled(true);
            Platform.runLater(() -> addWordBtn.setText("Add Word"));
            addingWord = false;
            removeHighlightAt(prevWordLocation);
        }
        else{
            fillTextPane.setEnabled(false);
            fillTextPane.setSelectionStart(-1);
            fillTextPane.setSelectionEnd(-1);
            addWordBtn.setText("Cancel Add");
            addingWord = true;
            if(removingWord)
                toggleRemoveWord();
        }
    }

    @Override
    public void setupQuestion(Question question) {
        this.question = (FillInTheBlank)question.getCopy();
        wordBankListView.setItems(FXCollections.observableArrayList(this.question.getWordBank()));
        fillTextPane.setText(this.question.getFillQuestion().replace("/\n/g", ",").replace("\r", ""));
        setupHighlights();
        questionName.setText(this.question.getName());
    }

    @Override
    public FillInTheBlank getQuestion() {
        return question;
    }

    private void setupHighlights(){
        questionHighlighter.removeAllHighlights();
        for (int x = 0; x < question.getWordBank().size(); x++) {
            int startPos = question.getWordPositions().get(x);
            int endPos = startPos + question.getWordBank().get(x).length();
            addHighlight(startPos, endPos, SELECTED_WORD_PAINT);
        }
    }
}
