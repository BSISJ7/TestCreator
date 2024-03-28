package TestCreator.audio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextDetector {

    private final List<String> TEXT_LIST = new ArrayList<>();

    public TextDetector(List<String> text) {
        TEXT_LIST.addAll(text);
    }

    public TextDetector(String[] textArray) {
        TEXT_LIST.addAll(Arrays.asList(textArray));
    }

    public TextDetector(){}

    public void addWord(String text) {
        TEXT_LIST.add(text);
    }

    public void removeWord(String text) {
        TEXT_LIST.remove(text);
    }

    public void clearText() {
        TEXT_LIST.clear();
    }

    public List<String> getText() {
        return new ArrayList<>(TEXT_LIST);
    }

    public boolean containsMatch(String text) {
        for (String listText : TEXT_LIST) {
            if (text.contains(listText)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsWord(String text) {
        return TEXT_LIST.contains(text);
    }
}
