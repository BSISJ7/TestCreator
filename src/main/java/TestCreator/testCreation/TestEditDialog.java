package TestCreator.testCreation;

import TestCreator.Main;
import TestCreator.Test;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;

import static TestCreator.Test.shortDateFormat;

public class TestEditDialog {

    @FXML
    TextField testName;
    @FXML
    TextArea testDesc;
    @FXML
    DatePicker reviewDate;
    @FXML
    private Test test;
    


    public void initialize() {
        test = new Test();
        reviewDate.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return shortDateFormat.format(date);
                }
                return "";
            }

            @Override
            public LocalDate fromString(String date) {
                if (date != null && !date.isEmpty()) {
                    return LocalDate.parse(date, shortDateFormat);
                }
                return null;
            }
        });

        Callback<DatePicker, DateCell> dateCell = new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        if (date.isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                };
            }
        };
        reviewDate.setDayCellFactory(dateCell);
    }

    public Test getTest() {
        test.setName(testName.getText());
        test.setDescription(testDesc.getText());
        test.setReviewDate(reviewDate.getValue());
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
        testName.setText(test.getName());
        testDesc.setText(test.getDescription());
        reviewDate.setValue(test.getReviewDate().toLocalDate());
    }
}
