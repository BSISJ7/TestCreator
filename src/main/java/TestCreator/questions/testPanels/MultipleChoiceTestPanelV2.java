package TestCreator.questions.testPanels;

import TestCreator.questions.MultipleChoice;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

//TODO see if this should replace the old MultipleChoiceTestPanel
public class MultipleChoiceTestPanelV2 extends Gradeable implements TestPanel<MultipleChoice> {

    private static final long serialVersionUID = 6030791732485081137L;
    /**
     *
     */

    @FXML
    private TextArea questionTextArea;
    @FXML
    private GridPane gridPane;
    @FXML
    private BorderPane rootNode;
    private JLabel questionText;
    private MultipleChoice question;
    private boolean correctAnswer = false;
    private JTextArea[] txtAreaAnswers;
    private JPanel pnlAnswers;
    private int selectedIndex = -1;


    public MultipleChoiceTestPanelV2() {
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

    @Override
    public void setupQuestion(MultipleChoice question) {
        this.question = question;

        questionText.setText(question.getQuestionText());
        txtAreaAnswers = new JTextArea[question.getChoicesCopy().size()];

        ArrayList<String> choices = new ArrayList<>(question.getChoicesCopy());
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

                    if (question.getAnswerIndex() == selectedIndex)
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
            return question.getWeight();
        else
            return 0.0f;
    }

    @Override
    public String getFXMLName() {
        return "MultChoiceTestPanel";
    }

    @Override
    public void disableAnswerChanges() {

    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }
}
