package com.jeans.trayicon;

import java.util.Enumeration;
import java.util.Vector;

public class TrayIconPopup implements TrayIconPopupItem
{
    protected Vector mVector;
    protected String mItem;
    
    public TrayIconPopup() {
        this.mVector = new Vector();
    }
    
    public TrayIconPopup(final String mItem) {
        this.mVector = new Vector();
        this.mItem = mItem;
    }
    
    public void addMenuItem(final TrayIconPopupItem trayIconPopupItem) {
        this.mVector.addElement(trayIconPopupItem);
    }
    
    public int getNbLevels() {
        int max = 0;
        final Enumeration elements = this.mVector.elements();
        while (elements.hasMoreElements()) {
            max = Math.max(max, ((TrayIconPopupItem)elements.nextElement()).getNbLevels());
        }
        return max + 1;
    }
    
    public boolean onSelected(final int n) {
        final Enumeration elements = this.mVector.elements();
        while (elements.hasMoreElements()) {
            if (((TrayIconPopupItem)elements.nextElement()).onSelected(n)) {
                return true;
            }
        }
        return false;
    }
    
    public void setTrayIcon(final WindowsTrayIcon windowsTrayIcon, final int n, final int n2) {
        final int n3 = n2 + 1;
        WindowsTrayIcon.subPopup(n, n3, this.mItem, 3, 0);
        final Enumeration elements = this.mVector.elements();
        while (elements.hasMoreElements()) {
            ((TrayIconPopupItem)elements.nextElement()).setTrayIcon(windowsTrayIcon, n, n3);
        }
        WindowsTrayIcon.subPopup(n, n3, this.mItem, 4, 0);
    }
}
