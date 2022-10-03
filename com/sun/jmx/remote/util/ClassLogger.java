package com.sun.jmx.remote.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassLogger
{
    private static final boolean ok;
    private final String className;
    private final Logger logger;
    
    public ClassLogger(final String s, final String className) {
        if (ClassLogger.ok) {
            this.logger = Logger.getLogger(s);
        }
        else {
            this.logger = null;
        }
        this.className = className;
    }
    
    public final boolean traceOn() {
        return this.finerOn();
    }
    
    public final boolean debugOn() {
        return this.finestOn();
    }
    
    public final boolean warningOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.WARNING);
    }
    
    public final boolean infoOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.INFO);
    }
    
    public final boolean configOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.CONFIG);
    }
    
    public final boolean fineOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.FINE);
    }
    
    public final boolean finerOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.FINER);
    }
    
    public final boolean finestOn() {
        return ClassLogger.ok && this.logger.isLoggable(Level.FINEST);
    }
    
    public final void debug(final String s, final String s2) {
        this.finest(s, s2);
    }
    
    public final void debug(final String s, final Throwable t) {
        this.finest(s, t);
    }
    
    public final void debug(final String s, final String s2, final Throwable t) {
        this.finest(s, s2, t);
    }
    
    public final void trace(final String s, final String s2) {
        this.finer(s, s2);
    }
    
    public final void trace(final String s, final Throwable t) {
        this.finer(s, t);
    }
    
    public final void trace(final String s, final String s2, final Throwable t) {
        this.finer(s, s2, t);
    }
    
    public final void error(final String s, final String s2) {
        this.severe(s, s2);
    }
    
    public final void error(final String s, final Throwable t) {
        this.severe(s, t);
    }
    
    public final void error(final String s, final String s2, final Throwable t) {
        this.severe(s, s2, t);
    }
    
    public final void finest(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINEST, this.className, s, s2);
        }
    }
    
    public final void finest(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINEST, this.className, s, t.toString(), t);
        }
    }
    
    public final void finest(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINEST, this.className, s, s2, t);
        }
    }
    
    public final void finer(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINER, this.className, s, s2);
        }
    }
    
    public final void finer(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINER, this.className, s, t.toString(), t);
        }
    }
    
    public final void finer(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINER, this.className, s, s2, t);
        }
    }
    
    public final void fine(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINE, this.className, s, s2);
        }
    }
    
    public final void fine(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINE, this.className, s, t.toString(), t);
        }
    }
    
    public final void fine(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.FINE, this.className, s, s2, t);
        }
    }
    
    public final void config(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.CONFIG, this.className, s, s2);
        }
    }
    
    public final void config(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.CONFIG, this.className, s, t.toString(), t);
        }
    }
    
    public final void config(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.CONFIG, this.className, s, s2, t);
        }
    }
    
    public final void info(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.INFO, this.className, s, s2);
        }
    }
    
    public final void info(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.INFO, this.className, s, t.toString(), t);
        }
    }
    
    public final void info(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.INFO, this.className, s, s2, t);
        }
    }
    
    public final void warning(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.WARNING, this.className, s, s2);
        }
    }
    
    public final void warning(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.WARNING, this.className, s, t.toString(), t);
        }
    }
    
    public final void warning(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.WARNING, this.className, s, s2, t);
        }
    }
    
    public final void severe(final String s, final String s2) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.SEVERE, this.className, s, s2);
        }
    }
    
    public final void severe(final String s, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.SEVERE, this.className, s, t.toString(), t);
        }
    }
    
    public final void severe(final String s, final String s2, final Throwable t) {
        if (ClassLogger.ok) {
            this.logger.logp(Level.SEVERE, this.className, s, s2, t);
        }
    }
    
    static {
        boolean ok2 = false;
        try {
            ok2 = true;
        }
        catch (final Error error) {}
        ok = ok2;
    }
}
