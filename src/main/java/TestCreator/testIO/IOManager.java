package TestCreator.testIO;

import TestCreator.Test;
import TestCreator.utilities.TestManager;

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

    private IOManager() {
        IOType = IOType.XML;
        loadTests();
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

    public void updateTest(Test oldTest, Test newTest) {
        switch (IOType) {
            case XML:
                XMLIO.getInstance().updateTest(oldTest, newTest);
                break;
        }
    }
}
