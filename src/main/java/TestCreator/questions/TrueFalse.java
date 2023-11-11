package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.editorPanels.EditorPanel;
import TestCreator.questions.testPanels.TestPanel;
import TestCreator.utilities.StageManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;

import static TestCreator.testIO.XMLIO.findNode;
import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public class TrueFalse extends Question {

    private String question;
    private boolean trueFalse = true;

    public TrueFalse() {
        this("True False Question");
    }

    public TrueFalse(String questionName) {
        super(questionName);
        questionType = "TrueFalse";
        question = "";
    }

    public TrueFalse(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public TrueFalse(String questionName, String type) {
        this(questionName);
        questionType = type;
    }

    public TrueFalse(String questionName, String type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public String getTrueFalseQuestion() {
        return question;
    }

    public void setTrueFalseQuestion(String question) {
        this.question = question;
    }

    public void setTrueFalse(boolean boolAnswer) {
        trueFalse = boolAnswer;
    }

    public boolean trueSelected() {
        return trueFalse;
    }

    @Override
    public boolean readyToRun() {
        if (question.equals(""))
            return false;
        else
            return true;
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element questionNode = super.getQuestionAsXMLNode(XMLDocument);

        Element question = XMLDocument.createElement("TrueFalseQuestion");
        question.setTextContent(this.question);
        questionNode.appendChild(question);

        Element trueFalseElement = XMLDocument.createElement("TrueFalse");
        trueFalseElement.setTextContent(Boolean.toString(trueFalse));
        questionNode.appendChild(trueFalseElement);

        return questionNode;
    }

    @Override
    public TrueFalse loadQuestionFromXMLNode(Node questionNode) {
        super.loadQuestionFromXMLNode(questionNode);
        this.question = findNode("TrueFalseQuestion", questionNode).getTextContent();
        trueFalse = Boolean.parseBoolean(findNode("TrueFalse", questionNode).getTextContent());

        return this;
    }

    @Override
    public TestPanel getTestPanel() throws IllegalStateException {
        return null;
    }

    @Override
    public EditorPanel getEditPanel() throws IllegalStateException {
        try{
            StageManager.setScene(("/questions/editorPanels/TrueFalseEditor.fxml"));
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public int getGradableParts() {
        return 1;
    }

    @Override
    public void autofillData() {
        question = "Is this a true false question?";
        trueFalse = true;

    }
}
