package org.apache.axiom.om.util;

import java.io.OutputStream;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMElement;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.StringWriter;

public class CommonUtils
{
    private CommonUtils() {
    }
    
    public static final String replace(final String name, final String oldT, final String newT) {
        if (name == null) {
            return "";
        }
        final StringBuffer sb = new StringBuffer(name.length() * 2);
        final int len = oldT.length();
        try {
            int start = 0;
            for (int i = name.indexOf(oldT, start); i >= 0; i = name.indexOf(oldT, start)) {
                sb.append(name.substring(start, i));
                sb.append(newT);
                start = i + len;
            }
            if (start < name.length()) {
                sb.append(name.substring(start));
            }
        }
        catch (final NullPointerException ex) {}
        return new String(sb);
    }
    
    public static String callStackToString() {
        return stackToString(new RuntimeException());
    }
    
    public static String stackToString(final Throwable e) {
        final StringWriter sw = new StringWriter();
        final BufferedWriter bw = new BufferedWriter(sw);
        final PrintWriter pw = new PrintWriter(bw);
        e.printStackTrace(pw);
        pw.close();
        String text = sw.getBuffer().toString();
        text = text.substring(text.indexOf("at"));
        text = replace(text, "at ", "DEBUG_FRAME = ");
        return text;
    }
    
    public static long logDebug(final OMElement om, final Log log) {
        return logDebug(om, log, Integer.MAX_VALUE);
    }
    
    public static long logDebug(final OMElement om, final Log log, final int limit) {
        final OMOutputFormat format = new OMOutputFormat();
        format.setDoOptimize(true);
        format.setIgnoreXMLDeclaration(true);
        return logDebug(om, log, limit, format);
    }
    
    public static long logDebug(final OMElement om, final Log log, final int limit, final OMOutputFormat format) {
        final LogOutputStream logStream = new LogOutputStream(log, limit);
        try {
            om.serialize(logStream, format);
            logStream.flush();
            logStream.close();
        }
        catch (final Throwable t) {
            log.debug((Object)t);
            log.error((Object)t);
        }
        return logStream.getLength();
    }
    
    public static boolean isTextualPart(final String contentType) {
        final String ct = contentType.trim();
        return ct.startsWith("text/") || ct.startsWith("application/soap") || ct.startsWith("application/xml") || ct.indexOf("charset") != -1;
    }
}
