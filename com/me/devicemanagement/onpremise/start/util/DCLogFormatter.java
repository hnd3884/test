package com.me.devicemanagement.onpremise.start.util;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.FieldPosition;
import com.adventnet.sym.logging.LoggerUtil;
import java.util.logging.LogRecord;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;

public class DCLogFormatter extends Formatter
{
    Date dat;
    private static final String FORMAT = "{0,date} {0,time}";
    private MessageFormat formatter;
    private Object[] args;
    private String lineSeparator;
    private static String enableUniformLogFormatter;
    
    public DCLogFormatter() {
        this.dat = new Date();
        this.args = new Object[1];
        this.lineSeparator = System.getProperty("line.separator");
    }
    
    @Override
    public synchronized String format(final LogRecord record) {
        if (DCLogFormatter.enableUniformLogFormatter.equalsIgnoreCase("true")) {
            final String message = this.formatMessage(record);
            return LoggerUtil.defaultLogFormatter(record, message);
        }
        final StringBuffer sb = new StringBuffer();
        this.dat.setTime(record.getMillis());
        this.args[0] = this.dat;
        final StringBuffer text = new StringBuffer();
        if (this.formatter == null) {
            this.formatter = new MessageFormat("{0,date} {0,time}");
        }
        this.formatter.format(this.args, text, null);
        sb.append(text);
        sb.append(" ");
        sb.append(" [");
        sb.append(record.getLoggerName());
        sb.append("] ");
        final String message2 = this.formatMessage(record);
        sb.append(" [");
        sb.append(record.getLevel().getLocalizedName());
        sb.append("] ");
        sb.append(": ");
        sb.append(message2);
        if (record.getThrown() != null) {
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (final Exception ex) {}
        }
        sb.append(this.lineSeparator);
        return sb.toString();
    }
    
    static {
        DCLogFormatter.enableUniformLogFormatter = System.getProperty("uniformlogformatter.enable", "false");
    }
}
