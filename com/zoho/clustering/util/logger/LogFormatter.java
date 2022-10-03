package com.zoho.clustering.util.logger;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

public class LogFormatter extends Formatter
{
    private static final SimpleDateFormat TIME_FORMATTER;
    private static final SimpleDateFormat DATE_FORMATTER;
    protected static final String F_SEP = "|";
    private static final String L_SEP;
    
    @Override
    public String format(final LogRecord record) {
        final StringBuilder buff = new StringBuilder();
        final Date date = new Date(record.getMillis());
        buff.append(brackets(LogFormatter.DATE_FORMATTER.format(date))).append("|");
        buff.append(brackets(LogFormatter.TIME_FORMATTER.format(date))).append("|");
        buff.append(brackets(record.getLoggerName())).append("|");
        buff.append(brackets(record.getThreadID())).append("|");
        buff.append(brackets(record.getLevel().getLocalizedName())).append("|");
        buff.append(this.formatMessage(record));
        final Throwable throwable = record.getThrown();
        if (throwable != null) {
            buff.append(LogFormatter.L_SEP);
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                pw.close();
                buff.append(sw.toString());
            }
            catch (final Exception ex) {}
        }
        buff.append(LogFormatter.L_SEP);
        return buff.toString();
    }
    
    protected static String brackets(final String str) {
        return "[" + ((str != null) ? str : "null") + "]";
    }
    
    protected static String brackets(final int intVal) {
        return "[" + String.valueOf(intVal) + "]";
    }
    
    static {
        TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS");
        DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
        L_SEP = System.getProperty("line.separator");
    }
}
