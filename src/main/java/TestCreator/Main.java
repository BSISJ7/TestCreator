package TestCreator;

import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import TestCreator.utilities.TestManager;
import com.jpro.webapi.JProApplication;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends JProApplication {

    public static final boolean TESTING_MODE = true;
    private StackPane stackpane;


    public static void main(String[] args) {
        launch(Main.class, args);
    }

    public void start(Stage stage) throws Exception {
        IOManager.getInstance().loadTests();
        TestManager.getInstance().autoFillTests();
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
