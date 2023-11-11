package TestCreator.utilities;

//http://www.codesenior.com/en/tutorial/Java-How-to-Get-Word-From-Caret-Position
public class WordAtCaretFinder {

    public static String getWordAtCaret(String hoveredWord, int caretPosition) {
        try {
            if (hoveredWord.isEmpty()) return "";

            //replace non-breaking character with space
            hoveredWord = hoveredWord.replace(String.valueOf((char) 160), " ");

            int lastSpace = hoveredWord.lastIndexOf(" ", caretPosition - 1);
            int lastLineBreak = hoveredWord.lastIndexOf("\n", caretPosition - 1);
            int selectionStart = Math.max(lastSpace, lastLineBreak) == -1 ? 0 : Math.max(lastSpace, lastLineBreak) + 1;
            hoveredWord = hoveredWord.substring(selectionStart);
            int i = 0;
            int length = hoveredWord.length();
            while (i != length && hoveredWord.charAt(i) != ' ' && hoveredWord.charAt(i) != '\n') {
                i++;
            }
            hoveredWord = hoveredWord.substring(0, i);
            return hoveredWord;
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static int getPositionStart(String content, int caretPosition) {

        if (content.isEmpty()) {
            return 0;
        }
        //replace non-breaking character with space
        content = content.replace(String.valueOf((char) 160), " ");

        int lastSpace = content.lastIndexOf(" ", caretPosition - 1);
        int lastLineBreak = content.lastIndexOf("\n", caretPosition - 1);
        int selectionStart = Math.max(lastSpace, lastLineBreak);
        if (selectionStart == -1) {
            selectionStart = 0;
        } else {
            //ignore space character
            selectionStart += 1;
        }
        return selectionStart;
    }
}
