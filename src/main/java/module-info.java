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
    requires software.amazon.awssdk.awscore;
    requires reactfx;
    requires com.google.gson;
    requires javafx.graphics;
    requires software.amazon.awssdk.services.polly;
    requires software.amazon.awssdk.core;
    requires javafx.media;
    requires jave.core;
    requires software.amazon.awssdk.services.transcribestreaming;
    requires org.reactivestreams;
    requires com.google.api.client.auth;
    requires com.google.api.client;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.api.services.calendar;
    requires com.google.api.services.oauth2;
    requires sdk.core;
    requires text.to.speech;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpmime;
    requires assemblyai.java;
    requires vosk;
    requires org.json;
    requires javafx.web;
    requires jdk.jsobject;

    opens TestCreator to javafx.fxml, com.google.gson;
    opens TestCreator.questions.quickEditors to javafx.fxml;
    opens TestCreator.questions.quickEditors.multiEditors to javafx.fxml;
    opens TestCreator.options to javafx.fxml;
    opens TestCreator.login to javafx.fxml;
    opens TestCreator.testCreation to javafx.fxml;
    opens TestCreator.questions.editorPanels to javafx.fxml;
    opens TestCreator.questions.testPanels to javafx.fxml;
    opens TestCreator.questions to javafx.fxml, com.google.gson;
    opens TestCreator.utilities to com.google.gson;
    opens TestCreator.audio to com.google.gson;
    opens TestCreator.users to javafx.fxml, com.google.gson;
    opens TestCreator.audio.textToSpeech to com.google.gson;
    opens TestCreator.audio.transcription to com.google.gson;

    exports TestCreator.utilities to com.google.gson;
    exports TestCreator.questions.quickEditors to javafx.fxml;
    exports TestCreator.options to javafx.fxml;
    exports TestCreator.questions.testPanels to javafx.fxml;
    exports TestCreator.questions.editorPanels to javafx.fxml, javafx.graphics;
    exports TestCreator.login to javafx.fxml, javafx.graphics;
    exports TestCreator.users to javafx.fxml, javafx.graphics, com.google.gson;
    exports TestCreator.audio to com.google.gson;
    exports TestCreator to com.google.gson, javafx.fxml, javafx.graphics;
    exports TestCreator.audio.textToSpeech to com.google.gson;
    exports TestCreator.audio.transcription to com.google.gson;
    exports TestCreator.questions.quickEditors.multiEditors to javafx.fxml;
}