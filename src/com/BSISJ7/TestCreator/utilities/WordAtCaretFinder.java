package com.BSISJ7.TestCreator.utilities;

//http://www.codesenior.com/en/tutorial/Java-How-to-Get-Word-From-Caret-Position
public class WordAtCaretFinder {

    public static String getWordAtCaret(String content, int caretPosition) {
        try {
            if (content.length() == 0) {
                return "";
            }
            //replace non breaking character with space
            content = content.replace(String.valueOf((char) 160), " ");

            int lastSpace = content.lastIndexOf(" ", caretPosition - 1);
            int lastLineBreak = content.lastIndexOf("\n", caretPosition - 1);
            int selectionStart = lastSpace > lastLineBreak ? lastSpace : lastLineBreak;
            if (selectionStart == -1) {
                selectionStart = 0;
            } else {
                //ignore space character
                selectionStart += 1;
            }
            content = content.substring(selectionStart);
            int i = 0;
            int length = content.length();
            while (i != length && !(content.substring(i, i + 1)).equals(" ") && !(content.substring(i, i + 1)).equals("\n")) {
                i++;
            }
            content = content.substring(0, i);
            return content;
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    public static int getPositionStart(String content, int caretPosition){

        if (content.length() == 0) {
            return 0;
        }
        //replace non breaking character with space
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
