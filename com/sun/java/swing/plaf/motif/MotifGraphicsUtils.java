package com.sun.java.swing.plaf.motif;

import javax.swing.SwingUtilities;
import java.awt.Container;
import javax.swing.KeyStroke;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.ButtonModel;
import javax.swing.JMenuBar;
import java.awt.Shape;
import javax.swing.text.View;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.UIManager;
import java.awt.Rectangle;
import javax.swing.JMenuItem;
import javax.swing.Icon;
import java.awt.FontMetrics;
import sun.swing.SwingUtilities2;
import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.SwingConstants;

public class MotifGraphicsUtils implements SwingConstants
{
    private static final String MAX_ACC_WIDTH = "maxAccWidth";
    
    static void drawPoint(final Graphics graphics, final int n, final int n2) {
        graphics.drawLine(n, n2, n, n2);
    }
    
    public static void drawGroove(final Graphics graphics, final int n, final int n2, final int n3, final int n4, final Color color, final Color color2) {
        final Color color3 = graphics.getColor();
        graphics.translate(n, n2);
        graphics.setColor(color);
        graphics.drawRect(0, 0, n3 - 2, n4 - 2);
        graphics.setColor(color2);
        graphics.drawLine(1, n4 - 3, 1, 1);
        graphics.drawLine(1, 1, n3 - 3, 1);
        graphics.drawLine(0, n4 - 1, n3 - 1, n4 - 1);
        graphics.drawLine(n3 - 1, n4 - 1, n3 - 1, 0);
        graphics.translate(-n, -n2);
        graphics.setColor(color3);
    }
    
    public static void drawStringInRect(final Graphics graphics, final String s, final int n, final int n2, final int n3, final int n4, final int n5) {
        drawStringInRect(null, graphics, s, n, n2, n3, n4, n5);
    }
    
    static void drawStringInRect(final JComponent component, final Graphics graphics, final String s, final int n, final int n2, final int n3, final int n4, final int n5) {
        if (graphics.getFont() == null) {
            return;
        }
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics);
        if (fontMetrics == null) {
            return;
        }
        int n6;
        if (n5 == 0) {
            int stringWidth = SwingUtilities2.stringWidth(component, fontMetrics, s);
            if (stringWidth > n3) {
                stringWidth = n3;
            }
            n6 = n + (n3 - stringWidth) / 2;
        }
        else if (n5 == 4) {
            int stringWidth2 = SwingUtilities2.stringWidth(component, fontMetrics, s);
            if (stringWidth2 > n3) {
                stringWidth2 = n3;
            }
            n6 = n + n3 - stringWidth2;
        }
        else {
            n6 = n;
        }
        int n7 = (n4 - fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
        if (n7 < 0) {
            n7 = 0;
        }
        SwingUtilities2.drawString(component, graphics, s, n6, n2 + n4 - n7 - fontMetrics.getDescent());
    }
    
