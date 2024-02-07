package TestCreator;

import TestCreator.login.WebLogin;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import com.jpro.webapi.JProApplication;
import javafx.application.Platform;
import javafx.stage.Stage;


public class Main extends JProApplication {
    public static final boolean TESTING_MODE = true;

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    public void start(Stage stage) throws Exception {
        IOManager.getInstance().backupDatabase();
        IOManager.getInstance().loadTests();

        System.setProperty("aws.accessKeyId", System.getenv("AWS_SES_ACCESS_KEY"));
        System.setProperty("aws.secretAccessKey", System.getenv("AWS_SES_SECRET_ACCESS_KEY"));

        stage.setTitle("Main Menu");
        StageManager.setStage(stage);
        StageManager.setScene("/login/WebLogin.fxml");
        ((WebLogin) StageManager.getStageController()).setupUserManager();
        StageManager.clearStageController();
        stage.setWidth(800);
        stage.setHeight(600);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        stage.setOnCloseRequest(_ -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
