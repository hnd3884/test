package sun.util.logging;

import java.util.List;

public interface LoggingProxy
{
    Object getLogger(final String p0);
    
    Object getLevel(final Object p0);
    
    void setLevel(final Object p0, final Object p1);
    
    boolean isLoggable(final Object p0, final Object p1);
    
    void log(final Object p0, final Object p1, final String p2);
    
    void log(final Object p0, final Object p1, final String p2, final Throwable p3);
    
    void log(final Object p0, final Object p1, final String p2, final Object... p3);
    
    List<String> getLoggerNames();
    
    String getLoggerLevel(final String p0);
    
    void setLoggerLevel(final String p0, final String p1);
    
    String getParentLoggerName(final String p0);
    
    Object parseLevel(final String p0);
    
    String getLevelName(final Object p0);
    
    int getLevelValue(final Object p0);
    
    String getProperty(final String p0);
}
