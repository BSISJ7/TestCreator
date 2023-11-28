package TestCreator.questions.testPanels;

import javax.swing.*;
import java.io.Serial;
import java.util.Random;

public class MatchingJList extends JList<String> {

    @Serial
    private static final long serialVersionUID = new Random(System.nanoTime()).nextLong();

    public MatchingJList() {
        this(new DefaultListModel<>());
    }

    public MatchingJList(ListModel<String> listModel) {
        super(listModel);
        int hoverIndex = 0;
    }
}
