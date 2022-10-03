package org.glassfish.jersey.process;

import org.glassfish.jersey.internal.LocalizationMessages;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JerseyProcessingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
{
    private static final Logger LOGGER;
    private final Level logLevel;
    
    public JerseyProcessingUncaughtExceptionHandler() {
        this(Level.WARNING);
    }
    
    public JerseyProcessingUncaughtExceptionHandler(final Level logLevel) {
        this.logLevel = logLevel;
    }
    
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        JerseyProcessingUncaughtExceptionHandler.LOGGER.log(this.logLevel, LocalizationMessages.UNHANDLED_EXCEPTION_DETECTED(t.getName()), e);
    }
    
    static {
        LOGGER = Logger.getLogger(JerseyProcessingUncaughtExceptionHandler.class.getName());
    }
}
