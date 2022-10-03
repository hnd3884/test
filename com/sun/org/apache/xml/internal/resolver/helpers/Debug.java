package com.sun.org.apache.xml.internal.resolver.helpers;

public class Debug
{
    protected int debug;
    
    public Debug() {
        this.debug = 0;
    }
    
    public void setDebug(final int newDebug) {
        this.debug = newDebug;
    }
    
    public int getDebug() {
        return this.debug;
    }
    
    public void message(final int level, final String message) {
        if (this.debug >= level) {
            System.out.println(message);
        }
    }
    
    public void message(final int level, final String message, final String spec) {
        if (this.debug >= level) {
            System.out.println(message + ": " + spec);
        }
    }
    
    public void message(final int level, final String message, final String spec1, final String spec2) {
        if (this.debug >= level) {
            System.out.println(message + ": " + spec1);
            System.out.println("\t" + spec2);
        }
    }
}
