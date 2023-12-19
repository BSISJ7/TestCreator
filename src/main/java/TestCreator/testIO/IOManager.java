package TestCreator.testIO;

import TestCreator.Test;
import TestCreator.utilities.TestManager;
import javafx.scene.layout.StackPane;

public class IOManager {

    public enum IOType {
        XML(),
        MYSQL(),
        DYNAMODB();
    }

    private static final IOManager instance = new IOManager();
    private static IOType IOType;

    private StackPane rootNode;

    private IOManager() {
        IOType = IOType.XML;
    }

    public static IOManager getInstance() {
        return instance;
    }

    public void loadTests() {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().loadTests();
                break;
        }
    }

    public void saveTests() {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().saveTests();
                break;
        }
    }

    public void deleteTest(Test test) {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().deleteTest(test);
                break;
        }
        TestManager.getInstance().removeTest(test);
    }

    public void backupDatabase() {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().backupDatabase();
                break;
        }
    }

    public void updateTest(Test oldTest, Test newTest) {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().updateTest(oldTest, newTest);
                break;
        }
    }
}
