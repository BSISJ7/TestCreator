module TestCreator {
    requires java.xml;
    requires reflections;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires mysql.connector.java;
    requires java.sql;
    requires java.desktop;
    requires aws.java.sdk.dynamodb;
    requires aws.java.sdk.core;

    opens com.BSISJ7.TestCreator to javafx.fxml;
    opens com.BSISJ7.TestCreator.testCreation to javafx.fxml;
    opens com.BSISJ7.TestCreator.questions.editorPanels to javafx.fxml;
    opens com.BSISJ7.TestCreator.questions.testPanels to javafx.fxml;

    exports com.BSISJ7.TestCreator;
    // exports com.BSISJ7.TestCreator to javafx.fxml, javafx.graphics;
    exports com.BSISJ7.TestCreator.questions.testPanels to javafx.fxml;
    exports com.BSISJ7.TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
}