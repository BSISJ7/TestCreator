package TestCreator.questions.editorPanels;

import TestCreator.questions.FillTheBlank;
import TestCreator.utilities.DefaultFillHighlighter;
import TestCreator.utilities.FillTextPane;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.WordAtCaretFinder;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import static TestCreator.questions.FillTheBlank.*;
import static javax.swing.text.Highlighter.Highlight;
import static javax.swing.text.Highlighter.HighlightPainter;

public class FillTheBlankEditor extends QuestionEditor<FillTheBlank> {
    @FXML
    ScrollPane questionScrollPane;
    private final FillTextPane questionTextPane = new FillTextPane(this);
    private DefaultFillHighlighter questionHighlighter;
    private int prevWordIndex = -1;
    private String prevWord;
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

    private final ArrayList<Integer> answerOffsets = new ArrayList<>();

    public void initialize() {
        StageManager.setTitle("Fill The Blank Editor");

        questionHighlighter = questionTextPane.getHighlighter();

        questionTextPane.setVisible(true);
        questionTextPane.setPreferredSize(new Dimension(300, 400));

        questionTextPane.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {}
            public void ancestorRemoved(AncestorEvent event) {}
            public void ancestorMoved(AncestorEvent event) {
                SwingUtilities.invokeLater(questionTextPane::repaint);
            }
        });

        questionTextPane.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                int caretPosition = questionTextPane.viewToModel(e.getPoint());
                String hoveredWord = WordAtCaretFinder.getWordAtCaret(questionTextPane.getText(), caretPosition).replaceAll("[.!?]+(?=[\\s]|$)", "");

                if (!hoveredWord.trim().equalsIgnoreCase("") && addingWord || removingWord) {
                    int wordLocation = WordAtCaretFinder.getPositionStart(questionTextPane.getText(), caretPosition);

                    if (addingWord && !mouseHeld && prevWordIndex != wordLocation) {
                        removeHighlightAt(prevWordIndex);

                        if (!isHighlighted(wordLocation)) {
                            addHighlight(wordLocation, wordLocation + hoveredWord.length(), NEW_WORD_PAINT);
                        }
                    } else if (addingWord && mouseHeld && prevWordIndex != wordLocation) {
                        if (wordLocation < prevWordIndex) {
                            int prevEndOffset = prevWordIndex + prevWord.length();
                            addHighlight(wordLocation, prevEndOffset, NEW_WORD_PAINT);
                        }

                    } else if (removingWord && prevWordIndex != wordLocation) {
                        removeHighlightAt(prevWordIndex);
                        if (isHighlighted(wordLocation) && isInWordBank(hoveredWord)) {
                            removeHighlightAt(prevWordIndex);
                            removeHighlightAt(wordLocation);
                            addHighlight(wordLocation, wordLocation + hoveredWord.length(), DELETE_WORD_PAINT);
                        }
                    }

                    if (isInWordBank(prevWord) && !isHighlighted(prevWordIndex) && answerOffsets.contains(prevWordIndex)) {
                        addHighlight(prevWordIndex, prevWordIndex + prevWord.length(), SELECTED_WORD_PAINT);
                    }
                    prevWordIndex = wordLocation;
                    prevWord = hoveredWord;
                }
            }
        });

        questionTextPane.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                Platform.runLater(() -> {
                    int caretPosition = questionTextPane.viewToModel2D(e.getPoint());
                    String hoveredWord = WordAtCaretFinder.getWordAtCaret(questionTextPane.getText(), caretPosition).replaceAll("[.!?]+(?=[\\s]|$)", "");
                    int wordLocation = WordAtCaretFinder.getPositionStart(questionTextPane.getText(), caretPosition);

                    if (removingWord && isInWordBank(hoveredWord)) {
                        removeHighlightAt(wordLocation);
                        updateWordBank();
                        toggleRemoveWord();
                    } else if (addingWord && !isInWordBank(hoveredWord) && !hoveredWord.trim().equalsIgnoreCase("")) {
                        removeHighlightAt(wordLocation);
                        addHighlight(wordLocation, wordLocation + hoveredWord.length(), SELECTED_WORD_PAINT);
                        updateWordBank();
                        toggleAddWord();
                    }
                });
            }

            public void mousePressed(MouseEvent e) {
                mouseHeld = true;
            }

            public void mouseReleased(MouseEvent e) {
                mouseHeld = false;
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
                if (addingWord && !answerOffsets.contains(prevWordIndex))
                    removeHighlightAt(prevWordIndex);
                else if (removingWord && answerOffsets.contains(prevWordIndex)) {
                    removeHighlightAt(prevWordIndex);
                    addHighlight(prevWordIndex, prevWordIndex + prevWord.length(), SELECTED_WORD_PAINT);
                }
                prevWordIndex = -1;
                prevWord = "";
            }
        });

        questionName.textProperty().addListener((_, _, _) ->
                question.setName(questionName.getText()));

        SwingNode swNode = new SwingNode();
        swNode.setContent(questionTextPane);
        questionScrollPane.setContent(swNode);
    }

    public void updateWordBank() {
        Platform.runLater(() -> {

            wordBankListView.getItems().clear();
            answerOffsets.clear();

            for (Highlight highlight : questionHighlighter.getHighlights()) {
                if (highlight.getEndOffset() <= questionTextPane.getText().length()) {
                    if(highlight.getPainter() == SELECTED_WORD_PAINT){
                        String highlightedWord = questionTextPane.getText().substring(highlight.getStartOffset(), highlight.getEndOffset());
                        if (!highlightedWord.trim().equalsIgnoreCase("")) {
                            answerOffsets.add(highlight.getStartOffset());
                            wordBankListView.getItems().add(highlightedWord);
                        }
                    }
                }
            }
            wordBankListView.refresh();
        });
    }

    private void removeHighlightAt(int wordLocation) {
        for (Highlight highlight : questionHighlighter.getHighlights()) {
            if (wordLocation == highlight.getStartOffset()) {
                questionHighlighter.removeHighlight(highlight);
            }
        }
    }

    private boolean insideHighlight(int startOffset, int endOffset) {
        return !questionHighlighter.getHighlightList().stream()
                .noneMatch(highlight -> !(endOffset < highlight.getStartOffset() || highlight.getEndOffset() < startOffset));
    }

    private void addHighlight(int startOffset, int endOffset, HighlightPainter painter) {
        try {
            questionHighlighter.addHighlight(startOffset, endOffset, painter);
        } catch (BadLocationException | NullPointerException e) {
            e.printStackTrace();
        }
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
        Platform.runLater(() ->{
            if (removingWord) {
                questionTextPane.setEnabled(true);
                removeWordBtn.setText("Remove Word");
                removingWord = false;
            } else {
                questionTextPane.setEnabled(false);
                questionTextPane.setSelectionStart(-1);
                questionTextPane.setSelectionEnd(-1);
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
                questionTextPane.setEnabled(true);
                addWordBtn.setText("Add Word");
                addingWord = false;
    //            removeHighlightAt(prevWordIndex);
            } else {
                questionTextPane.setEnabled(false);
                questionTextPane.setSelectionStart(-1);
                questionTextPane.setSelectionEnd(-1);
                addWordBtn.setText("Cancel Add");
                addingWord = true;
                if (removingWord)
                    toggleRemoveWord();
            }
        });
    }

    @Override
    public void setupQuestion(FillTheBlank question) {
        this.question = question;
        wordBankListView.setItems(question.getWordBankCopy());
        questionTextPane.setText(question.getFillQuestion().replace("/\n/g", ",").replace("\r", ""));
        answerOffsets.addAll(question.getAnswerOffsets());
        questionName.setText(question.getName());
        setupHighlights();
    }

    @Override
    public void updateQuestion() {
        question.setWordBank(wordBankListView.getItems());
        question.setFillTheBlankQuestion(questionTextPane.getText());
        question.setName(questionName.getText());
        question.setWordIndicies(answerOffsets);
    }

    private void setupHighlights() {
        questionHighlighter.removeAllHighlights();
        for (int x = 0; x < wordBankListView.getItems().size(); x++) {
            int startPos = answerOffsets.get(x);
            int endPos = startPos + wordBankListView.getItems().get(x).length();
            addHighlight(startPos, endPos, SELECTED_WORD_PAINT);
        }
    }
}
