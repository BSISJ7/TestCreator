package TestCreator.utilities;

import TestCreator.options.OptionsMenu;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class StageManager {

    private static Stage stage;
    private StageManager() {}

    public static void setStage(Stage stage) {
        StageManager.stage = stage;
    }

    public static void setScene(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(StageManager.class.getResource(fxmlPath));
        Parent root = loader.load();
        root.getStylesheets().add(OptionsMenu.getProjectCssTheme());
        stage.setScene(new Scene(root));
    }

    public static void setTitle(String title) {
        stage.setTitle(title);
    }
}