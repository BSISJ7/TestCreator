package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class StageManager {

    private static StackPane rootPane;
    private static Stage stage;
    private static Object stageController;

    private StageManager() {}

    public static void setStage(Stage stage) {
        StageManager.stage = stage;
    }

    public static void setRootPane(StackPane root) throws IOException {
        rootPane = root;
        stageController = root;
        root.getStylesheets().add(OptionsMenu.getCssFullPath());
        stage.setScene(new Scene(root));
    }

    public static void setTitle(String title) {
        stage.setTitle(title);
    }

    public static Object getStageController() {
        return stageController;
    }

    public static Object getController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));
        loader.load();
        return loader.getController();
    }

    public static void clearStageController() {
        stageController = null;
    }

    public static Window getStage() {
        return stage;
    }

    public static void setScene(String newScene) throws IOException {
        rootPane.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(newScene));
        Parent node = loader.load();
        rootPane.getChildren().add(node);
        stageController = loader.getController();
    }

    public static void showAlert(String message) {
        new StackPaneAlert(rootPane, message);
    }

    public static CompletableFuture<Boolean> showDialog(String message) {
        return new StackPaneDialogue(rootPane, message).showAndWait();
    }
}