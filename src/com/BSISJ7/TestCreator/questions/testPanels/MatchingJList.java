package com.BSISJ7.TestCreator.questions.testPanels;

import javax.swing.*;
import java.util.Random;

public class MatchingJList extends JList<String> {

    private static final long serialVersionUID = new Random(System.nanoTime()).nextLong();
    private int hoverIndex;

    public MatchingJList() {
        this(new DefaultListModel<String>());
    }

    public MatchingJList(ListModel<String> listModel) {
        super(listModel);
        hoverIndex = 0;
    }
}
