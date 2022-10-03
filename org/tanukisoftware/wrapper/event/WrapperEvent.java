package org.tanukisoftware.wrapper.event;

import org.tanukisoftware.wrapper.WrapperManager;
import java.util.EventObject;

public abstract class WrapperEvent extends EventObject
{
    protected WrapperEvent() {
        super(WrapperManager.class);
    }
    
    public long getFlags() {
        return 0L;
    }
}
