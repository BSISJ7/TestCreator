package TestCreator.options;

import TestCreator.utilities.StackPaneAlert;
import TestCreator.utilities.StageManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    private static CSS_THEMES projectCssTheme = CSS_THEMES.PRIMER_DARK;

    public enum CSS_THEMES {
        NORD_DARK("/css/themes/nord-dark.css"),
        NORD_LIGHT("/css/themes/nord-light.css"),
        DRACULA("/css/themes/dracula.css"),
        CUPERTINO_DARK("/css/themes/cupertino-dark.css"),
        CUPERTINO_LIGHT("/css/themes/cupertino-light.css"),
        PRIMER_DARK("/css/themes/primer-dark.css"),
        PRIMER_LIGHT("/css/themes/primer-light.css"),
        DARK_THEME("/css/themes/DarkTheme.css"),
        DEFAULT(null);

        private final String theme;

        CSS_THEMES(String theme) {
            this.theme = theme;
        }

        public String getTheme() {
            return theme;
        }
    };


    @FXML
    private CheckBox audioCheckbox;



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
                projectCssTheme = CSS_THEMES.NORD_DARK;
            } else if (newValue == nordLight) {
                projectCssTheme = CSS_THEMES.NORD_LIGHT;
            } else if (newValue == dracula) {
                projectCssTheme = CSS_THEMES.DRACULA;
            } else if (newValue == cupertinoDark) {
                projectCssTheme = CSS_THEMES.CUPERTINO_DARK;
            } else if (newValue == cupertinoLight) {
                projectCssTheme = CSS_THEMES.CUPERTINO_LIGHT;
            } else if (newValue == primerDark) {
                projectCssTheme = CSS_THEMES.PRIMER_DARK;
            } else if (newValue == primerLight) {
                projectCssTheme = CSS_THEMES.PRIMER_LIGHT;
            } else if (newValue == darkTheme) {
                projectCssTheme = CSS_THEMES.DARK_THEME;
            } else {
                projectCssTheme = CSS_THEMES.DEFAULT;
            }

            try{
                StageManager.setScene("/options/OptionsMenu.fxml");
                StageManager.clearStageController();
            } catch (IOException e) {
                new StackPaneAlert(rootNode, "Error loading OptionsMenu.fxml").show();
                throw new RuntimeException(e);
            }
        }));

        switch (projectCssTheme) {
            case NORD_DARK -> nordDark.setSelected(true);
            case NORD_LIGHT -> nordLight.setSelected(true);
            case DRACULA -> dracula.setSelected(true);
            case CUPERTINO_DARK -> cupertinoDark.setSelected(true);
            case CUPERTINO_LIGHT -> cupertinoLight.setSelected(true);
            case PRIMER_DARK -> primerDark.setSelected(true);
            case PRIMER_LIGHT -> primerLight.setSelected(true);
            case DARK_THEME -> darkTheme.setSelected(true);
            case DEFAULT -> defaultTheme.setSelected(true);
        }
    }

  public static String getCssName() {
        return switch (projectCssTheme) {
            case NORD_DARK -> "nord-dark";
            case NORD_LIGHT -> "nord-light";
            case DRACULA -> "dracula";
            case CUPERTINO_DARK -> "cupertino-dark";
            case CUPERTINO_LIGHT -> "cupertino-light";
            case PRIMER_DARK -> "primer-dark";
            case PRIMER_LIGHT -> "primer-light";
            case DARK_THEME -> "DarkTheme";
            case DEFAULT -> "";
        };
    }

    public static String getCssFullPath() {
        return Objects.requireNonNull(StageManager.class.getResource(projectCssTheme.getTheme())).toExternalForm();
    }

    public void setReadQuestion(ActionEvent actionEvent) {
        //TODO: set user question reading preference
    }
}
