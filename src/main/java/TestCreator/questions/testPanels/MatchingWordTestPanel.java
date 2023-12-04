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
import javafx.util.Callback;

import java.net.URL;
import java.util.*;

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
    private URL location;
    @FXML
    private HBox rootNode;

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
                TextFieldListCell<String> TextFieldListCell = new TextFieldListCell<>() {
                    public void updateItem(String key, boolean isEmptyCell) {

                        //If value has been paired with a key prepend key index to text
                        int keyIndex = getMatchingKeyIndex(key);
                        if (keyIndex >= 0)
                            super.updateItem(keyIndex + 1 + ". " + key, isEmptyCell);
                        else
                            super.updateItem(key, isEmptyCell);

                        if (key != null && !isEmptyCell) {
                            boolean correct = keyIndex >= 0 && !isCorrectAnswerList.isEmpty()
                                    && isCorrectAnswerList.get(keyIndex);
                            if (!testIsOver) {
                                boolean isSelected = (keyListView.getSelectionModel().getSelectedIndex() >= 0)
                                        && keyListView.getSelectionModel().getSelectedItem().equals(key);
                                int thisIndex = keyListView.getItems().indexOf(key);

                                if (isSelected && keyHoverIndex != thisIndex)
                                    setStyle("-fx-background-color: rgb(0,150,0)");
                                else if (isSelected)
                                    setStyle("-fx-background-color: rgb(0,200,0)");
                                else if (keyHoverIndex == thisIndex)
                                    setStyle("-fx-background-color: rgba(3,150,21,0.39)");
                            }  else if(correct){
                                    setStyle("-fx-background-color: rgba(0,150,0,0.62)");
                            }   else {
                                    setStyle("-fx-background-color: rgba(220,34,0,0.62)");
                            }
                        } else//Reset style of isEmptyCell cells
                            setStyle(null);
                    }
                };

                //If the key is paired with a value highlight it in the valueListView
                TextFieldListCell.setOnMouseEntered(_ -> {
                    if (!testIsOver && TextFieldListCell.getItem() != null) {
                        keyHoverIndex = TextFieldListCell.getIndex();
                        String matchingPair = matchingPairs.get(keyListView.getItems().get(keyHoverIndex));
                        if (matchingPair != null) {
                            valueHoverIndex = valueListView.getItems().indexOf(matchingPair);
                            valueListView.refresh();
                        }
                        keyListView.refresh();
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

        valueFactory = new Callback<>() {
            @Override
            public TextFieldListCell<String> call(ListView<String> param) {
                TextFieldListCell<String> TextFieldListCell = new TextFieldListCell<>() {
                    public void updateItem(String value, boolean empty) {

                        int keyIndex = getMatchingKeyIndex(findKeyFromValue(value));
                        if (keyIndex >= 0)
                            super.updateItem(keyIndex + 1 + ". " + value, empty);
                        else
                            super.updateItem(value, empty);

                        if (value != null && !empty) {
                            boolean correct = keyIndex >= 0 && !isCorrectAnswerList.isEmpty() && isCorrectAnswerList.get(keyIndex);
                            if (!testIsOver) {
                                boolean isSelected = (valueListView.getSelectionModel().getSelectedIndex() >= 0)
                                        && valueListView.getSelectionModel().getSelectedItem().equals(value);
                                int thisIndex = valueListView.getItems().indexOf(value);

                                if (isSelected && valueHoverIndex != thisIndex)
                                    setStyle("-fx-background-color: rgb(0,150,0)");
                                else if (isSelected)
                                    setStyle("-fx-background-color: rgb(0,200,0)");
                                else if (valueHoverIndex == thisIndex)
                                    setStyle("-fx-background-color: rgba(3,150,21,0.39)");

                            } else if(correct){
                                setStyle("-fx-background-color: rgba(0,150,0,0.62)");
                            } else {
                                setStyle("-fx-background-color: rgba(220,34,0,0.62)");
                            }
                        } else//Reset style of new cells being painted
                            setStyle(null);
                    }
                };


                //If the value is paired with a key highlight it in the keyListView
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

    private void clearSelection() {

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
}