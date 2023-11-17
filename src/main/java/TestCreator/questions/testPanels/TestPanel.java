package TestCreator.questions.testPanels;

import TestCreator.questions.Question;
import javafx.scene.Node;

public interface TestPanel<T extends Question> extends GradeableFX {

    void setupQuestion(T question);

    public String getFXMLName();

    public void disableAnswerChanges();

    public Node getRootNode();
}
