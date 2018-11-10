package Server.Utils;

import java.util.logging.*;

public class CSVFormatter extends SimpleFormatter {

    public String format(LogRecord record) {

        return record.getMessage() + "\n";
    }
}
