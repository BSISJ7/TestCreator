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

    private TestManager() {
    }

    public static TestManager getInstance() {
        return TEST_MANAGER;
    }

    public void addTest(Test test) {
        if (!testList.contains(test)) {
            testList.add(test);
        }
    }

    public void removeTest(Test test) {
        testList.remove(test);
        selectedTest = null;
        if (!testList.isEmpty()) {
            setSelectedTest(testList.get(0));
            if (!questionList.isEmpty()) selectedQuestion = questionList.get(0);
        }
    }

    public Test getSelectedTest() {
        return selectedTest;
    }

    public void setSelectedTest(Test test) {
        selectedTest = test;
        questionList = selectedTest.getQuestionList();
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(Question question) {
        selectedQuestion = question;
    }

    public ObservableList<Test> getObservableTestList() {
        return FXCollections.observableArrayList(testList);
    }

    public void addQuestion(Question question) {
        questionList.add(question);
    }

    public void autoFillTests() {
        if (Main.TESTING_MODE) {
            for (int x = testList.size(); x < Question.QuestionTypes.values().length; x++) {
                Test newTest = new Test(STR. "Test # \{ new Random(nanoTime()).nextInt(999) }" );
                Arrays.stream(Question.QuestionTypes.values()).forEach(questionType -> {
                    String qName = STR. "\{ questionType } # \{ new Random().nextInt(200) }" ;
                    Question newQuestion = Question.createQuestion(qName, questionType, newTest);
                    newQuestion.autofillData();
                    if (newQuestion.readyToRun()) newTest.addQuestion(newQuestion);
                });
                addTest(newTest);
            }
            IOManager.getInstance().saveTests();
        }
    }

    public <T extends Question> boolean containsQuestion(T question) {
        return questionList.contains(question);
    }

    public int getNumOfTests() {
        return testList.size();
    }

    public Test getTestAt(int index) {
        return testList.get(index);
    }
}