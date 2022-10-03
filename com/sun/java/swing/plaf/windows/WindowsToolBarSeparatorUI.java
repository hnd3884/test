package com.sun.java.swing.plaf.windows;

import javax.swing.UIDefaults;
import java.awt.Color;
import javax.swing.UIManager;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import java.awt.Dimension;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

public class WindowsToolBarSeparatorUI extends BasicToolBarSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new WindowsToolBarSeparatorUI();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Dimension separatorSize = ((JToolBar.Separator)component).getSeparatorSize();
        Dimension size;
        if (separatorSize != null) {
            size = separatorSize.getSize();
        }
        else {
            size = new Dimension(6, 6);
            final XPStyle xp = XPStyle.getXP();
            if (xp != null) {
                final XPStyle.Skin skin = xp.getSkin(component, (((JSeparator)component).getOrientation() == 1) ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT);
                size.width = skin.getWidth();
                size.height = skin.getHeight();
            }
            if (((JSeparator)component).getOrientation() == 1) {
                size.height = 0;
            }
            else {
                size.width = 0;
            }
        }
        return size;
    }
    
    @Override
    public Dimension getMaximumSize(final JComponent component) {
        final Dimension preferredSize = this.getPreferredSize(component);
        if (((JSeparator)component).getOrientation() == 1) {
            return new Dimension(preferredSize.width, 32767);
        }
        return new Dimension(32767, preferredSize.height);
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final boolean b = ((JSeparator)component).getOrientation() == 1;
        final Dimension size = component.getSize();
        final XPStyle xp = XPStyle.getXP();
        if (xp != null) {
            final XPStyle.Skin skin = xp.getSkin(component, b ? TMSchema.Part.TP_SEPARATOR : TMSchema.Part.TP_SEPARATORVERT);
            skin.paintSkin(graphics, b ? ((size.width - skin.getWidth()) / 2) : 0, b ? 0 : ((size.height - skin.getHeight()) / 2), b ? skin.getWidth() : size.width, b ? size.height : skin.getHeight(), null);
        }
        else {
            final Color color = graphics.getColor();
            final UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
            final Color color2 = lookAndFeelDefaults.getColor("ToolBar.shadow");
            final Color color3 = lookAndFeelDefaults.getColor("ToolBar.highlight");
            if (b) {
                final int n = size.width / 2 - 1;
                graphics.setColor(color2);
                graphics.drawLine(n, 2, n, size.height - 2);
                graphics.setColor(color3);
                graphics.drawLine(n + 1, 2, n + 1, size.height - 2);
            }
            else {
                final int n2 = size.height / 2 - 1;
                graphics.setColor(color2);
                graphics.drawLine(2, n2, size.width - 2, n2);
                graphics.setColor(color3);
                graphics.drawLine(2, n2 + 1, size.width - 2, n2 + 1);
            }
            graphics.setColor(color);
        }
    }
}
