package com.sun.corba.se.spi.logging;

import java.util.logging.LogRecord;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LogWrapperBase
{
    protected Logger logger;
    protected String loggerName;
    
    protected LogWrapperBase(final Logger logger) {
        this.logger = logger;
        this.loggerName = logger.getName();
    }
    
    protected void doLog(final Level level, final String s, final Object[] parameters, final Class clazz, final Throwable thrown) {
        final LogRecord logRecord = new LogRecord(level, s);
        if (parameters != null) {
            logRecord.setParameters(parameters);
        }
        this.inferCaller(clazz, logRecord);
        logRecord.setThrown(thrown);
        logRecord.setLoggerName(this.loggerName);
        logRecord.setResourceBundle(this.logger.getResourceBundle());
        this.logger.log(logRecord);
    }
    
    private void inferCaller(final Class clazz, final LogRecord logRecord) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement stackTraceElement = null;
        final String name = clazz.getName();
        final String name2 = LogWrapperBase.class.getName();
        int i;
        for (i = 0; i < stackTrace.length; ++i) {
            stackTraceElement = stackTrace[i];
            final String className = stackTraceElement.getClassName();
            if (!className.equals(name) && !className.equals(name2)) {
                break;
            }
        }
        if (i < stackTrace.length) {
            logRecord.setSourceClassName(stackTraceElement.getClassName());
            logRecord.setSourceMethodName(stackTraceElement.getMethodName());
        }
    }
    
    protected void doLog(final Level level, final String s, final Class clazz, final Throwable t) {
        this.doLog(level, s, null, clazz, t);
    }
}
