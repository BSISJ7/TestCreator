package TestCreator.utilities;

import TestCreator.Main;
import TestCreator.Test;
import TestCreator.questions.MultipleChoice;
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

    public void addTest(Test newTest) throws IllegalArgumentException{
        testList.forEach(test -> {
            if (newTest.getName().equals(test.getName()))
                throw new IllegalArgumentException("A test with the same name already exists.");
            if(test.getID().equals(newTest.getID()) || test.equals(newTest))
                throw new IllegalArgumentException("The test list already contains this test.");
        });
        testList.add(newTest);
    }

    public void removeTest(Test test) {
        testList.remove(test);
        selectedTest = null;
        if (!testList.isEmpty()) {
            setSelectedTest(testList.getFirst());
            if (!questionList.isEmpty()) selectedQuestion = questionList.getFirst();
        }
    }

    public Test getSelectedTest() {
        return selectedTest;
    }

    public String getSelectedTestName() {
        return selectedTest.getName();
    }

    public void setSelectedTest(Test test) {
        selectedTest = test;
        questionList = selectedTest.getQuestionListCopy();
        selectedQuestion = !questionList.isEmpty() ? questionList.getFirst() : null;
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void selectQuestion(Question question) {
        selectedQuestion = question;
    }

    public ObservableList<Test> getTestlistCopy() {
        return FXCollections.observableArrayList(testList);
    }

    public void addQuestion(Question question) {
        selectedTest.addQuestion(question);
        questionList.add(question);
    }

    public void autoFillTests() {
        if (Main.TESTING_MODE) {
            for (int x = testList.size(); x < 4; x++) {
                Test newTest = new Test(STR. "Test # \{ new Random(nanoTime()).nextInt(999) }" );
                Arrays.stream(Question.QuestionTypes.values()).forEach(questionType -> {
                    String qName = STR. "\{ questionType } # \{ new Random().nextInt(200) }" ;
                    Question newQuestion = Question.createQuestion(qName, questionType, newTest);
                    newQuestion.autofillData();
                    if (newQuestion.readyToRun()) newTest.addQuestion(newQuestion);
                });

                try{
                    addTest(newTest);
                } catch (IllegalArgumentException e) {
                    newTest.setName(STR. "\{ newTest.getName() } # \{ new Random().nextInt(9999) }");
                    addTest(newTest);
                }
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

    public void clearTestList() {
        testList.clear();
    }

    public boolean containsTest(String testID) {
        return testList.stream().anyMatch(test -> test.getID().equals(testID));
    }

    public void selectFirstTest() {
        if (!testList.isEmpty()) {
            selectedTest = testList.getFirst();
            questionList = selectedTest.getQuestionListCopy();
            if (!questionList.isEmpty()) {
                selectedQuestion = questionList.getFirst();
            }
        }
    }

    public void addQuestions(List<MultipleChoice> questionsList) {
        questionsList.forEach(this::addQuestion);
    }
}