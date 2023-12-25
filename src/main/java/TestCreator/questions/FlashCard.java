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

public class FlashCard extends Question {

    private String flashQuestion;
    private String flashAnswer;

    public FlashCard() {
        super();
        questionType = QuestionTypes.FLASH_CARD;
        questionName = STR."\{questionType.getDisplayName()} \{ID.substring(0,5)}";
    }

    public FlashCard(String questionName) {
        super(questionName);
        questionType = QuestionTypes.FLASH_CARD;
    }

    public FlashCard(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public FlashCard(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public FlashCard(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public String getFlashQuestion() {
        return flashQuestion;
    }

    public void setFlashQuestion(String flashQuestion) {
        this.flashQuestion = flashQuestion;
    }

    public String getFlashAnswer() {
        return flashAnswer;
    }

    public void setFlashAnswer(String flashAnswer) {
        this.flashAnswer = flashAnswer;
    }

    @Override
    public boolean readyToRun() {
        return flashQuestion != null && !flashQuestion.isEmpty() && flashAnswer != null && !flashAnswer.isEmpty();
    }

    @Override
    public TestPanel<FlashCard> getTestPanel() throws IOException {
        Object testPanel = StageManager.getController("/questions/testPanels/FlashCardTestPanel.fxml");
        if(!(testPanel instanceof TestPanel))
            throw new IOException("TestPanel not found");
        return (TestPanel<FlashCard>) testPanel;
    }

    public void autofillData() {
        int random = (int) (Math.random() * 100);
        int random2 = (int) (Math.random() * 100);
        flashQuestion = STR."\{random} + \{random2} = ?";
        flashAnswer = STR."\{random + random2}";
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);

        Element flashQuestionNode = XMLDocument.createElement("FlashCardQuestion");
        flashQuestionNode.setTextContent(flashQuestion);
        question.appendChild(flashQuestionNode);

        Element flashAnswerNode = XMLDocument.createElement("FlashCardAnswer");
        flashAnswerNode.setTextContent(flashAnswer);
        question.appendChild(flashAnswerNode);

        return question;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) throws NullPointerException{
        super.loadQuestionFromXMLNode(questionNode);
        flashQuestion = Objects.requireNonNull(findNode("FlashCardQuestion", questionNode)).getTextContent();
        flashAnswer = Objects.requireNonNull(findNode("FlashCardAnswer", questionNode)).getTextContent();
    }
}

