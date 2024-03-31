package TestCreator.questions.quickEditors.multiEditors;

import TestCreator.dataIO.IOManager;
import TestCreator.questions.FlashCard;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FlashCardQuickMultiEditor {

    @FXML
    public TextArea questionTextArea;
    @FXML
    public TextField questionNamePattern;
    @FXML
    public Button acceptButton;

    private List<FlashCard> flashCards = new ArrayList<>();

    @FXML
    public void initialize() {
        questionTextArea.setWrapText(true);
        questionNamePattern.setText(STR."\{TestManager.getInstance().getSelectedTestName()} Flash Cards");

        questionTextArea.textProperty().addListener((_, _, _) ->
            acceptButton.setDisable(questionTextArea.getText().trim().lines().count() < 3));
    }

    public void acceptQuestion() {
        List<String> sentencesList = new ArrayList<>(questionTextArea.getText().trim().lines().toList());
        //remove empty lines for sentencesList
        sentencesList.removeIf(String::isBlank);

        int questionNum = 1;
        while(sentencesList.size() >= 2) {
            FlashCard flashCard = new FlashCard(STR."\{questionNamePattern.getText()} \{questionNum++}");
            flashCard.setFlashQuestion(sentencesList.getFirst());
            sentencesList.removeFirst();
            flashCard.setFlashAnswer(sentencesList.getFirst());
            sentencesList.removeFirst();
            flashCards.add(flashCard);
        }

        for (FlashCard flashCard : flashCards)
            TestManager.getInstance().addQuestion(flashCard);
        returnToMainMenu();
    }

    @FXML
    private void returnToMainMenu() {
        try {
            IOManager.getInstance().saveTests();
            StageManager.setScene("/MainMenu.fxml");
            StageManager.clearStageController();
        } catch (IOException e) {
            StageManager.showAlert("Error loading NewQuestionEditor.fxml");
            throw new RuntimeException(e);
        }
    }
}
