module TestCreator {
    requires javafx.graphics;
    requires java.xml;
    requires reflections;
    requires javafx.fxml;
    requires javafx.controls;
    requires mysql.connector.java;
    requires java.sql;
    requires java.desktop;
    requires javafx.swing;
    requires aws.java.sdk.dynamodb;
    requires aws.java.sdk.core;

    opens com.BSISJ7.TestCreator to javafx.fxml;
    opens com.BSISJ7.TestCreator.testCreation to javafx.fxml;
    opens com.BSISJ7.TestCreator.questions.editorPanels to javafx.fxml;
    opens com.BSISJ7.TestCreator.questions.testPanels to javafx.fxml;

    exports com.BSISJ7.TestCreator to javafx.fxml, javafx.graphics;
    exports com.BSISJ7.TestCreator.questions.testPanels to javafx.fxml;
    exports com.BSISJ7.TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
}