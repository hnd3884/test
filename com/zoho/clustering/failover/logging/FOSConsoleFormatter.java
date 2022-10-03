package com.zoho.clustering.failover.logging;

import com.zoho.clustering.failover.FOSMain;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.LogRecord;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;

public class FOSConsoleFormatter extends Formatter
{
    private static final SimpleDateFormat TIME_FORMATTER;
    private static final SimpleDateFormat DATE_FORMATTER;
    private static final String FIELD_SEP = " |";
    private static final String LINE_SEP;
    private static FOSConsoleFormatter inst;
    
    public static FOSConsoleFormatter getInst() {
        return FOSConsoleFormatter.inst;
    }
    
    @Override
    public String format(final LogRecord record) {
        final StringBuilder buff = new StringBuilder();
        final Date date = new Date(record.getMillis());
        buff.append(this.getModeString()).append(" |");
        buff.append(FOSConsoleFormatter.TIME_FORMATTER.format(date)).append(" |");
        buff.append(FOSConsoleFormatter.DATE_FORMATTER.format(date)).append(" |");
        buff.append(super.formatMessage(record));
        final Throwable throwable = record.getThrown();
        if (throwable != null) {
            buff.append(FOSConsoleFormatter.LINE_SEP);
            try {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                pw.close();
                buff.append(sw.toString());
            }
            catch (final Exception ignored) {
                ignored.printStackTrace();
            }
        }
        buff.append(FOSConsoleFormatter.LINE_SEP);
        return buff.toString();
    }
    
    private String getModeString() {
        try {
            return FOSMain.getFOS().getMode().toString();
        }
        catch (final IllegalStateException exp) {
            return null;
        }
    }
    
    static {
        TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss:SSS");
        DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
        LINE_SEP = System.getProperty("line.separator");
        FOSConsoleFormatter.inst = new FOSConsoleFormatter();
    }
}
