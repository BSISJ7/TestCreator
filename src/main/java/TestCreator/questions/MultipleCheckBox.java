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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static TestCreator.testIO.XMLIO.findNode;

public class MultipleCheckBox extends Question {

    private final List<CheckBoxAnswer> answersList = new ArrayList<>();

    private String checkBoxQuestion = "";

    public static final int MAX_CHOICES = 15;

    public MultipleCheckBox(String questionName, QuestionTypes type, Test test) {
        this(questionName, type);
        this.test = test;
    }

    public MultipleCheckBox(String questionName, QuestionTypes type) {
        this(questionName);
        questionType = type;
    }

    public MultipleCheckBox(String questionName) {
        super(questionName);
        questionType = QuestionTypes.MULTIPLE_CHECKBOX;
    }

    public MultipleCheckBox() {
        super();
        questionType = QuestionTypes.MULTIPLE_CHECKBOX;
        questionName = STR."\{questionType.getDisplayName()} \{ID.substring(0,5)}";
    }

    @Override
    public TestPanel<MultipleCheckBox> getTestPanel() throws IOException {
        return (TestPanel<MultipleCheckBox>) StageManager.getController("/questions/testPanels/MultipleCheckBoxTestPanel.fxml");
    }

    @Override
    public Element getQuestionAsXMLNode(Document XMLDocument) {
        Element question = super.getQuestionAsXMLNode(XMLDocument);

        Element checkBoxQuestionNode = XMLDocument.createElement("CheckBoxQuestion");
        checkBoxQuestionNode.setTextContent(checkBoxQuestion);
        question.appendChild(checkBoxQuestionNode);

        Element answersNode = XMLDocument.createElement("Answers");
        for(CheckBoxAnswer answer : this.answersList){
            Element answerNode = XMLDocument.createElement("Answer");
            answerNode.setAttribute("isCorrect", String.valueOf(answer.isCorrect));
            answerNode.appendChild(XMLDocument.createTextNode(answer.answer));
            answersNode.appendChild(answerNode);
        }
        question.appendChild(answersNode);

        return question;
    }

    @Override
    public void loadQuestionFromXMLNode(Node questionNode) throws NullPointerException{
        super.loadQuestionFromXMLNode(questionNode);
        checkBoxQuestion = Objects.requireNonNull(findNode("CheckBoxQuestion", questionNode)).getTextContent();
        NodeList answers = ((Element) questionNode).getElementsByTagName("Answer");
        for (int x = 0; x < answers.getLength(); x++) {
            this.answersList.add(new CheckBoxAnswer(answers.item(x).getTextContent(),
                    answers.item(x).getAttributes().getNamedItem("isCorrect").getTextContent().equals("true")));
        }
    }

    @Override
    public String loadFromSQLStatement(String sqlStatement) {
        return null;
    }

    public void autofillData() {
        checkBoxQuestion = "Which sums are correct?";

        int num1 = 100 + (int)(Math.random() * ((3000 - 100) + 1));
        int num2 = 100 + (int)(Math.random() * ((3000 - 100) + 1));
        answersList.add(new CheckBoxAnswer(STR."\{num1} + \{num2} = \{num1 + num2}", true));

        while(answersList.size() < 5){
            boolean isCorrect = Math.random() < 0.50;
            if(isCorrect){
                int x = 100 + (int)(Math.random() * ((3000 - 100) + 1));
                int y = 100 + (int)(Math.random() * ((3000 - 100) + 1));
                answersList.add(new CheckBoxAnswer(STR."\{x} + \{y} = \{x + y}", true));
            }else{
                int x = 100 + (int)(Math.random() * ((3000 - 100) + 1));
                int y = 100 + (int)(Math.random() * ((3000 - 100) + 1));
                answersList.add(new CheckBoxAnswer(STR."\{x} + \{y} = \{x - y}", false));
            }
        }
    }

    public int getMaxScore() {
        return answersList.size();
    }

    public int getNumChoices(){
        return answersList.size();
    }

    public String getQuestionText() {
        return checkBoxQuestion;
    }

    public ObservableList<CheckBoxAnswer> getChoicesCopy() {
        return FXCollections.observableArrayList(answersList);
    }

    public void setQuestionText(String text) {
        checkBoxQuestion = text;
    }

    public void setAnswers(ObservableList<CheckBoxAnswer> answersList) {
        this.answersList.clear();
        this.answersList.addAll(answersList);
    }

    public boolean isCorrect(int i) {
        return answersList.get(i).isCorrect();
    }

    public boolean isCorrect(String answer) {
        for(CheckBoxAnswer checkBoxAnswer : answersList){
            if(checkBoxAnswer.getAnswer().equals(answer)){
                return checkBoxAnswer.isCorrect();
            }
        }
        return false;
    }


    public static class CheckBoxAnswer{
        private final String answer;
        private boolean isCorrect;

        public CheckBoxAnswer(String answer, boolean isCorrect) {
            this.answer = answer;
            this.isCorrect = isCorrect;
        }

        public String getAnswer() {
            return answer;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public void setCorrect(boolean isCorrect) {
            this.isCorrect = isCorrect;
        }

    }

    @Override
    public boolean readyToRun() {
        if(answersList.isEmpty() || checkBoxQuestion.isEmpty()){
            return false;
        }

        for(CheckBoxAnswer answer : answersList){
            if(answer.isCorrect())
                return true;
        }

        return false;
    }
}