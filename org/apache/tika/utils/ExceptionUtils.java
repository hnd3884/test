package org.apache.tika.utils;

import java.util.regex.Matcher;
import java.io.Writer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.tika.exception.TikaException;
import java.util.regex.Pattern;

public class ExceptionUtils
{
    private static final Pattern MSG_PATTERN;
    
    public static String getFilteredStackTrace(final Throwable t) {
        Throwable cause = t;
        if (t.getClass().equals(TikaException.class) && t.getCause() != null) {
            cause = t.getCause();
        }
        return getStackTrace(cause);
    }
    
    public static String getStackTrace(final Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter writer = new PrintWriter(result);
        t.printStackTrace(writer);
        try {
            writer.flush();
            result.flush();
            writer.close();
            result.close();
        }
        catch (final IOException ex) {}
        return result.toString();
    }
    
    public static String trimMessage(final String trace) {
        final Matcher msgMatcher = ExceptionUtils.MSG_PATTERN.matcher(trace);
        if (msgMatcher.find()) {
            return msgMatcher.replaceFirst("");
        }
        return trace;
    }
    
    static {
        MSG_PATTERN = Pattern.compile(":[^\r\n]+");
    }
}
