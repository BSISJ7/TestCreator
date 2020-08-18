package com.BSISJ7.TestCreator.questions.testPanels;

import com.BSISJ7.TestCreator.questions.MultipleChoice;
import com.BSISJ7.TestCreator.questions.Question;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

public class MultipleChoiceTestPanel extends Gradeable {

    /**
     *
     */
    private static final long serialVersionUID = 6030791732485081137L;
    private JLabel questionText;
    private MultipleChoice currentQuestion;
    private boolean correctAnswer = false;
    private JTextArea[] txtAreaAnswers;
    private JPanel pnlAnswers;
    private int selectedIndex = -1;

    public MultipleChoiceTestPanel() {
        setBackground(SystemColor.inactiveCaptionBorder);
        setPreferredSize(new Dimension(670, 432));
        setLayout(null);

        JPanel QuestionPanel = new JPanel();
        QuestionPanel.setBackground(SystemColor.inactiveCaptionBorder);
        QuestionPanel.setBorder(new TitledBorder(null, "Question", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        QuestionPanel.setBounds(25, 0, 617, 100);
        add(QuestionPanel);
        QuestionPanel.setLayout(null);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 18, 597, 71);
        QuestionPanel.add(scrollPane_1);

        questionText = new JLabel();
        questionText.setOpaque(true);
        questionText.setBackground(Color.WHITE);
        scrollPane_1.setViewportView(questionText);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(25, 116, 617, 291);
        add(scrollPane);

        pnlAnswers = new JPanel();
        pnlAnswers.setBackground(SystemColor.activeCaption);
        scrollPane.setViewportView(pnlAnswers);
        pnlAnswers.setLayout(new GridBagLayout());
    }

    public MultipleChoiceTestPanel(Question newQuestion) {
        this();
        setQuestion((MultipleChoice) newQuestion);
    }

    public void setQuestion(MultipleChoice newQuestion) {
        currentQuestion = newQuestion;

        questionText.setText(currentQuestion.getMultChoiceQuestion());
        txtAreaAnswers = new JTextArea[currentQuestion.getChoices().size()];

        ArrayList<String> choices = new ArrayList<>(currentQuestion.getChoices());
        Collections.shuffle(choices);

        GridBagConstraints gbConst = new GridBagConstraints();
        gbConst.gridx = 0;
        gbConst.gridy = 0;
        gbConst.ipady = 20;
        for (int x = 0; x < choices.size(); x++) {
            final int index = x;
            txtAreaAnswers[x] = new JTextArea(choices.get(x));
            txtAreaAnswers[x].setBorder(BorderFactory.createRaisedBevelBorder());
            txtAreaAnswers[x].setMinimumSize(new Dimension(590, 70));
            txtAreaAnswers[x].setEditable(false);
            txtAreaAnswers[x].setName(x + "");
            txtAreaAnswers[x].addMouseListener(new MouseListener() {
                public void mouseClicked(MouseEvent arg0) {
                    if (selectedIndex != -1)
                        txtAreaAnswers[selectedIndex].setBackground(Color.WHITE);

                    selectedIndex = Integer.parseInt(arg0.getComponent().getName());
                    txtAreaAnswers[selectedIndex].setBackground(new Color(173, 255, 47));

                    if (currentQuestion.getAnswerIndex() == selectedIndex)
                        correctAnswer = true;
                    else
                        correctAnswer = true;
                }

                public void mouseEntered(MouseEvent arg0) {
                    txtAreaAnswers[index].setBorder(BorderFactory.createRaisedBevelBorder());
                }

                public void mouseExited(MouseEvent arg0) {
                    txtAreaAnswers[index].setBorder(BorderFactory.createRaisedBevelBorder());
                }

                public void mousePressed(MouseEvent arg0) {
                    txtAreaAnswers[index].setBorder(BorderFactory.createLoweredBevelBorder());
                }

                public void mouseReleased(MouseEvent arg0) {
                    txtAreaAnswers[index].setBorder(BorderFactory.createRaisedBevelBorder());
                }
            });

            JScrollPane scrlTextAnswers = new JScrollPane(txtAreaAnswers[x]);
            scrlTextAnswers.setPreferredSize(new Dimension(590, 70));
            pnlAnswers.add(scrlTextAnswers, gbConst);
            gbConst.gridy++;
        }
    }

    @Override
    public float getPointsScored() {
        if (correctAnswer)
            return currentQuestion.getWeight();
        else
            return 0.0f;
    }
}
