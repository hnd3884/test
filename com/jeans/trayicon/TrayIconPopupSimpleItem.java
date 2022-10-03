package com.jeans.trayicon;

import java.util.Enumeration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class TrayIconPopupSimpleItem implements TrayIconPopupItem
{
    protected String m_Item;
    protected int m_MenuId;
    protected boolean m_Enabled;
    protected boolean m_Default;
    protected WindowsTrayIcon m_TrayIcon;
    private Vector m_Listeners;
    
    public TrayIconPopupSimpleItem(final String item) {
        this.m_Item = item;
        this.m_Enabled = true;
    }
    
    public String getName() {
        return this.m_Item;
    }
    
    public void addActionListener(final ActionListener actionListener) {
        if (this.m_Listeners == null) {
            this.m_Listeners = new Vector();
        }
        this.m_Listeners.addElement(actionListener);
    }
    
    public int getNbLevels() {
        return 0;
    }
    
    public void setEnabled(final boolean enabled) {
        this.m_Enabled = enabled;
        if (this.m_TrayIcon != null) {
            this.m_TrayIcon.modifyPopup(this.m_MenuId, 1, this.m_Enabled);
        }
    }
    
    public void setDefault(final boolean default1) {
        this.m_Default = default1;
        if (this.m_TrayIcon != null) {
            this.m_TrayIcon.modifyPopup(this.m_MenuId, 4, this.m_Default);
        }
    }
    
    public boolean onSelected(final int n) {
        final boolean b = n == this.m_MenuId;
        if (b && this.m_Listeners != null) {
            final ActionEvent actionEvent = new ActionEvent(this, 0, "");
            final Enumeration elements = this.m_Listeners.elements();
            while (elements.hasMoreElements()) {
                ((ActionListener)elements.nextElement()).actionPerformed(actionEvent);
            }
        }
        return b;
    }
    
    public void setTrayIcon(final WindowsTrayIcon trayIcon, final int n, final int n2) {
        int enabled = this.m_Enabled ? 1 : 0;
        if (this.m_Default) {
            enabled |= 0x4;
        }
        this.m_MenuId = WindowsTrayIcon.subPopup(n, n2, this.m_Item, 0, enabled);
        this.m_TrayIcon = trayIcon;
    }
}
