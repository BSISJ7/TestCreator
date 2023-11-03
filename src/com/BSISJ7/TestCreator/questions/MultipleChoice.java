package com.BSISJ7.TestCreator.questions;

import com.BSISJ7.TestCreator.Test;
import com.BSISJ7.TestCreator.questions.editorPanels.EditorPanel;
import com.BSISJ7.TestCreator.questions.testPanels.TestPanel;
import com.BSISJ7.TestCreator.utilities.Dictionary;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.BSISJ7.TestCreator.testIO.XMLIO.findNode;

public class MultipleChoice extends Question {

    private List<String> choices;

    private String multChoiceQuestion = "";
    private int answerIndex = -1;

    public MultipleChoice(String questionName, List<String> choices) {
        this(questionName);
        this.choices = choices;
    }

    public MultipleChoice(String questionName, int answerIndex, List<String> choices) {
        this(questionName);
        this.choices = choices;
        this.answerIndex = answerIndex;
    }

    public MultipleChoice(String questionName, List<String> choices, Test test) {
        this(questionName, test);
        this.choices = choices;
    }

    public MultipleChoice(String questionName, Test test) {
        this(questionName);
        this.test = test;
    }

    public MultipleChoice(String questionName) {
        super(questionName);
        questionType = "MultipleChoice";
        choices = new ArrayList<String>();
    }

    public MultipleChoice() {
        this("Multiple Choice Question");
    }

    public MultipleChoice(String questionName, String type) {
        this(questionName);
        questionType = type;
    }

    public MultipleChoice(String questionName, String type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public String getAnswer() throws NullPointerException{
        return choices.get(answerIndex);
    }

    public List<String> getChoices() {
        return choices;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int index) {
        answerIndex = index;
    }

    public void setQuestion(String question) {
        multChoiceQuestion = question;
    }

    public String getMultChoiceQuestion() {
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

        for (int x = 0; x < choices.size(); x++) {
            Element choice = XMLDocument.createElement("Choice");
            choice.setTextContent(choices.get(x));
            question.appendChild(choice);
        }
        return question;
    }

    @Override
    public MultipleChoice loadQuestionFromXMLNode(Node questionNode) throws NullPointerException {
        super.loadQuestionFromXMLNode(questionNode);
        multChoiceQuestion = findNode("MultQuestion", questionNode).getTextContent();
        answerIndex = Integer.parseInt(findNode("AnswerIndex", questionNode).getTextContent());
        NodeList choices = ((Element) questionNode).getElementsByTagName("Choice");
        for (int x = 0; x < choices.getLength(); x++) {
            this.choices.add(choices.item(x).getTextContent());
        }

        return this;
    }

    @Override
    public boolean readyToRun() {
        return choices.size() > 0 && answerIndex >= 0 && multChoiceQuestion.length() > 0;
    }

    @Override
    public TestPanel getTestPanel() throws IllegalStateException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(TEST_PANELS_LOCATION
                +"MultipleChoiceTestPanel.fxml"));
        try {
            loader.load();
        } catch (IOException e) {e.printStackTrace();}
        loader.setRoot(new BorderPane());
        TestPanel controller = loader.getController();
        controller.setupQuestion(this);
        return controller;
    }

    @Override
    public EditorPanel getEditPanel() throws IllegalStateException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(EDITOR_PANELS_LOCATION
                +"MultipleChoiceEditor.fxml"));
        try {
            loader.load();
        } catch (IOException e) {e.printStackTrace();}
        loader.setRoot(new BorderPane());
        return loader.<EditorPanel>getController();
    }

    @Override
    public int getGradableParts() {
        return 1;
    }

    @Override
    public void autofillData() {
        if(Dictionary.getDictionary().size() < 1)
            return;

        for(int x = 0; x < 5; x++){
            choices.add(Dictionary.getDictionary().getRandomWord());
        }
        int answerLocation = new Random().nextInt(5);
        answerIndex = answerLocation;
        multChoiceQuestion = "Which is the correct word?";

    }
}