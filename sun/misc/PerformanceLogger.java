package sun.misc;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.Writer;
import java.util.Vector;

public class PerformanceLogger
{
    private static final int START_INDEX = 0;
    private static final int LAST_RESERVED = 0;
    private static boolean perfLoggingOn;
    private static boolean useNanoTime;
    private static Vector<TimeData> times;
    private static String logFileName;
    private static Writer logWriter;
    private static long baseTime;
    
    public static boolean loggingEnabled() {
        return PerformanceLogger.perfLoggingOn;
    }
    
    private static long getCurrentTime() {
        if (PerformanceLogger.useNanoTime) {
            return System.nanoTime();
        }
        return System.currentTimeMillis();
    }
    
    public static void setStartTime(final String s) {
        if (loggingEnabled()) {
            setStartTime(s, getCurrentTime());
        }
    }
    
    public static void setBaseTime(final long baseTime) {
        if (loggingEnabled()) {
            PerformanceLogger.baseTime = baseTime;
        }
    }
    
    public static void setStartTime(final String s, final long n) {
        if (loggingEnabled()) {
            PerformanceLogger.times.set(0, new TimeData(s, n));
        }
    }
    
    public static long getStartTime() {
        if (loggingEnabled()) {
            return PerformanceLogger.times.get(0).getTime();
        }
        return 0L;
    }
    
    public static int setTime(final String s) {
        if (loggingEnabled()) {
            return setTime(s, getCurrentTime());
        }
        return 0;
    }
    
    public static int setTime(final String s, final long n) {
        if (loggingEnabled()) {
            synchronized (PerformanceLogger.times) {
                PerformanceLogger.times.add(new TimeData(s, n));
                return PerformanceLogger.times.size() - 1;
            }
        }
        return 0;
    }
    
    public static long getTimeAtIndex(final int n) {
        if (loggingEnabled()) {
            return PerformanceLogger.times.get(n).getTime();
        }
        return 0L;
    }
    
    public static String getMessageAtIndex(final int n) {
        if (loggingEnabled()) {
            return PerformanceLogger.times.get(n).getMessage();
        }
        return null;
    }
    
    public static void outputLog(final Writer writer) {
        if (loggingEnabled()) {
            try {
                synchronized (PerformanceLogger.times) {
                    for (int i = 0; i < PerformanceLogger.times.size(); ++i) {
                        final TimeData timeData = PerformanceLogger.times.get(i);
                        if (timeData != null) {
                            writer.write(i + " " + timeData.getMessage() + ": " + (timeData.getTime() - PerformanceLogger.baseTime) + "\n");
                        }
                    }
                }
                writer.flush();
            }
            catch (final Exception ex) {
                System.out.println(ex + ": Writing performance log to " + writer);
            }
        }
    }
    
    public static void outputLog() {
        outputLog(PerformanceLogger.logWriter);
    }
    
    static {
        PerformanceLogger.perfLoggingOn = false;
        PerformanceLogger.useNanoTime = false;
        PerformanceLogger.logFileName = null;
        PerformanceLogger.logWriter = null;
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.perflog"));
        if (s != null) {
            PerformanceLogger.perfLoggingOn = true;
            if (AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.perflog.nano")) != null) {
                PerformanceLogger.useNanoTime = true;
            }
            if (s.regionMatches(true, 0, "file:", 0, 5)) {
                PerformanceLogger.logFileName = s.substring(5);
            }
            if (PerformanceLogger.logFileName != null && PerformanceLogger.logWriter == null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        try {
                            final File file = new File(PerformanceLogger.logFileName);
                            file.createNewFile();
                            PerformanceLogger.logWriter = new FileWriter(file);
                        }
                        catch (final Exception ex) {
                            System.out.println(ex + ": Creating logfile " + PerformanceLogger.logFileName + ".  Log to console");
                        }
                        return null;
                    }
                });
            }
            if (PerformanceLogger.logWriter == null) {
                PerformanceLogger.logWriter = new OutputStreamWriter(System.out);
            }
        }
        PerformanceLogger.times = new Vector<TimeData>(10);
        for (int i = 0; i <= 0; ++i) {
            PerformanceLogger.times.add(new TimeData("Time " + i + " not set", 0L));
        }
    }
    
    static class TimeData
    {
        String message;
        long time;
        
        TimeData(final String message, final long time) {
            this.message = message;
            this.time = time;
        }
        
        String getMessage() {
            return this.message;
        }
        
        long getTime() {
            return this.time;
        }
    }
}
