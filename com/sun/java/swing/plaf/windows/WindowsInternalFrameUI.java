package com.sun.java.swing.plaf.windows;

import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.DesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.plaf.ComponentUI;
import javax.swing.LookAndFeel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class WindowsInternalFrameUI extends BasicInternalFrameUI
{
    XPStyle xp;
    
    public void installDefaults() {
        super.installDefaults();
        if (this.xp != null) {
            this.frame.setBorder(new XPBorder());
        }
        else {
            this.frame.setBorder(UIManager.getBorder("InternalFrame.border"));
        }
    }
    
    @Override
    public void installUI(final JComponent component) {
        super.installUI(component);
        LookAndFeel.installProperty(component, "opaque", (this.xp == null) ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void uninstallDefaults() {
        this.frame.setBorder(null);
        super.uninstallDefaults();
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsInternalFrameUI((JInternalFrame)component);
    }
    
    public WindowsInternalFrameUI(final JInternalFrame internalFrame) {
        super(internalFrame);
        this.xp = XPStyle.getXP();
    }
    
    @Override
    protected DesktopManager createDesktopManager() {
        return new WindowsDesktopManager();
    }
    
    @Override
    protected JComponent createNorthPane(final JInternalFrame internalFrame) {
        return this.titlePane = new WindowsInternalFrameTitlePane(internalFrame);
    }
    
    private class XPBorder extends AbstractBorder
    {
        private XPStyle.Skin leftSkin;
        private XPStyle.Skin rightSkin;
        private XPStyle.Skin bottomSkin;
        
        private XPBorder() {
            this.leftSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMELEFT);
            this.rightSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMERIGHT);
            this.bottomSkin = WindowsInternalFrameUI.this.xp.getSkin(WindowsInternalFrameUI.this.frame, TMSchema.Part.WP_FRAMEBOTTOM);
        }
        
        @Override
        public void paintBorder(final Component component, final Graphics graphics, final int n, final int n2, final int n3, final int n4) {
            final TMSchema.State state = ((JInternalFrame)component).isSelected() ? TMSchema.State.ACTIVE : TMSchema.State.INACTIVE;
            final int n5 = (WindowsInternalFrameUI.this.titlePane != null) ? WindowsInternalFrameUI.this.titlePane.getSize().height : 0;
            this.bottomSkin.paintSkin(graphics, 0, n4 - this.bottomSkin.getHeight(), n3, this.bottomSkin.getHeight(), state);
            this.leftSkin.paintSkin(graphics, 0, n5 - 1, this.leftSkin.getWidth(), n4 - n5 - this.bottomSkin.getHeight() + 2, state);
            this.rightSkin.paintSkin(graphics, n3 - this.rightSkin.getWidth(), n5 - 1, this.rightSkin.getWidth(), n4 - n5 - this.bottomSkin.getHeight() + 2, state);
        }
        
        @Override
        public Insets getBorderInsets(final Component component, final Insets insets) {
            insets.top = 4;
            insets.left = this.leftSkin.getWidth();
            insets.right = this.rightSkin.getWidth();
            insets.bottom = this.bottomSkin.getHeight();
            return insets;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return true;
        }
    }
}
