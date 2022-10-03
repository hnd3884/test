package com.sun.java.swing.plaf.windows;

import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import java.awt.Component;
import java.awt.Color;
import javax.swing.ButtonModel;
import javax.swing.AbstractButton;
import javax.swing.JMenu;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicMenuItemUI;

public class WindowsMenuItemUI extends BasicMenuItemUI
{
    final WindowsMenuItemUIAccessor accessor;
    
    public WindowsMenuItemUI() {
        this.accessor = new WindowsMenuItemUIAccessor() {
            @Override
            public JMenuItem getMenuItem() {
                return WindowsMenuItemUI.this.menuItem;
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
        return new WindowsMenuItemUI();
    }
    
    @Override
    protected void paintText(final Graphics graphics, final JMenuItem menuItem, final Rectangle rectangle, final String s) {
        if (isVistaPainting()) {
            paintText(this.accessor, graphics, menuItem, rectangle, s);
            return;
        }
        final ButtonModel model = menuItem.getModel();
        final Color color = graphics.getColor();
        if (model.isEnabled() && (model.isArmed() || (menuItem instanceof JMenu && model.isSelected()))) {
            graphics.setColor(this.selectionForeground);
        }
        WindowsGraphicsUtils.paintText(graphics, menuItem, rectangle, s, 0);
        graphics.setColor(color);
    }
    
    @Override
    protected void paintBackground(final Graphics graphics, final JMenuItem menuItem, final Color color) {
        if (isVistaPainting()) {
            paintBackground(this.accessor, graphics, menuItem, color);
            return;
        }
        super.paintBackground(graphics, menuItem, color);
    }
    
    static void paintBackground(final WindowsMenuItemUIAccessor windowsMenuItemUIAccessor, final Graphics graphics, final JMenuItem menuItem, final Color color) {
        final XPStyle xp = XPStyle.getXP();
        assert isVistaPainting(xp);
        if (isVistaPainting(xp)) {
            final int width = menuItem.getWidth();
            final int height = menuItem.getHeight();
            if (menuItem.isOpaque()) {
                final Color color2 = graphics.getColor();
                graphics.setColor(menuItem.getBackground());
                graphics.fillRect(0, 0, width, height);
                graphics.setColor(color2);
            }
            xp.getSkin(menuItem, windowsMenuItemUIAccessor.getPart(menuItem)).paintSkin(graphics, 0, 0, width, height, windowsMenuItemUIAccessor.getState(menuItem));
        }
    }
    
    static void paintText(final WindowsMenuItemUIAccessor windowsMenuItemUIAccessor, final Graphics graphics, final JMenuItem menuItem, final Rectangle rectangle, final String s) {
        assert isVistaPainting();
        if (isVistaPainting()) {
            final TMSchema.State state = windowsMenuItemUIAccessor.getState(menuItem);
            final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(menuItem, graphics);
            int displayedMnemonicIndex = menuItem.getDisplayedMnemonicIndex();
            if (WindowsLookAndFeel.isMnemonicHidden()) {
                displayedMnemonicIndex = -1;
            }
            WindowsGraphicsUtils.paintXPText(menuItem, windowsMenuItemUIAccessor.getPart(menuItem), state, graphics, rectangle.x, rectangle.y + fontMetrics.getAscent(), s, displayedMnemonicIndex);
        }
    }
    
    static TMSchema.State getState(final WindowsMenuItemUIAccessor windowsMenuItemUIAccessor, final JMenuItem menuItem) {
        final ButtonModel model = menuItem.getModel();
        TMSchema.State state;
        if (model.isArmed()) {
            state = (model.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT);
        }
        else {
            state = (model.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED);
        }
        return state;
    }
    
    static TMSchema.Part getPart(final WindowsMenuItemUIAccessor windowsMenuItemUIAccessor, final JMenuItem menuItem) {
        return TMSchema.Part.MP_POPUPITEM;
    }
    
    static boolean isVistaPainting(final XPStyle xpStyle) {
        return xpStyle != null && xpStyle.isSkinDefined(null, TMSchema.Part.MP_POPUPITEM);
    }
    
    static boolean isVistaPainting() {
        return isVistaPainting(XPStyle.getXP());
    }
}
