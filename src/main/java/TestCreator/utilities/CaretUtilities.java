package TestCreator.utilities;

//http://www.codesenior.com/en/tutorial/Java-How-to-Get-Word-From-Caret-Position
public class CaretUtilities {

    public static final String REPLACE_REGEX = "[.!?]+(?=\\s|$)";

    /**
     * This method retrieves the word at the caret position in a given text.
     *
     * @param text The text from which to extract the word.
     * @param caretPosition The position of the caret in the text.
     * @return The word at the caret position. If the caret is at a break character, an empty string is returned.
     */
    public static String getWordAtCaret(String text, int caretPosition) {
        try {
            if (text.isEmpty() || caretPosition < 0 || caretPosition >= text.length()) return "";

            //replace non-breaking character with space
            text = text.replace(String.valueOf((char) 160), " ");

            int lastSpace = text.lastIndexOf(" ", caretPosition - 1);
            int lastLineBreak = text.lastIndexOf("\n", caretPosition - 1);
            int selectionStart = Math.max(lastSpace, lastLineBreak) == -1 ? 0 : Math.max(lastSpace, lastLineBreak) + 1;
            text = text.substring(selectionStart);
            int i = 0;
            int length = text.length();
            while (i != length && text.charAt(i) != ' ' && text.charAt(i) != '\n') {
                i++;
            }
            text = text.substring(0, i);
            return text.replaceAll(REPLACE_REGEX, "");
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * This method retrieves the word at the caret position in a given text, considering a set of break characters.
     *
     * @param text The text from which to extract the word.
     * @param caretPosition The position of the caret in the text.
     * @param breakCharactersRegex A string containing characters that are considered as word separators.
     * @return The word at the caret position. If the caret is at a break character, an empty string is returned.
     */
    public static String getWordAtCaret(String text, int caretPosition, String breakCharactersRegex) {
        if (text.isEmpty() || caretPosition < 0 || caretPosition >= text.length()) return "";

        int startIndex = caretPosition;
        while (startIndex > 0 && !String.valueOf(text.charAt(startIndex)).matches(breakCharactersRegex)) {
            if (String.valueOf(text.charAt(startIndex - 1)).matches(breakCharactersRegex)) break;
            startIndex--;
        }
        int endIndex = caretPosition;
        while (endIndex < text.length() && !String.valueOf(text.charAt(endIndex)).matches(breakCharactersRegex)) {
            endIndex++;
        }

        return text.substring(startIndex, endIndex);
    }


    /**
     * This method retrieves the index of the first character of the word at the caret position in a given text, considering a set of break characters.
     *
     * @param text The text from which to extract the word.
     * @param caretPosition The position of the caret in the text.
     * @param breakCharactersRegex A string containing characters that are considered as word separators.
     * @return The index of the first character of the word at the caret position. If the caret is at a break character, 0 is returned.
     */
    public static int getBeginningIndex(String text, int caretPosition, String breakCharactersRegex) {
        if (text.isEmpty() || caretPosition < 0 || caretPosition >= text.length()) return 0;

        int startIndex = caretPosition;
        while (startIndex > 0 && !String.valueOf(text.charAt(startIndex)).matches(breakCharactersRegex)) {
            if (String.valueOf(text.charAt(startIndex - 1)).matches(breakCharactersRegex)) break;
            startIndex--;
        }
        return startIndex;
    }

    /**
     * This method retrieves the index of the last character of the word at the caret position in a given text.
     *
     * @param text The text from which to extract the word.
     * @param caretPosition The position of the caret in the text.
     * @return The index of the last character of the word at the caret position. If the caret is at a break character, 0 is returned.
     */
    public static boolean isInsideWord(String text, int caretPosition) {
        if (text.isEmpty() || caretPosition < 0 || caretPosition >= text.length()) return false;
        try {
            boolean charToLeft = text.charAt(caretPosition - 1) != ' ' && text.charAt(caretPosition - 1) != '\n';
            boolean charToRight = text.charAt(caretPosition) != ' ' && text.charAt(caretPosition) != '\n';

            return charToLeft && charToRight;
        }catch (StringIndexOutOfBoundsException e){
            return false;
        }
    }
}
