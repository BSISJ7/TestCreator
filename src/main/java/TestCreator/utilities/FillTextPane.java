package TestCreator.utilities;

import TestCreator.questions.editorPanels.FillTheBlankEditor;
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

import static TestCreator.questions.FillTheBlank.SELECTED_WORD_PAINT;
import static java.util.Arrays.asList;

public class FillTextPane extends JTextPane {

    private final Stack<String> textUndoStack = new Stack<>();
    private final Stack<String> textRedoStack = new Stack<>();

    private final Stack<ArrayList<HighlightInfo>> highlightUndoStack = new Stack<>();
    private final Stack<ArrayList<HighlightInfo>> highlightRedoStack = new Stack<>();

    private final FillTextPane fillTextPane = this;

    private final DefaultFillHighlighter fillHighlighter = new DefaultFillHighlighter();
    private final List<FillHighlight> highlightList = new ArrayList<>();

    private final static List<Character> EXCLUDED_HIGHLIGHT_CHARS = asList(' ', '\t', '\n', '\r', ';', ':', ',', '<',
            '>', '`', '~', '.', '!', '?', '/', '@', '#', '$', '%', '^', '&', '*', '(', ')',
            '-', '_', '=', '+', '[', ']', '{', '}', '|', '\\');

    public FillTextPane() {
    }

    public FillTextPane(FillTheBlankEditor fillInEditor) {
        setHighlighter(fillHighlighter);

        //TODO Fix undo
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
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();
                        }
                    });
                    fillInEditor.updateWordBank();
                }
            }
        });
        //TODO: Fix redo
//        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl Y"), "Redo");
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
                for (HighlightInfo highlight : fillHighlighter.getHighlights()) {
                    if (caretPosition == highlight.getEndOffset() && highlight.getPainter() != SELECTED_WORD_PAINT) {
                        fillHighlighter.removeHighlightAt(highlight.getStartOffset());
                        break;
                    }
                }
                fillTextPane.paste();
                pushHighlights();
            }
        });

        fillTextPane.addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
            }

            @Override
            public void keyTyped(KeyEvent event) {
                String typedChar = String.valueOf(event.getKeyChar());
                if (typedChar.matches("[\t]|[;:,<> `~.!?/@#$%^&*)(]")) {
                    insertText(event);
                }
                fillInEditor.updateWordBank();
            }

            public void keyPressed(KeyEvent event) {
                if (!(event.getModifiersEx() == InputEvent.CTRL_DOWN_MASK && (event.getKeyChar() == 'c'
                        || event.getKeyChar() == 'y' || event.getKeyChar() == 'z')) && event.getKeyCode() != 17) {
                    pushHighlights();
                    textUndoStack.push(fillTextPane.getText());
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

    public void removeHighlight(int startOffset) {
        for (FillHighlight fillHighlight : highlightList) {
            if (fillHighlight.startOffset == startOffset) {
                getStyledDocument().setCharacterAttributes(startOffset, fillHighlight.length,
                        new SimpleAttributeSet(), true);
                highlightList.remove(fillHighlight);
                return;
            }
        }
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
        if (!highlightList.isEmpty()) {
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
                event.consume();
                try {
                    fillTextPane.getStyledDocument().insertString
                            (highlight.getEndOffset(), String.valueOf(event.getKeyChar()), new SimpleAttributeSet());
                    if(EXCLUDED_HIGHLIGHT_CHARS.contains(event.getKeyChar())) {
                        fillHighlighter.setHighlight(highlight.getStartOffset(), highlight.getEndOffset() - 1,
                                SELECTED_WORD_PAINT, highlight);
                    }
                } catch (BadLocationException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void pushHighlights() {
        ArrayList<HighlightInfo> undoHighlights = new ArrayList<>();
        asList(fillHighlighter.getHighlights()).forEach(highlight -> {
            if (highlight.getPainter() == SELECTED_WORD_PAINT)
                undoHighlights.add(fillHighlighter.new HighlightInfo(highlight.getStartOffset(),
                    highlight.getEndOffset(), highlight.getPainter()));
        });

        highlightUndoStack.push(undoHighlights);
    }

    private static class FillHighlight {

        private int startOffset;
        private int length;

        private FillHighlight(int startOffset, int length) {
            this.startOffset = startOffset;
            this.length = length;
        }
    }
}
