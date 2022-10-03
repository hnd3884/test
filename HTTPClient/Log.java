package HTTPClient;

import java.io.FileWriter;
import java.util.TimeZone;
import java.util.Calendar;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.Writer;

public class Log
{
    public static final int CONN = 1;
    public static final int RESP = 2;
    public static final int DEMUX = 4;
    public static final int AUTH = 8;
    public static final int COOKI = 16;
    public static final int MODS = 32;
    public static final int SOCKS = 64;
    public static final int URLC = 128;
    public static final int ALL = -1;
    private static final String NL;
    private static final long TZ_OFF;
    private static int facMask;
    private static Writer logWriter;
    private static boolean closeWriter;
    static /* synthetic */ Class class$HTTPClient$Log;
    
    private Log() {
    }
    
    public static void write(final int facility, final String msg) {
        if ((Log.facMask & facility) == 0x0) {
            return;
        }
        try {
            writePrefix();
            Log.logWriter.write(msg);
            Log.logWriter.write(Log.NL);
            Log.logWriter.flush();
        }
        catch (final IOException ioe) {
            System.err.println("Failed to write to log: " + ioe);
            System.err.println("Failed log Entry was: " + msg);
        }
    }
    
    public static void write(final int facility, final String prefix, final Throwable t) {
        if ((Log.facMask & facility) == 0x0) {
            return;
        }
        final Class clazz = (Log.class$HTTPClient$Log != null) ? Log.class$HTTPClient$Log : (Log.class$HTTPClient$Log = class$("HTTPClient.Log"));
        synchronized (clazz) {
            if (!(Log.logWriter instanceof PrintWriter)) {
                Log.logWriter = new PrintWriter(Log.logWriter);
            }
        }
        try {
            writePrefix();
            if (prefix != null) {
                Log.logWriter.write(prefix);
            }
            t.printStackTrace((PrintWriter)Log.logWriter);
            Log.logWriter.flush();
        }
        catch (final IOException ioe) {
            System.err.println("Failed to write to log: " + ioe);
            System.err.print("Failed log Entry was: " + prefix);
            t.printStackTrace(System.err);
        }
    }
    
    public static void write(final int facility, final String prefix, final ByteArrayOutputStream buf) {
        if ((Log.facMask & facility) == 0x0) {
            return;
        }
        try {
            writePrefix();
            if (prefix != null) {
                Log.logWriter.write(prefix);
            }
            Log.logWriter.write(Log.NL);
            Log.logWriter.write(new String(buf.toByteArray(), "ISO_8859-1"));
            Log.logWriter.flush();
        }
        catch (final IOException ioe) {
            System.err.println("Failed to write to log: " + ioe);
            System.err.println("Failed log Entry was: " + prefix);
            System.err.println(new String(buf.toByteArray()));
        }
    }
    
    private static final void writePrefix() throws IOException {
        Log.logWriter.write("{" + Thread.currentThread().getName() + "} ");
        final int mill = (int)((System.currentTimeMillis() + Log.TZ_OFF) % 86400000L);
        final int secs = mill / 1000;
        final int mins = secs / 60;
        final int hours = mins / 60;
        Log.logWriter.write("[" + fill2(hours) + ':' + fill2(mins - hours * 60) + ':' + fill2(secs - mins * 60) + '.' + fill3(mill - secs * 1000) + "] ");
    }
    
    private static final String fill2(final int num) {
        return String.valueOf((num < 10) ? "0" : "") + num;
    }
    
    private static final String fill3(final int num) {
        return String.valueOf((num < 10) ? "00" : ((num < 100) ? "0" : "")) + num;
    }
    
    public static boolean isEnabled(final int facility) {
        return (Log.facMask & facility) != 0x0;
    }
    
    public static void setLogging(final int facilities, final boolean enable) {
        if (enable) {
            Log.facMask |= facilities;
        }
        else {
            Log.facMask &= ~facilities;
        }
    }
    
    public static void setLogWriter(final Writer log, final boolean closeWhenDone) {
        if (log == null) {
            return;
        }
        if (Log.closeWriter) {
            try {
                Log.logWriter.close();
            }
            catch (final IOException ioe) {
                System.err.println("Error closing log stream: " + ioe);
            }
        }
        Log.logWriter = log;
        Log.closeWriter = closeWhenDone;
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    static {
        NL = System.getProperty("line.separator");
        Log.logWriter = new OutputStreamWriter(System.err);
        final Calendar now = Calendar.getInstance();
        TZ_OFF = TimeZone.getDefault().getOffset(now.get(0), now.get(1), now.get(2), now.get(5), now.get(7), now.get(14));
        try {
            final String file = System.getProperty("HTTPClient.log.file");
            if (file != null) {
                try {
                    setLogWriter(new FileWriter(file), true);
                }
                catch (final IOException ioe) {
                    System.err.println("failed to open file log stream `" + file + "': " + ioe);
                }
            }
        }
        catch (final Exception ex) {}
        try {
            Log.facMask = Integer.getInteger("HTTPClient.log.mask", 0);
        }
        catch (final Exception ex2) {}
    }
}
