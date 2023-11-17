package TestCreator.questions.editorPanels;

import TestCreator.questions.Question;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public abstract class QuestionEditor<T extends Question> {

    T question;

    public T getQuestion() {
        return question;
    }

    public abstract void setupQuestion(T question);

    public abstract void setupQuestion();

    public void acceptQuestion() {
        if (!TestManager.getInstance().containsQuestion(question)) {
            TestManager.getInstance().addQuestion(question);
        }
        IOManager.getInstance().saveTests();

        try {
            StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        try {
            StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

}
