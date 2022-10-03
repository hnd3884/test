package org.w3c.dom.events;

import org.w3c.dom.views.AbstractView;

public interface MouseEvent extends UIEvent
{
    int getScreenX();
    
    int getScreenY();
    
    int getClientX();
    
    int getClientY();
    
    boolean getCtrlKey();
    
    boolean getShiftKey();
    
    boolean getAltKey();
    
    boolean getMetaKey();
    
    short getButton();
    
    EventTarget getRelatedTarget();
    
    void initMouseEvent(final String p0, final boolean p1, final boolean p2, final AbstractView p3, final int p4, final int p5, final int p6, final int p7, final int p8, final boolean p9, final boolean p10, final boolean p11, final boolean p12, final short p13, final EventTarget p14);
}
