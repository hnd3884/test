package com.sun.org.slf4j.internal;

import java.util.logging.Level;

public class Logger
{
    private final java.util.logging.Logger impl;
    
    public Logger(final String s) {
        this.impl = java.util.logging.Logger.getLogger(s);
    }
    
    public boolean isDebugEnabled() {
        return this.impl.isLoggable(Level.FINE);
    }
    
    public boolean isTraceEnabled() {
        return this.impl.isLoggable(Level.FINE);
    }
    
    public void debug(final String s) {
        this.impl.log(Level.FINE, s);
    }
    
    public void debug(final String s, final Throwable t) {
        this.impl.log(Level.FINE, s, t);
    }
    
    public void debug(final String s, final Object... array) {
        this.impl.log(Level.FINE, s, array);
    }
    
    public void trace(final String s) {
        this.impl.log(Level.FINE, s);
    }
    
    public void error(final String s) {
        this.impl.log(Level.SEVERE, s);
    }
    
    public void error(final String s, final Throwable t) {
        this.impl.log(Level.SEVERE, s, t);
    }
    
    public void error(final String s, final Object... array) {
        this.impl.log(Level.SEVERE, s, array);
    }
    
    public void warn(final String s) {
        this.impl.log(Level.WARNING, s);
    }
    
    public void warn(final String s, final Throwable t) {
        this.impl.log(Level.WARNING, s, t);
    }
}
