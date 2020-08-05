package com.BSISJ7.TestCreator.questions;

import com.BSISJ7.TestCreator.Test;
import com.BSISJ7.TestCreator.questions.editorPanels.EditorPanel;
import com.BSISJ7.TestCreator.questions.testPanels.TestPanel;
import com.BSISJ7.TestCreator.testIO.XMLIO;
import org.reflections.Reflections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.BSISJ7.TestCreator.testIO.XMLIO.findNode;

public abstract class Question implements TestableQuestion{

    final static String INVALID_XML = "[^"
            + "\u0001-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]+";
    private final static List<String> questionTypes = new ArrayList<String>();
    static Set<Class<? extends Question>> questionClasses;
    Test test;
    String questionName = "";

    List<String> tags = new ArrayList();

    LocalDateTime DATE_CREATED = LocalDateTime.now();

    int gradableParts = 0;

    String questionType;

    float questionWeight;

    Document XMLDocument;

    public Question(String questionName, String questionType) {
        this(questionName);
        this.questionType = questionType;
    }

    public Question(String questionName, String questionType, Test test) {
        this(questionName, questionType);
        this.test = test;
    }

    public Question(String questionName) {
        this.questionName = questionName;
        questionType = questionTypes.get(0);
        questionWeight = 1.0F;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            XMLDocument = docBuilder.newDocument();
        } catch (ParserConfigurationException e) {e.printStackTrace();}
    }

    public Question(String questionName, Test owningTest) {
        this(questionName);
        this.test = owningTest;
    }

    public Question() {
        this("New Question");
    }

    public static void initializeQuestionTypeList() {
        /*
         * Slawek
         * https://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection
         */
        Reflections reflection = new Reflections("com.BSISJ7.TestCreator.questions");
        questionClasses = reflection.getSubTypesOf(Question.class);
        questionClasses.forEach(e -> questionTypes.add(e.getSimpleName()));
        Collections.sort(questionTypes);
    }

    public static Question getQuestionInstance(String name, String type, Test test) throws NullPointerException {
        try {
            Constructor<?> constructor =
                    Class.forName("com.BSISJ7.TestCreator.questions." + type)
                            .getConstructor(String.class, String.class, Test.class);

            return (Question) constructor.newInstance(name, type, test);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<String> getQuestionTypesList() {
        return Collections.unmodifiableList(questionTypes);
    }

    public String getType() {
        return this.getClass().getSimpleName();
    }

    public void setQuestionType(String type) {
        questionType = type;
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
        type.setTextContent(questionType);
        question.appendChild(type);

        Element weight = XMLDocument.createElement("QuestionWeight");
        weight.setTextContent(Float.toString(questionWeight));
        question.appendChild(weight);

        return question;
    }

    public Element getQuestionAsXMLNode() {
        return getQuestionAsXMLNode(XMLDocument);
    }

    public Question loadQuestionFromXMLNode(Node question) {
        XMLIO.getInstance();
        questionName = findNode("QuestionName", question).getTextContent();
        questionType = findNode("QuestionType", question).getTextContent();
        questionWeight = Float.parseFloat(findNode("QuestionWeight", question).getTextContent());

        return this;
    }

    public Question getCopy(){
        Question copy = getQuestionInstance(getName(), getType(), test);
        copy.loadQuestionFromXMLNode(getQuestionAsXMLNode());
        return copy;
    }

    public boolean readyToRun(){
        return false;
    }

    public int compareTo(Question question) {
        return this.questionName.compareTo(question.getName());
    }

    public abstract TestPanel getTestPanel() throws IllegalStateException;

    public abstract EditorPanel getEditPanel() throws IllegalStateException;

    public int getGradableParts() {
        return 1;
    }
}