    public static void paintMenuItem(final Graphics graphics, final JComponent component, final Icon icon, final Icon icon2, final Color color, final Color color2, final int n) {
        final JMenuItem menuItem = (JMenuItem)component;
        final ButtonModel model = menuItem.getModel();
        final Dimension size = menuItem.getSize();
        final Insets insets = component.getInsets();
        final Rectangle rectangle2;
        final Rectangle rectangle = rectangle2 = new Rectangle(size);
        rectangle2.x += insets.left;
        final Rectangle rectangle3 = rectangle;
        rectangle3.y += insets.top;
        final Rectangle rectangle4 = rectangle;
        rectangle4.width -= insets.right + rectangle.x;
        final Rectangle rectangle5 = rectangle;
        rectangle5.height -= insets.bottom + rectangle.y;
        final Rectangle rectangle6 = new Rectangle();
        final Rectangle rectangle7 = new Rectangle();
        final Rectangle rectangle8 = new Rectangle();
        final Rectangle rectangle9 = new Rectangle();
        final Rectangle rectangle10 = new Rectangle();
        final Font font = graphics.getFont();
        final Font font2 = component.getFont();
        graphics.setFont(font2);
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(component, graphics, font2);
        final FontMetrics fontMetrics2 = SwingUtilities2.getFontMetrics(component, graphics, UIManager.getFont("MenuItem.acceleratorFont"));
        if (component.isOpaque()) {
            if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(color);
            }
            else {
                graphics.setColor(component.getBackground());
            }
            graphics.fillRect(0, 0, size.width, size.height);
        }
        final KeyStroke accelerator = menuItem.getAccelerator();
        String s = "";
        if (accelerator != null) {
            final int modifiers = accelerator.getModifiers();
            if (modifiers > 0) {
                s = KeyEvent.getKeyModifiersText(modifiers) + "+";
            }
            s += KeyEvent.getKeyText(accelerator.getKeyCode());
        }
        final String layoutMenuItem = layoutMenuItem(component, fontMetrics, menuItem.getText(), fontMetrics2, s, menuItem.getIcon(), icon, icon2, menuItem.getVerticalAlignment(), menuItem.getHorizontalAlignment(), menuItem.getVerticalTextPosition(), menuItem.getHorizontalTextPosition(), rectangle, rectangle6, rectangle7, rectangle8, rectangle9, rectangle10, (menuItem.getText() == null) ? 0 : n, n);
        final Color color3 = graphics.getColor();
        if (icon != null) {
            if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(color2);
            }
            icon.paintIcon(component, graphics, rectangle9.x, rectangle9.y);
            graphics.setColor(color3);
        }
        if (menuItem.getIcon() != null) {
            Icon icon3;
            if (!model.isEnabled()) {
                icon3 = menuItem.getDisabledIcon();
            }
            else if (model.isPressed() && model.isArmed()) {
                icon3 = menuItem.getPressedIcon();
                if (icon3 == null) {
                    icon3 = menuItem.getIcon();
                }
            }
            else {
                icon3 = menuItem.getIcon();
            }
            if (icon3 != null) {
                icon3.paintIcon(component, graphics, rectangle6.x, rectangle6.y);
            }
        }
        if (layoutMenuItem != null && !layoutMenuItem.equals("")) {
            final View view = (View)component.getClientProperty("html");
            if (view != null) {
                view.paint(graphics, rectangle7);
            }
            else {
                final int displayedMnemonicIndex = menuItem.getDisplayedMnemonicIndex();
                if (!model.isEnabled()) {
                    graphics.setColor(menuItem.getBackground().brighter());
                    SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, layoutMenuItem, displayedMnemonicIndex, rectangle7.x, rectangle7.y + fontMetrics2.getAscent());
                    graphics.setColor(menuItem.getBackground().darker());
                    SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, layoutMenuItem, displayedMnemonicIndex, rectangle7.x - 1, rectangle7.y + fontMetrics2.getAscent() - 1);
                }
                else {
                    if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                        graphics.setColor(color2);
                    }
                    else {
                        graphics.setColor(menuItem.getForeground());
                    }
                    SwingUtilities2.drawStringUnderlineCharAt(menuItem, graphics, layoutMenuItem, displayedMnemonicIndex, rectangle7.x, rectangle7.y + fontMetrics.getAscent());
                }
            }
        }
        if (s != null && !s.equals("")) {
            int n2 = 0;
            final Container parent = menuItem.getParent();
            if (parent != null && parent instanceof JComponent) {
                final Integer n3 = (Integer)((JComponent)parent).getClientProperty("maxAccWidth");
                n2 = ((n3 != null) ? n3 : rectangle8.width) - rectangle8.width;
            }
            graphics.setFont(UIManager.getFont("MenuItem.acceleratorFont"));
            if (!model.isEnabled()) {
                graphics.setColor(menuItem.getBackground().brighter());
                SwingUtilities2.drawString(component, graphics, s, rectangle8.x - n2, rectangle8.y + fontMetrics.getAscent());
                graphics.setColor(menuItem.getBackground().darker());
                SwingUtilities2.drawString(component, graphics, s, rectangle8.x - n2 - 1, rectangle8.y + fontMetrics.getAscent() - 1);
            }
            else {
                if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                    graphics.setColor(color2);
                }
                else {
                    graphics.setColor(menuItem.getForeground());
                }
                SwingUtilities2.drawString(component, graphics, s, rectangle8.x - n2, rectangle8.y + fontMetrics2.getAscent());
            }
        }
        if (icon2 != null) {
            if (model.isArmed() || (component instanceof JMenu && model.isSelected())) {
                graphics.setColor(color2);
            }
            if (!(menuItem.getParent() instanceof JMenuBar)) {
                icon2.paintIcon(component, graphics, rectangle10.x, rectangle10.y);
            }
        }
        graphics.setColor(color3);
        graphics.setFont(font);
    }
    
    private static String layoutMenuItem(final JComponent component, final FontMetrics fontMetrics, final String s, final FontMetrics fontMetrics2, String s2, final Icon icon, final Icon icon2, final Icon icon3, final int n, final int n2, final int n3, final int n4, final Rectangle rectangle, final Rectangle rectangle2, final Rectangle rectangle3, final Rectangle rectangle4, final Rectangle rectangle5, final Rectangle rectangle6, final int n5, final int n6) {
        SwingUtilities.layoutCompoundLabel(component, fontMetrics, s, icon, n, n2, n3, n4, rectangle, rectangle2, rectangle3, n5);
        if (s2 == null || s2.equals("")) {
            final int n7 = 0;
            rectangle4.height = n7;
            rectangle4.width = n7;
            s2 = "";
        }
        else {
            rectangle4.width = SwingUtilities2.stringWidth(component, fontMetrics2, s2);
            rectangle4.height = fontMetrics2.getHeight();
        }
        if (icon2 != null) {
            rectangle5.width = icon2.getIconWidth();
            rectangle5.height = icon2.getIconHeight();
        }
        else {
            final int n8 = 0;
            rectangle5.height = n8;
            rectangle5.width = n8;
        }
        if (icon3 != null) {
            rectangle6.width = icon3.getIconWidth();
            rectangle6.height = icon3.getIconHeight();
        }
        else {
            final int n9 = 0;
            rectangle6.height = n9;
            rectangle6.width = n9;
        }
        final Rectangle union = rectangle2.union(rectangle3);
        if (isLeftToRight(component)) {
            rectangle3.x += rectangle5.width + n6;
            rectangle2.x += rectangle5.width + n6;
            rectangle4.x = rectangle.x + rectangle.width - rectangle6.width - n6 - rectangle4.width;
            rectangle5.x = rectangle.x;
            rectangle6.x = rectangle.x + rectangle.width - n6 - rectangle6.width;
        }
        else {
            rectangle3.x -= rectangle5.width + n6;
            rectangle2.x -= rectangle5.width + n6;
            rectangle4.x = rectangle.x + rectangle6.width + n6;
            rectangle5.x = rectangle.x + rectangle.width - rectangle5.width;
            rectangle6.x = rectangle.x + n6;
        }
        rectangle4.y = union.y + union.height / 2 - rectangle4.height / 2;
        rectangle6.y = union.y + union.height / 2 - rectangle6.height / 2;
        rectangle5.y = union.y + union.height / 2 - rectangle5.height / 2;
        return s;
    }
    
    private static void drawMenuBezel(final Graphics graphics, final Color color, final int n, final int n2, final int n3, final int n4) {
        graphics.setColor(color);
        graphics.fillRect(n, n2, n3, n4);
        graphics.setColor(color.brighter().brighter());
        graphics.drawLine(n + 1, n2 + n4 - 1, n + n3 - 1, n2 + n4 - 1);
        graphics.drawLine(n + n3 - 1, n2 + n4 - 2, n + n3 - 1, n2 + 1);
        graphics.setColor(color.darker().darker());
        graphics.drawLine(n, n2, n + n3 - 2, n2);
        graphics.drawLine(n, n2 + 1, n, n2 + n4 - 2);
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
}
