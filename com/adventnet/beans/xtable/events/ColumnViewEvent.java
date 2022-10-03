package com.adventnet.beans.xtable.events;

import java.util.EventObject;

public class ColumnViewEvent extends EventObject
{
    int fromIndex;
    int toIndex;
    
    public ColumnViewEvent(final Object o, final int fromIndex, final int toIndex) {
        super(o);
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
    }
    
    public int getFromIndex() {
        return this.fromIndex;
    }
    
    public int getToIndex() {
        return this.toIndex;
    }
}
