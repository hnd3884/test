package com.sun.java.swing.plaf.windows;

import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicDesktopPaneUI;

public class WindowsDesktopPaneUI extends BasicDesktopPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsDesktopPaneUI();
    }
    
    @Override
    protected void installDesktopManager() {
        this.desktopManager = this.desktop.getDesktopManager();
        if (this.desktopManager == null) {
            this.desktopManager = new WindowsDesktopManager();
            this.desktop.setDesktopManager(this.desktopManager);
        }
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
    }
    
    @Override
    protected void installKeyboardActions() {
        super.installKeyboardActions();
        if (!this.desktop.requestDefaultFocus()) {
            this.desktop.requestFocus();
        }
    }
}
