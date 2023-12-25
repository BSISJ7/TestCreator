package TestCreator.options;

import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;


public class OptionsMenu {
    public RadioButton defaultTheme;
    @FXML
    private RadioButton darkTheme;
    @FXML
    private StackPane rootNode;
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
        okayButton.setOnAction(_ -> {
            try{
                StageManager.setScene("/MainMenu.fxml");
                StageManager.clearStageController();
            } catch (IOException e) {
                new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
                throw new RuntimeException(e);
            }
        });
        cancelButton.setOnAction(_ -> {
            try{
                StageManager.setScene("/MainMenu.fxml");
                StageManager.clearStageController();
            } catch (IOException e) {
                new StackPaneAlert(rootNode, "Error loading MainMenu.fxml").show();
                throw new RuntimeException(e);
            }
        });

        Platform.runLater(() -> themeGroup.selectedToggleProperty().addListener((_, _, newValue) -> {
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
            } else if (newValue == darkTheme) {
                projectCssTheme = "/css/themes/DarkTheme.css";
            }else
                projectCssTheme = "";

            try{
                StageManager.setScene("/options/OptionsMenu.fxml");
                StageManager.clearStageController();
            } catch (IOException e) {
                new StackPaneAlert(rootNode, "Error loading OptionsMenu.fxml").show();
                throw new RuntimeException(e);
            }
        }));

        switch (projectCssTheme) {
            case "/css/themes/nord-dark.css" -> nordDark.setSelected(true);
            case "/css/themes/nord-light.css" -> nordLight.setSelected(true);
            case "/css/themes/dracula.css" -> dracula.setSelected(true);
            case "/css/themes/cupertino-dark.css" -> cupertinoDark.setSelected(true);
            case "/css/themes/cupertino-light.css" -> cupertinoLight.setSelected(true);
            case "/css/themes/primer-dark.css" -> primerDark.setSelected(true);
            case "/css/themes/primer-light.css" -> primerLight.setSelected(true);
            case "/css/themes/DarkTheme.css" -> darkTheme.setSelected(true);
            case null, default -> defaultTheme.setSelected(true);
        }
    }

  public static String getCssName() {
        return switch (projectCssTheme) {
            case "/css/themes/nord-dark.css" -> "nord-dark";
            case "/css/themes/nord-light.css" -> "nord-light";
            case "/css/themes/dracula.css" -> "dracula";
            case "/css/themes/cupertino-dark.css" -> "cupertino-dark";
            case "/css/themes/cupertino-light.css" -> "cupertino-light";
            case "/css/themes/primer-dark.css" -> "primer-dark";
            case "/css/themes/primer-light.css" -> "primer-light";
            case "/css/themes/DarkTheme.css" -> "DarkTheme";
            case null, default -> "";
        };
    }

    public static String getCssFullPath() {
        return Objects.requireNonNull(StageManager.class.getResource(projectCssTheme)).toExternalForm();
    }
}
