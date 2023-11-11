package TestCreator.questions.editorPanels;

import TestCreator.questions.Question;

public interface EditorPanel {
    void setupQuestion(Question fillInQuestion);

    Question getQuestion();
}
