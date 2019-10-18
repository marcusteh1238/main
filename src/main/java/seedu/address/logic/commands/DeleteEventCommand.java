package seedu.address.logic.commands;

import static seedu.address.commons.core.Messages.MESSAGE_DELETE_EVENT_SUCCESS;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_EVENT_INDEX;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.events.EventSource;
import seedu.address.ui.UserOutput;

/**
 * Represents a Command that deletes EventSources from the Model.
 */
public class DeleteEventCommand extends Command {

    private final ModelManager model;
    private final List<Integer> indexes;
    private final List<String> tags;

    DeleteEventCommand(DeleteEventCommandBuilder builder) {
        this.model = builder.getModel();
        this.indexes = builder.getIndexes();
        this.tags = builder.getTags();
    }

    public static CommandBuilder newBuilder(ModelManager model) {
        return new DeleteEventCommandBuilder(model).init();
    }

    @Override
    public UserOutput execute() throws CommandException {
        List<EventSource> list = model.getEventList();

        List<EventSource> events = new ArrayList<>();
        for (Integer index : indexes) {
            try {
                events.add(list.get(index));
            } catch (IndexOutOfBoundsException e) {
                throw new CommandException(String.format(MESSAGE_INVALID_EVENT_INDEX, index + 1));
            }
        }

        for (EventSource event : events) {
            model.removeEvent(event);
        }
        return new UserOutput(String.format(MESSAGE_DELETE_EVENT_SUCCESS, events.stream()
            .map(EventSource::getDescription)
            .collect(Collectors.joining(", "))));
    }
}
