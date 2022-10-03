package com.sun.java.swing.plaf.windows;

import java.awt.Dimension;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicDesktopIconUI;

public class WindowsDesktopIconUI extends BasicDesktopIconUI
{
    private int width;
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsDesktopIconUI();
    }
    
    public void installDefaults() {
        super.installDefaults();
        this.width = UIManager.getInt("DesktopIcon.width");
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        component.setOpaque(XPStyle.getXP() == null);
    }
    
    @Override
    public void uninstallUI(final JComponent component) {
        final WindowsInternalFrameTitlePane windowsInternalFrameTitlePane = (WindowsInternalFrameTitlePane)this.iconPane;
        super.uninstallUI(component);
        windowsInternalFrameTitlePane.uninstallListeners();
    }
    
    @Override
    protected void installComponents() {
        this.iconPane = new WindowsInternalFrameTitlePane(this.frame);
        this.desktopIcon.setLayout(new BorderLayout());
        this.desktopIcon.add(this.iconPane, "Center");
        if (XPStyle.getXP() != null) {
            this.desktopIcon.setBorder(null);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return this.getMinimumSize(component);
    }
    
    @Override
    public Dimension getMinimumSize(final JComponent component) {
        final Dimension minimumSize = super.getMinimumSize(component);
        minimumSize.width = this.width;
        return minimumSize;
    }
}
