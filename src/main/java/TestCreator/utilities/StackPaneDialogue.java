package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

public class StackPaneDialogue extends StackPane{

    private Boolean okayClicked = false;
    private boolean waiting = true;
    StackPane context = null;

    private String message;
    private StackPane stackpane;

    public StackPaneDialogue(StackPane stackpane, String message) {
        this.stackpane = stackpane;
        this.message = message;
    }

    public void show() {
        context = stackpane;
        Button closeButton = new Button("Close");
        Button okayButton = new Button("Okay");
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
        this.setMaxSize(context.getWidth() * .75, context.getHeight() * .75);
        this.setPrefSize(context.getWidth() * .75, context.getHeight() * .75);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            boolean isOkayButton = event.getTarget() == okayButton;
            boolean isOkayBtnText = ((Node) event.getTarget()).getParent() == okayButton;
            if (!isCloseButton && !isCloseBtnText && !isOkayButton && !isOkayBtnText)
                event.consume();
        };
        context.addEventFilter(MouseEvent.ANY, eventFilter);

        okayButton.setOnAction(_ -> {
            context.getChildren().remove(this);
            context.removeEventFilter(MouseEvent.ANY, eventFilter);
        });
        closeButton.setOnAction(_ ->{
            context.getChildren().remove(this);
            context.removeEventFilter(MouseEvent.ANY, eventFilter);
        });
        context.getChildren().add(this);
    }

    public CompletableFuture<Boolean> showAndWait() {
        context = stackpane;
        Button closeButton = new Button("Close");
        Button okayButton = new Button("Okay");
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
        this.setMaxSize(context.getWidth() * .75, context.getHeight() * .75);
        this.setPrefSize(context.getWidth() * .75, context.getHeight() * .75);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
            boolean isOkayButton = event.getTarget() == okayButton;
            boolean isOkayBtnText = ((Node) event.getTarget()).getParent() == okayButton;
            if (!isCloseButton && !isCloseBtnText && !isOkayButton && !isOkayBtnText)
                event.consume();
        };
        context.addEventFilter(MouseEvent.ANY, eventFilter);

        CompletableFuture<Boolean> okayClicked = new CompletableFuture<>();
        okayButton.setOnAction(_ -> {
            context.getChildren().remove(this);
            okayClicked.complete(true);
            context.removeEventFilter(MouseEvent.ANY, eventFilter);
        });
        closeButton.setOnAction(_ -> {
            context.getChildren().remove(this);
            okayClicked.complete(false);
            context.removeEventFilter(MouseEvent.ANY, eventFilter);
        });
        context.getChildren().add(this);

        return okayClicked;
    }
}
