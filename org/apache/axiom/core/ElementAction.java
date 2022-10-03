package org.apache.axiom.core;

public enum ElementAction
{
    SKIP("SKIP", 0), 
    RECURSE("RECURSE", 1), 
    RETURN_NULL("RETURN_NULL", 2);
    
    private ElementAction(final String s, final int n) {
    }
}
