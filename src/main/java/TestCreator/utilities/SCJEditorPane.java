package TestCreator.utilities;

import javax.swing.*;
import javax.swing.text.TextAction;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class SCJEditorPane extends JTextPane {

    private Stack<String> undoStack;
    private Stack<String> redoStack;
    private SCJEditorPane editorPane;

    public SCJEditorPane() {
        editorPane = this;
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        addListener();
    }

    public void addListener() {
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl Z"), "Undo");
        getActionMap().put("Undo", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                if (!undoStack.empty()) {
                    redoStack.push(editorPane.getText());
                    setText(undoStack.pop());
                }
            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl Y"), "Redo");
        getActionMap().put("Redo", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                if (redoStack.size() > 0) {
                    undoStack.push(editorPane.getText());
                    setText(redoStack.pop());
                }

            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl X"), "CutText");
        getActionMap().put("CutText", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                undoStack.push(editorPane.getText());
                copy();
                if (getSelectedText() != null) {
                    replaceSelection("");
                }
            }
        });
        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ctrl V"), "PasteText");
        getActionMap().put("PasteText", new TextAction("") {
            public void actionPerformed(ActionEvent event) {
                undoStack.push(editorPane.getText());
                if (getSelectedText() != null) {
                    replaceSelection("");
                }
                paste();
            }
        });

        addKeyListener(new KeyListener() {
            public void keyReleased(KeyEvent event) {
            }

            public void keyPressed(KeyEvent event) {
//                if (event.getKeyCode() >= 32 && event.getKeyCode() <= 90) {
                if (event.getModifiers() != InputEvent.CTRL_MASK && !event.getSource().equals("z")
                        && event.getKeyCode() != 17) {
                    undoStack.push(editorPane.getText());
                }
//                }
            }

            public void keyTyped(KeyEvent event) {
            }
        });
    }
}
