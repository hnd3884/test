package com.adventnet.beans.criteriatable.events;

import java.util.EventObject;

public class CriteriaChangeEvent extends EventObject
{
    public static final int CRITERION_ADDED = 0;
    public static final int CRITERION_INSERTED = 1;
    public static final int CRITERION_REMOVED = 2;
    public static final int CRITERION_CLEARED = 3;
    public static final int CRITERION_MOVED_UP = 4;
    public static final int CRITERION_MOVED_DOWN = 5;
    public static final int CRITERION_GROUPED = 6;
    public static final int CRITERION_UNGROUPED = 7;
    public static final int CRITERION_UPDATED = 8;
    protected int type;
    
    public CriteriaChangeEvent(final Object o, final int type) {
        super(o);
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
}
