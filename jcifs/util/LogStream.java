package jcifs.util;

import java.io.OutputStream;
import java.io.PrintStream;

public class LogStream extends PrintStream
{
    private static LogStream inst;
    public static int level;
    
    public LogStream(final PrintStream stream) {
        super(stream);
    }
    
    public static void setLevel(final int level) {
        LogStream.level = level;
    }
    
    public static void setInstance(final PrintStream stream) {
        LogStream.inst = new LogStream(stream);
    }
    
    public static LogStream getInstance() {
        if (LogStream.inst == null) {
            setInstance(System.err);
        }
        return LogStream.inst;
    }
    
    static {
        LogStream.level = 1;
    }
}
