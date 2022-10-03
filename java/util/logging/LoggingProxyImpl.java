package java.util.logging;

import java.util.List;
import sun.util.logging.LoggingProxy;

class LoggingProxyImpl implements LoggingProxy
{
    static final LoggingProxy INSTANCE;
    
    private LoggingProxyImpl() {
    }
    
    @Override
    public Object getLogger(final String s) {
        return Logger.getPlatformLogger(s);
    }
    
    @Override
    public Object getLevel(final Object o) {
        return ((Logger)o).getLevel();
    }
    
    @Override
    public void setLevel(final Object o, final Object o2) {
        ((Logger)o).setLevel((Level)o2);
    }
    
    @Override
    public boolean isLoggable(final Object o, final Object o2) {
        return ((Logger)o).isLoggable((Level)o2);
    }
    
    @Override
    public void log(final Object o, final Object o2, final String s) {
        ((Logger)o).log((Level)o2, s);
    }
    
    @Override
    public void log(final Object o, final Object o2, final String s, final Throwable t) {
        ((Logger)o).log((Level)o2, s, t);
    }
    
    @Override
    public void log(final Object o, final Object o2, final String s, final Object... array) {
        ((Logger)o).log((Level)o2, s, array);
    }
    
    @Override
    public List<String> getLoggerNames() {
        return LogManager.getLoggingMXBean().getLoggerNames();
    }
    
    @Override
    public String getLoggerLevel(final String s) {
        return LogManager.getLoggingMXBean().getLoggerLevel(s);
    }
    
    @Override
    public void setLoggerLevel(final String s, final String s2) {
        LogManager.getLoggingMXBean().setLoggerLevel(s, s2);
    }
    
    @Override
    public String getParentLoggerName(final String s) {
        return LogManager.getLoggingMXBean().getParentLoggerName(s);
    }
    
    @Override
    public Object parseLevel(final String s) {
        final Level level = Level.findLevel(s);
        if (level == null) {
            throw new IllegalArgumentException("Unknown level \"" + s + "\"");
        }
        return level;
    }
    
    @Override
    public String getLevelName(final Object o) {
        return ((Level)o).getLevelName();
    }
    
    @Override
    public int getLevelValue(final Object o) {
        return ((Level)o).intValue();
    }
    
    @Override
    public String getProperty(final String s) {
        return LogManager.getLogManager().getProperty(s);
    }
    
    static {
        INSTANCE = new LoggingProxyImpl();
    }
}
