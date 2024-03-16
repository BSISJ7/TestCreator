package TestCreator.questions.testPanels;

import TestCreator.questions.MatchingWord;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.*;


//TODO Fix highlights not clearing when new selection is made after test is over
public class MatchingWordTestPanel implements TestPanel<MatchingWord> {

    private MatchingWord question;
    private Map<String, String> matchingPairs;

    private int keyHoverIndex;
    private int valueHoverIndex;

    @FXML
    private ListView<String> keyListView;
    @FXML
    private ListView<String> valueListView;
    @FXML
    private VBox rootNode;
    @FXML
    public HBox listViewContainer;

    private Callback<ListView<String>, ListCell<String>> valueFactory;
    private Callback<ListView<String>, ListCell<String>> keyFactory;
    private Map<String, Integer> valueIndices;

    private boolean testIsOver = false;
    private final List<Boolean> isCorrectAnswerList = new ArrayList<>();

    @FXML
    public void initialize() {
        valueIndices = new HashMap<>();
        matchingPairs = new LinkedHashMap<>();
        keyHoverIndex = -1;
        valueHoverIndex = -1;
        ContextMenu contextMenu = new ContextMenu();

//        TODO: Add clear selection button
        MenuItem clearItem = new MenuItem("Clear Selection");
        clearItem.setOnAction(_ -> clearSelection());
        contextMenu.getItems().addAll(clearItem);

        keyFactory = new Callback<>() {
            @Override
            public TextFieldListCell<String> call(ListView<String> param) {
                TextFieldListCell<String> textFieldListCell = new TextFieldListCell<>() {
                    private final Label label;

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        label = new Label();
                        label.setWrapText(true);
                        label.prefWidthProperty().bind(keyListView.widthProperty().multiply(.95));
                        label.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-padding: 0;");
                    }

                    public void updateItem(String key, boolean isEmptyCell) {
                        int keyIndex = getMatchingKeyIndex(key);
                        String displayText = (keyIndex >= 0) ? STR."\{keyIndex + 1}. \{key}" : key;
                        super.updateItem(displayText, isEmptyCell);

                        if (key != null && !isEmptyCell) {
                            label.setText(displayText);
                            setGraphic(label);
                            boolean correct = keyIndex >= 0 && !isCorrectAnswerList.isEmpty() && isCorrectAnswerList.get(keyIndex);
                            String selectedItem = keyListView.getSelectionModel().getSelectedItem();
                            boolean isSelected = selectedItem != null && selectedItem.equals(key);
                            int thisIndex = keyListView.getItems().indexOf(key);

                            setSelectionStyle(this, isSelected, keyHoverIndex, thisIndex, correct, keyIndex);
                            label.setStyle("-fx-border-color: white; -fx-border-radius: 1;");
                        } else {
                            setStyle(null);
                            setText(null);
                            label.setText("");
                            setGraphic(label);
                            label.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-border-radius: 0;");
                        }
                    }
                };

                //If the key is paired with a value highlight it in the valueListView
                textFieldListCell.setOnMouseEntered(_ -> {
                    if (!testIsOver && textFieldListCell.getItem() != null) {
                        keyHoverIndex = textFieldListCell.getIndex();
                        String matchingPair = matchingPairs.get(keyListView.getItems().get(keyHoverIndex));
                        if (matchingPair != null) {
                            valueHoverIndex = valueListView.getItems().indexOf(matchingPair);
                            valueListView.refresh();
                        }
                        keyListView.refresh();
                    }
                });

                resetHoverIndexes(textFieldListCell);

                textFieldListCell.emptyProperty().addListener((_, _, isEmpty) -> {
                    if (isEmpty) {
                        textFieldListCell.setContextMenu(null);
                    } else {
                        textFieldListCell.setContextMenu(contextMenu);
                    }
                });
                return textFieldListCell;
            }
        };

