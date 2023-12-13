package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.StageManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.Objects;

import static TestCreator.testIO.XMLIO.findNode;

public class TrueFalse extends Question {

    private String questionText = "";
    private boolean isTrue = true;

    public TrueFalse() {
        super();
        questionType = QuestionTypes.TRUE_FALSE;
    }

    public TrueFalse(String questionName) {
        super(questionName);
        questionType = QuestionTypes.TRUE_FALSE;
        questionText = "";
    }

    public TrueFalse(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public TrueFalse(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public TrueFalse(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public String getTrueFalseQuestion() {
        return questionText;
    }

    public void setTrueFalseQuestion(String question) {
        this.questionText = question;
    }

    public void setTrue(boolean boolAnswer) {
        isTrue = boolAnswer;
    }

    public boolean trueSelected() {
        return isTrue;
    }

    @Override
    public boolean readyToRun() {
        return !questionText.isEmpty();
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element questionNode = super.getQuestionAsXMLNode(XMLDocument);

        Element question = XMLDocument.createElement("TrueFalseQuestion");
        question.setTextContent(this.questionText);
        questionNode.appendChild(question);

        Element trueFalseElement = XMLDocument.createElement("TrueFalse");
        trueFalseElement.setTextContent(Boolean.toString(isTrue));
        questionNode.appendChild(trueFalseElement);

        return questionNode;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        this.questionText = Objects.requireNonNull(findNode("TrueFalseQuestion", questionNode)).getTextContent();
        isTrue = Boolean.parseBoolean(Objects.requireNonNull(findNode("TrueFalse", questionNode)).getTextContent());
    }

    public TestPanel getTestPanel() throws IllegalStateException, IOException {
        return (TestPanel) StageManager.getController("/questions/testPanels/TrueFalseTestPanel.fxml");
    }

    @Override
    public int getMaxScore() {
        return 1;
    }

    @Override
    public void autofillData() {

        int num1 = 100 + (int)(Math.random() * ((3000 - 100) + 1));
        int num2 = 100 + (int)(Math.random() * ((3000 - 100) + 1));
        float halfChance = (float) Math.random();
        questionText = STR."The sum of \{num1} and \{num2} is \{(halfChance < 0.5) ? (num1 + num2) : (num1 - num2)}";
        isTrue = halfChance < 0.5;
    }
}
