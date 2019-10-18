package seedu.address.ui.panel.list;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.events.EventSource;
import seedu.address.ui.UiParser;
import seedu.address.ui.UiPart;

/**
 * An UI component that displays information of a {@code Event}.
 */
public class EventCardStub extends UiPart<Region> {

    private static final String FXML = "EventListCard.fxml";

    public final EventSource eventSource;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label dateTime;

    /**
     * Constructor for EventCard. Creates an event card that will be placed in EventListPanel.
     * @param eventSource The instance of the event itself.
     * @param displayedIndex The number representing the event.
     */
    public EventCardStub(EventSource eventSource, int displayedIndex, UiParser uiParser) {
        super(FXML);
        this.eventSource = eventSource;
        name.setText(displayedIndex + ". " + eventSource.getDescription());
        dateTime.setText(uiParser.parseDateToString(eventSource.getStartDateTime().toInstant()));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof EventCardStub)) {
            return false;
        }

        // state check
        EventCardStub card = (EventCardStub) other;
        return eventSource.equals(card.eventSource);
    }
}
