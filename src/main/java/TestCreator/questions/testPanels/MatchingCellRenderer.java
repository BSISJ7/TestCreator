package TestCreator.questions.testPanels;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/*
 * If ListCellRenderer<?> doesn't match getListCellRendererComponent(JList<? extends ?>) get this error:
 * The method must override or implement a supertype method
 */

class MatchingCellRenderer extends JButton implements ListCellRenderer<String> {

    public static final int QUESTION_RENDERER = 0;
    public static final int ANSWER_RENDERER = 1;

    public static final long serialVersionUID = new Random(System.nanoTime()).nextLong();
    private int hoverIndex;
    private List<Integer> matchingItemIndexes;
    private int rendererType;

    public MatchingCellRenderer(int rendererType) {
        setOpaque(true);
        hoverIndex = -1;
        this.rendererType = rendererType;
    }

    public void setHoverIndex(int hoverIndex) {
        this.hoverIndex = hoverIndex;
    }

    public void setMatchingItemIndexes(List<Integer> matchingItemIndexes) {
        this.matchingItemIndexes = matchingItemIndexes;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list,
                                                  String itemVal, int index, boolean isSelected, boolean cellHasFocus) {


        if (isSelected && hoverIndex != index)
            setBackground(new Color(0, 150, 0));
        else if (isSelected && hoverIndex == index)
            setBackground(new Color(0, 200, 0));
        else if (!isSelected && hoverIndex == index)
            setBackground(SystemColor.inactiveCaption);
        else
            setBackground(SystemColor.inactiveCaptionBorder);


        if (rendererType == QUESTION_RENDERER) {
            if (matchingItemIndexes.get(index) != -1)
                setForeground(Color.ORANGE);
            else {
                setForeground(Color.black);
            }
        } else if (rendererType == ANSWER_RENDERER) {
            if (matchingItemIndexes.contains(index))
                setForeground(Color.ORANGE);
            else {
                setForeground(Color.black);
            }
        }

        setText(itemVal);
        return this;
    }
}
