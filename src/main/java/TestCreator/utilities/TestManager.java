package TestCreator.utilities;

import TestCreator.Main;
import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.testIO.IOManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.System.nanoTime;

public class TestManager {
    private static final List<Test> testList = FXCollections.observableArrayList();
    private static Test selectedTest;
    private static List<Question> questionList = FXCollections.observableArrayList();
    private static Question selectedQuestion;

    public static final TestManager TEST_MANAGER = new TestManager();

    private TestManager() {}

    public static TestManager getInstance() {
        return TEST_MANAGER;
    }

    public void addTest(Test test) {
        if (!testList.contains(test))
            testList.add(test);
    }

    public void removeTest(Test test) {
        testList.remove(test);
        if(!testList.isEmpty()) {
            selectedTest = testList.get(0);
            questionList = selectedTest.getQuestionList();
            if (!questionList.isEmpty()) selectedQuestion = questionList.get(0);
        }
    }

    public void removeQuestion(Question question) {
        questionList.remove(question);
        if(!questionList.contains(selectedQuestion) && !questionList.isEmpty())
            selectedQuestion = questionList.get(0);
    }

    public Test findTestByID(String testID) throws NullPointerException{
        for (Test test : testList) {
            if (test.getID().equals(testID)) {
                return test;
            }
        }
        throw new NullPointerException("Test not found");
    }

    public Question findQuestionByID(String questionID) throws NullPointerException{
        for (Question question : questionList) {
            if (question.getID().equals(questionID)) {
                return question.getCopy();
            }
        }
        throw new NullPointerException("Question not found");
    }

    public Test getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Test test) {
        selectedTest = test;

    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(Question question) { selectedQuestion = question;
    }

    public ObservableList<Test> getObservableTestList() {
        return FXCollections.observableArrayList(FXCollections.observableArrayList(testList));
    }

    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public void autoFillTests() {
        if(Main.TESTING_MODE) {
            for(int x = testList.size(); x < 4; x++)
                addTest(new Test(STR."Test # \{new Random(nanoTime()).nextInt(999)}"));

            testList.forEach(test -> {
                Arrays.asList(Question.QuestionTypes.values()).forEach(questionType -> {
                    String qName = STR."\{questionType} # \{new Random().nextInt(200)}";
                    Question newQuestion = Question.getQuestionInstance(qName, questionType, test);
                    assert newQuestion != null; //TODO Review this
                    newQuestion.autofillData();
                    if (newQuestion.readyToRun()) test.addQuestion(newQuestion);
                });
            });
            IOManager.getInstance().saveTests();
        }
    }

    public void deselectTest() {
        selectedTest = null;
    }

    public void deselectQuestion() {
        selectedQuestion = null;
    }

    public <T extends Question> boolean containsQuestion(T question) {
        return questionList.contains(question);
    }

    public int getNumOfQuestions() {
        return questionList.size();
    }

    public int getNumOfTests() {
        return testList.size();
    }
}