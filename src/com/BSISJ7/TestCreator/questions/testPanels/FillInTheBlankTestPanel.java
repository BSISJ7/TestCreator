package com.BSISJ7.TestCreator.questions.testPanels;

import com.BSISJ7.TestCreator.questions.FillInTheBlank;
import com.BSISJ7.TestCreator.utilities.FillTextPane;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.List;
import java.util.*;

import static com.BSISJ7.TestCreator.questions.FillInTheBlank.*;
import static com.BSISJ7.TestCreator.utilities.WordAtCaretFinder.getWordAtCaret;
import static java.lang.System.out;


public class FillInTheBlankTestPanel implements TestPanel<FillInTheBlank> {

    @FXML
    private BorderPane mainWindow;
    @FXML
    public ListView<String> wordBankListView;
    @FXML
    private URL location;
    @FXML
    private HBox questionContainer;
    @FXML
    private RadioButton dispCorrectBtn;
    @FXML
    private RadioButton dispAnswersBtn;

    private final FillTextPane questionTextArea = new FillTextPane();

    private FillInTheBlank question;

    private StyledDocument doc = questionTextArea.getStyledDocument();


    private List<String> wordBank;
    private List<Integer> wordOffsets;
    private List<Integer> origOffsetsList;

    private String previousHover = "";
    private String emptySpace = "";

    private int prevPosition = -1;
    private int caretPosition = -1;

    private boolean isShowingAnswer = false;
    private boolean showingCorrectAnswers = false;
    private boolean keyPressed;
    private boolean isTestGraded = false;


