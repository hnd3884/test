package com.adventnet.sym.logging;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class OneLineLogFormatter extends Formatter
{
    private static final String LINE_SEPARATOR;
    
    @Override
    public String format(final LogRecord record) {
        final String message = this.formatMessage(record);
        final Throwable thrown = record.getThrown();
        final StringBuilder buf = new StringBuilder();
        buf.append(message);
        if (thrown != null) {
            buf.append(" ").append(OneLineLogFormatter.LINE_SEPARATOR);
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        buf.append(OneLineLogFormatter.LINE_SEPARATOR);
        return buf.toString();
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
