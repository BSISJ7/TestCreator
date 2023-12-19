package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class StackPaneDialogue extends StackPane{
    private final String message;
    private StackPane stackpane;

    public StackPaneDialogue(StackPane stackpane, String message) {
        this.stackpane = stackpane;
        this.message = message;
    }

    public void show() {
        Button closeButton = new Button("Close");
        Button okayButton = new Button("Okay");
        okayButton.requestFocus();

        okayButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                closeButton.requestFocus();
            }
        });
        closeButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                okayButton.requestFocus();
            }
        });

        HBox buttonHBox = new HBox(okayButton, closeButton);
        buttonHBox.setSpacing(40);
        buttonHBox.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);

        VBox contentVBox = new VBox(messageLabel,buttonHBox);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;" +
                " -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setSpacing(40);
        contentVBox.setFillWidth(true);
        contentVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().add(contentVBox);

        this.setStyle(OptionsMenu.getCssFullPath());
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(stackpane.getWidth() * .75, stackpane.getHeight() * .75);
        this.setPrefSize(stackpane.getWidth() * .75, stackpane.getHeight() * .75);
        this.setMinSize(400, 200);

        EventHandler<KeyEvent> keyEventFilter = KeyEvent::consume;
        stackpane.addEventFilter(KeyEvent.ANY, keyEventFilter);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            boolean isOkayButton = event.getTarget() == okayButton;
            boolean isOkayBtnText = ((Node) event.getTarget()).getParent() == okayButton;
            if (!isCloseButton && !isCloseBtnText && !isOkayButton && !isOkayBtnText)
                event.consume();
        };
        stackpane.addEventFilter(MouseEvent.ANY, eventFilter);

        okayButton.setOnAction(_ -> {
            stackpane.getChildren().remove(this);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            stackpane.removeEventFilter(KeyEvent.ANY, keyEventFilter);
        });
        closeButton.setOnAction(_ ->{
            stackpane.getChildren().remove(this);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            stackpane.removeEventFilter(KeyEvent.ANY, keyEventFilter);
        });
        stackpane.getChildren().add(this);
        //TODO Fix focus so that the okay button is focused when the dialogue is shown
        Platform.runLater(() -> {
            okayButton.setFocusTraversable(true);
            okayButton.requestFocus();
        });
    }

    public CompletableFuture<Boolean> showAndWait() {
        Button closeButton = new Button("Close");
        Button okayButton = new Button("Okay");
        okayButton.requestFocus();

        okayButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                closeButton.requestFocus();
            }
        });
        closeButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                event.consume();
                okayButton.requestFocus();
            }
        });

        HBox buttonHBox = new HBox(okayButton, closeButton);
        buttonHBox.setSpacing(40);
        buttonHBox.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);

        VBox contentVBox = new VBox(messageLabel,buttonHBox);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;" +
                " -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setSpacing(40);
        contentVBox.setFillWidth(true);
        contentVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().add(contentVBox);

        this.setStyle(OptionsMenu.getCssFullPath());
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(stackpane.getWidth() * .75, stackpane.getHeight() * .75);
        this.setPrefSize(stackpane.getWidth() * .75, stackpane.getHeight() * .75);
        this.setMinSize(400, 200);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            boolean isOkayButton = event.getTarget() == okayButton;
            boolean isOkayBtnText = ((Node) event.getTarget()).getParent() == okayButton;
            if (!isCloseButton && !isCloseBtnText && !isOkayButton && !isOkayBtnText)
                event.consume();
        };
        stackpane.addEventFilter(MouseEvent.ANY, eventFilter);

        CompletableFuture<Boolean> okayClicked = new CompletableFuture<>();
        okayButton.setOnAction(_ -> {
            stackpane.getChildren().remove(this);
            okayClicked.complete(true);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            okayButton.setOnAction(null);
            closeButton.setOnAction(null);
            stackpane = null;
        });
        closeButton.setOnAction(_ -> {
            stackpane.getChildren().remove(this);
            okayClicked.complete(false);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            okayButton.setOnAction(null);
            closeButton.setOnAction(null);
            stackpane = null;
        });
        stackpane.getChildren().add(this);

        okayButton.requestFocus();
        return okayClicked;
    }
}