    public void initialize() {
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(questionTextArea);
        questionContainer.getChildren().addAll(swingNode);
        Platform.runLater(swingNode::requestFocus);

        dispCorrectBtn.setStyle("-fx-font-size: 15; -fx-padding: 10");
        dispAnswersBtn.setStyle("-fx-font-size: 15; -fx-padding: 10");

        questionTextArea.setPreferredSize(new Dimension(800, 500));
        questionTextArea.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent event) {
                event.consume();
                if(!isTestGraded) {
                    int startOffset = questionTextArea.getCurrentHighlightOffset();
                    int endOffset = startOffset + emptySpace.length();

                    int wordPosition = startOffset;
                    for (int x = startOffset; x < endOffset; x++) {
                        if (questionTextArea.getText().charAt(x) == ' ')
                            break;
                        wordPosition++;
                    }

                    if (event.getKeyChar() >= ' ' && event.getKeyChar() <= '~' && wordPosition < endOffset) {
                        try {
                            doc.insertString(wordPosition + 1, Character.toString(event.getKeyChar()), SELECTED_WORD);
                            doc.remove(wordPosition, 1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    } else if (event.getKeyChar() == KeyEvent.VK_BACK_SPACE && wordPosition > startOffset) {
                        try {
                            doc.insertString(wordPosition - 1, " ", SELECTED_WORD);
                            doc.remove(wordPosition, 1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    } else if (wordPosition < endOffset && event.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
                        try {
                            doc.insertString(wordPosition + 1, Character.toString(event.getKeyChar()), SELECTED_WORD);
                            doc.remove(wordPosition, 1);
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    }

                    setCaretAtWordEnd(startOffset);
                    questionTextArea.setToolTipText("This is a test tooltip");
                }

            }
            @Override
            public void keyPressed(KeyEvent event) {
                event.consume();
                selectWord(event.getKeyCode());
            }
            public void keyReleased(KeyEvent event) {
                event.consume();
                if(!isTestGraded) {
                    keyPressed = false;
                }
            }
        });

        questionTextArea.addMouseMotionListener(new MouseMotionListener() {
            private int lastPos;

            public void mouseDragged(MouseEvent e) {}
            public void mouseMoved(MouseEvent e) {
                if (!showingCorrectAnswers) {
                    caretPosition = questionTextArea.viewToModel2D(e.getPoint());
                    int startOffset = getNearestStartOffset(caretPosition);
                    int endOffset = startOffset + emptySpace.length();
                    boolean isCaretBetweenOffsets = startOffset <= caretPosition && caretPosition < endOffset;

                    if (lastPos != caretPosition) {
                        lastPos = caretPosition;
                    }

                    //Change cursor to a hand if it is hovering an answer blank.
                    if (isCaretBetweenOffsets && !isTestGraded) {
                        mainWindow.setCursor(javafx.scene.Cursor.HAND);
                        questionTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else if (!isTestGraded) {
                        mainWindow.setCursor(javafx.scene.Cursor.DEFAULT);
                        questionTextArea.setCursor(Cursor.getDefaultCursor());
                    }

                    if (isTestGraded && isCaretBetweenOffsets && !isShowingAnswer && !origOffsetsList.isEmpty()
                        && wordOffsets.contains(startOffset)) {
                        try {
//                            out.println(STR. """
//                            CaretPos: \{ caretPosition }
//                            Start Offset: \{ startOffset }
//                            End Offset: \{ endOffset }
//                            """ );

                            String hoveredWord = getWordAtCaret(questionTextArea.getText(), startOffset);
                            String correctWord = getWordAtCaret(question.getFillQuestion().replace("\r",
                                    ""), origOffsetsList.get(wordOffsets.indexOf(startOffset)));

                            if (!hoveredWord.equalsIgnoreCase(correctWord)) {
                                //Fill in remaining space with ' '
                                String blankSpaces = " ".repeat(emptySpace.length() - correctWord.length());

                                doc.remove(startOffset, emptySpace.length());
                                doc.insertString(startOffset, correctWord + blankSpaces, INCORRECT_WORD);
                                previousHover = hoveredWord;
                                prevPosition = startOffset;
                                isShowingAnswer = true;
                            }
                        } catch(BadLocationException | NullPointerException exception){
                            exception.printStackTrace();
                        }
                    } else if (isTestGraded && (caretPosition < startOffset || endOffset < caretPosition) && isShowingAnswer) {
                        String blankSpaces = " ".repeat(emptySpace.length() - previousHover.length());

                        try {
                            doc.remove(prevPosition, emptySpace.length());
                            doc.insertString(prevPosition, previousHover + blankSpaces, INCORRECT_WORD);
                            previousHover = "";
                            isShowingAnswer = false;
                            prevPosition = -1;
                        } catch (BadLocationException | NullPointerException exception) {
                            exception.printStackTrace();
                        }
                    }
                }
            }
        });

        questionTextArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(!showingCorrectAnswers) {
                    int caretPosition = questionTextArea.viewToModel2D(e.getPoint());
                    int nearestStartOffset = getNearestStartOffset(caretPosition);
                    int endOffset = nearestStartOffset + emptySpace.length();
                    if (nearestStartOffset <= caretPosition && caretPosition < endOffset && !keyPressed && !isTestGraded) {
                        questionTextArea.replaceLastHighlight(UNSELECTED_WORD);
                        questionTextArea.setHighlight(nearestStartOffset, emptySpace.length(), SELECTED_WORD, true);
                        Platform.runLater(swingNode::requestFocus);
                        setCaretAtWordEnd(nearestStartOffset);
                    }
                }
            }
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {}
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        });

        questionTextArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                questionTextArea.repaint();
                out.println("Component shown");
            }
        });

        wordBankListView.setOnMouseClicked(event -> {
            if(!isTestGraded && !showingCorrectAnswers && wordBankListView.getSelectionModel().getSelectedIndex() != -1) {
                try {
                    String selectedWord = wordBankListView.getSelectionModel().getSelectedItem();
                    int startOffset = questionTextArea.getCurrentHighlightOffset();
                    String blankSpaces = " ".repeat(emptySpace.length() - selectedWord.length());

                    doc.remove(startOffset, emptySpace.length());
                    doc.insertString(startOffset, selectedWord + blankSpaces, SELECTED_WORD);
                    wordBankListView.getSelectionModel().clearSelection();
                    Platform.runLater(swingNode::requestFocus);
                    questionTextArea.setCaretPosition(startOffset + selectedWord.length());
                } catch (BadLocationException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        wordBankListView.setOnKeyPressed(event -> {
            if(!showingCorrectAnswers) {
                if (event.getCode().equals(KeyCode.LEFT))
                    selectWord(KeyEvent.VK_LEFT);
                else if (event.getCode().equals(KeyCode.RIGHT))
                    selectWord(KeyEvent.VK_RIGHT);
            }
        });


//        https://stackoverflow.com/questions/9486631/disable-double-click-selection-in-a-jtextcomponent
//        StanislavL
        DefaultCaret c = new DefaultCaret() {
            public void mouseClicked(MouseEvent e) {
//                int numClicks = SwingUtilities2.getAdjustedClickCount(getComponent(), e);
                int numClicks = e.getClickCount();
//                boolean leftClick = e.getButton() == 1;
                if (! e.isConsumed() && SwingUtilities.isLeftMouseButton(e) && numClicks == 2) {
                    return;
                }

                super.mouseClicked(e);
            }
            public void mousePressed(MouseEvent e) {
//                int numClicks = SwingUtilities2.getAdjustedClickCount(getComponent(), e);
                int numClicks = e.getClickCount();
                if (! e.isConsumed() && SwingUtilities.isLeftMouseButton(e) && numClicks == 2) {
                    return;
                }
                super.mousePressed(e);
            }
        };
        c.setBlinkRate(questionTextArea.getCaret().getBlinkRate());
        questionTextArea.setCaret(c);
    }

    private void selectWord(int keyCode){
        if(!isTestGraded && !showingCorrectAnswers) {
            keyPressed = true;
            List<Integer> sortedWordOffsets = new ArrayList<>(wordOffsets);
            sortedWordOffsets.sort(Integer::compareTo);

            if (keyCode == KeyEvent.VK_LEFT) {
                int prevWordIndex = sortedWordOffsets.indexOf(questionTextArea.getCurrentHighlightOffset());
                if (prevWordIndex == 0)
                    prevWordIndex = sortedWordOffsets.size() - 1;
                else
                    prevWordIndex--;

                int startOffset = sortedWordOffsets.get(prevWordIndex);

                questionTextArea.replaceLastHighlight(UNSELECTED_WORD);
                questionTextArea.setHighlight(startOffset, emptySpace.length(), SELECTED_WORD, true);

                caretPosition = startOffset;
                setCaretAtWordEnd(startOffset);
            } else if (keyCode == KeyEvent.VK_RIGHT) {
                int nextWordIndex = sortedWordOffsets.indexOf(questionTextArea.getCurrentHighlightOffset());

                if (nextWordIndex == sortedWordOffsets.size() - 1)
                    nextWordIndex = 0;
                else
                    nextWordIndex++;

                int startOffset = sortedWordOffsets.get(nextWordIndex);
                out.println(startOffset);

                questionTextArea.replaceLastHighlight(UNSELECTED_WORD);
                questionTextArea.setHighlight(startOffset, emptySpace.length(), SELECTED_WORD, true);

                caretPosition = startOffset;
                setCaretAtWordEnd(startOffset);
            }
        }

    }

    private int getNearestStartOffset(int caretPosition) throws IndexOutOfBoundsException{
        return wordOffsets.stream()
                .filter(position -> position < caretPosition)
                .reduce(0, (pos1, pos2) -> pos1 > pos2 ? pos1 : pos2);
    }

    public void setupQuestion(FillInTheBlank question) {
        int maxWordLength = 0;
        this.question = question;
        long randNum = System.nanoTime();

        questionTextArea.setPreferredSize(new Dimension(800, 500));
        questionTextArea.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {}

            public void ancestorRemoved(AncestorEvent event) {}

            public void ancestorMoved(AncestorEvent event) {
                questionTextArea.repaint();
            }
        });

        origOffsetsList = new ArrayList<>(this.question.getWordPositions());
        Collections.sort(origOffsetsList);

        wordBank = new ArrayList<>(this.question.getWordBank());
        Collections.shuffle(wordBank, new Random(randNum));
        wordOffsets = new ArrayList<>(this.question.getWordPositions());
        Collections.shuffle(wordOffsets, new Random(randNum));
        List<Integer> sortedPositions = new ArrayList<>(wordOffsets);
        sortedPositions.sort(Comparator.naturalOrder());

        for (String word : wordBank) {
            if (word.length() > maxWordLength)
                maxWordLength = word.length();
        }

        wordBankListView.setItems(FXCollections.observableArrayList(wordBank));
        questionTextArea.setText(this.question.getFillQuestion());

        doc = questionTextArea.getStyledDocument();
        Font font = new Font("Courier", Font.BOLD,15);
        questionTextArea.setFont(font);


        //Replace all word-bank words with spaces and underline them
        for (int x = 0; x < wordBank.size(); x++) {
            String word = wordBank.get(x);
            int position = wordOffsets.get(x);
            emptySpace = "     ";
            while (emptySpace.length() < maxWordLength)
                emptySpace += "     ";
            try {
                doc.remove(position, word.length());
                doc.insertString(position, emptySpace, UNSELECTED_WORD);
            } catch (BadLocationException e) {e.printStackTrace();}
            doc.setCharacterAttributes(position, emptySpace.length(), UNSELECTED_WORD, true);
            adjustAnswerOffsets(position, emptySpace.length()-word.length());
        }

        caretPosition = sortedPositions.get(0);
        questionTextArea.setCaretPosition(sortedPositions.get(0));
        questionTextArea.replaceLastHighlight(UNSELECTED_WORD);
        questionTextArea.setHighlight(caretPosition, emptySpace.length(), SELECTED_WORD, true);

        ToggleGroup toggle = new ToggleGroup();
        dispAnswersBtn.setToggleGroup(toggle);
        dispCorrectBtn.setToggleGroup(toggle);
    }

    private void adjustAnswerOffsets(int wordPosition, int increment){
        for(int x = 0; x < wordOffsets.size(); x++){
            if(wordOffsets.get(x) > wordPosition)
                wordOffsets.set(x, wordOffsets.get(x)+increment);
        }
    }

    /**
     * Displays the correct answers in the corresponding blanks.
     */
    private void displayCorrectAnswers(){
        if(isTestGraded && !showingCorrectAnswers) {
            for (int x = 0; x < wordOffsets.size(); x++) {
                String correctAnswer = getWordAtCaret(question.getFillQuestion().replace("\r", ""), origOffsetsList.get(x));
                String blankSpaces = " ".repeat(emptySpace.length() - correctAnswer.length());
                String answer = wordBank.get(x);
                try {
                    wordBank.add(doc.getText(wordOffsets.get(x), emptySpace.length()).trim());
                    doc.remove(wordOffsets.get(x), emptySpace.length());

                    if (answer.equals(correctAnswer))
                        doc.insertString(wordOffsets.get(x), correctAnswer + blankSpaces, CORRECT_WORD);
                    else
                        doc.insertString(wordOffsets.get(x), correctAnswer + blankSpaces, INCORRECT_WORD);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            showingCorrectAnswers = true;
        }
    }

    /**
     * Displays the given user answers in the corresponding blanks.
     */
    private void displayAnswers(){
        if(isTestGraded && showingCorrectAnswers) {
            for (int x = 0; x < wordOffsets.size(); x++) {
                String answer = wordBank.get(x);
                String correctAnswer = getWordAtCaret(question.getFillQuestion().replace("\r", ""), origOffsetsList.get(x));
                String blankSpaces = " ".repeat(emptySpace.length() - answer.length());

                try {
                    doc.remove(wordOffsets.get(x), emptySpace.length());
                    if (answer.equals(correctAnswer))
                        doc.insertString(wordOffsets.get(x), answer + blankSpaces, CORRECT_WORD);
                    else
                        doc.insertString(wordOffsets.get(x), answer + blankSpaces, INCORRECT_WORD);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
            showingCorrectAnswers = false;
        }
    }

    /**
     * Sets the caret position where the first space is located in the selected word or the last letter of the selected word if there are none.
     *
     * @param startOffset Location where method will begin checking for non-space characters.
     */
    private void setCaretAtWordEnd(int startOffset){
        questionTextArea.setCaretPosition(startOffset + emptySpace.length());
        for(int x = startOffset; x < startOffset + emptySpace.length(); x++){
            if(questionTextArea.getText().charAt(x) == ' ') {
                questionTextArea.setCaretPosition(x);
                break;
            }
        }
    }

    @Override
    public String getFXMLName() {
        return location.toString();
    }

    @Override
    public Node getQuestionScene() {
        return mainWindow;
    }

    @Override
    public void disableAnswerChanges() {
        questionTextArea.setEditable(false);
        wordBankListView.setDisable(true);
        mainWindow.setCursor(javafx.scene.Cursor.DEFAULT);
    }

    @Override
    public float getPointsScored() {
        float score = 0.0f;
        isTestGraded = true;
        questionTextArea.clearHighlights();
        dispAnswersBtn.setSelected(true);
        dispAnswersBtn.setDisable(false);
        dispCorrectBtn.setDisable(false);

        Collections.sort(wordOffsets);
        for(int x = 0; x < wordOffsets.size(); x++){
            int endOffset = wordOffsets.get(x) + emptySpace.length();
            String answer = questionTextArea.getText().substring(wordOffsets.get(x), endOffset).trim();
            String answerCheck = getWordAtCaret(question.getFillQuestion().replace("\r", ""), origOffsetsList.get(x));
            if(answer.equalsIgnoreCase(answerCheck)) {
                score++;//= question.getWeight() / question.getWordBank().size();
                questionTextArea.setHighlight(wordOffsets.get(x), emptySpace.length(), CORRECT_WORD, true);
            }
            else {
                questionTextArea.setHighlight(wordOffsets.get(x), emptySpace.length(), INCORRECT_WORD, true);
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