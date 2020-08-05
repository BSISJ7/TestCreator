package com.BSISJ7.TestCreator.questions.editorPanels;

import com.BSISJ7.TestCreator.questions.Question;

public interface EditorPanel {
    void setupQuestion(Question fillInQuestion);
    Question getQuestion();
}
