package com.BSISJ7.TestCreator.testIO;

import com.BSISJ7.TestCreator.Test;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class TestData {

    private static final TestData instance = new TestData();
    private static IOType IOType;
    private ObservableList<Test> tests;
    private TestData() {
        IOType = IOType.XML;
        loadTests();
    }

    public static TestData getInstance() {
        return instance;
    }

    public ObservableList<Test> getTests() {
        return tests;
    }

    public void addTest(Test test) {
        tests.add(test);
    }

    public void removeItemAtIndex(int index) {
        tests.remove(index);
    }

    public void removeTest(String name) {
        for (Test test : tests) {
            if (test.getName().equals(name)) {
                removeTest(test);
                break;
            }
        }
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

    public enum IOType {
        XML(0),
        MYSQL(1),
        DYNAMODB(2);

        private int typeIndex;

        IOType(int typeIndex) {
            this.typeIndex = typeIndex;
        }
    }
}
