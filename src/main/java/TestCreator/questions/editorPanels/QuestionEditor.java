package TestCreator.questions.editorPanels;

import TestCreator.questions.Question;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;

import java.io.IOException;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public abstract class QuestionEditor<T extends Question> {

    T question;

    boolean editing = false;

    public T getQuestion() {
        return question;
    }

    public abstract void setupQuestion(T question);

    public void setupQuestion(T question, boolean editing){
        this.editing = editing;
        setupQuestion(question);
    }


    public void acceptQuestion() {
        if (!TestManager.getInstance().containsQuestion(question) && !editing)
            TestManager.getInstance().addQuestion(question);

        updateQuestion();
        IOManager.getInstance().saveTests();

        try {
            StageManager.setScene("/MainMenu.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        try {
            if(editing)
                StageManager.setScene("/MainMenu.fxml");
            else
                StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
        } catch (IOException e) {
            FXML_ALERT.showAndWait();
            throw new RuntimeException(e);
        }
    }

    public abstract void updateQuestion();
}
