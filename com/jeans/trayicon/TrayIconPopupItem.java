package com.jeans.trayicon;

interface TrayIconPopupItem
{
    int getNbLevels();
    
    boolean onSelected(final int p0);
    
    void setTrayIcon(final WindowsTrayIcon p0, final int p1, final int p2);
}
