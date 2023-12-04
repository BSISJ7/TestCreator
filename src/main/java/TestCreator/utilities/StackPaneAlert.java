package TestCreator.utilities;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StackPaneAlert extends StackPane {

    StackPane context;

    public StackPaneAlert(StackPane stackpane, String message) {
        context = stackpane;

        Button closeButton = new Button("Close");

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

//        this.setStyle(OptionsMenu.getCssFullPath());
        this.setAlignment(Pos.CENTER);
        this.setMaxSize(context.getWidth() * .75, context.getHeight() * .75);
        this.setPrefSize(context.getWidth() * .75, context.getHeight() * .75);

        EventHandler<MouseEvent> eventFilter = event -> {
            boolean isCloseButton = event.getTarget() == closeButton;
            boolean isCloseBtnText = ((Node) event.getTarget()).getParent() == closeButton;
//            System.out.println("isCloseButton: " + isCloseButton);
//            System.out.println("isCloseBtnText: " + isCloseBtnText);
            if (!isCloseButton && !isCloseBtnText)
                event.consume();
        };
        context.addEventFilter(MouseEvent.ANY, eventFilter);

        closeButton.setOnAction((_) -> {
            context.getChildren().remove(this);
            context.removeEventFilter(MouseEvent.ANY, eventFilter);
        });
    }

    public void show() {
        context.getChildren().add(this);
    }
}
