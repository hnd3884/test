package com.sun.java.swing.plaf.motif;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;

public class MotifPopupMenuSeparatorUI extends MotifSeparatorUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifPopupMenuSeparatorUI();
    }
    
    @Override
    public void paint(final Graphics graphics, final JComponent component) {
        final Dimension size = component.getSize();
        graphics.setColor(component.getForeground());
        graphics.drawLine(0, 0, size.width, 0);
        graphics.setColor(component.getBackground());
        graphics.drawLine(0, 1, size.width, 1);
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        return new Dimension(0, 2);
    }
}
