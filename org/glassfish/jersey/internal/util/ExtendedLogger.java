package org.glassfish.jersey.internal.util;

import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.util.logging.Filter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ExtendedLogger
{
    private final Logger logger;
    private final Level debugLevel;
    
    public ExtendedLogger(final Logger logger, final Level debugLevel) {
        this.logger = logger;
        this.debugLevel = debugLevel;
    }
    
    public boolean isDebugLoggable() {
        return this.logger.isLoggable(this.debugLevel);
    }
    
    public Level getDebugLevel() {
        return this.debugLevel;
    }
    
    public void debugLog(final String message) {
        this.debugLog(message, (Object[])null);
    }
    
    public void debugLog(final String messageTemplate, final Object... args) {
        if (this.logger.isLoggable(this.debugLevel)) {
            Object[] messageArguments;
            if (args == null || args.length == 0) {
                messageArguments = new Object[] { null };
            }
            else {
                messageArguments = Arrays.copyOf(args, args.length + 1);
            }
            messageArguments[messageArguments.length - 1] = Thread.currentThread().getName();
            this.logger.log(this.debugLevel, "[DEBUG] " + messageTemplate + " on thread {" + (messageArguments.length - 1) + '}', messageArguments);
        }
    }
    
    @Override
    public String toString() {
        return "ExtendedLogger{logger=" + this.logger + ", debugLevel=" + this.debugLevel + '}';
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ExtendedLogger other = (ExtendedLogger)obj;
        return (this.logger == other.logger || (this.logger != null && this.logger.equals(other.logger))) && (this.debugLevel == other.debugLevel || (this.debugLevel != null && this.debugLevel.equals(other.debugLevel)));
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + ((this.logger != null) ? this.logger.hashCode() : 0);
        hash = 17 * hash + ((this.debugLevel != null) ? this.debugLevel.hashCode() : 0);
        return hash;
    }
    
    public void warning(final String msg) {
        this.logger.warning(msg);
    }
    
    public void throwing(final String sourceClass, final String sourceMethod, final Throwable thrown) {
        this.logger.throwing(sourceClass, sourceMethod, thrown);
    }
    
    public void severe(final String msg) {
        this.logger.severe(msg);
    }
    
    public void setUseParentHandlers(final boolean useParentHandlers) {
        this.logger.setUseParentHandlers(useParentHandlers);
    }
    
    public void setParent(final Logger parent) {
        this.logger.setParent(parent);
    }
    
    public void setLevel(final Level newLevel) throws SecurityException {
        this.logger.setLevel(newLevel);
    }
    
    public void setFilter(final Filter newFilter) throws SecurityException {
        this.logger.setFilter(newFilter);
    }
    
    public void removeHandler(final Handler handler) throws SecurityException {
        this.logger.removeHandler(handler);
    }
    
    public void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg, final Throwable thrown) {
        this.logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, thrown);
    }
    
    public void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg, final Object[] params) {
        this.logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, params);
    }
    
    public void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg, final Object param1) {
        this.logger.logrb(level, sourceClass, sourceMethod, bundleName, msg, param1);
    }
    
    public void logrb(final Level level, final String sourceClass, final String sourceMethod, final String bundleName, final String msg) {
        this.logger.logrb(level, sourceClass, sourceMethod, bundleName, msg);
    }
    
    public void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Throwable thrown) {
        this.logger.logp(level, sourceClass, sourceMethod, msg, thrown);
    }
    
    public void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Object[] params) {
        this.logger.logp(level, sourceClass, sourceMethod, msg, params);
    }
    
    public void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg, final Object param1) {
        this.logger.logp(level, sourceClass, sourceMethod, msg, param1);
    }
    
    public void logp(final Level level, final String sourceClass, final String sourceMethod, final String msg) {
        this.logger.logp(level, sourceClass, sourceMethod, msg);
    }
    
    public void log(final Level level, final String msg, final Throwable thrown) {
        this.logger.log(level, msg, thrown);
    }
    
    public void log(final Level level, final String msg, final Object[] params) {
        this.logger.log(level, msg, params);
    }
    
    public void log(final Level level, final String msg, final Object param1) {
        this.logger.log(level, msg, param1);
    }
    
    public void log(final Level level, final String msg) {
        this.logger.log(level, msg);
    }
    
    public void log(final LogRecord record) {
        this.logger.log(record);
    }
    
    public boolean isLoggable(final Level level) {
        return this.logger.isLoggable(level);
    }
    
    public void info(final String msg) {
        this.logger.info(msg);
    }
    
    public boolean getUseParentHandlers() {
        return this.logger.getUseParentHandlers();
    }
    
    public String getResourceBundleName() {
        return this.logger.getResourceBundleName();
    }
    
    public ResourceBundle getResourceBundle() {
        return this.logger.getResourceBundle();
    }
    
    public Logger getParent() {
        return this.logger.getParent();
    }
    
    public String getName() {
        return this.logger.getName();
    }
    
    public Level getLevel() {
        return this.logger.getLevel();
    }
    
    public Handler[] getHandlers() {
        return this.logger.getHandlers();
    }
    
    public Filter getFilter() {
        return this.logger.getFilter();
    }
    
    public void finest(final String msg) {
        this.logger.finest(msg);
    }
    
    public void finer(final String msg) {
        this.logger.finer(msg);
    }
    
    public void fine(final String msg) {
        this.logger.fine(msg);
    }
    
    public void exiting(final String sourceClass, final String sourceMethod, final Object result) {
        this.logger.exiting(sourceClass, sourceMethod, result);
    }
    
    public void exiting(final String sourceClass, final String sourceMethod) {
        this.logger.exiting(sourceClass, sourceMethod);
    }
    
    public void entering(final String sourceClass, final String sourceMethod, final Object[] params) {
        this.logger.entering(sourceClass, sourceMethod, params);
    }
    
    public void entering(final String sourceClass, final String sourceMethod, final Object param1) {
        this.logger.entering(sourceClass, sourceMethod, param1);
    }
    
    public void entering(final String sourceClass, final String sourceMethod) {
        this.logger.entering(sourceClass, sourceMethod);
    }
    
    public void config(final String msg) {
        this.logger.config(msg);
    }
    
    public void addHandler(final Handler handler) throws SecurityException {
        this.logger.addHandler(handler);
    }
}
