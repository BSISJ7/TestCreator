package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.StageManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.Random;

public class MatchingWord extends Question {

    private ObservableList<String> keys = FXCollections.observableArrayList();
    private ObservableList<String> values = FXCollections.observableArrayList();

    public MatchingWord() {
        super();
        questionType = QuestionTypes.MATCHING_WORD;
    }

    public MatchingWord(String questionName) {
        super(questionName);
        questionType = QuestionTypes.MATCHING_WORD;
    }

    public MatchingWord(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public MatchingWord(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public MatchingWord(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }


    public void setQuestionAt(int index, String text) throws IndexOutOfBoundsException {
        keys.set(index, text);
    }

    public void setAnswerAt(int index, String text) throws IndexOutOfBoundsException {
        values.set(index, text);
    }

    public String getMatchingQuestion(int index) {
        return keys.get(index);
    }

    public String getMatchingAnswer(int index) {
        return values.get(index);
    }

    public void addKey(String question) {
        keys.add(question);
    }

    public void addValue(String answer) {
        values.add(answer);
    }

    public int getTotalPairs() {
        return keys.size();
    }

    public void removeKeyAt(int index) {
        keys.remove(index);
    }

    public void removeValueAt(int index) {
        values.remove(index);
    }

    public ObservableList<String> getKeyList() {
        return keys;
    }

    public ObservableList<String> getValueList() {
        return values;
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);
        for (int x = 0; x < keys.size(); x++) {

            Element matchingKey = XMLDocument.createElement("MatchingQuestion");
            matchingKey.setTextContent(keys.get(x));
            Element matchingValue = XMLDocument.createElement("MatchingAnswer");
            matchingValue.setTextContent(values.get(x));

            question.appendChild(matchingKey);
            question.appendChild(matchingValue);
        }
        return question;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);

        NodeList questions = ((Element) questionNode).getElementsByTagName("MatchingQuestion");
        for (int x = 0; x < questions.getLength(); x++) {
            keys.add(questions.item(x).getTextContent());
        }

        NodeList answers = ((Element) questionNode).getElementsByTagName("MatchingAnswer");
        for (int x = 0; x < answers.getLength(); x++) {
            values.add(answers.item(x).getTextContent());
        }
    }


    @Override
    public boolean readyToRun() {
        return getTotalPairs() > 0;
    }

    @Override
    public TestPanel getTestPanel() throws IOException {
        return (TestPanel) StageManager.getController("/questions/testPanels/MatchingWordTestPanel.fxml");
    }

    @Override
    public int getGradableParts() {
        return keys.size();
    }

    @Override
    public void autofillData() {
        int minVal = 0;
        while (keys.size() < 5 && values.size() < 5) {
            int num1 = new Random(System.nanoTime()).nextInt(minVal + 5);
            int num2 = new Random(System.nanoTime()).nextInt(minVal + 5);
            minVal += 5;
            if (keys.contains(num1 + " + " + num2 + " = ") || values.contains(num1 + num2 + ""))
                continue;
            keys.add(num1 + " + " + num2 + " = ");
            values.add(num1 + num2 + "");
        }
    }
}
