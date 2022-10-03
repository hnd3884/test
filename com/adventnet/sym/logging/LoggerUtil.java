package com.adventnet.sym.logging;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public class LoggerUtil
{
    private static final String LINE_SEPARATOR;
    
    public static String defaultLogFormatter(final LogRecord record, final String message) {
        final String name = record.getLoggerName();
        final long time = record.getMillis();
        final Throwable thrown = record.getThrown();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("[HH:mm:ss:SSS]|[MM-dd-yyyy]");
        final StringBuilder buf = new StringBuilder();
        buf.append(dateFormatter.format(time));
        buf.append("|[");
        buf.append(name);
        buf.append("]|[");
        buf.append(record.getLevel().toString());
        buf.append("]|[");
        buf.append(record.getThreadID());
        buf.append("]|[");
        buf.append(LoggingThreadLocal.getLoggingId());
        buf.append("]: ");
        buf.append(message);
        buf.append("|");
        if (thrown != null) {
            buf.append(" ").append(LoggerUtil.LINE_SEPARATOR);
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        buf.append(LoggerUtil.LINE_SEPARATOR);
        return buf.toString();
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
