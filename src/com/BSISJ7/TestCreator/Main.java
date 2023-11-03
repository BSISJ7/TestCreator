package com.BSISJ7.TestCreator;

import com.BSISJ7.TestCreator.questions.Question;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

import static com.BSISJ7.TestCreator.MainMenu.MAIN_MENU_LOCATION;

public class Main extends Application {

    public final static String workDir = System.getProperty("user.dir");

    public static void main(String[] args) {

        Question.initializeQuestionTypeList();
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(MAIN_MENU_LOCATION)));
        root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/BSISJ7/TestCreator/LoginPanel.fxml")));
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        primaryStage.setOnCloseRequest(_ -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
