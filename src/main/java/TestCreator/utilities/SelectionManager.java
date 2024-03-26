package TestCreator.utilities;

import TestCreator.questions.FillTheBlank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelectionManager {

    public List<SelectionAnswer> getAnswerList() {
        return selectedWordsList;
    }

    public enum STYLE {
        ANSWER("-fx-fill: blue;"),
        REMOVAL("-fx-fill: red;"),
        HOVER("-fx-fill: dodgerblue;"),
        DEFAULT("-fx-fill: black;");

        private final String styleColor;

        STYLE(String color) {
            this.styleColor = color;
        }

        public String getStyle() {
            return styleColor;
        }
    }

    private final List<SelectionAnswer> selectedWordsList = new ArrayList<>();

    public int size() {
        return selectedWordsList.size();
    }

    public void sortList() {
        selectedWordsList.sort(Comparator.comparingInt(FillTheBlank.FillAnswer::getPositionStart));
    }

    public List<Integer> getAnswerOffsets() {
        List<Integer> answerOffsets = new ArrayList<>();
        for (SelectionAnswer selectedWord : selectedWordsList) {
            if (selectedWord.getStyle() == STYLE.ANSWER) {
                answerOffsets.add(selectedWord.getPositionStart());
            }
        }
        return answerOffsets;
    }

    public int getOffset(int x) {
        return selectedWordsList.get(x).getPositionStart();
    }

    public List<String> getAnswers() {
        StringBuilder answers = new StringBuilder();
        for (SelectionAnswer selectedWord : selectedWordsList) {
            if (selectedWord.getStyle() == STYLE.ANSWER) {
                answers.append(selectedWord.getWord()).append(" ");
            }
        }
        return List.of(answers.toString().split(" "));
    }

    public void setOffsets(List<Integer> offsets) {
        for (int x = 0; x < offsets.size(); x++) {
            selectedWordsList.get(x).setWord(String.valueOf(offsets.get(x)));
        }
    }

    public void addSelectedWord(String word, int positionStart, int positionEnd, STYLE status) {
        selectedWordsList.add(new SelectionAnswer(word, positionStart, positionEnd, status));
    }

    public boolean isAnswer(int position) {
        for (SelectionAnswer selectedWord : selectedWordsList) {
            if (selectedWord.getPositionStart() == position) {
                return selectedWord.getStyle() == STYLE.ANSWER;
            }
        }
        return false;
    }

    public void clearList() {
        selectedWordsList.clear();
    }

    public boolean containsWord(String word) {
        for (FillTheBlank.FillAnswer selectedWord : selectedWordsList) {
            if (selectedWord.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }

    public void removeWord(String word) {
        for (FillTheBlank.FillAnswer selectedWord : selectedWordsList) {
            if (selectedWord.getWord().equals(word)) {
                selectedWordsList.remove(selectedWord);
                return;
            }
        }
    }

    public boolean isBetweenIndexes(int position) {
        for (FillTheBlank.FillAnswer selectedWord : selectedWordsList) {
            if (position > selectedWord.getPositionStart() && position < selectedWord.getPositionEnd()) {
                return true;
            }
        }
        return false;
    }

    public static class SelectionAnswer extends FillTheBlank.FillAnswer{

        public static final String ANSWER = ("-fx-fill: blue;");
        public static final String DEFAULT = ("-fx-fill: black;");
        public static final String CORRECT = ("-fx-fill: green;");
        public static final String INCORRECT = ("-fx-fill: red;");
        public static final String ALLOWED_CHARS = "[a-zA-Z0-9-'_]";
        private final SelectionManager.STYLE style;

        public SelectionAnswer(String word, int positionStart, int positionEnd, STYLE style) {
            super(word, positionStart, positionEnd);
            this.style = style;
        }

        public SelectionManager.STYLE getStyle() {
            return style;
        }

        public FillTheBlank.FillAnswer getFillAnswer() {
            return new FillTheBlank.FillAnswer(getWord(), getPositionStart(), getPositionEnd());
        }
    }
}