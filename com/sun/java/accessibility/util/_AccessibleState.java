package com.sun.java.accessibility.util;

import javax.accessibility.AccessibleState;

class _AccessibleState extends AccessibleState
{
    public static final _AccessibleState MANAGES_DESCENDANTS;
    
    protected _AccessibleState(final String s) {
        super(s);
    }
    
    static {
        MANAGES_DESCENDANTS = new _AccessibleState("managesDescendants");
    }
}
