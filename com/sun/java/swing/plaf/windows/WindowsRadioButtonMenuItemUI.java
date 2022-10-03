package com.sun.java.swing.plaf.windows;

import javax.swing.ButtonModel;
import javax.swing.AbstractButton;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;

public class WindowsRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI
{
    final WindowsMenuItemUIAccessor accessor;
    
    public WindowsRadioButtonMenuItemUI() {
        this.accessor = new WindowsMenuItemUIAccessor() {
            @Override
            public JMenuItem getMenuItem() {
                return WindowsRadioButtonMenuItemUI.this.menuItem;
            }
            
            @Override
            public TMSchema.State getState(final JMenuItem menuItem) {
                return WindowsMenuItemUI.getState(this, menuItem);
            }
            
            @Override
            public TMSchema.Part getPart(final JMenuItem menuItem) {
                return WindowsMenuItemUI.getPart(this, menuItem);
            }
        };
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsRadioButtonMenuItemUI();
    }
    
    @Override
    protected void paintBackground(final Graphics graphics, final JMenuItem menuItem, final Color color) {
        if (WindowsMenuItemUI.isVistaPainting()) {
            WindowsMenuItemUI.paintBackground(this.accessor, graphics, menuItem, color);
            return;
        }
        super.paintBackground(graphics, menuItem, color);
    }
    
    @Override
    protected void paintText(final Graphics graphics, final JMenuItem menuItem, final Rectangle rectangle, final String s) {
        if (WindowsMenuItemUI.isVistaPainting()) {
            WindowsMenuItemUI.paintText(this.accessor, graphics, menuItem, rectangle, s);
            return;
        }
        final ButtonModel model = menuItem.getModel();
        final Color color = graphics.getColor();
        if (model.isEnabled() && model.isArmed()) {
            graphics.setColor(this.selectionForeground);
        }
        WindowsGraphicsUtils.paintText(graphics, menuItem, rectangle, s, 0);
        graphics.setColor(color);
    }
}
