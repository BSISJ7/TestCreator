package TestCreator.questions;

import TestCreator.questions.testPanels.FillInTheBlankTestPanel;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.WordAtCaretFinder;
import TestCreator.Test;
import TestCreator.questions.editorPanels.EditorPanel;
import TestCreator.questions.testPanels.TestPanel;
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

import static TestCreator.testIO.XMLIO.findNode;

public class FillInTheBlank extends Question {

    public final static SimpleAttributeSet UNSELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet SELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet INCORRECT_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet CORRECT_WORD = new SimpleAttributeSet();

    public final static HighlightPainter NEW_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(196, 238, 129));
    public final static HighlightPainter SELECTED_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(193, 239, 248));
    public final static HighlightPainter DELETE_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(248, 96, 97));
    public static final String ANSWER_REPLACEMENT_REGEX = "\\[.+?\\] ?";

    static {
        StyleConstants.setUnderline(UNSELECTED_WORD, true);

        StyleConstants.setUnderline(SELECTED_WORD, true);
        StyleConstants.setBackground(SELECTED_WORD, new Color(193, 239, 248));

        StyleConstants.setUnderline(INCORRECT_WORD, true);
        StyleConstants.setBackground(INCORRECT_WORD, new Color(248, 96, 97));

        StyleConstants.setUnderline(CORRECT_WORD, true);
        StyleConstants.setBackground(CORRECT_WORD, Color.GREEN.brighter().brighter());
    }

    private final ArrayList<String> wordBank = new ArrayList<>();
    private String fillQuestion = "";
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
        return !wordBank.isEmpty() && !fillQuestion.isEmpty();
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
        try {
            StageManager.setScene(("/questions/testPanels/FillInTheBlankTestPanel.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FillInTheBlankTestPanel();
    }

    @Override
    public EditorPanel getEditPanel() throws IllegalStateException {
        return null;
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
        fillQuestion = """
                Matching question is completely changed to now [match] mulitple questions and answers
                Question worth is weighted instead of [correct] or incorrect
                Scores are now determined [based] on question weight
                """;

        for (int x = 0; x < 5; x++) {
            int randPos = new Random().nextInt(fillQuestion.length());
            String word = WordAtCaretFinder.getWordAtCaret(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);
            int position = WordAtCaretFinder.getPositionStart(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);

            if (wordPositions.contains(position)) {
                x--;
                continue;
            }
            wordBank.add(word);
            wordPositions.add(position);
        }
    }
}