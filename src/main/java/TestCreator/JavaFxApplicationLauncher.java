package TestCreator;

import javafx.application.Application;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JavaFxApplicationLauncher implements CommandLineRunner {
    private final JavaFxApplication javaFxApplication;

    public JavaFxApplicationLauncher(JavaFxApplication javaFxApplication) {
        this.javaFxApplication = javaFxApplication;
    }

    @Override
    public void run(String... args) throws Exception {
        Application.launch(Main.class, args);
    }
}

