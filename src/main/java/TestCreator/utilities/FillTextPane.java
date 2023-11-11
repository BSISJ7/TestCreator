package TestCreator.utilities;

import TestCreator.questions.editorPanels.FillInTheBlankEditor;
import TestCreator.utilities.DefaultFillHighlighter.HighlightInfo;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static TestCreator.questions.FillInTheBlank.SELECTED_WORD_PAINT;
import static java.util.Arrays.asList;

public class FillTextPane extends JTextPane {

    private final Stack<String> textUndoStack = new Stack<>();
    private final Stack<String> textRedoStack = new Stack<>();

    private final Stack<ArrayList<HighlightInfo>> highlightUndoStack = new Stack<>();
    private final Stack<ArrayList<HighlightInfo>> highlightRedoStack = new Stack<>();

    private final FillTextPane fillTextPane = this;

    private final boolean settingUp = false;
    private final DefaultFillHighlighter fillHighlighter = new DefaultFillHighlighter();
    private List<FillHighlight> highlightList = new ArrayList<>();

    public FillTextPane() {
    }

    public FillTextPane(FillInTheBlankEditor fillInEditor) {
        setHighlighter(fillHighlighter);

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl Z"), "Undo");
        getActionMap().put("Undo", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                if (!textUndoStack.empty()) {
                    final ArrayList<HighlightInfo> redoHighlights = new ArrayList<>();
                    fillHighlighter.getHighlightList().forEach(highlight -> redoHighlights.add(
                            fillHighlighter.new HighlightInfo(highlight.getStartOffset(),
                                    highlight.getEndOffset(), highlight.getPainter())));
                    highlightRedoStack.push(redoHighlights);
                    textRedoStack.push(fillTextPane.getText());

                    fillTextPane.setText(textUndoStack.pop());
                    fillHighlighter.removeAllHighlights();

                    highlightUndoStack.pop().forEach(highlight -> {
                        try {
                            fillHighlighter.addHighlight(highlight.getIntStart(), highlight.getIntEnd(), highlight.getPainter());
                            System.out.println("Adding Highlight At: " + highlight.getStartOffset());
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                    fillInEditor.updateWordBank();
                }
            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl Y"), "Redo");
        getActionMap().put("Redo", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                if (!textRedoStack.isEmpty()) {
                    pushHighlights();
                    textUndoStack.push(fillTextPane.getText());

                    fillTextPane.setText(textRedoStack.pop());
                    fillHighlighter.removeAllHighlights();
                    highlightRedoStack.pop().forEach(highlight -> {
                        try {
                            fillHighlighter.addHighlight(highlight.getIntStart(), highlight.getIntEnd(), highlight.getPainter());
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl V"), "PasteText");
        getActionMap().put("PasteText", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                int caretPosition = fillTextPane.getCaretPosition();
                boolean endOfHighlight = false;
                int startOffset = -1;
                int endOffset = -1;

                for (HighlightInfo highlight : fillHighlighter.getHighlights())
                    if (caretPosition == highlight.getEndOffset()) {
                        endOfHighlight = true;
                        fillHighlighter.removeHighlightAt(highlight.getStartOffset());
                        startOffset = highlight.getStartOffset();
                        endOffset = highlight.getEndOffset();
                        break;
                    }

                fillTextPane.paste();
                if (endOfHighlight) {
                    try {
                        fillHighlighter.addHighlight(startOffset, endOffset, SELECTED_WORD_PAINT);
                    } catch (BadLocationException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                pushHighlights();
            }
        });

        fillTextPane.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
                fillInEditor.updateWordBank();
                fillInEditor.getQuestion().setFillInQuestion(fillTextPane.getText());
            }

            @Override
            public void keyTyped(KeyEvent event) {
                String typedChar = String.valueOf(event.getKeyChar());

                if (typedChar.matches("[\t]|[;:,<> `~.!?/@#$%^&*)(]")) {
                    insertText(event);
                }
            }

            public void keyPressed(KeyEvent event) {
                if (!(event.getModifiers() == InputEvent.CTRL_MASK && (event.getKeyChar() == 'c' || event.getKeyChar() == 'y' || event.getKeyChar() == 'z')) && event.getKeyCode() != 17) {

                    int selectionStart = fillTextPane.getSelectionStart();
                    int selectionEnd = fillTextPane.getSelectionEnd();
                    fillTextPane.select(-1, -1);

                    pushHighlights();
                    textUndoStack.push(fillTextPane.getText());
                    fillTextPane.select(selectionStart, selectionEnd);
                }
            }
        });
    }

    public void setHighlight(int startOffset, int length, SimpleAttributeSet selectionType, boolean replace) {
        if (isDuplicateHighlight(startOffset) && replace)
            removeHighlight(startOffset);
        else if (isDuplicateHighlight(startOffset) && !replace)
            return;

        getStyledDocument().setCharacterAttributes(startOffset, length, selectionType, true);
        highlightList.add(new FillHighlight(startOffset, length));
    }

    public boolean removeHighlight(int startOffset) {
        for (FillHighlight fillHighlight : highlightList) {
            if (fillHighlight.startOffset == startOffset) {
                getStyledDocument().setCharacterAttributes(startOffset, fillHighlight.length,
                        new SimpleAttributeSet(), true);
                highlightList.remove(fillHighlight);
                return true;
            }
        }
        return false;
    }

    public void clearHighlights() {
        for (int x = 0; x < highlightList.size(); x++) {
            getStyledDocument().setCharacterAttributes(highlightList.get(x).startOffset, highlightList.get(x).length,
                    new SimpleAttributeSet(), true);
            highlightList.remove(x);
        }
    }

    public int getCurrentHighlightOffset() {
        return highlightList.get(highlightList.size() - 1).startOffset;
    }

    public void replaceLastHighlight(SimpleAttributeSet selectionType) {
        if (highlightList.size() > 0) {
            FillHighlight previousHighlight = highlightList.get(highlightList.size() - 1);
            int prevStartOffset = previousHighlight.startOffset;
            int prevLength = previousHighlight.length;
            setHighlight(prevStartOffset, prevLength, selectionType, true);

        }
    }


    public DefaultFillHighlighter getHighlighter() {
        return fillHighlighter;
    }

    private boolean isDuplicateHighlight(int startOffset) {
        for (FillHighlight fillHighlight : highlightList) {
            if (fillHighlight.startOffset == startOffset)
                return true;
        }
        return false;
    }

    private void insertText(KeyEvent event) {
        int caretPosition = fillTextPane.getCaretPosition();
        for (Highlight highlight : fillHighlighter.getHighlights()) {
            if (caretPosition == highlight.getEndOffset()) {
                int startOffset = highlight.getStartOffset();
                int endOffset = highlight.getEndOffset();
                event.consume();
                fillHighlighter.removeHighlightAt(highlight.getStartOffset());

                try {
                    fillTextPane.getStyledDocument().insertString
                            (highlight.getEndOffset(), String.valueOf(event.getKeyChar()), new SimpleAttributeSet());
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                try {
                    fillHighlighter.addHighlight(startOffset, endOffset, SELECTED_WORD_PAINT);
                } catch (BadLocationException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pushHighlights() {
        ArrayList<HighlightInfo> undoHighlights = new ArrayList<>();
        asList(fillHighlighter.getHighlights()).forEach(highlight ->
                undoHighlights.add(fillHighlighter.new HighlightInfo(highlight.getStartOffset(),
                        highlight.getEndOffset(), highlight.getPainter()))
        );
        highlightUndoStack.push(undoHighlights);
    }

    private class FillHighlight {

        private int startOffset = 0;
        private int length = 0;

        private FillHighlight(int startOffset, int length) {
            this.startOffset = startOffset;
            this.length = length;
        }
    }
}
