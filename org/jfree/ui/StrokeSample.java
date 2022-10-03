package org.jfree.ui;

import java.awt.Insets;
import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JList;
import java.awt.Dimension;
import java.awt.Stroke;
import javax.swing.ListCellRenderer;
import javax.swing.JComponent;

public class StrokeSample extends JComponent implements ListCellRenderer
{
    private Stroke stroke;
    private Dimension preferredSize;
    
    public StrokeSample(final Stroke stroke) {
        this.stroke = stroke;
        this.preferredSize = new Dimension(80, 18);
    }
    
    public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        if (value instanceof StrokeSample) {
            final StrokeSample in = (StrokeSample)value;
            this.setStroke(in.getStroke());
        }
        return this;
    }
    
    public Dimension getPreferredSize() {
        return this.preferredSize;
    }
    
    public Stroke getStroke() {
        return this.stroke;
    }
    
    public void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        final Dimension size = this.getSize();
        final Insets insets = this.getInsets();
        final double xx = insets.left;
        final double yy = insets.top;
        final double ww = size.getWidth() - insets.left - insets.right;
        final double hh = size.getHeight() - insets.top - insets.bottom;
        final Point2D one = new Point2D.Double(xx + 6.0, yy + hh / 2.0);
        final Point2D two = new Point2D.Double(xx + ww - 6.0, yy + hh / 2.0);
        final Ellipse2D circle1 = new Ellipse2D.Double(one.getX() - 5.0, one.getY() - 5.0, 10.0, 10.0);
        final Ellipse2D circle2 = new Ellipse2D.Double(two.getX() - 6.0, two.getY() - 5.0, 10.0, 10.0);
        g2.draw(circle1);
        g2.fill(circle1);
        g2.draw(circle2);
        g2.fill(circle2);
        final Line2D line = new Line2D.Double(one, two);
        if (this.stroke != null) {
            g2.setStroke(this.stroke);
        }
        else {
            g2.setStroke(new BasicStroke(0.0f));
        }
        g2.draw(line);
    }
    
    public void setStroke(final Stroke stroke) {
        this.stroke = stroke;
        this.repaint();
    }
}
