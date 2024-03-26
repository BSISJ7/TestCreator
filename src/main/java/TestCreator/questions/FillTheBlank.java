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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static TestCreator.dataIO.XMLIO.findNode;

public class FillTheBlank extends Question {

    private String fillQuestion = "";
    private final List<FillAnswer> answersList = new ArrayList<>();

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

    public ObservableList<String> getWordBankCopy() {
        List<String> wordBank = new ArrayList<>();
        answersList.forEach(answer -> wordBank.add(answer.getWord()));
        return FXCollections.observableArrayList(wordBank);
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);
        Element fillInAnswer = XMLDocument.createElement("FillTheBlankQuestion");
        fillInAnswer.setTextContent(fillQuestion);

        answersList.forEach(answer -> {
            Element newAnswer = XMLDocument.createElement("FillAnswer");
            newAnswer.setTextContent(answer.getWord());

            Element newPos = XMLDocument.createElement("WordBankPos");
            newPos.setTextContent(String.valueOf(answer.getPositionStart()));

            question.appendChild(newAnswer);
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
        return !answersList.isEmpty() && !fillQuestion.isEmpty();
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        fillQuestion = findNode("FillTheBlankQuestion", questionNode).getTextContent();

        NodeList wordBankItems = ((Element) questionNode).getElementsByTagName("FillAnswer");
        NodeList wordBankPos = ((Element) questionNode).getElementsByTagName("WordBankPos");
        for (int x = 0; x < wordBankItems.getLength(); x++) {
            String word = wordBankItems.item(x).getTextContent();
            int pos = Integer.parseInt(wordBankPos.item(x).getTextContent());
            answersList.add(new FillAnswer(word, pos, pos + word.length()));
        }

        displayAnswers = Boolean.parseBoolean(Objects.requireNonNull(findNode("DisplayAnswers", questionNode)).getTextContent());
        sortAnswers();
    }

    @Override
    public String loadFromSQLStatement(String sqlStatement) {
        return null;
    }

    @Override
    public TestPanel<FillTheBlank> getTestPanel() throws IOException {
        return (TestPanel) StageManager.getController("/questions/testPanels/FillTheBlankTestPanel.fxml");
    }

    @Override
    public int getMaxScore() {
        return answersList.size();
    }

    public List<Integer> getAnswerOffsetsCopy() {
        ObservableList<Integer> wordPositions = FXCollections.observableArrayList();
        answersList.forEach(answer -> wordPositions.add(answer.getPositionStart()));
        return wordPositions;
    }

    public void autofillData() {
        int maxQuestions = 5;
        StringBuilder fillBuilder = new StringBuilder();
        for(int x = 0; x < maxQuestions; x++) {
            int randNum1 = 1 + (int) (Math.random() * ((10000 - 1) + 1));
            int randNum2 = 1 + (int) (Math.random() * ((10000 - 1) + 1));

            String mathProblem = STR."\{randNum1} + \{randNum2} = \{randNum1 + randNum2}";
            fillBuilder.append((x == maxQuestions-1) ? mathProblem : STR."\{mathProblem}\n");

            int answerPosition = fillBuilder.lastIndexOf(STR."\{randNum1 + randNum2}");
            String answer = String.valueOf(randNum1 + randNum2);

            answersList.add(new FillAnswer(answer, answerPosition, answerPosition + answer.length()));
        }
        fillQuestion = fillBuilder.toString();
    }

    public boolean hintsDisplayed() {
        return displayAnswers;
    }

    public void setDisplayAnswers(boolean displayAnswers) {
        this.displayAnswers = displayAnswers;
    }

    public List<Integer> getWordIndexes() {
        return answersList.stream().map(FillAnswer::getPositionStart).toList();
    }

    public String getAnswer(int index) {
        return answersList.get(index).getWord();
    }

    public int getWordIndex(int index) {
        return answersList.get(index).getPositionStart();
    }

    public void setAnswerList(List<SelectionManager.SelectionAnswer> answerList) {
        answersList.clear();
        answerList.forEach(answer -> answersList.add(answer.getFillAnswer()));
    }

    public void sortAnswers(){
        answersList.sort(Comparator.comparingInt(FillAnswer::getPositionStart));
    }


    public static class FillAnswer {
        private String word;
        private final int positionStart;
        private final int positionEnd;
        private final int length;

        public FillAnswer(String word, int positionStart, int positionEnd) {
            this.word = word;
            this.positionStart = positionStart;
            this.positionEnd = positionEnd;
            this.length = positionEnd - positionStart;
        }

        public String getWord() {
            return word;
        }

        public int getLength() {
            return length;
        }

        public int getPositionStart() {
            return positionStart;
        }

        public int getPositionEnd() {
            return positionEnd;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
