package com.sun.java.swing.plaf.windows;

import java.awt.Font;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

public class WindowsPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsPopupMenuSeparatorUI();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Dimension size = component.getSize();
        final XPStyle xp = XPStyle.getXP();
        if (WindowsMenuItemUI.isVistaPainting(xp)) {
            int n = 1;
            final Container parent = component.getParent();
            if (parent instanceof JComponent) {
                final Object clientProperty = ((JComponent)parent).getClientProperty(WindowsPopupMenuUI.GUTTER_OFFSET_KEY);
                if (clientProperty instanceof Integer) {
                    n = (int)clientProperty - component.getX() + WindowsPopupMenuUI.getGutterWidth();
                }
            }
            final XPStyle.Skin skin = xp.getSkin(component, TMSchema.Part.MP_POPUPSEPARATOR);
            final int height = skin.getHeight();
            skin.paintSkin(graphics, n, (size.height - height) / 2, size.width - n - 1, height, TMSchema.State.NORMAL);
        }
        else {
            final int n2 = size.height / 2;
            graphics.setColor(component.getForeground());
            graphics.drawLine(1, n2 - 1, size.width - 2, n2 - 1);
            graphics.setColor(component.getBackground());
            graphics.drawLine(1, n2, size.width - 2, n2);
        }
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        int height = 0;
        final Font font = component.getFont();
        if (font != null) {
            height = component.getFontMetrics(font).getHeight();
        }
        return new Dimension(0, height / 2 + 2);
    }
}
