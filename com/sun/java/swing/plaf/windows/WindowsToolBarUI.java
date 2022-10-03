package com.sun.java.swing.plaf.windows;

import javax.swing.AbstractButton;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToolBarUI;

public class WindowsToolBarUI extends BasicToolBarUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsToolBarUI();
    }
    
    @Override
    protected void installDefaults() {
        if (XPStyle.getXP() != null) {
            this.setRolloverBorders(true);
        }
        super.installDefaults();
    }
    
    @Override
    protected Border createRolloverBorder() {
        if (XPStyle.getXP() != null) {
            return new EmptyBorder(3, 3, 3, 3);
        }
        return super.createRolloverBorder();
    }
    
    @Override
    protected Border createNonRolloverBorder() {
        if (XPStyle.getXP() != null) {
            return new EmptyBorder(3, 3, 3, 3);
        }
        return super.createNonRolloverBorder();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            xp.getSkin(component, TMSchema.Part.TP_TOOLBAR).paintSkin(graphics, 0, 0, component.getWidth(), component.getHeight(), null, true);
        }
        else {
            super.paint(graphics, component);
        }
    }
    
    @Override
    protected Border getRolloverBorder(final AbstractButton abstractButton) {
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            return xp.getBorder(abstractButton, WindowsButtonUI.getXPButtonType(abstractButton));
        }
        return super.getRolloverBorder(abstractButton);
    }
}
