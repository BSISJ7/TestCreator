package TestCreator.questions.testPanels;

import TestCreator.questions.Question;
import javafx.scene.Node;

public interface TestPanel<T extends Question> extends GradableFX {

    void setupQuestion(T question);

    Node getRootNode();

    void cleanUp();
}
