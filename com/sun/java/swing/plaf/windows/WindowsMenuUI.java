package com.sun.java.swing.plaf.windows;

import java.awt.event.MouseEvent;
import java.awt.Dimension;
import javax.swing.Icon;
import javax.swing.event.MouseInputListener;
import javax.swing.AbstractButton;
import java.awt.Rectangle;
import javax.swing.UIDefaults;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.MenuElement;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.plaf.basic.BasicMenuUI;

public class WindowsMenuUI extends BasicMenuUI
{
    protected Integer menuBarHeight;
    protected boolean hotTrackingOn;
    final WindowsMenuItemUIAccessor accessor;
    
    public WindowsMenuUI() {
        this.accessor = new WindowsMenuItemUIAccessor() {
            @Override
            public JMenuItem getMenuItem() {
                return WindowsMenuUI.this.menuItem;
            }
            
            @Override
            public TMSchema.State getState(final JMenuItem menuItem) {
                TMSchema.State state = menuItem.isEnabled() ? TMSchema.State.NORMAL : TMSchema.State.DISABLED;
                final ButtonModel model = menuItem.getModel();
                if (model.isArmed() || model.isSelected()) {
                    state = (menuItem.isEnabled() ? TMSchema.State.PUSHED : TMSchema.State.DISABLEDPUSHED);
                }
                else if (model.isRollover() && ((JMenu)menuItem).isTopLevelMenu()) {
                    final TMSchema.State state2 = state;
                    state = (menuItem.isEnabled() ? TMSchema.State.HOT : TMSchema.State.DISABLEDHOT);
                    final MenuElement[] subElements = ((JMenuBar)menuItem.getParent()).getSubElements();
                    for (int length = subElements.length, i = 0; i < length; ++i) {
                        if (((JMenuItem)subElements[i]).isSelected()) {
                            state = state2;
                            break;
                        }
                    }
                }
                if (!((JMenu)menuItem).isTopLevelMenu()) {
                    if (state == TMSchema.State.PUSHED) {
                        state = TMSchema.State.HOT;
                    }
                    else if (state == TMSchema.State.DISABLEDPUSHED) {
                        state = TMSchema.State.DISABLEDHOT;
                    }
                }
                if (((JMenu)menuItem).isTopLevelMenu() && WindowsMenuItemUI.isVistaPainting() && !WindowsMenuBarUI.isActive(menuItem)) {
                    state = TMSchema.State.DISABLED;
                }
                return state;
            }
            
            @Override
            public TMSchema.Part getPart(final JMenuItem menuItem) {
                return ((JMenu)menuItem).isTopLevelMenu() ? TMSchema.Part.MP_BARITEM : TMSchema.Part.MP_POPUPITEM;
            }
        };
    }
    
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsMenuUI();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        if (!WindowsLookAndFeel.isClassicWindows()) {
            this.menuItem.setRolloverEnabled(true);
        }
        this.menuBarHeight = UIManager.getInt("MenuBar.height");
        final Object value = UIManager.get("MenuBar.rolloverEnabled");
        this.hotTrackingOn = (!(value instanceof Boolean) || (boolean)value);
    }
    
    @Override
    protected void paintBackground(final Graphics graphics, final JMenuItem menuItem, final Color color) {
        if (WindowsMenuItemUI.isVistaPainting()) {
            WindowsMenuItemUI.paintBackground(this.accessor, graphics, menuItem, color);
            return;
        }
        final JMenu menu = (JMenu)menuItem;
        final ButtonModel model = menu.getModel();
        if (WindowsLookAndFeel.isClassicWindows() || !menu.isTopLevelMenu() || (XPStyle.getXP() != null && (model.isArmed() || model.isSelected()))) {
            super.paintBackground(graphics, menu, color);
            return;
        }
        final Color color2 = graphics.getColor();
        final int width = menu.getWidth();
        final int height = menu.getHeight();
        final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        final Color color3 = lookAndFeelDefaults.getColor("controlLtHighlight");
        final Color color4 = lookAndFeelDefaults.getColor("controlShadow");
        graphics.setColor(menu.getBackground());
        graphics.fillRect(0, 0, width, height);
        if (menu.isOpaque()) {
            if (model.isArmed() || model.isSelected()) {
                graphics.setColor(color4);
                graphics.drawLine(0, 0, width - 1, 0);
                graphics.drawLine(0, 0, 0, height - 2);
                graphics.setColor(color3);
                graphics.drawLine(width - 1, 0, width - 1, height - 2);
                graphics.drawLine(0, height - 2, width - 1, height - 2);
            }
            else if (model.isRollover() && model.isEnabled()) {
                boolean b = false;
                final MenuElement[] subElements = ((JMenuBar)menu.getParent()).getSubElements();
                for (int i = 0; i < subElements.length; ++i) {
                    if (((JMenuItem)subElements[i]).isSelected()) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    if (XPStyle.getXP() != null) {
                        graphics.setColor(this.selectionBackground);
                        graphics.fillRect(0, 0, width, height);
                    }
                    else {
                        graphics.setColor(color3);
                        graphics.drawLine(0, 0, width - 1, 0);
                        graphics.drawLine(0, 0, 0, height - 2);
                        graphics.setColor(color4);
                        graphics.drawLine(width - 1, 0, width - 1, height - 2);
                        graphics.drawLine(0, height - 2, width - 1, height - 2);
                    }
                }
            }
        }
        graphics.setColor(color2);
    }
    
    @Override
    protected void paintText(final Graphics graphics, final JMenuItem menuItem, final Rectangle rectangle, final String s) {
        if (WindowsMenuItemUI.isVistaPainting()) {
            WindowsMenuItemUI.paintText(this.accessor, graphics, menuItem, rectangle, s);
            return;
        }
        final JMenu menu = (JMenu)menuItem;
        final ButtonModel model = menuItem.getModel();
        final Color color = graphics.getColor();
        int rollover = model.isRollover() ? 1 : 0;
        if (rollover != 0 && menu.isTopLevelMenu()) {
            final MenuElement[] subElements = ((JMenuBar)menu.getParent()).getSubElements();
            for (int i = 0; i < subElements.length; ++i) {
                if (((JMenuItem)subElements[i]).isSelected()) {
                    rollover = 0;
                    break;
                }
            }
        }
        if ((model.isSelected() && (WindowsLookAndFeel.isClassicWindows() || !menu.isTopLevelMenu())) || (XPStyle.getXP() != null && (rollover != 0 || model.isArmed() || model.isSelected()))) {
            graphics.setColor(this.selectionForeground);
        }
        WindowsGraphicsUtils.paintText(graphics, menuItem, rectangle, s, 0);
        graphics.setColor(color);
    }
    
    @Override
    protected MouseInputListener createMouseInputListener(final JComponent component) {
        return new WindowsMouseInputHandler();
    }
    
    @Override
    protected Dimension getPreferredMenuItemSize(final JComponent component, final Icon icon, final Icon icon2, final int n) {
        final Dimension preferredMenuItemSize = super.getPreferredMenuItemSize(component, icon, icon2, n);
        if (component instanceof JMenu && ((JMenu)component).isTopLevelMenu() && this.menuBarHeight != null && preferredMenuItemSize.height < this.menuBarHeight) {
            preferredMenuItemSize.height = this.menuBarHeight;
        }
        return preferredMenuItemSize;
    }
    
    protected class WindowsMouseInputHandler extends MouseInputHandler
    {
        @Override
        public void mouseEntered(final MouseEvent mouseEvent) {
            super.mouseEntered(mouseEvent);
            final JMenu menu = (JMenu)mouseEvent.getSource();
            if (WindowsMenuUI.this.hotTrackingOn && menu.isTopLevelMenu() && menu.isRolloverEnabled()) {
                menu.getModel().setRollover(true);
                WindowsMenuUI.this.menuItem.repaint();
            }
        }
        
        @Override
        public void mouseExited(final MouseEvent mouseEvent) {
            super.mouseExited(mouseEvent);
            final JMenu menu = (JMenu)mouseEvent.getSource();
            final ButtonModel model = menu.getModel();
            if (menu.isRolloverEnabled()) {
                model.setRollover(false);
                WindowsMenuUI.this.menuItem.repaint();
            }
        }
    }
}
