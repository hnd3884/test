package org.jfree.util;

public class LogContext
{
    private String contextPrefix;
    
    public LogContext(final String contextPrefix) {
        this.contextPrefix = contextPrefix;
    }
    
    public void debug(final Object message) {
        this.log(3, message);
    }
    
    public void debug(final Object message, final Exception e) {
        this.log(3, message, e);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogContext)) {
            return false;
        }
        final LogContext logContext = (LogContext)o;
        if (this.contextPrefix != null) {
            if (!this.contextPrefix.equals(logContext.contextPrefix)) {
                return false;
            }
        }
        else if (logContext.contextPrefix != null) {
            return false;
        }
        return true;
    }
    
    public void error(final Object message) {
        this.log(0, message);
    }
    
    public void error(final Object message, final Exception e) {
        this.log(0, message, e);
    }
    
    public int hashCode() {
        return (this.contextPrefix != null) ? this.contextPrefix.hashCode() : 0;
    }
    
    public void info(final Object message) {
        this.log(2, message);
    }
    
    public void info(final Object message, final Exception e) {
        this.log(2, message, e);
    }
    
    public boolean isDebugEnabled() {
        return Log.isDebugEnabled();
    }
    
    public boolean isErrorEnabled() {
        return Log.isErrorEnabled();
    }
    
    public boolean isInfoEnabled() {
        return Log.isInfoEnabled();
    }
    
    public boolean isWarningEnabled() {
        return Log.isWarningEnabled();
    }
    
    public void log(final int level, final Object message) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new Log.SimpleMessage(this.contextPrefix, ":", message));
        }
        else {
            Log.getInstance().doLog(level, message);
        }
    }
    
    public void log(final int level, final Object message, final Exception e) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new Log.SimpleMessage(this.contextPrefix, ":", message), e);
        }
        else {
            Log.getInstance().doLog(level, message, e);
        }
    }
    
    public void warn(final Object message) {
        this.log(1, message);
    }
    
    public void warn(final Object message, final Exception e) {
        this.log(1, message, e);
    }
}
