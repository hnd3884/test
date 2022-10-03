package com.adventnet.beans.xtable.events;

import java.util.EventListener;

public interface ColumnViewListener extends EventListener
{
    void columnShown(final ColumnViewEvent p0);
    
    void columnHidden(final ColumnViewEvent p0);
}
