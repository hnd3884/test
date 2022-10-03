package com.sun.java.swing.plaf.motif;

import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Container;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicOptionPaneUI;

public class MotifOptionPaneUI extends BasicOptionPaneUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifOptionPaneUI();
    }
    
    @Override
    protected Container createButtonArea() {
        final Container buttonArea = super.createButtonArea();
        if (buttonArea != null && buttonArea.getLayout() instanceof ButtonAreaLayout) {
            ((ButtonAreaLayout)buttonArea.getLayout()).setCentersChildren(false);
        }
        return buttonArea;
    }
    
    @Override
    public Dimension getMinimumOptionPaneSize() {
        return null;
    }
    
    @Override
    protected Container createSeparator() {
        return new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(10, 2);
            }
            
            @Override
            public void paint(final Graphics graphics) {
                final int width = this.getWidth();
                graphics.setColor(Color.darkGray);
                graphics.drawLine(0, 0, width, 0);
                graphics.setColor(Color.white);
                graphics.drawLine(0, 1, width, 1);
            }
        };
    }
    
    @Override
    protected void addIcon(final Container container) {
        final Icon icon = this.getIcon();
        if (icon != null) {
            final JLabel label = new JLabel(icon);
            label.setVerticalAlignment(0);
            container.add(label, "West");
        }
    }
}
