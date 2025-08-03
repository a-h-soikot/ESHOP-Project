package application;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FilterDialogController {

    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField userIdField;

    private FilterData filterData;

    public void onApply() {
        filterData = new FilterData(
            startDatePicker.getValue(),
            endDatePicker.getValue(),
            userIdField.getText()
        );

        ((Stage) startDatePicker.getScene().getWindow()).close();
    }
    
    public void setFilterData(FilterData data) {
        if (data.startDate != null)
            startDatePicker.setValue(data.startDate);
        if (data.endDate != null)
            endDatePicker.setValue(data.endDate);
        userIdField.setText(data.userId != null ? data.userId : "");
    }

    public FilterData getFilterData() {
        return filterData;
    }
}
