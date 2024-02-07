module TestCreator {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
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
    requires com.google.gson;
    requires javafx.graphics;

    opens TestCreator.questions.quickEditors to javafx.fxml;
    opens TestCreator to javafx.fxml, com.google.gson;
    opens TestCreator.options to javafx.fxml;
    opens TestCreator.login to javafx.fxml;
    opens TestCreator.testCreation to javafx.fxml;
    opens TestCreator.questions.editorPanels to javafx.fxml;
    opens TestCreator.questions.testPanels to javafx.fxml;
    opens TestCreator.questions to javafx.fxml, com.google.gson;
    opens TestCreator.utilities to com.google.gson;

    exports TestCreator.utilities to com.google.gson;
    exports TestCreator.questions.quickEditors to javafx.fxml;
    exports TestCreator to javafx.graphics, javafx.fxml;
    exports TestCreator.options to javafx.fxml;
    exports TestCreator.questions.testPanels to javafx.fxml;
    exports TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
    exports TestCreator.login to javafx.fxml, javafx.graphics;
    exports TestCreator.users to javafx.fxml, javafx.graphics, com.google.gson;
    opens TestCreator.users to javafx.fxml, com.google.gson;
}