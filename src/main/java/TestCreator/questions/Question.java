package TestCreator.questions;

import TestCreator.Test;
import TestCreator.questions.testPanels.TestPanel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static TestCreator.testIO.XMLIO.findNode;

public abstract class Question {

    public static final String sysSeparator;

    public enum QuestionTypes {
        MULTIPLE_CHOICE("MultipleChoice", "Multiple Choice"),
        TRUE_FALSE("TrueFalse", "True False"),
        MATCHING_WORD("MatchingWord", "Matching Word"),
        FILL_THE_BLANK("FillTheBlank", "Fill The Blank"),
        MULTIPLE_CHECKBOX("MultipleCheckBox", "Multiple CheckBox"),
        FLASH_CARD("FlashCard", "Flash Card");

        private final String questionType;
        private final String displayName;

        QuestionTypes(String questionType, String displayName) {
            this.questionType = questionType;
            this.displayName = displayName;
        }

        public String getQuestionType() {
            return questionType;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
            sysSeparator = "\\" + File.separator;
        else
            sysSeparator = File.separator;
    }

    Test test;

    String questionName;
    QuestionTypes questionType;

    float questionWeight;

    Document XMLDocument;

    final String ID = UUID.randomUUID().toString();

    public Question(String questionName, QuestionTypes questionType) {
        this(questionName);
        this.questionType = questionType;
    }

    public Question(String questionName, QuestionTypes questionType, Test test) {
        this(questionName, questionType);
        this.test = test;
    }

    public Question(String questionName) {
        this.questionName = questionName;
        questionWeight = 1.0F;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            XMLDocument = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public Question(String questionName, Test owningTest) {
        this(questionName);
        this.test = owningTest;
    }

    public Question() {
        this("Question");
        questionName = STR."\{questionName} \{ID.substring(0,5)}";
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public Test getOwningTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public float getWeight() {
        return questionWeight;
    }

    public void setQuestionWeight(float questionWeight) {
        this.questionWeight = questionWeight;
    }

    public String getName() {
        return questionName;
    }

    public void setName(String name) {
        questionName = name;
    }

    public Element getQuestionAsXMLNode(Document XMLDocument) {

        Element question = XMLDocument.createElement("Question");

        Element name = XMLDocument.createElement("QuestionName");
        name.setTextContent(questionName);
        question.appendChild(name);

        Element type = XMLDocument.createElement("QuestionType");
        type.setTextContent(questionType.name());
        question.appendChild(type);

        Element weight = XMLDocument.createElement("QuestionWeight");
        weight.setTextContent(Float.toString(questionWeight));
        question.appendChild(weight);

        return question;
    }

    public Element getQuestionAsXMLNode() {
        return getQuestionAsXMLNode(XMLDocument);
    }

    public void loadQuestionFromXMLNode(Node question) {
        questionName = Objects.requireNonNull(findNode("QuestionName", question)).getTextContent();
        questionType = QuestionTypes.valueOf(Objects.requireNonNull(findNode("QuestionType", question)).getTextContent());
        questionWeight = Float.parseFloat(Objects.requireNonNull(findNode("QuestionWeight", question)).getTextContent());
    }

    public Question getCopy() {
        Question copy = createQuestion(questionName, questionType, test);
        copy.loadQuestionFromXMLNode(getQuestionAsXMLNode());
        return copy;
    }

    public boolean readyToRun() {
        return false;
    }

    public int compareTo(Question question) {
        return ID.compareTo(question.getID());
    }


    public abstract TestPanel getTestPanel() throws IOException;


    public int getMaxScore() {
        return 1;
    }

    public String getID() {
        return ID;
    }

    public static Question createQuestion(QuestionTypes questionType) {
        return createQuestion("", questionType, null);
    }

    public static Question createQuestion(String name, QuestionTypes type, Test test){
        switch(type){
            case MATCHING_WORD -> {
                return test == null ? new MatchingWord() : new MatchingWord(name, type, test);
            }case TRUE_FALSE -> {
                return test == null ? new TrueFalse() : new TrueFalse(name, type, test);
            }case FILL_THE_BLANK -> {
                return test == null ? new FillTheBlank() : new FillTheBlank(name, type, test);
            }case MULTIPLE_CHECKBOX -> {
                return test == null ? new MultipleCheckBox() : new MultipleCheckBox(name, type, test);
            }case FLASH_CARD -> {
                return  test == null ? new FlashCard() : new FlashCard(name, type, test);
            }default -> {
                return test == null ? new MultipleChoice() : new MultipleChoice(name, type, test);
            }
        }
    }

    public abstract void autofillData();
}
