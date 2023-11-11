package TestCreator.options;

import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static TestCreator.utilities.FXMLAlert.FXML_ALERT;

public class OptionsMenu {

    public RadioButton DarkTheme;
    @FXML
    private ToggleGroup themeGroup;

    @FXML
    private RadioButton nordDark;
    @FXML
    private RadioButton nordLight;
    @FXML
    private RadioButton dracula;
    @FXML
    private RadioButton cupertinoDark;
    @FXML
    private RadioButton cupertinoLight;
    @FXML
    private RadioButton primerDark;
    @FXML
    private RadioButton primerLight;

    @FXML
    private Button okayButton;
    @FXML
    private Button cancelButton;
    private static String projectCssTheme = "/css/themes/primer-dark.css";

    @FXML
    public void initialize() {
        StageManager.setTitle("Options");
        okayButton.setOnAction(event -> {
            try{
                StageManager.setScene("/MainMenu.fxml");
            } catch (IOException e) {
                FXML_ALERT.showAndWait();
                throw new RuntimeException(e);
            }
        });
        cancelButton.setOnAction(event -> {
            try{
                StageManager.setScene("/MainMenu.fxml");
            } catch (IOException e) {
                FXML_ALERT.showAndWait();
                throw new RuntimeException(e);
            }
        });

        Platform.runLater(() -> {
            themeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == nordDark) {
                    projectCssTheme = "/css/themes/nord-dark.css";
                } else if (newValue == nordLight) {
                    projectCssTheme = "/css/themes/nord-light.css";
                } else if (newValue == dracula) {
                    projectCssTheme = "/css/themes/dracula.css";
                } else if (newValue == cupertinoDark) {
                    projectCssTheme = "/css/themes/cupertino-dark.css";
                } else if (newValue == cupertinoLight) {
                    projectCssTheme = "/css/themes/cupertino-light.css";
                } else if (newValue == primerDark) {
                    projectCssTheme = "/css/themes/primer-dark.css";
                } else if (newValue == primerLight) {
                    projectCssTheme = "/css/themes/primer-light.css";
                } else if (newValue == DarkTheme) {
                    projectCssTheme = "/css/themes/DarkTheme.css";
                }

                try{
                    StageManager.setScene("/options/OptionsMenu.fxml");
                } catch (IOException e) {
                    FXML_ALERT.showAndWait();
                    throw new RuntimeException(e);
                }
            });
        });

        if (projectCssTheme.equals("/css/themes/nord-dark.css")) {
            nordDark.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/nord-light.css")) {
            nordLight.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/dracula.css")) {
            dracula.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/cupertino-dark.css")) {
            cupertinoDark.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/cupertino-light.css")) {
            cupertinoLight.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/primer-dark.css")) {
            primerDark.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/primer-light.css")) {
            primerLight.setSelected(true);
        } else if (projectCssTheme.equals("/css/themes/DarkTheme.css")) {
            DarkTheme.setSelected(true);
        }
    }

    public static String getProjectCssTheme() {
        return Objects.requireNonNull(StageManager.class.getResource(projectCssTheme)).toExternalForm();
    }
}
