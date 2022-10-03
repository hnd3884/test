package org.apache.axiom.core;

public enum Axis
{
    CHILDREN("CHILDREN", 0), 
    DESCENDANTS("DESCENDANTS", 1), 
    DESCENDANTS_OR_SELF("DESCENDANTS_OR_SELF", 2);
    
    private Axis(final String s, final int n) {
    }
}
