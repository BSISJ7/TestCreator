package TestCreator;

import TestCreator.questions.Question;
import TestCreator.testIO.XMLIO;
import TestCreator.testIO.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Test {

    public final static DateTimeFormatter shortDateFormat = DateTimeFormatter.ofPattern("dd/MM/yy");
    public final static DateTimeFormatter shortTimeFormat = DateTimeFormatter.ofPattern("hh:mm");
    private ArrayList<Question> questionList;
    private String testName;
    private String description;
    private LocalDateTime reviewDateTime;
    private LocalDateTime DATE_CREATED;
    private String ID;

    public Test(String name) {
        ID = UUID.randomUUID().toString();
        testName = name;
        reviewDateTime = LocalDateTime.now().plusDays(1L);
        description = "";
        questionList = new ArrayList<Question>();
    }

    public Test(String name, String description) {
        this(name);
        this.description = description;
    }

    /**
     * Creates a new test with the default name "New Test".
     */
    public Test() {
        this("New Test");
    }

    /**
     * Returns the ID for this test.
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets the test ID to the passed value.
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the date this test is set to be reviewed at.
     */
    public LocalDateTime getReviewDate() {
        return reviewDateTime;
    }

    /**
     * Sets the date this test will be reviewed at.
     */
    public void setReviewDate(LocalDate revDate) {
        reviewDateTime = LocalDateTime.of(revDate, reviewDateTime.toLocalTime());
    }

    /**
     * Sets the time this test will be reviewed at.
     */
    public void setReviewDate(LocalTime revTime) {
        reviewDateTime = LocalDateTime.of(reviewDateTime.toLocalDate(), revTime);
    }

    /**
     * Sets the date time this test will be reviewed at.
     */
    public void setReviewDate(LocalDateTime revDateTime) {
        reviewDateTime = revDateTime;
    }

    /**
     * Changes a question at the selected index to the new question.
     */
    public void setQuestionAt(int index, Question newQuestion) {
        questionList.set(index, newQuestion);
    }

    /**
     * Adds a new vocabulary word to questionList ArrayList.
     */
    public void addQuestion(Question newQuestion) {
        newQuestion.setTest(this);
        questionList.add(newQuestion);
    }


    /**
     * Removes the matching question from questionList ArrayList and reduces the totalQuestions by one.
     */
    public void removeQuestion(Question oldQuestion) {
        questionList.remove(oldQuestion);
    }

    /**
     * Removes the question at the selected index from questionList ArrayList and reduces the totalQuestions by one.
     */
    public void removeQuestion(int index) {
        questionList.remove(index);
    }

    /**
     * Returns the ArrayList of questions questionList
     */
    public List<Question> getQuestionList() {
        return questionList;
    }


    /**
     * Returns the name of this test.
     */
    public String getName() {
        return testName;
    }


    /**
     * Sets the name of this test.
     */
    public void setName(String name) {
        testName = name;
    }

    /**
     * Gets the question at the specified index.
     */
    public Question getQuestionAtIndex(int index) {
        return questionList.get(index);
    }


    public Node getTestAsXMLNode(Document XMLDocument) {
        Node testNode = XMLDocument.createElement("Test");

        Node name = XMLDocument.createElement("TestName");
        name.setTextContent(testName);
        testNode.appendChild(name);

        Node description = XMLDocument.createElement("TestDescription");
        description.setTextContent(this.description);
        testNode.appendChild(description);
        questionList.forEach(question -> {
            testNode.appendChild(question.getQuestionAsXMLNode(XMLDocument));
        });

        return testNode;
    }

    public void loadFromXMLNode(Element testNode) {
        testName = XMLIO.findNode("TestName", testNode).getTextContent();
        description = XMLIO.findNode("TestDescription", testNode).getTextContent();
        for (Node questionNode : XmlUtil.asList(testNode.getElementsByTagName("Question"))) {
            try {
                String questionName = XMLIO.findNode("QuestionName", questionNode).getTextContent();
                String questionType = XMLIO.findNode("QuestionType", questionNode).getTextContent();
                Question newQuestion = Question.getQuestionInstance(questionName, questionType, this);
                newQuestion.loadQuestionFromXMLNode(questionNode);
                questionList.add(newQuestion);
            } catch (NullPointerException e) {
                System.out.println();
            }
        }
    }

    public boolean readyToRun() {
        if (questionList == null || questionList.size() == 0)
            return false;
        return questionList.stream().anyMatch(question -> question.readyToRun());
    }
}
