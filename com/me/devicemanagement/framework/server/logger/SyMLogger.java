package com.me.devicemanagement.framework.server.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SyMLogger
{
    public static Logger getSoMLogger() {
        return Logger.getLogger("SoMLogger");
    }
    
    protected SyMLogger() {
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg) {
        logger.logp(Level.INFO, sourceClass, sourceMethod, msg);
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object param) {
        logger.logp(Level.INFO, sourceClass, sourceMethod, msg, param);
    }
    
    public static void info(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object[] params) {
        logger.logp(Level.INFO, sourceClass, sourceMethod, msg, params);
    }
    
    public static void warning(final Logger logger, final String sourceClass, final String sourceMethod, final String msg) {
        logger.logp(Level.WARNING, sourceClass, sourceMethod, msg);
    }
    
    public static void warning(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object param) {
        logger.logp(Level.WARNING, sourceClass, sourceMethod, msg, param);
    }
    
    public static void warning(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object[] params) {
        logger.logp(Level.WARNING, sourceClass, sourceMethod, msg, params);
    }
    
    public static void debug(final Logger logger, final String sourceClass, final String sourceMethod, final String msg) {
        logger.logp(Level.FINEST, sourceClass, sourceMethod, msg);
    }
    
    public static void debug(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object param) {
        logger.logp(Level.FINEST, sourceClass, sourceMethod, msg, param);
    }
    
    public static void debug(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Object[] params) {
        logger.logp(Level.FINEST, sourceClass, sourceMethod, msg, params);
    }
    
    public static void error(final Logger logger, final String sourceClass, final String sourceMethod, final String msg, final Throwable thrown) {
        logger.logp(Level.SEVERE, sourceClass, sourceMethod, msg, thrown);
    }
    
    private static Logger getLogger(final String loggerName) {
        final Logger logger = Logger.getLogger(loggerName);
        return logger;
    }
    
    public static void log(final String loggerName, final Level level, final String msg) {
        getLogger(loggerName).log(level, msg);
    }
    
    public static void log(final String loggerName, final Level level, final String msg, final Object[] params) {
        getLogger(loggerName).log(level, msg, params);
    }
    
    public static void log(final String loggerName, final Level level, final String msg, final Throwable thrown) {
        getLogger(loggerName).log(level, msg, thrown);
    }
    
    public static Logger getCustomerLogger() {
        return Logger.getLogger("CustomerLogger");
    }
}
