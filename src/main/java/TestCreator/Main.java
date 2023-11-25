package TestCreator;

import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    public final static String workDir = System.getProperty("user.dir");

    public static final boolean TESTING_MODE = true;


    public static void main(String[] args) {
        IOManager.getInstance().loadTests();
        TestManager.getInstance().autoFillTests();
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
}
