package TestCreator.questions.testPanels;

import javafx.scene.Node;

public interface TestPanel<T> extends GradeableFX {

    public void setupQuestion(T question);

    public String getFXMLName();

    public Node getQuestionScene();

    public void disableAnswerChanges();
}