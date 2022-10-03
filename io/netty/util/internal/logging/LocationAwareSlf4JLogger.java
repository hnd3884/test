package io.netty.util.internal.logging;

import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

final class LocationAwareSlf4JLogger extends AbstractInternalLogger
{
    static final String FQCN;
    private static final long serialVersionUID = -8292030083201538180L;
    private final transient LocationAwareLogger logger;
    
    LocationAwareSlf4JLogger(final LocationAwareLogger logger) {
        super(logger.getName());
        this.logger = logger;
    }
    
    private void log(final int level, final String message) {
        this.logger.log((Marker)null, LocationAwareSlf4JLogger.FQCN, level, message, (Object[])null, (Throwable)null);
    }
    
    private void log(final int level, final String message, final Throwable cause) {
        this.logger.log((Marker)null, LocationAwareSlf4JLogger.FQCN, level, message, (Object[])null, cause);
    }
    
    private void log(final int level, final FormattingTuple tuple) {
        this.logger.log((Marker)null, LocationAwareSlf4JLogger.FQCN, level, tuple.getMessage(), tuple.getArgArray(), tuple.getThrowable());
    }
    
    @Override
    public boolean isTraceEnabled() {
        return this.logger.isTraceEnabled();
    }
    
    @Override
    public void trace(final String msg) {
        if (this.isTraceEnabled()) {
            this.log(0, msg);
        }
    }
    
    @Override
    public void trace(final String format, final Object arg) {
        if (this.isTraceEnabled()) {
            this.log(0, MessageFormatter.format(format, arg));
        }
    }
    
    @Override
    public void trace(final String format, final Object argA, final Object argB) {
        if (this.isTraceEnabled()) {
            this.log(0, MessageFormatter.format(format, argA, argB));
        }
    }
    
    @Override
    public void trace(final String format, final Object... argArray) {
        if (this.isTraceEnabled()) {
            this.log(0, MessageFormatter.arrayFormat(format, argArray));
        }
    }
    
    @Override
    public void trace(final String msg, final Throwable t) {
        if (this.isTraceEnabled()) {
            this.log(0, msg, t);
        }
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }
    
    @Override
    public void debug(final String msg) {
        if (this.isDebugEnabled()) {
            this.log(10, msg);
        }
    }
    
    @Override
    public void debug(final String format, final Object arg) {
        if (this.isDebugEnabled()) {
            this.log(10, MessageFormatter.format(format, arg));
        }
    }
    
    @Override
    public void debug(final String format, final Object argA, final Object argB) {
        if (this.isDebugEnabled()) {
            this.log(10, MessageFormatter.format(format, argA, argB));
        }
    }
    
    @Override
    public void debug(final String format, final Object... argArray) {
        if (this.isDebugEnabled()) {
            this.log(10, MessageFormatter.arrayFormat(format, argArray));
        }
    }
    
    @Override
    public void debug(final String msg, final Throwable t) {
        if (this.isDebugEnabled()) {
            this.log(10, msg, t);
        }
    }
    
    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }
    
    @Override
    public void info(final String msg) {
        if (this.isInfoEnabled()) {
            this.log(20, msg);
        }
    }
    
    @Override
    public void info(final String format, final Object arg) {
        if (this.isInfoEnabled()) {
            this.log(20, MessageFormatter.format(format, arg));
        }
    }
    
    @Override
    public void info(final String format, final Object argA, final Object argB) {
        if (this.isInfoEnabled()) {
            this.log(20, MessageFormatter.format(format, argA, argB));
        }
    }
    
    @Override
    public void info(final String format, final Object... argArray) {
        if (this.isInfoEnabled()) {
            this.log(20, MessageFormatter.arrayFormat(format, argArray));
        }
    }
    
    @Override
    public void info(final String msg, final Throwable t) {
        if (this.isInfoEnabled()) {
            this.log(20, msg, t);
        }
    }
    
    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }
    
    @Override
    public void warn(final String msg) {
        if (this.isWarnEnabled()) {
            this.log(30, msg);
        }
    }
    
    @Override
    public void warn(final String format, final Object arg) {
        if (this.isWarnEnabled()) {
            this.log(30, MessageFormatter.format(format, arg));
        }
    }
    
    @Override
    public void warn(final String format, final Object... argArray) {
        if (this.isWarnEnabled()) {
            this.log(30, MessageFormatter.arrayFormat(format, argArray));
        }
    }
    
    @Override
    public void warn(final String format, final Object argA, final Object argB) {
        if (this.isWarnEnabled()) {
            this.log(30, MessageFormatter.format(format, argA, argB));
        }
    }
    
    @Override
    public void warn(final String msg, final Throwable t) {
        if (this.isWarnEnabled()) {
            this.log(30, msg, t);
        }
    }
    
    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }
    
    @Override
    public void error(final String msg) {
        if (this.isErrorEnabled()) {
            this.log(40, msg);
        }
    }
    
    @Override
    public void error(final String format, final Object arg) {
        if (this.isErrorEnabled()) {
            this.log(40, MessageFormatter.format(format, arg));
        }
    }
    
    @Override
    public void error(final String format, final Object argA, final Object argB) {
        if (this.isErrorEnabled()) {
            this.log(40, MessageFormatter.format(format, argA, argB));
        }
    }
    
    @Override
    public void error(final String format, final Object... argArray) {
        if (this.isErrorEnabled()) {
            this.log(40, MessageFormatter.arrayFormat(format, argArray));
        }
    }
    
    @Override
    public void error(final String msg, final Throwable t) {
        if (this.isErrorEnabled()) {
            this.log(40, msg, t);
        }
    }
    
    static {
        FQCN = LocationAwareSlf4JLogger.class.getName();
    }
}
