package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class StackPaneAlert extends StackPane {

    private StackPane stackpane;
    private final  String message;

    public StackPaneAlert(StackPane stackpane, String message) {
        this.stackpane = stackpane;
        this.message = message;
    }

    public void show() {
        Button closeButton = new Button("Close");
        closeButton.requestFocus();

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);

        VBox contentVBox = new VBox(messageLabel,closeButton);
        contentVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;" +
                " -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setSpacing(40);
        contentVBox.setFillWidth(true);
        contentVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().add(contentVBox);

        this.setStyle(OptionsMenu.getCssFullPath());
        this.setAlignment(Pos.CENTER);
        this.prefWidthProperty().bind(StageManager.getStage().widthProperty());
        this.prefHeightProperty().bind(StageManager.getStage().heightProperty());
        this.setMaxSize(this.stackpane.getWidth() * .75, this.stackpane.getHeight() * .75);
//        this.setPrefSize(this.stackpane.getWidth() * .75, this.stackpane.getHeight() * .75);
        this.setMinSize(400, 200);

        EventHandler<KeyEvent> keyEventFilter = KeyEvent::consume;
        stackpane.addEventFilter(KeyEvent.ANY, keyEventFilter);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            if (!isCloseButton && !isCloseBtnText)
                event.consume();
        };
        stackpane.addEventFilter(MouseEvent.ANY, eventFilter);
        closeButton.setOnAction((_) -> {
            stackpane.getChildren().remove(this);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            stackpane.removeEventFilter(KeyEvent.ANY, keyEventFilter);
        });
        stackpane.getChildren().add(this);
        closeButton.requestFocus();
    }

    public CompletableFuture<Boolean> showAndWait() {
        Button closeButton = new Button("Close");
        closeButton.requestFocus();

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: black;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        messageLabel.setAlignment(Pos.CENTER);

        VBox contentVBox = new VBox(messageLabel,closeButton);
        contentVBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10;" +
                " -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;");
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setSpacing(40);
        contentVBox.setFillWidth(true);
        contentVBox.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getChildren().add(contentVBox);

        this.setStyle(OptionsMenu.getCssFullPath());
        this.setAlignment(Pos.CENTER);
        this.prefWidthProperty().bind(StageManager.getStage().widthProperty());
        this.prefHeightProperty().bind(StageManager.getStage().heightProperty());
        this.setMaxSize(this.stackpane.getWidth() * .75, this.stackpane.getHeight() * .75);
//        this.setPrefSize(this.stackpane.getWidth() * .75, this.stackpane.getHeight() * .75);
        this.setMinSize(400, 200);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            if (!isCloseButton && !isCloseBtnText)
                event.consume();
        };
        stackpane.addEventFilter(MouseEvent.ANY, eventFilter);

        CompletableFuture<Boolean> closeClicked = new CompletableFuture<>();
        closeButton.setOnAction((_) -> {
            stackpane.getChildren().remove(this);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            closeClicked.complete(true);
            stackpane.removeEventFilter(MouseEvent.ANY, eventFilter);
            closeButton.setOnAction(null);
            stackpane = null;
        });

        stackpane.getChildren().add(this);

        return closeClicked;
    }
}
