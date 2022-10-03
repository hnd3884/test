package org.apache.xerces.dom.events;

import org.w3c.dom.views.AbstractView;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;

public class MouseEventImpl extends UIEventImpl implements MouseEvent
{
    private int fScreenX;
    private int fScreenY;
    private int fClientX;
    private int fClientY;
    private boolean fCtrlKey;
    private boolean fAltKey;
    private boolean fShiftKey;
    private boolean fMetaKey;
    private short fButton;
    private EventTarget fRelatedTarget;
    
    public int getScreenX() {
        return this.fScreenX;
    }
    
    public int getScreenY() {
        return this.fScreenY;
    }
    
    public int getClientX() {
        return this.fClientX;
    }
    
    public int getClientY() {
        return this.fClientY;
    }
    
    public boolean getCtrlKey() {
        return this.fCtrlKey;
    }
    
    public boolean getAltKey() {
        return this.fAltKey;
    }
    
    public boolean getShiftKey() {
        return this.fShiftKey;
    }
    
    public boolean getMetaKey() {
        return this.fMetaKey;
    }
    
    public short getButton() {
        return this.fButton;
    }
    
    public EventTarget getRelatedTarget() {
        return this.fRelatedTarget;
    }
    
    public void initMouseEvent(final String s, final boolean b, final boolean b2, final AbstractView abstractView, final int n, final int fScreenX, final int fScreenY, final int fClientX, final int fClientY, final boolean fCtrlKey, final boolean fAltKey, final boolean fShiftKey, final boolean fMetaKey, final short fButton, final EventTarget fRelatedTarget) {
        this.fScreenX = fScreenX;
        this.fScreenY = fScreenY;
        this.fClientX = fClientX;
        this.fClientY = fClientY;
        this.fCtrlKey = fCtrlKey;
        this.fAltKey = fAltKey;
        this.fShiftKey = fShiftKey;
        this.fMetaKey = fMetaKey;
        this.fButton = fButton;
        this.fRelatedTarget = fRelatedTarget;
        super.initUIEvent(s, b, b2, abstractView, n);
    }
}
