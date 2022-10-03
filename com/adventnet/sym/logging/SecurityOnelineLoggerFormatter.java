package com.adventnet.sym.logging;

import java.util.Iterator;
import java.text.MessageFormat;
import org.json.simple.JSONObject;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public class SecurityOnelineLoggerFormatter extends EnhancedLogFormatter
{
    private static final String LINE_SEPARATOR;
    static final String DEFAULT_VAL = "-";
    static final String SECURITY_ONELINE_LOG = "SecurityOnelineLog";
    static final int USERNAME_MAX_LENGTH = 20;
    static final int IPADDRESS_MAX_LENGTH = 18;
    static final int ROLE_MAX_LENGTH = 20;
    static final int SESSIONID_MAX_LENGTH = 25;
    static final int MODULE_MAX_LENGTH = 15;
    static final int OPERATION_MAX_LENGTH = 22;
    
    public static String formattedOneLineLog(final LogRecord record) {
        final Object[] objects = record.getParameters();
        final StringBuilder logData = new StringBuilder();
        final long time = record.getMillis();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("[HH:mm:ss:SSS]|[MM-dd-yyyy]");
        final StringBuilder buffer = new StringBuilder();
        buffer.append(dateFormatter.format(time));
        buffer.append("|[");
        buffer.append("SecurityOnelineLog");
        buffer.append("]|[");
        buffer.append(record.getLevel().toString());
        buffer.append("]|[");
        buffer.append(record.getThreadID());
        buffer.append("]|[");
        buffer.append(LoggingThreadLocal.getLoggingId());
        buffer.append("]:  ");
        logData.append(rightFixedLengthString(buffer.toString(), 115));
        logData.append(contructmsgData(objects)).append("[[").append(SecurityOnelineLoggerFormatter.LINE_SEPARATOR);
        return logData.toString();
    }
    
    public static String contructmsgData(final Object[] objects) {
        final HashMap<String, String> metadataList = (HashMap<String, String>)objects[0];
        final StringBuilder stringBuilder = new StringBuilder(constructMetaData(metadataList));
        final Object obj = objects[1];
        stringBuilder.append(constructMsgValue(obj));
        return stringBuilder.toString();
    }
    
    public static String constructMsgValue(final Object obj) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (obj instanceof JSONObject) {
            final JSONObject jsonObject = (JSONObject)obj;
            for (final String key : jsonObject.keySet()) {
                stringBuilder.append(key).append(":").append(jsonObject.get((Object)key)).append(" |");
            }
        }
        else if (obj instanceof String[]) {
            final String[] strings = (String[])obj;
            for (int i = 0; i < strings.length; ++i) {
                stringBuilder.append(strings[i]).append(" |");
            }
        }
        else {
            final Object[] objects1 = (Object[])obj;
            String msg = (String)objects1[0];
            if (objects1.length >= 2) {
                final Object[] objects2 = (Object[])objects1[1];
                msg = MessageFormat.format(msg, objects2);
            }
            stringBuilder.append(msg);
        }
        return stringBuilder.toString();
    }
    
    private static String constructMetaData(final HashMap<String, String> metadataList) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        appendValue(stringBuilder, metadataList.getOrDefault("MODULE", "-"), 15);
        stringBuilder.append("] [");
        appendValue(stringBuilder, metadataList.getOrDefault("OPERATION", "-"), 22);
        stringBuilder.append("] [");
        appendValue(stringBuilder, metadataList.getOrDefault("DONE_BY", "-"), 20);
        stringBuilder.append("] [");
        appendValue(stringBuilder, metadataList.getOrDefault("WITH_UMROLE", "-"), 20);
        stringBuilder.append("] [");
        appendValue(stringBuilder, metadataList.getOrDefault("REMOTE_IP", "-"), 18);
        stringBuilder.append("] [");
        appendValue(stringBuilder, metadataList.getOrDefault("SESSION_ID", "-"), 25);
        stringBuilder.append("] ");
        return stringBuilder.toString();
    }
    
    private static void appendValue(final StringBuilder logData, String value, final int length) {
        if (value == null) {
            value = "";
        }
        logData.append(leftFixedLengthString(value, length));
    }
    
    @Override
    public synchronized String format(final LogRecord record) {
        final String msg = record.getMessage();
        final Object[] objects = record.getParameters();
        if (msg.equals("SecurityOnelineLogger") && objects.length >= 2) {
            return formattedOneLineLog(record);
        }
        return super.format(record);
    }
    
    private static String leftFixedLengthString(final String string, final int length) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int width = length - string.length(); width > 0; --width) {
            stringBuilder.append(' ');
        }
        return stringBuilder.append(string).toString();
    }
    
    private static String rightFixedLengthString(final String string, final int length) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(string);
        for (int width = length - string.length(); width > 0; --width) {
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
