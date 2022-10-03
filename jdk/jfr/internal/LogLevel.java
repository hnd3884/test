package jdk.jfr.internal;

public enum LogLevel
{
    TRACE(1), 
    DEBUG(2), 
    INFO(3), 
    WARN(4), 
    ERROR(5);
    
    final int level;
    
    private LogLevel(final int level) {
        this.level = level;
    }
}
