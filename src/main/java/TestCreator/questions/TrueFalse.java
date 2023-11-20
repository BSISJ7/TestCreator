package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.StageManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;

import static TestCreator.testIO.XMLIO.findNode;

public class TrueFalse extends Question {

    private String questionText = "";
    private boolean trueFalse = true;

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

    public void setTrueFalse(boolean boolAnswer) {
        trueFalse = boolAnswer;
    }

    public boolean trueSelected() {
        return trueFalse;
    }

    @Override
    public boolean readyToRun() {
        if (questionText.equals(""))
            return false;
        else
            return true;
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element questionNode = super.getQuestionAsXMLNode(XMLDocument);

        Element question = XMLDocument.createElement("TrueFalseQuestion");
        question.setTextContent(this.questionText);
        questionNode.appendChild(question);

        Element trueFalseElement = XMLDocument.createElement("TrueFalse");
        trueFalseElement.setTextContent(Boolean.toString(trueFalse));
        questionNode.appendChild(trueFalseElement);

        return questionNode;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        this.questionText = findNode("TrueFalseQuestion", questionNode).getTextContent();
        trueFalse = Boolean.parseBoolean(findNode("TrueFalse", questionNode).getTextContent());
    }

    public TestPanel getTestPanel() throws IllegalStateException, IOException {
        return (TestPanel) StageManager.getController("/questions/testPanels/TrueFalseTestPanel.fxml");
    }

    @Override
    public int getGradableParts() {
        return 1;
    }

    @Override
    public void autofillData() {
        questionText = "Is this a true false question?";
        trueFalse = true;
    }
}
