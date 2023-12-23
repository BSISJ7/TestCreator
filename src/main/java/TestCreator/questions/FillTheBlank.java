package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.SelectionManager;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static TestCreator.testIO.XMLIO.findNode;

public class FillTheBlank extends Question {

    public final static SimpleAttributeSet UNSELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet SELECTED_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet INCORRECT_WORD = new SimpleAttributeSet();
    public final static SimpleAttributeSet CORRECT_WORD = new SimpleAttributeSet();

    public final static HighlightPainter NEW_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(196, 238, 129));
    public final static HighlightPainter SELECTED_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(193, 239, 248));
    public final static HighlightPainter DELETE_WORD_PAINT = new DefaultHighlighter.DefaultHighlightPainter(new Color(248, 96, 97));
    public static final String UNDERLINE_WORD = "fx-underline: true;";

    public static final String ANSWER = ("-fx-fill: blue;");
    public static final String REMOVAL = ("-fx-fill: red;");
    public static final String HOVER = ("-fx-fill: dodgerblue;");
    public static final String DEFAULT = ("-fx-fill: black;");
    public static final String CORRECT = ("-fx-fill: green;");
    public static final String INCORRECT = ("-fx-fill: red;");

    static {
        StyleConstants.setUnderline(UNSELECTED_WORD, true);

        StyleConstants.setUnderline(SELECTED_WORD, true);
        StyleConstants.setBackground(SELECTED_WORD, new Color(193, 239, 248));

        StyleConstants.setUnderline(INCORRECT_WORD, true);
        StyleConstants.setBackground(INCORRECT_WORD, new Color(248, 96, 97));

        StyleConstants.setUnderline(CORRECT_WORD, true);
        StyleConstants.setBackground(CORRECT_WORD, Color.GREEN.brighter().brighter());
    }

    public static final String ALLOWED_CHARS = "[a-zA-Z0-9-'_]";


    private final ObservableList<String> wordBank = FXCollections.observableArrayList();
    private String fillQuestion = "";
    private ArrayList<Integer> wordPositions = new ArrayList<>();

    private SelectionManager selectionManager = new SelectionManager();

    private boolean displayAnswers = true;


    public FillTheBlank() {
        super();
        questionType = QuestionTypes.FILL_THE_BLANK;
        questionName = STR."\{questionType.getDisplayName()} \{ID.substring(0,5)}";
    }

    public FillTheBlank(String questionName) {
        super(questionName);
        questionType = QuestionTypes.FILL_THE_BLANK;
    }

    public FillTheBlank(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public FillTheBlank(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public FillTheBlank(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public void setFillTheBlankQuestion(String fillQuestion) {
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

    public ObservableList<String> getWordBankCopy() {
        return FXCollections.observableArrayList(wordBank);
    }

    public void setWordBank(List<String> newWordBank) {
        wordBank.clear();
        wordBank.addAll(newWordBank);
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);
        Element fillInAnswer = XMLDocument.createElement("FillTheBlankQuestion");
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

        Element displayAnswers = XMLDocument.createElement("DisplayAnswers");
        displayAnswers.setTextContent(String.valueOf(this.displayAnswers));
        question.appendChild(displayAnswers);

        question.appendChild(fillInAnswer);

        return question;
    }

    @Override
    public boolean readyToRun() {
        return !wordBank.isEmpty() && !fillQuestion.isEmpty();
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        fillQuestion = Objects.requireNonNull(findNode("FillTheBlankQuestion", questionNode)).getTextContent();

        NodeList wordBankItems = ((Element) questionNode).getElementsByTagName("WordBankItem");
        for (int x = 0; x < wordBankItems.getLength(); x++) {
            wordBank.add(wordBankItems.item(x).getTextContent());
        }

        NodeList wordBankPos = ((Element) questionNode).getElementsByTagName("WordBankPos");
        for (int x = 0; x < wordBankPos.getLength(); x++) {
            wordPositions.add(Integer.parseInt(wordBankPos.item(x).getTextContent()));
        }

        displayAnswers = Boolean.parseBoolean(Objects.requireNonNull(findNode("DisplayAnswers", questionNode)).getTextContent());
    }

    @Override
    public TestPanel getTestPanel() throws IOException {
        return (TestPanel) StageManager.getController("/questions/testPanels/FillTheBlankTestPanel.fxml");
    }

    @Override
    public int getMaxScore() {
        return wordBank.size();
    }

    public List<Integer> getAnswerOffsetsCopy() {
        return FXCollections.observableArrayList(wordPositions);
    }

    public void setWordIndexes(ArrayList<Integer> locations) {
        wordPositions.clear();
        wordPositions.addAll(locations);
    }

    @Override
    public void autofillData() {
        int maxQuestions = 5;
        StringBuilder fillBuilder = new StringBuilder();
        for(int x = 0; x < maxQuestions; x++) {
            int randNum1 = 1 + (int) (Math.random() * ((10000 - 1) + 1));
            int randNum2 = 1 + (int) (Math.random() * ((10000 - 1) + 1));

            String mathProblem = STR."\{randNum1} + \{randNum2} = \{randNum1 + randNum2}";
            fillBuilder.append((x == maxQuestions-1) ? mathProblem : mathProblem + "\n");

            int answerPosition = fillBuilder.lastIndexOf(STR."\{randNum1 + randNum2}");
            String answer = String.valueOf(randNum1 + randNum2);

            wordBank.add(answer);
            wordPositions.add(answerPosition);
        }
        fillQuestion = fillBuilder.toString();

//        fillQuestion = """
//                Matching question is completely changed to now [match] multiple questions and answers
//                Question worth is weighted instead of [correct] or incorrect
//                Scores are now determined [based] on question weight
//                """;
//
//        for (int x = 0; x < 5; x++) {
//            int randPos = new Random().nextInt(fillQuestion.length());
//            String word = CaretUtilities.getWordAtCaret(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);
//            int position = CaretUtilities.getBeginningIndex(fillQuestion.replace("/\n/g", ",").replace("\r", ""), randPos);
//
//            if (wordPositions.contains(position)) {
//                x--;
//                continue;
//            }
//            wordBank.add(word);
//            wordPositions.add(position);
//        }
    }

    public boolean hintsDisplayed() {
        return displayAnswers;
    }

    public void setDisplayAnswers(boolean displayAnswers) {
        this.displayAnswers = displayAnswers;
    }
}
