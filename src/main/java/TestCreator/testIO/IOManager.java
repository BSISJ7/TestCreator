package TestCreator.testIO;

import TestCreator.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class IOManager {

    public enum IOType {
        XML(0),
        MYSQL(1),
        DYNAMODB(2);

        private int storageTypeIndex;

        IOType(int typeIndex) {
            storageTypeIndex = typeIndex;
        }
    }

    private static final IOManager instance = new IOManager();
    private static IOType IOType;
    private ObservableList<Test> tests;

    private IOManager() {
        IOType = IOType.XML;
        loadTests();
    }

    public static IOManager getInstance() {
        return instance;
    }

    public int size() {
        return tests.size();
    }

    private void loadTests() {
        switch (IOType) {
            case XML:
                tests = FXCollections.observableArrayList(XMLIO.getInstance().loadTests());
                break;
        }
    }

    public void saveTests() {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().saveTests(new ArrayList<>(tests));
                break;
        }
    }

    public void removeTest(Test test) {
        tests.remove(test);

        switch (IOType) {
            case XML:
                XMLIO.getInstance().deleteTest(test);
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
