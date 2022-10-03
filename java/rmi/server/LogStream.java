package java.rmi.server;

import java.util.HashMap;
import java.io.IOException;
import java.util.Date;
import java.security.Permission;
import java.util.logging.LoggingPermission;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.util.Map;
import java.io.PrintStream;

@Deprecated
public class LogStream extends PrintStream
{
    private static Map<String, LogStream> known;
    private static PrintStream defaultStream;
    private String name;
    private OutputStream logOut;
    private OutputStreamWriter logWriter;
    private StringBuffer buffer;
    private ByteArrayOutputStream bufOut;
    public static final int SILENT = 0;
    public static final int BRIEF = 10;
    public static final int VERBOSE = 20;
    
    @Deprecated
    private LogStream(final String name, final OutputStream outputStream) {
        super(new ByteArrayOutputStream());
        this.buffer = new StringBuffer();
        this.bufOut = (ByteArrayOutputStream)super.out;
        this.name = name;
        this.setOutputStream(outputStream);
    }
    
    @Deprecated
    public static LogStream log(final String s) {
        LogStream logStream;
        synchronized (LogStream.known) {
            logStream = LogStream.known.get(s);
            if (logStream == null) {
                logStream = new LogStream(s, LogStream.defaultStream);
            }
            LogStream.known.put(s, logStream);
        }
        return logStream;
    }
    
    @Deprecated
    public static synchronized PrintStream getDefaultStream() {
        return LogStream.defaultStream;
    }
    
    @Deprecated
    public static synchronized void setDefaultStream(final PrintStream defaultStream) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new LoggingPermission("control", null));
        }
        LogStream.defaultStream = defaultStream;
    }
    
    @Deprecated
    public synchronized OutputStream getOutputStream() {
        return this.logOut;
    }
    
    @Deprecated
    public synchronized void setOutputStream(final OutputStream logOut) {
        this.logOut = logOut;
        this.logWriter = new OutputStreamWriter(this.logOut);
    }
    
    @Deprecated
    @Override
    public void write(final int n) {
        if (n == 10) {
            synchronized (this) {
                synchronized (this.logOut) {
                    this.buffer.setLength(0);
                    this.buffer.append(new Date().toString());
                    this.buffer.append(':');
                    this.buffer.append(this.name);
                    this.buffer.append(':');
                    this.buffer.append(Thread.currentThread().getName());
                    this.buffer.append(':');
                    try {
                        this.logWriter.write(this.buffer.toString());
                        this.logWriter.flush();
                        this.bufOut.writeTo(this.logOut);
                        this.logOut.write(n);
                        this.logOut.flush();
                    }
                    catch (final IOException ex) {
                        this.setError();
                    }
                    finally {
                        this.bufOut.reset();
                    }
                }
            }
        }
        else {
            super.write(n);
        }
    }
    
    @Deprecated
    @Override
    public void write(final byte[] array, final int n, final int n2) {
        if (n2 < 0) {
            throw new ArrayIndexOutOfBoundsException(n2);
        }
        for (int i = 0; i < n2; ++i) {
            this.write(array[n + i]);
        }
    }
    
    @Deprecated
    @Override
    public String toString() {
        return this.name;
    }
    
    @Deprecated
    public static int parseLevel(final String s) {
        if (s == null || s.length() < 1) {
            return -1;
        }
        try {
            return Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {
            if (s.length() < 1) {
                return -1;
            }
            if ("SILENT".startsWith(s.toUpperCase())) {
                return 0;
            }
            if ("BRIEF".startsWith(s.toUpperCase())) {
                return 10;
            }
            if ("VERBOSE".startsWith(s.toUpperCase())) {
                return 20;
            }
            return -1;
        }
    }
    
    static {
        LogStream.known = new HashMap<String, LogStream>(5);
        LogStream.defaultStream = System.err;
    }
}
