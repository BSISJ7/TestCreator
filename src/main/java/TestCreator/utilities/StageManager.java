package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class StageManager {

    private static Stage stage;
    private static Object stageController;

    private StageManager() {}

    public static void setStage(Stage stage) {
        StageManager.stage = stage;
    }

    public static void setScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        stageController = loader.getController();
        root.getStylesheets().add(OptionsMenu.getCssFullPath());
        stage.setScene(new Scene(root));
    }

    public static void setScene(Parent root) throws IOException {
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

    public static Window getStage() {
        return stage;
    }
}