package TestCreator;


import TestCreator.login.WebLogin;
import TestCreator.testIO.IOManager;
import TestCreator.utilities.StageManager;
import com.jpro.webapi.JProApplication;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends JProApplication {
    public static final boolean TESTING_MODE = true;
//    private ConfigurableApplicationContext applicationContext;

    public static void main(String[] args) {
        launch(Main.class, args);
    }

    @Override
    public void init() {
//        applicationContext = new SpringApplicationBuilder(SpringJfxApp.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
//        applicationContext.publishEvent(new StageReadyEvent(stage));

        IOManager.getInstance().backupDatabase();
        IOManager.getInstance().loadTests();

        System.setProperty("aws.accessKeyId", System.getenv("AWS_SES_ACCESS_KEY"));
        System.setProperty("aws.secretAccessKey", System.getenv("AWS_SES_SECRET_ACCESS_KEY"));

        stage.setTitle("Main Menu");
        StageManager.setStage(stage);
        StageManager.setRootPane(new StackPane());
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

    @Override
    public void stop() {
//        applicationContext.stopRecording();
        Platform.exit();
    }

//    protected class StageReadyEvent extends ApplicationEvent {
//        public StageReadyEvent(Stage stage) {
//            super(stage);
//        }
//
//        public Stage getStage() {
//            return ((Stage) getSource());
//        }
//    }
}
