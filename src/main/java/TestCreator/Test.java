package TestCreator;

import TestCreator.dataIO.XMLIO;
import TestCreator.dataIO.XmlUtil;
import TestCreator.questions.Question;
import TestCreator.users.User;
import TestCreator.users.UserManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Test {
    public final static DateTimeFormatter shortDateFormat = DateTimeFormatter.ofPattern("dd/MM/yy");
    public final static DateTimeFormatter shortTimeFormat = DateTimeFormatter.ofPattern("hh:mm");
    private final List<Question> questionList = new ArrayList<>();
    private String testName;
    private String description = "";
    private String ID = UUID.randomUUID().toString();

    private User user;

    public Test(String name) {
        testName = STR."\{name} \{ID.substring(0, 5)}";
    }

    public Test(String name, User user) {
        this(name);
        this.user = user;
    }

    public Test(String name, String description, User user) {
        this(name);
        this.description = description;
        this.user = user;
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
    public List<Question> getQuestionListCopy() {
        return new ArrayList<>(questionList);
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

        Node testID = XMLDocument.createElement("ID");
        testID.setTextContent(ID);
        testNode.appendChild(testID);

        Node name = XMLDocument.createElement("TestName");
        name.setTextContent(testName);
        testNode.appendChild(name);

        Node description = XMLDocument.createElement("TestDescription");
        description.setTextContent(this.description);
        testNode.appendChild(description);
        questionList.forEach(question -> testNode.appendChild(question.getQuestionAsXMLNode(XMLDocument)));

        return testNode;
    }

    public void loadFromXMLNode(Element testNode) {
        Node IDNode = XMLIO.findNode("ID", testNode);
        ID = IDNode == null ? "" : IDNode.getTextContent();

        Node nameNode = XMLIO.findNode("TestName", testNode);
        testName = nameNode == null ? "" : nameNode.getTextContent();

        Node descriptionNode = XMLIO.findNode("TestDescription", testNode);
        description = descriptionNode == null ? "" : descriptionNode.getTextContent();

        for (Node questionNode : XmlUtil.asList(testNode.getElementsByTagName("Question"))) {
            try {
                String questionName = Objects.requireNonNull(XMLIO.findNode("QuestionName", questionNode)).getTextContent();
                Question.QuestionTypes questionType = Question.QuestionTypes.valueOf(Objects.requireNonNull(XMLIO.
                        findNode("QuestionType", questionNode)).getTextContent());
                Question newQuestion = Question.createQuestion(questionName, questionType, this);
                newQuestion.loadQuestionFromXMLNode(questionNode);
                questionList.add(newQuestion);
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String getAsSQLStatement() {
        StringBuilder sqlStatement = new StringBuilder("INSERT INTO Tests (ID, TestName, TestDescription, UserID, QuestionIDs) VALUES (");
        sqlStatement.append("'").append(ID).append("', ");
        sqlStatement.append("'").append(testName).append("', ");
        sqlStatement.append("'").append(description).append("', ");
        sqlStatement.append("'").append(user.getUsername()).append("', ");

        StringBuilder questionIDs = new StringBuilder();
        for (Question question : questionList) {
            questionIDs.append(question.getID()).append(",");
        }

        if (!questionIDs.isEmpty()) {
            questionIDs.setLength(questionIDs.length() - 1);
        }
        sqlStatement.append("'").append(questionIDs).append("');");

        return sqlStatement.toString();
    }



    public void loadFromSQLStatement(String sqlStatement) {
        UserManager userManager = new UserManager();
        String[] values = sqlStatement.split("VALUES")[1].split(",");
        ID = values[0].replaceAll("'", "").trim();
        testName = values[1].replaceAll("'", "").trim();
        description = values[2].replaceAll("'", "").trim();
        user = userManager.getUser(values[3].replaceAll("'", "").trim());
        String[] questionIDs = values[4].replaceAll("'", "").replaceAll("\\)", "").trim().split(",");
        for (String questionID : questionIDs) {
//            questionList.addWord();
        }
    }

    public boolean notReadyToRun() {
        if (questionList.isEmpty())
            return true;
        return questionList.stream().noneMatch(Question::readyToRun);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Test test = (Test) obj;
        return Objects.equals(ID, test.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}