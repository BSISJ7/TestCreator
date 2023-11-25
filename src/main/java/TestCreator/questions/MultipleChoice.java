package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.DictionaryManager;
import TestCreator.utilities.StageManager;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static TestCreator.testIO.XMLIO.findNode;

public class MultipleChoice extends Question {

    private final List<String> choices;

    private String multChoiceQuestion = "";
    private int answerIndex = -1;

    public static final int MAX_CHOICES = 10;

    public MultipleChoice(String questionName) {
        super(questionName);
        questionType = QuestionTypes.MULTIPLE_CHOICE;
        choices = new ArrayList<>();
    }

    public MultipleChoice() {
        super();
        questionType = QuestionTypes.MULTIPLE_CHOICE;
        choices = new ArrayList<>();
    }

    public MultipleChoice(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public MultipleChoice(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public String getAnswer() throws NullPointerException {
        return choices.get(answerIndex);
    }

    public List<String> getChoicesCopy() {
        return new ArrayList<>(choices);
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int index) {
        answerIndex = index;
    }

    public void setQuestionText(String question) {
        multChoiceQuestion = question;
    }

    public String getQuestionText() {
        return multChoiceQuestion;
    }

    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);

        Element selectedAnswer = XMLDocument.createElement("AnswerIndex");
        selectedAnswer.setTextContent(Integer.toString(answerIndex));
        question.appendChild(selectedAnswer);

        Element multQuestion = XMLDocument.createElement("MultQuestion");
        multQuestion.setTextContent(multChoiceQuestion);
        question.appendChild(multQuestion);

        for (String s : choices) {
            Element choice = XMLDocument.createElement("Choice");
            choice.setTextContent(s);
            question.appendChild(choice);
        }
        return question;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) throws NullPointerException {
        super.loadQuestionFromXMLNode(questionNode);
        multChoiceQuestion = Objects.requireNonNull(findNode("MultQuestion", questionNode)).getTextContent();
        answerIndex = Integer.parseInt(Objects.requireNonNull(findNode("AnswerIndex", questionNode)).getTextContent());
        NodeList choices = ((Element) questionNode).getElementsByTagName("Choice");
        for (int x = 0; x < choices.getLength(); x++) {
            this.choices.add(choices.item(x).getTextContent());
        }
    }

    @Override
    public boolean readyToRun() {
        return !choices.isEmpty() && answerIndex >= 0 && !multChoiceQuestion.isEmpty();
    }

    @Override
    public TestPanel<MultipleChoice> getTestPanel() throws IOException {
        return (TestPanel<MultipleChoice>) StageManager.getController("/questions/testPanels/MultipleChoiceTestPanel.fxml");
    }

    @Override
    public int getGradableParts() {
        return 1;
    }

    @Override
    public void autofillData() {
        if (DictionaryManager.getDictionary().size() < 1)
            return;

        for (int x = 0; x < 5; x++) {
            choices.add(DictionaryManager.getDictionary().getRandomWord());
        }
        answerIndex = new Random().nextInt(5);
        multChoiceQuestion = "Which is the correct word?";

    }

    public void setChoices(ObservableList<String> choiceObsList) {
        choices.clear();
        choices.addAll(choiceObsList);
    }
}