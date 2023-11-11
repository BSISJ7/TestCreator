package TestCreator.utilities;

import TestCreator.Main;
import TestCreator.Test;
import TestCreator.questions.Question;
import TestCreator.testIO.TestData;

import java.util.Random;

import static java.lang.System.nanoTime;

public class TestingAutofiller {

    public static void generateTests() {
        while (TestData.getInstance().size() < 4) {
            TestData.getInstance().addTest(new Test("Test #" + new Random(nanoTime()).nextInt(999)));
        }

        TestData.getInstance().getTests().forEach(test -> {
            for (int x = 0; test.getQuestionList().size() < 4; x++) {
                String qName = Question.getQuestionTypesList().get(x) + " #" + new Random().nextInt(200);
                Question newQuestion = Question.getQuestionInstance(qName, Question.getQuestionTypesList().get(x), test);
                newQuestion.autofillData();
                if (newQuestion.readyToRun()) {
                    test.addQuestion(newQuestion);
                }
            }
        });
        TestData.getInstance().saveTests();
    }
}
