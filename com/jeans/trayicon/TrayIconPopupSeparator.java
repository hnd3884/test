package com.jeans.trayicon;

public class TrayIconPopupSeparator implements TrayIconPopupItem
{
    public int getNbLevels() {
        return 0;
    }
    
    public boolean onSelected(final int n) {
        return false;
    }
    
    public void setTrayIcon(final WindowsTrayIcon windowsTrayIcon, final int n, final int n2) {
        WindowsTrayIcon.subPopup(n, n2, "", 1, 0);
    }
}
