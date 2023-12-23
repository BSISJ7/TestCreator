package TestCreator.utilities;

import java.util.ArrayList;
import java.util.List;

public class SelectionManager {

    public int size() {
        return selectedWordsList.size();
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

    private final List<SelectedWord> selectedWordsList = new ArrayList<>();

    public void addSelectedWord(String word, int positionStart, int positionEnd, STYLE status) {
        selectedWordsList.add(new SelectedWord(word, positionStart, positionEnd, status));
    }

    public boolean isAnswer(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
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
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getWord().equals(word)) {
                return true;
            }
        }
        return false;
    }

    public void removeWord(String word) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getWord().equals(word)) {
                selectedWordsList.remove(selectedWord);
                return;
            }
        }
    }

    public boolean isBetweenIndexes(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (position > selectedWord.getPositionStart() && position < selectedWord.getPositionEnd()) {
                return true;
            }
        }
        return false;
    }

    public static class SelectedWord {
        private String word;
        private final int positionStart;
        private final int positionEnd;
        private final int length;
        private final STYLE style;

        public SelectedWord(String word, int positionStart, int positionEnd, STYLE style) {
            this.word = word;
            this.positionStart = positionStart;
            this.positionEnd = positionEnd;
            this.length = positionEnd - positionStart;
            this.style = style;
        }

        public STYLE getStyle() {
            return style;
        }

        public String getWord() {
            return word;
        }

        public int getLength() {
            return length;
        }

        public int getPositionStart() {
            return positionStart;
        }

        public int getPositionEnd() {
            return positionEnd;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
