package com.adventnet.beans.xtable.events;

import java.util.EventListener;

public interface ColumnSortedListener extends EventListener
{
    void beforeColumnSorted(final ColumnSortedEvent p0);
    
    void afterColumnSorted(final ColumnSortedEvent p0);
}
