package io.netty.util.internal.logging;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

class Log4J2Logger extends ExtendedLoggerWrapper implements InternalLogger
{
    private static final long serialVersionUID = 5485418394879791397L;
    private static final boolean VARARGS_ONLY;
    
    Log4J2Logger(final Logger logger) {
        super((ExtendedLogger)logger, logger.getName(), logger.getMessageFactory());
        if (Log4J2Logger.VARARGS_ONLY) {
            throw new UnsupportedOperationException("Log4J2 version mismatch");
        }
    }
    
    public String name() {
        return this.getName();
    }
    
    public void trace(final Throwable t) {
        this.log(Level.TRACE, "Unexpected exception:", t);
    }
    
    public void debug(final Throwable t) {
        this.log(Level.DEBUG, "Unexpected exception:", t);
    }
    
    public void info(final Throwable t) {
        this.log(Level.INFO, "Unexpected exception:", t);
    }
    
    public void warn(final Throwable t) {
        this.log(Level.WARN, "Unexpected exception:", t);
    }
    
    public void error(final Throwable t) {
        this.log(Level.ERROR, "Unexpected exception:", t);
    }
    
    public boolean isEnabled(final InternalLogLevel level) {
        return this.isEnabled(toLevel(level));
    }
    
    public void log(final InternalLogLevel level, final String msg) {
        this.log(toLevel(level), msg);
    }
    
    public void log(final InternalLogLevel level, final String format, final Object arg) {
        this.log(toLevel(level), format, arg);
    }
    
    public void log(final InternalLogLevel level, final String format, final Object argA, final Object argB) {
        this.log(toLevel(level), format, argA, argB);
    }
    
    public void log(final InternalLogLevel level, final String format, final Object... arguments) {
        this.log(toLevel(level), format, arguments);
    }
    
    public void log(final InternalLogLevel level, final String msg, final Throwable t) {
        this.log(toLevel(level), msg, t);
    }
    
    public void log(final InternalLogLevel level, final Throwable t) {
        this.log(toLevel(level), "Unexpected exception:", t);
    }
    
    private static Level toLevel(final InternalLogLevel level) {
        switch (level) {
            case INFO: {
                return Level.INFO;
            }
            case DEBUG: {
                return Level.DEBUG;
            }
            case WARN: {
                return Level.WARN;
            }
            case ERROR: {
                return Level.ERROR;
            }
            case TRACE: {
                return Level.TRACE;
            }
            default: {
                throw new Error();
            }
        }
    }
    
    static {
        VARARGS_ONLY = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                try {
                    Logger.class.getMethod("debug", String.class, Object.class);
                    return false;
                }
                catch (final NoSuchMethodException ignore) {
                    return true;
                }
                catch (final SecurityException ignore2) {
                    return false;
                }
            }
        });
    }
}
