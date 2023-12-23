package TestCreator.questions.editorPanels;

import TestCreator.questions.Question;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public abstract class QuestionEditor<T extends Question> {

    T question;

    boolean editingQuestion = false;

    StackPane rootNode;

    public T getQuestion() {
        return question;
    }

    public abstract void setupQuestion(T question);

    public void setupQuestion(T question, boolean editing, StackPane rootNode){
        this.rootNode = rootNode;
        this.editingQuestion = editing;
        setupQuestion(question);
    }


    public void acceptQuestion() {
        if (!TestManager.getInstance().containsQuestion(question) && !editingQuestion)
            TestManager.getInstance().addQuestion(question);

        updateQuestion();
        IOManager.getInstance().saveTests();
        TestManager.getInstance().selectQuestion(question);
        try {
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        try {
            if(editingQuestion){
                StageManager.setScene("/MainMenu.fxml");
                StageManager.clearStageController();
            }
            else{
                StageManager.setScene("/questions/editorPanels/NewQuestionEditor.fxml");
            }
        } catch (IOException e) {
            new StackPaneAlert(rootNode, "Error loading NewQuestionEditor.fxml").show();
            throw new RuntimeException(e);
        }
    }

    public abstract void updateQuestion();
}
