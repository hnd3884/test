package com.sun.java.swing.plaf.windows;

import javax.swing.JLabel;
import java.awt.Container;
import java.awt.Window;
import java.awt.Point;
import java.awt.Component;
import javax.swing.plaf.UIResource;
import java.awt.Color;
import javax.swing.ButtonModel;
import javax.swing.UIManager;
import javax.swing.JMenu;
import java.awt.FontMetrics;
import javax.swing.JMenuItem;
import javax.swing.JComponent;
import sun.swing.SwingUtilities2;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import java.awt.Graphics;

public class WindowsGraphicsUtils
{
    public static void paintText(final Graphics graphics, final AbstractButton abstractButton, final Rectangle rectangle, final String s, final int n) {
        final FontMetrics fontMetrics = SwingUtilities2.getFontMetrics(abstractButton, graphics);
        int displayedMnemonicIndex = abstractButton.getDisplayedMnemonicIndex();
        if (WindowsLookAndFeel.isMnemonicHidden()) {
            displayedMnemonicIndex = -1;
        }
        if (XPStyle.getXP() != null && !(abstractButton instanceof JMenuItem)) {
            paintXPText(abstractButton, graphics, rectangle.x + n, rectangle.y + fontMetrics.getAscent() + n, s, displayedMnemonicIndex);
        }
        else {
            paintClassicText(abstractButton, graphics, rectangle.x + n, rectangle.y + fontMetrics.getAscent() + n, s, displayedMnemonicIndex);
        }
    }
    
    static void paintClassicText(final AbstractButton abstractButton, final Graphics graphics, final int n, final int n2, final String s, final int n3) {
        final ButtonModel model = abstractButton.getModel();
        abstractButton.getForeground();
        if (model.isEnabled()) {
            if ((!(abstractButton instanceof JMenuItem) || !model.isArmed()) && (!(abstractButton instanceof JMenu) || (!model.isSelected() && !model.isRollover()))) {
                graphics.setColor(abstractButton.getForeground());
            }
            SwingUtilities2.drawStringUnderlineCharAt(abstractButton, graphics, s, n3, n, n2);
        }
        else {
            Color color = UIManager.getColor("Button.shadow");
            Color color2 = UIManager.getColor("Button.disabledShadow");
            if (model.isArmed()) {
                color = UIManager.getColor("Button.disabledForeground");
            }
            else {
                if (color2 == null) {
                    color2 = abstractButton.getBackground().darker();
                }
                graphics.setColor(color2);
                SwingUtilities2.drawStringUnderlineCharAt(abstractButton, graphics, s, n3, n + 1, n2 + 1);
            }
            if (color == null) {
                color = abstractButton.getBackground().brighter();
            }
            graphics.setColor(color);
            SwingUtilities2.drawStringUnderlineCharAt(abstractButton, graphics, s, n3, n, n2);
        }
    }
    
    static void paintXPText(final AbstractButton abstractButton, final Graphics graphics, final int n, final int n2, final String s, final int n3) {
        paintXPText(abstractButton, WindowsButtonUI.getXPButtonType(abstractButton), WindowsButtonUI.getXPButtonState(abstractButton), graphics, n, n2, s, n3);
    }
    
    static void paintXPText(final AbstractButton abstractButton, final TMSchema.Part part, final TMSchema.State state, final Graphics graphics, final int n, final int n2, final String s, final int n3) {
        final XPStyle xp = XPStyle.getXP();
        if (xp == null) {
            return;
        }
        Color color = abstractButton.getForeground();
        if (color instanceof UIResource) {
            color = xp.getColor(abstractButton, part, state, TMSchema.Prop.TEXTCOLOR, abstractButton.getForeground());
            if (part == TMSchema.Part.TP_BUTTON && state == TMSchema.State.DISABLED && color.equals(xp.getColor(abstractButton, part, TMSchema.State.NORMAL, TMSchema.Prop.TEXTCOLOR, abstractButton.getForeground()))) {
                color = xp.getColor(abstractButton, TMSchema.Part.BP_PUSHBUTTON, state, TMSchema.Prop.TEXTCOLOR, color);
            }
            final TMSchema.TypeEnum typeEnum = xp.getTypeEnum(abstractButton, part, state, TMSchema.Prop.TEXTSHADOWTYPE);
            if (typeEnum == TMSchema.TypeEnum.TST_SINGLE || typeEnum == TMSchema.TypeEnum.TST_CONTINUOUS) {
                final Color color2 = xp.getColor(abstractButton, part, state, TMSchema.Prop.TEXTSHADOWCOLOR, Color.black);
                final Point point = xp.getPoint(abstractButton, part, state, TMSchema.Prop.TEXTSHADOWOFFSET);
                if (point != null) {
                    graphics.setColor(color2);
                    SwingUtilities2.drawStringUnderlineCharAt(abstractButton, graphics, s, n3, n + point.x, n2 + point.y);
                }
            }
        }
        graphics.setColor(color);
        SwingUtilities2.drawStringUnderlineCharAt(abstractButton, graphics, s, n3, n, n2);
    }
    
    static boolean isLeftToRight(final Component component) {
        return component.getComponentOrientation().isLeftToRight();
    }
    
    static void repaintMnemonicsInWindow(final Window window) {
        if (window == null || !window.isShowing()) {
            return;
        }
        final Window[] ownedWindows = window.getOwnedWindows();
        for (int i = 0; i < ownedWindows.length; ++i) {
            repaintMnemonicsInWindow(ownedWindows[i]);
        }
        repaintMnemonicsInContainer(window);
    }
    
    static void repaintMnemonicsInContainer(final Container container) {
        for (int i = 0; i < container.getComponentCount(); ++i) {
            final Component component = container.getComponent(i);
            if (component != null) {
                if (component.isVisible()) {
                    if (component instanceof AbstractButton && ((AbstractButton)component).getMnemonic() != 0) {
                        component.repaint();
                    }
                    else if (component instanceof JLabel && ((JLabel)component).getDisplayedMnemonic() != 0) {
                        component.repaint();
                    }
                    else if (component instanceof Container) {
                        repaintMnemonicsInContainer((Container)component);
                    }
                }
            }
        }
    }
}
