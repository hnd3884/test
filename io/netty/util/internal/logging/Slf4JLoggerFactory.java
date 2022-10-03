package io.netty.util.internal.logging;

import org.slf4j.spi.LocationAwareLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLoggerFactory;

public class Slf4JLoggerFactory extends InternalLoggerFactory
{
    public static final InternalLoggerFactory INSTANCE;
    static final InternalLoggerFactory INSTANCE_WITH_NOP_CHECK;
    
    @Deprecated
    public Slf4JLoggerFactory() {
    }
    
    Slf4JLoggerFactory(final boolean failIfNOP) {
        assert failIfNOP;
        if (LoggerFactory.getILoggerFactory() instanceof NOPLoggerFactory) {
            throw new NoClassDefFoundError("NOPLoggerFactory not supported");
        }
    }
    
    public InternalLogger newInstance(final String name) {
        return wrapLogger(LoggerFactory.getLogger(name));
    }
    
    static InternalLogger wrapLogger(final Logger logger) {
        return (logger instanceof LocationAwareLogger) ? new LocationAwareSlf4JLogger((LocationAwareLogger)logger) : new Slf4JLogger(logger);
    }
    
    static {
        INSTANCE = new Slf4JLoggerFactory();
        INSTANCE_WITH_NOP_CHECK = new Slf4JLoggerFactory(true);
    }
}
