package org.jfree.chart.needle;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.io.Serializable;

public class MiddlePinNeedle extends MeterNeedle implements Cloneable, Serializable
{
    private static final long serialVersionUID = 6237073996403125310L;
    
    protected void drawNeedle(final Graphics2D g2, final Rectangle2D plotArea, final Point2D rotate, final double angle) {
        final GeneralPath pointer = new GeneralPath();
        final int minY = (int)plotArea.getMinY();
        final int maxY = (int)plotArea.getMaxY();
        final int midY = (maxY - minY) / 2 + minY;
        final int midX = (int)(plotArea.getMinX() + plotArea.getWidth() / 2.0);
        int lenX = (int)(plotArea.getWidth() / 10.0);
        if (lenX < 2) {
            lenX = 2;
        }
        pointer.moveTo((float)(midX - lenX), (float)(midY - lenX));
        pointer.lineTo((float)(midX + lenX), (float)(midY - lenX));
        pointer.lineTo((float)midX, (float)minY);
        pointer.closePath();
        lenX *= 4;
        final Ellipse2D circle = new Ellipse2D.Double(midX - lenX / 2, midY - lenX, lenX, lenX);
        final Area shape = new Area(circle);
        shape.add(new Area(pointer));
        if (rotate != null && angle != 0.0) {
            this.getTransform().setToRotation(angle, rotate.getX(), rotate.getY());
            shape.transform(this.getTransform());
        }
        this.defaultDisplay(g2, shape);
    }
    
    public boolean equals(final Object object) {
        return object != null && (object == this || (super.equals(object) && object instanceof MiddlePinNeedle));
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
