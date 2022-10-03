package com.adventnet.mfw.logging;

import com.zoho.conf.Configuration;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

public class DefaultFormatter extends Formatter
{
    private static final String LINE_SEPARATOR;
    protected static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER;
    protected static final ThreadLocal<StringBuilder> BUILDER;
    
    @Override
    public String format(final LogRecord record) {
        final String name = record.getLoggerName();
        final long time = record.getMillis();
        final String message = this.formatMessage(record);
        final Throwable thrown = record.getThrown();
        final StringBuilder buf = DefaultFormatter.BUILDER.get();
        buf.append(DefaultFormatter.DATE_FORMATTER.get().format(time));
        buf.append("|[");
        buf.append(name);
        buf.append("]|[");
        buf.append(record.getLevel().toString());
        buf.append("]|[");
        buf.append(record.getThreadID());
        buf.append("]: ");
        buf.append(message);
        buf.append("|");
        if (thrown != null) {
            buf.append(" ").append(DefaultFormatter.LINE_SEPARATOR);
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            thrown.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        buf.append(DefaultFormatter.LINE_SEPARATOR);
        return buf.toString();
    }
    
    static {
        LINE_SEPARATOR = Configuration.getString("line.separator");
        DATE_FORMATTER = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat("[HH:mm:ss:SSS]|[MM-dd-yyyy]");
            }
        };
        BUILDER = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder(250);
            }
            
            @Override
            public StringBuilder get() {
                final StringBuilder sb = super.get();
                sb.setLength(0);
                return sb;
            }
        };
    }
}
