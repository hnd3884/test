package com.sun.org.glassfish.external.arc;

public enum Stability
{
    COMMITTED("Committed"), 
    UNCOMMITTED("Uncommitted"), 
    VOLATILE("Volatile"), 
    NOT_AN_INTERFACE("Not-An-Interface"), 
    PRIVATE("Private"), 
    EXPERIMENTAL("Experimental"), 
    UNSPECIFIED("Unspecified");
    
    private final String mName;
    
    private Stability(final String name) {
        this.mName = name;
    }
    
    @Override
    public String toString() {
        return this.mName;
    }
}
