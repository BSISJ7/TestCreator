package TestCreator.utilities;

import java.util.ArrayList;
import java.util.List;

public class SelectionManager {

    public void setWordAt(int wordStartIndex, String word) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getPositionStart() == wordStartIndex) {
                selectedWord.setWord(word);
                return;
            }
        }
    }

    public int size() {
        return selectedWordsList.size();
    }

    public enum STYLE {
        ANSWER("-fx-fill: blue;"),
        REMOVAL("-fx-fill: red;"),
        HOVER("-fx-fill: dodgerblue;"),
        DEFAULT("-fx-fill: black;");

        private final String styleColor;

        private STYLE(String color) {
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

    public STYLE getStyle(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getPositionStart() == position) {
                return selectedWord.getStyle();
            }
        }
        return STYLE.DEFAULT;
    }

    public boolean isAnswer(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getPositionStart() == position) {
                return selectedWord.getStyle() == STYLE.ANSWER;
            }
        }
        return false;
    }

    public void removeWordAtPosition(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (selectedWord.getPositionStart() == position) {
                selectedWordsList.remove(selectedWord);
                return;
            }
        }
    }

    public void clearList() {
        selectedWordsList.clear();
    }

    public boolean containsWord(String word, int positionStart, int positionEnd) {
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

    public int isBetweenIndexes(int position) {
        for (SelectedWord selectedWord : selectedWordsList) {
            if (position > selectedWord.getPositionStart() && position < selectedWord.getPositionEnd()) {
                return selectedWord.getPositionStart();
            }
        }
        return -1;
    }

    public class SelectedWord {
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
