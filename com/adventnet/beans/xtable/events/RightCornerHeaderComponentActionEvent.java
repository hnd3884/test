package com.adventnet.beans.xtable.events;

import java.util.EventObject;

public class RightCornerHeaderComponentActionEvent extends EventObject
{
    int x;
    int y;
    
    public RightCornerHeaderComponentActionEvent(final Object o, final int x, final int y) {
        super(o);
        this.x = x;
        this.y = y;
    }
    
    int getX() {
        return this.x;
    }
    
    int getY() {
        return this.y;
    }
}
