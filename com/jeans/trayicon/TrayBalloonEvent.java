package com.jeans.trayicon;

public class TrayBalloonEvent
{
    public static final int SHOW = 1;
    public static final int HIDE = 2;
    public static final int CLICK = 4;
    public static final int TIMEOUT = 8;
    protected int m_Mask;
    
    public TrayBalloonEvent(final int mask) {
        this.m_Mask = mask;
    }
    
    public int getMask() {
        return this.m_Mask;
    }
}
