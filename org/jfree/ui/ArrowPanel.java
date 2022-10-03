package org.jfree.ui;

import java.awt.Insets;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class ArrowPanel extends JPanel
{
    public static final int UP = 0;
    public static final int DOWN = 1;
    private int type;
    private Rectangle2D available;
    
    public ArrowPanel(final int type) {
        this.type = 0;
        this.available = new Rectangle2D.Float();
        this.type = type;
        this.setPreferredSize(new Dimension(14, 9));
    }
    
    private Shape getArrow(final int t) {
        switch (t) {
            case 0: {
                return this.getUpArrow();
            }
            case 1: {
                return this.getDownArrow();
            }
            default: {
                return this.getUpArrow();
            }
        }
    }
    
    private Shape getDownArrow() {
        final Polygon result = new Polygon();
        result.addPoint(7, 7);
        result.addPoint(2, 2);
        result.addPoint(12, 2);
        return result;
    }
    
    private Shape getUpArrow() {
        final Polygon result = new Polygon();
        result.addPoint(7, 2);
        result.addPoint(2, 7);
        result.addPoint(12, 7);
        return result;
    }
    
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D)g;
        final Dimension size = this.getSize();
        final Insets insets = this.getInsets();
        this.available.setRect(insets.left, insets.top, size.getWidth() - insets.left - insets.right, size.getHeight() - insets.top - insets.bottom);
        g2.translate(insets.left, insets.top);
        g2.fill(this.getArrow(this.type));
    }
}
