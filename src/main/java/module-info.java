module TestCreator {
    requires java.xml;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.media;
    requires javafx.swing;
    requires javafx.web;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;
    requires aws.java.sdk.dynamodb;
    requires aws.java.sdk.core;
    requires org.reflections;

    opens TestCreator to javafx.fxml;
    opens TestCreator.options to javafx.fxml;
    opens TestCreator.login to javafx.fxml;
    opens TestCreator.testCreation to javafx.fxml;
    opens TestCreator.questions.editorPanels to javafx.fxml;
    opens TestCreator.questions.testPanels to javafx.fxml;
    opens TestCreator.questions to javafx.fxml;

    exports TestCreator to javafx.graphics, javafx.fxml;
    exports TestCreator.login to javafx.fxml;
    exports TestCreator.options to javafx.fxml;
    exports TestCreator.questions.testPanels to javafx.fxml;
    exports TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
}