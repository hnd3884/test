package com.unboundid.util;

import java.util.Date;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.util.logging.Formatter;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MinimalLogFormatter extends Formatter implements Serializable
{
    public static final String DEFAULT_TIMESTAMP_FORMAT = "'['dd/MMM/yyyy:HH:mm:ss Z']'";
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTERS;
    private static final ThreadLocal<StringBuilder> BUFFERS;
    private static final long serialVersionUID = -2884878613513769233L;
    private final boolean includeLevel;
    private final boolean lineBreakAfterHeader;
    private final boolean lineBreakAfterMessage;
    private final String timestampFormat;
    
    public MinimalLogFormatter() {
        this("'['dd/MMM/yyyy:HH:mm:ss Z']'", false, false, false);
    }
    
    public MinimalLogFormatter(final String timestampFormat, final boolean includeLevel, final boolean lineBreakAfterHeader, final boolean lineBreakAfterMessage) {
        this.timestampFormat = timestampFormat;
        this.includeLevel = includeLevel;
        this.lineBreakAfterHeader = lineBreakAfterHeader;
        this.lineBreakAfterMessage = lineBreakAfterMessage;
    }
    
    @Override
    public String format(final LogRecord record) {
        StringBuilder b = MinimalLogFormatter.BUFFERS.get();
        if (b == null) {
            b = new StringBuilder();
            MinimalLogFormatter.BUFFERS.set(b);
        }
        else {
            b.setLength(0);
        }
        if (this.timestampFormat != null) {
            SimpleDateFormat f = MinimalLogFormatter.DATE_FORMATTERS.get();
            if (f == null) {
                f = new SimpleDateFormat(this.timestampFormat);
                MinimalLogFormatter.DATE_FORMATTERS.set(f);
            }
            b.append(f.format(new Date()));
        }
        if (this.includeLevel) {
            if (b.length() > 0) {
                b.append(' ');
            }
            b.append(record.getLevel().toString());
        }
        if (this.lineBreakAfterHeader) {
            b.append(StaticUtils.EOL);
        }
        else if (b.length() > 0) {
            b.append(' ');
        }
        b.append(this.formatMessage(record));
        if (this.lineBreakAfterMessage) {
            b.append(StaticUtils.EOL);
        }
        return b.toString();
    }
    
    static {
        DATE_FORMATTERS = new ThreadLocal<SimpleDateFormat>();
        BUFFERS = new ThreadLocal<StringBuilder>();
    }
}
