package org.jfree.ui;

import java.awt.Insets;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Paint;
import javax.swing.JComponent;

public class PaintSample extends JComponent
{
    private Paint paint;
    private Dimension preferredSize;
    
    public PaintSample(final Paint paint) {
        this.paint = paint;
        this.preferredSize = new Dimension(80, 12);
    }
    
    public Paint getPaint() {
        return this.paint;
    }
    
    public Dimension getPreferredSize() {
        return this.preferredSize;
    }
    
    public void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D)g;
        final Dimension size = this.getSize();
        final Insets insets = this.getInsets();
        final double xx = insets.left;
        final double yy = insets.top;
        final double ww = size.getWidth() - insets.left - insets.right - 1.0;
        final double hh = size.getHeight() - insets.top - insets.bottom - 1.0;
        final Rectangle2D area = new Rectangle2D.Double(xx, yy, ww, hh);
        g2.setPaint(this.paint);
        g2.fill(area);
        g2.setPaint(Color.black);
        g2.draw(area);
    }
    
    public void setPaint(final Paint paint) {
        this.paint = paint;
        this.repaint();
    }
}
