package seedu.address.ics;

import static seedu.address.commons.util.IcsUtil.parseTimeStamp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import seedu.address.model.events.DateTime;
import seedu.address.model.events.EventSource;

/***
 * Parses an ICS file to allow importing into Horo.
 */
public class IcsParser {

    private static final String FILE_DOES_NOT_EXIST = "Sorry, the file path you've specified is invalid!";
    private static final String FILE_CANNOT_BE_READ = "Sorry, the file specified cannot be read!";
    private static final String FILE_CANNOT_BE_FOUND = "Sorry, the file specified cannot be found!";
    private static final String INVALID_FILE_EXTENSION = "The file specified is not an ICS file!";
    private static final String FILE_IS_CORRUPTED = "The ICS file is corrupted!";
    private static final String EVENT_DESC_CANNOT_BE_EMPTY = "The description of an event cannot be empty!";

    /**
     * This enum represents the different types of objects the IcsParser could be parsing at any given point in time.
     */
    private enum ParseState { None, Todo, Event }

    private File icsFile;

    private IcsParser(String path) throws IcsException {
        this.icsFile = getIcsFile(path);
    }

    public static IcsParser getParser(String path) throws IcsException {
        return new IcsParser(path);
    }

    private File getIcsFile(String path) throws IcsException {
        File file = new File(path);
        if (!file.exists()) {
            throw new IcsException(FILE_DOES_NOT_EXIST);
        } else if (!file.canRead()) {
            throw new IcsException(FILE_CANNOT_BE_READ);
        } else if (!file.toString().endsWith(".ics")) {
            throw new IcsException(INVALID_FILE_EXTENSION);
        }
        return file;
    }

    /**
     * Parses the file provided in the file path and returns an ArrayList of EventSources.
     * @return An ArrayList of EventSources from the file.
     * @throws IcsException Thrown if the file cannot be found or read,
     * is not a proper Ics file, or if a description for an event in the file is empty.
     */
    public ArrayList<EventSource> parse() throws IcsException {
        String fileContent = getFileContent();
        return parseFileContent(fileContent);
    }

    /**
     * Obtains the file content of the ics file specified in the filepath.
     * @return A single String of the whole file.
     * @throws IcsException Thrown if the file cannot be found or read.
     */
    private String getFileContent() throws IcsException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(icsFile));
            StringBuilder sb = new StringBuilder("");
            boolean first = true;
            while (br.ready()) {
                String line = br.readLine();
                if (first) {
                    sb.append(line);
                    first = false;
                } else {
                    sb.append("\n").append(line);
                }
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            throw new IcsException(FILE_CANNOT_BE_FOUND);
        } catch (IOException e) {
            throw new IcsException(FILE_CANNOT_BE_READ);
        }
    }

    /**
     * Parses the Ics file.
     * @param fileContent The contents of the Ics file.
     * @return An ArrayList of EventSources provided by the Ics file.
     * @throws IcsException If the file is not a proper Ics file, or if a description for an event is empty.
     */
    public ArrayList<EventSource> parseFileContent(String fileContent) throws IcsException {
        ArrayList<EventSource> events = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder("");

        ParseState currentlyParsing = ParseState.None;
        String[] lines = fileContent.split("\\r?\\n");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (currentlyParsing == ParseState.Event) {
                if (line.startsWith("END:VEVENT")) {
                    currentlyParsing = ParseState.None;
                    EventSource eventSource = parseSingleEvent(stringBuilder.toString());
                    events.add(eventSource);
                } else {
                    stringBuilder.append(line).append("\n");
                }
            } else {
                if (line.equals("BEGIN:VEVENT")) {
                    if (currentlyParsing != ParseState.None) {
                        throw new IcsException(FILE_IS_CORRUPTED);
                    } else {
                        currentlyParsing = ParseState.Event;
                        stringBuilder = new StringBuilder("");
                    }
                }
            }
        }
        return events;
    }

    /**
     * Creates an EventSource object from the data provided in the ICS File.
     * Currently it will only parse the Start time and Description of the ICS VEvent.
     * @param segment A String that represents the Event object in the ICS File.
     * @return an EventSource object representing the data provided.
     * @throws IcsException Exception thrown when there was an issue while making the EventSource object.
     */
    public EventSource parseSingleEvent(String segment) throws IcsException {
        String[] lines = segment.split("\\r?\\n");
        String description = "";
        DateTime eventStart = null;
        DateTime eventEnd = null;
        for (String line : lines) {
            if (line.startsWith("DESCRIPTION:")) {
                description = line.replaceFirst("DESCRIPTION:", "");
                if (description.equals("")) {
                    throw new IcsException(EVENT_DESC_CANNOT_BE_EMPTY);
                }
            } else if (line.startsWith("DTSTART:")) {
                String timestamp = line.replaceFirst("DTSTART:", "");
                eventStart = parseTimeStamp(timestamp);
            } else if (line.startsWith("DTEND:")) {
                String timestamp = line.replaceFirst("DTEND:", "");
                eventEnd = parseTimeStamp(timestamp);
            } else if (line.equals("END:VCALENDAR") && !line.equals(lines[lines.length - 1])) {
                throw new IcsException(FILE_IS_CORRUPTED);
            }
        }
        return EventSource.newBuilder(description, eventStart)
            .build();
    }
}