        valueFactory = new Callback<>() {
            @Override
            public TextFieldListCell<String> call(ListView<String> param) {
                TextFieldListCell<String> TextFieldListCell = new TextFieldListCell<>() {
                    private final Label label;

                    {
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        label = new Label();
                        label.setWrapText(true);
                        label.prefWidthProperty().bind(valueListView.widthProperty().multiply(.95));
                        label.setStyle("-fx-border-color: white; -fx-border-width: 1; -fx-padding: 0;");
                    }

                    public void updateItem(String value, boolean empty) {
                        int keyIndex = getMatchingKeyIndex(findKeyFromValue(value));
                        String displayText = (keyIndex >= 0) ? STR."\{keyIndex + 1}. \{value}" : value;
                        super.updateItem(displayText, empty);

                        if (value != null && !empty) {
                            label.setText(displayText);
                            setGraphic(label);
                            boolean correct = keyIndex >= 0 && !isCorrectAnswerList.isEmpty() && isCorrectAnswerList.get(keyIndex);
                            String selectedItem = valueListView.getSelectionModel().getSelectedItem();
                            boolean isSelected = selectedItem != null && selectedItem.equals(value);
                            int thisIndex = valueListView.getItems().indexOf(value);

                            setSelectionStyle(this, isSelected, valueHoverIndex, thisIndex, correct, keyIndex);
                            label.setStyle("-fx-border-color: white; -fx-border-radius: 1;");
                        } else {
                            setStyle(null);
                            setText(null);
                            label.setText("");
                            setGraphic(label);
                            label.setStyle("-fx-border-color: transparent; -fx-padding: 0; -fx-border-radius: 0;");
                        }
                    }
                };


                //If the value is paired with a key already highlight it in the keyListView
                TextFieldListCell.setOnMouseEntered(_ -> {
                    if (!testIsOver && TextFieldListCell.getItem() != null) {
                        valueHoverIndex = TextFieldListCell.getIndex();
                        if (valueIndices.containsValue(valueHoverIndex)) {
                            try {
                                String questionKey = findKeyFromValue(valueListView.getItems().get(valueHoverIndex));
                                keyHoverIndex = keyListView.getItems().indexOf(questionKey);
                                keyListView.refresh();
                            } catch (NullPointerException _) {
                            }
                        }
                        valueListView.refresh();
                    }
                });
                resetHoverIndexes(TextFieldListCell);

                TextFieldListCell.emptyProperty().addListener((_, _, isEmpty) -> {
                    if (isEmpty) {
                        TextFieldListCell.setContextMenu(null);
                    } else {
                        TextFieldListCell.setContextMenu(contextMenu);
                    }
                });
                return TextFieldListCell;
            }
        };

        EventHandler<MouseEvent> listClicked = _ -> {
            if (keyListView.getSelectionModel().getSelectedIndex() >= 0 && valueListView.getSelectionModel().getSelectedIndex() >= 0 && !testIsOver) {
                for (Map.Entry<String, String> keyValuePairs : matchingPairs.entrySet()) {
                    if (keyValuePairs.getValue() != null && keyValuePairs.getValue().equals(valueListView.getSelectionModel().getSelectedItem())) {
                        keyValuePairs.setValue(null);
                    }
                }
                valueIndices.put(keyListView.getSelectionModel().getSelectedItem(), valueListView.getSelectionModel().getSelectedIndex());
                matchingPairs.put(keyListView.getSelectionModel().getSelectedItem(), valueListView.getSelectionModel().getSelectedItem());
                keyHoverIndex = keyListView.getSelectionModel().getSelectedIndex();
                valueHoverIndex = valueListView.getSelectionModel().getSelectedIndex();
                keyListView.getSelectionModel().clearSelection();
                valueListView.getSelectionModel().clearSelection();
            }
            keyListView.refresh();
            valueListView.refresh();
        };

        keyListView.setOnMouseClicked(listClicked);
        valueListView.setOnMouseClicked(listClicked);

        keyListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        valueListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listViewContainer.setStyle("-fx-padding: 0px; -fx-margin: 0px; -fx-indent: 0;");
    }

    private void setSelectionStyle(TextFieldListCell<String> textFieldListCell, boolean isSelected, int keyHoverIndex, int thisIndex, boolean correct, int keyIndex) {
        if (!testIsOver) {
            if (isSelected && keyHoverIndex != thisIndex)
                textFieldListCell.setStyle("-fx-background-color: rgb(0,150,0)");
            else if (isSelected)
                textFieldListCell.setStyle("-fx-background-color: rgb(0,200,0)");
            else if (keyHoverIndex == thisIndex)
                textFieldListCell.setStyle("-fx-background-color: rgba(3,150,21,0.69)");
        } else if (correct) {
            textFieldListCell.setStyle("-fx-background-color: rgba(0,150,0,0.62)");
        } else if (keyIndex >= 0) {
            textFieldListCell.setStyle("-fx-background-color: rgba(220,34,0,0.62)");
        }
    }

    private void resetHoverIndexes(TextFieldListCell<String> textFieldListCell) {
        textFieldListCell.setOnMouseExited(_ -> {
            if (!testIsOver) {
                keyHoverIndex = -1;
                valueHoverIndex = -1;
                keyListView.refresh();
                valueListView.refresh();
            }
        });
    }

    @FXML
    private void clearSelection() {
        if (!testIsOver) {
            keyHoverIndex = -1;
            valueHoverIndex = -1;
            keyListView.getSelectionModel().clearSelection();
            valueListView.getSelectionModel().clearSelection();
            keyListView.refresh();
            valueListView.refresh();
        }
    }

    @Override
    public void setupQuestion(MatchingWord question) {
        this.question = question;
        List<String> shuffledKeys = question.getKeyListCopy();
        List<String> shuffledValues = question.getValueListCopy();
        Collections.shuffle(shuffledKeys);
        Collections.shuffle(shuffledValues);
        keyListView.setItems(FXCollections.observableArrayList(shuffledKeys));
        valueListView.setItems(FXCollections.observableArrayList(shuffledValues));
        keyListView.setCellFactory(keyFactory);
        valueListView.setCellFactory(valueFactory);
        keyListView.refresh();

        keyListView.getItems().forEach(key -> valueIndices.put(key, -1));
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public float getPointsScored() {
        testIsOver = true;
        keyListView.setEditable(false);
        valueListView.setEditable(false);

        float points = 0.0f;

        for (Map.Entry<String, String> keyValue : matchingPairs.entrySet()) {
            int index = question.getKeyIndex(keyValue.getKey());
            String value = question.getValueAt(index);
            if (keyValue.getValue().equals(value)) {
                isCorrectAnswerList.add(true);
                points++;
            } else {
                keyValue.setValue(value);
                isCorrectAnswerList.add(false);
            }
        }

        while (isCorrectAnswerList.size() < question.numberOfKeys()) {
            isCorrectAnswerList.add(false);
        }

        for (int x = 0; x < question.numberOfKeys(); x++) {
            matchingPairs.put(question.getKeyAt(x), question.getValueListCopy().get(x));
        }

        keyListView.refresh();
        valueListView.refresh();
        return points;
    }

    private String findKeyFromValue(String value) throws NullPointerException {
        String key = null;
        for (Map.Entry<String, String> keyValuePairs : matchingPairs.entrySet()) {
            if (keyValuePairs.getValue() != null && keyValuePairs.getValue().equals(value))
                key = keyValuePairs.getKey();
        }
        return key;
    }

    private int getMatchingKeyIndex(String key) throws IndexOutOfBoundsException {
        int index = 0;
        for (Map.Entry<String, String> keyValuePairs : matchingPairs.entrySet()) {
            if (keyValuePairs.getKey().equals(key) && keyValuePairs.getValue() != null)
                return index;
            else if (keyValuePairs.getKey().equals(key))
                return -1;
            index++;
        }
        return -1;
    }

    public void cleanUp() {
        keyListView.getItems().clear();
        valueListView.getItems().clear();
        matchingPairs.clear();
        valueIndices.clear();
        keyHoverIndex = -1;
        valueHoverIndex = -1;
        testIsOver = false;
        isCorrectAnswerList.clear();
    }
}