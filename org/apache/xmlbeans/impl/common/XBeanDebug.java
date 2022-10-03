package org.apache.xmlbeans.impl.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.xmlbeans.SystemProperties;
import java.io.PrintStream;

public class XBeanDebug
{
    public static final int TRACE_SCHEMA_LOADING = 1;
    public static final String traceProp = "org.apache.xmlbeans.impl.debug";
    public static final String defaultProp = "";
    private static int _enabled;
    private static int _indent;
    private static String _indentspace;
    static PrintStream _err;
    
    private static int initializeBitsFromProperty() {
        int bits = 0;
        final String prop = SystemProperties.getProperty("org.apache.xmlbeans.impl.debug", "");
        if (prop.indexOf("TRACE_SCHEMA_LOADING") >= 0) {
            bits |= 0x1;
        }
        return bits;
    }
    
    public static void enable(final int bits) {
        XBeanDebug._enabled |= bits;
    }
    
    public static void disable(final int bits) {
        XBeanDebug._enabled &= ~bits;
    }
    
    public static void trace(final int bits, final String message, final int indent) {
        if (test(bits)) {
            synchronized (XBeanDebug.class) {
                if (indent < 0) {
                    XBeanDebug._indent += indent;
                }
                final String spaces = (XBeanDebug._indent < 0) ? "" : ((XBeanDebug._indent > XBeanDebug._indentspace.length()) ? XBeanDebug._indentspace : XBeanDebug._indentspace.substring(0, XBeanDebug._indent));
                final String logmessage = Thread.currentThread().getName() + ": " + spaces + message + "\n";
                System.err.print(logmessage);
                if (indent > 0) {
                    XBeanDebug._indent += indent;
                }
            }
        }
    }
    
    public static boolean test(final int bits) {
        return (XBeanDebug._enabled & bits) != 0x0;
    }
    
    public static String log(final String message) {
        log(message, null);
        return message;
    }
    
    public static String logStackTrace(final String message) {
        log(message, new Throwable());
        return message;
    }
    
    private static synchronized String log(final String message, final Throwable stackTrace) {
        if (XBeanDebug._err == null) {
            try {
                final File diagnosticFile = File.createTempFile("xmlbeandebug", ".log");
                XBeanDebug._err = new PrintStream(new FileOutputStream(diagnosticFile));
                System.err.println("Diagnostic XML Bean debug log file created: " + diagnosticFile);
            }
            catch (final IOException e) {
                XBeanDebug._err = System.err;
            }
        }
        XBeanDebug._err.println(message);
        if (stackTrace != null) {
            stackTrace.printStackTrace(XBeanDebug._err);
        }
        return message;
    }
    
    public static Throwable logException(final Throwable t) {
        log(t.getMessage(), t);
        return t;
    }
    
    static {
        XBeanDebug._enabled = initializeBitsFromProperty();
        XBeanDebug._indent = 0;
        XBeanDebug._indentspace = "                                                                                ";
    }
}
