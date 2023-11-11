package TestCreator.utilities;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class FXMLAlert {
    public static final Alert FXML_ALERT = new Alert(Alert.AlertType.ERROR, "Error loading FXML", ButtonType.OK);
}
