package TestCreator;

import TestCreator.utilities.StageManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    public final static String workDir = System.getProperty("user.dir");

    public static final boolean TESTING_MODE = true;

    private static Stage stage;

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    public void start(Stage stage) throws Exception {
        stage.setTitle("Main Menu");
        StageManager.setStage(stage);
        StageManager.setScene("/login/WebLogin.fxml");
        stage.setWidth(800);
        stage.setHeight(600);
        stage.show();
        stage.setOnCloseRequest(_ -> {
            Platform.exit();
            System.exit(0);
        });
    }

    public static Stage getStage() {
        return stage;
    }
}
