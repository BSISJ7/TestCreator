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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.UUID;

import static TestCreator.testIO.XMLIO.findNode;

public abstract class Question implements TestableQuestion {

    public static final String sysSeparator;

    public enum QuestionTypes {
        MULTIPLE_CHOICE("MultipleChoice", "Multiple Choice"),
        TRUE_FALSE("TrueFalse", "True False"),
        MATCHING_WORD("MatchingWord", "Matching Word"),
        FILL_IN_THE_BLANK("FillInTheBlank", "Fill In The Blank");

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

    String ID = UUID.randomUUID().toString();

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
        questionType = QuestionTypes.MULTIPLE_CHOICE;
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
        this(STR."Question");
        questionName = STR."\{questionName} \{ID.substring(0,5)}";
    }

    public static Question getQuestionInstance(String name, QuestionTypes type, Test test) throws NullPointerException {
        try {
            Constructor<?> constructor =
                    Class.forName("TestCreator.questions." + type)
                            .getConstructor(String.class, String.class, Test.class);

            return (Question) constructor.newInstance(name, type, test);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException |
                 IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
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
        type.setTextContent(questionType.getQuestionType());
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
        Question copy = getQuestionInstance(getName(), QuestionTypes.valueOf(getType()), test);
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


    public int getGradableParts() {
        return 1;
    }

    public String getID() {
        return ID;
    }

    public static Question createQuestion(QuestionTypes questionType) {
        switch(questionType){
            case MATCHING_WORD -> {
                return new MatchingWord();
            }case TRUE_FALSE -> {
                return new TrueFalse();
            }case FILL_IN_THE_BLANK -> {
                return new FillInTheBlank();
            }default -> {
                return new MultipleChoice();
            }
        }
    }
}
