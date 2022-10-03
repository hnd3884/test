package org.apache.juli;

import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class VerbatimFormatter extends Formatter
{
    @Override
    public String format(final LogRecord record) {
        return record.getMessage() + System.lineSeparator();
    }
}
