package com.sun.java.swing.plaf.motif;

import sun.swing.SwingUtilities2;
import java.awt.Rectangle;
import java.awt.Graphics;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MotifScrollBarUI extends BasicScrollBarUI
{
    public static ComponentUI createUI(final JComponent component) {
        return new MotifScrollBarUI();
    }
    
    @Override
    public Dimension getPreferredSize(final JComponent component) {
        final Insets insets = component.getInsets();
        final int n = insets.left + insets.right;
        final int n2 = insets.top + insets.bottom;
        return (this.scrollbar.getOrientation() == 1) ? new Dimension(n + 11, n2 + 33) : new Dimension(n + 33, n2 + 11);
    }
    
    @Override
    protected JButton createDecreaseButton(final int n) {
        return new MotifScrollBarButton(n);
    }
    
    @Override
    protected JButton createIncreaseButton(final int n) {
        return new MotifScrollBarButton(n);
    }
    
    public void paintTrack(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        graphics.setColor(this.trackColor);
        graphics.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }
    
    public void paintThumb(final Graphics graphics, final JComponent component, final Rectangle rectangle) {
        if (rectangle.isEmpty() || !this.scrollbar.isEnabled()) {
            return;
        }
        final int width = rectangle.width;
        final int height = rectangle.height;
        graphics.translate(rectangle.x, rectangle.y);
        graphics.setColor(this.thumbColor);
        graphics.fillRect(0, 0, width - 1, height - 1);
        graphics.setColor(this.thumbHighlightColor);
        SwingUtilities2.drawVLine(graphics, 0, 0, height - 1);
        SwingUtilities2.drawHLine(graphics, 1, width - 1, 0);
        graphics.setColor(this.thumbLightShadowColor);
        SwingUtilities2.drawHLine(graphics, 1, width - 1, height - 1);
        SwingUtilities2.drawVLine(graphics, width - 1, 1, height - 2);
        graphics.translate(-rectangle.x, -rectangle.y);
    }
}
