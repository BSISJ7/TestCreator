package TestCreator.login;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.oauth2.Oauth2Scopes;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleAuthenticator {

    private static final String CREDENTIALS_FILE_PATH = System.getenv("GOOGLE_CREDENTIALS");

    private static final List<String> SCOPES = List.of(
            Oauth2Scopes.USERINFO_EMAIL,
            Oauth2Scopes.USERINFO_PROFILE
//            CalendarScopes.CALENDAR_READONLY
    );
    private static final String TOKENS_DIRECTORY_PATH = STR."\{System.getProperty("user.home")}\{File.separator}.tokens";

    public static Credential authorize() throws IOException, GeneralSecurityException {

        // Load client secrets
//        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
//                GsonFactory.getDefaultInstance(),
//                new InputStreamReader(Objects.requireNonNull(GoogleAuthenticator.class.getResourceAsStream(CREDENTIALS_FILE_PATH))));

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                GsonFactory.getDefaultInstance(),
                new FileReader(CREDENTIALS_FILE_PATH));

        // Build flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // Authorize
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
