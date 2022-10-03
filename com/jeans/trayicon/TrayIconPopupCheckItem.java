package com.jeans.trayicon;

public class TrayIconPopupCheckItem extends TrayIconPopupSimpleItem
{
    protected boolean m_Selected;
    
    public TrayIconPopupCheckItem(final String s) {
        super(s);
    }
    
    public void setCheck(final boolean selected) {
        this.m_Selected = selected;
        if (this.m_TrayIcon != null) {
            this.m_TrayIcon.modifyPopup(this.m_MenuId, 2, this.m_Selected);
        }
    }
    
    public boolean getCheck() {
        return this.m_Selected;
    }
    
    public boolean onSelected(final int n) {
        if (n == this.m_MenuId) {
            this.m_Selected = !this.m_Selected;
            this.m_TrayIcon.modifyPopup(this.m_MenuId, 2, this.m_Selected);
        }
        return super.onSelected(n);
    }
    
    public void setTrayIcon(final WindowsTrayIcon trayIcon, final int n, final int n2) {
        int n3 = 0;
        if (this.m_Enabled) {
            n3 |= 0x1;
        }
        if (this.m_Selected) {
            n3 |= 0x2;
        }
        this.m_MenuId = WindowsTrayIcon.subPopup(n, n2, this.m_Item, 2, n3);
        this.m_TrayIcon = trayIcon;
    }
}
