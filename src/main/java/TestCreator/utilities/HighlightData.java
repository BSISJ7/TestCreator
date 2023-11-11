package TestCreator.utilities;

import javax.swing.text.Highlighter.HighlightPainter;

public class HighlightData {
    private int startOffset;
    private int endOffset;
    private HighlightPainter painter;

    public HighlightData(int startOffset, int endOffset, HighlightPainter painter) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.painter = painter;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public HighlightPainter getPainter() {
        return painter;
    }

    public int getStartOffset() {
        return startOffset;
    }
}
