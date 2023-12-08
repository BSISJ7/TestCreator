module TestCreator {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.reflections;
    requires java.xml;
//    requires javafx.swing;
    requires aws.java.sdk.core;
    requires aws.java.sdk.dynamodb;
    requires java.sql;
    requires java.net.http;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.services.secretsmanager;
    requires java.mail;
    requires jpro.webapi;
    requires com.jfoenix;
    requires software.amazon.awssdk.services.ses;
    requires org.controlsfx.controls;
    requires transitive javafx.swing;
    requires org.fxmisc.richtext;
    requires undofx;
    requires reactfx;


    opens TestCreator to javafx.fxml;
    opens TestCreator.options to javafx.fxml;
    opens TestCreator.login to javafx.fxml;
    opens TestCreator.testCreation to javafx.fxml;
    opens TestCreator.questions.editorPanels to javafx.fxml;
    opens TestCreator.questions.testPanels to javafx.fxml;
    opens TestCreator.questions to javafx.fxml;

    exports TestCreator to javafx.graphics, javafx.fxml;
    exports TestCreator.options to javafx.fxml;
    exports TestCreator.questions.testPanels to javafx.fxml;
    exports TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
    exports TestCreator.login to javafx.fxml, javafx.graphics;
}