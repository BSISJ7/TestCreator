package com.BSISJ7.TestCreator.questions;

import com.BSISJ7.TestCreator.Test;
import com.BSISJ7.TestCreator.questions.editorPanels.EditorPanel;
import com.BSISJ7.TestCreator.questions.testPanels.TestPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.*;

import static com.BSISJ7.TestCreator.testIO.XMLIO.findNode;
import static com.BSISJ7.TestCreator.utilities.WordAtCaretFinder.getPositionStart;
import static com.BSISJ7.TestCreator.utilities.WordAtCaretFinder.getWordAtCaret;

public class FillInTheBlank extends Question {

    public final static SimpleAttributeSet UNSELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet SELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet INCORRECT_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet CORRECT_WORD = new SimpleAttributeSet();

    public final static HighlightPainter NEW_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(196, 238, 129));
    public final static HighlightPainter SELECTED_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(193, 239, 248));
    public final static HighlightPainter DELETE_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(248, 96, 97));

    static{
        StyleConstants.setUnderline(UNSELECTED_WORD, true);

        StyleConstants.setUnderline(SELECTED_WORD, true);
        StyleConstants.setBackground(SELECTED_WORD, new Color(193, 239, 248));

        StyleConstants.setUnderline(INCORRECT_WORD, true);
        StyleConstants.setBackground(INCORRECT_WORD, new Color(248, 96, 97));

        StyleConstants.setUnderline(CORRECT_WORD, true);
        StyleConstants.setBackground(CORRECT_WORD, Color.GREEN.brighter().brighter());
    }

    public static final String ANSWER_REPLACEMENT_REGEX = "\\[.+?\\] ?";
    private String fillQuestion = "";
    private ArrayList<String> wordBank = new ArrayList<>();
    private ArrayList<Integer> wordPositions = new ArrayList<>();


    public FillInTheBlank() {
        this("Fill In Question");
    }

    public FillInTheBlank(String questionName) {
        super(questionName);
        questionType = "FillInTheBlank";
    }

    public FillInTheBlank(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public FillInTheBlank(String questionName, String type) {
        this(questionName);
        questionType = type;
    }

    public FillInTheBlank(String questionName, String type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public void setFillInQuestion(String fillQuestion) {
        this.fillQuestion = fillQuestion.replaceAll("(\\r)", "");
    }

    public String getFillQuestion() {
        return fillQuestion.replaceAll("(\\r)", "");
    }

    public void add(String answer) {
        wordBank.add(answer);
    }

    public void remove(String answer) {
        wordBank.remove(answer);
    }

    public List<String> getWordBank() {
        return Collections.unmodifiableList(wordBank);
    }

    public void setWordBank(ArrayList<String> newWordBank) {
        wordBank.clear();
        wordBank.addAll(newWordBank);
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);
        Element fillInAnswer = XMLDocument.createElement("FillInQuestion");
        fillInAnswer.setTextContent(fillQuestion);

        wordBank.forEach(word -> {
            Element newWord = XMLDocument.createElement("WordBankItem");
            newWord.setTextContent(word);
            question.appendChild(newWord);
        });

        wordPositions.forEach(location -> {
            Element newPos = XMLDocument.createElement("WordBankPos");
            newPos.setTextContent(location.toString());
            question.appendChild(newPos);
        });

        question.appendChild(fillInAnswer);

        return question;
    }

    @Override
    public boolean readyToRun() {
        return wordBank.size() > 0 && fillQuestion.length() > 0;
    }

    @Override
    public FillInTheBlank loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        fillQuestion = Objects.requireNonNull(findNode("FillInQuestion", questionNode)).getTextContent();

        NodeList wordBankItems = ((Element) questionNode).getElementsByTagName("WordBankItem");
        for (int x = 0; x < wordBankItems.getLength(); x++) {
            wordBank.add(wordBankItems.item(x).getTextContent());
        }

        NodeList wordBankPos = ((Element) questionNode).getElementsByTagName("WordBankPos");
        for (int x = 0; x < wordBankPos.getLength(); x++) {
            wordPositions.add(Integer.parseInt(wordBankPos.item(x).getTextContent()));
        }
        return this;
    }

    @Override
    public TestPanel getTestPanel() throws IllegalStateException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BSISJ7/TestCreator/questions/testPanels/" +
                "FillInTestPanel.fxml"));
        try {
            loader.load();
        } catch (IOException e) {e.printStackTrace();}
        loader.setRoot(new BorderPane());
        TestPanel controller = loader.<TestPanel>getController();
        controller.setupQuestion(this);
        return controller;
    }

    @Override
    public EditorPanel getEditPanel() throws IllegalStateException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/BSISJ7/TestCreator/questions/editorPanels/" +
                "FillInEditor.fxml"));
        try {
            loader.load();
        } catch (IOException e) {e.printStackTrace();}
        loader.setRoot(new BorderPane());
        EditorPanel controller = loader.<EditorPanel>getController();
        return controller;
    }

    @Override
    public int getGradableParts() {
        return wordBank.size();
    }

    public List<Integer> getWordPositions() {
        return Collections.unmodifiableList(wordPositions);
    }

    public void setWordPositions(ArrayList<Integer> locations) {
        wordPositions = new ArrayList<>(locations);
    }

    @Override
    public void autofillData() {
        fillQuestion = "Matching question is completely changed to now [match] mulitple questions and answers\n" +
                "Question worth is weighted instead of [correct] or incorrect\n" +
                "Scores are now determined [based] on question weight";

        for(int x = 0; x < 5; x++){
            int randPos = new Random().nextInt(fillQuestion.length());
            String word = getWordAtCaret(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);
            int position = getPositionStart(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);

            if(wordPositions.contains(position)) {
                x--;
                continue;
            }
            wordBank.add(word);
            wordPositions.add(position);
        }
    }
}
