package seedu.address.ui.panel.calendar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.ui.UiPart;

/**
 * An UI component that displays the day on the calendar.
 */
public class CalendarGridDay extends UiPart<Region> {

    private static final String FXML = "CalendarGridDay.fxml";
    private Integer dayIndex;
    private Integer totalEvents;

    @FXML
    private Label calendarDay;

    // TODO: Add total events to make the color changeable for each day.
    public CalendarGridDay(Integer dayIndex) {
        super(FXML);
        this.dayIndex = dayIndex;
        calendarDay.setText(dayIndex.toString());
    }


}
