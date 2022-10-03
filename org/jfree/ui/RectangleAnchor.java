package org.jfree.ui;

import java.io.ObjectStreamException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public final class RectangleAnchor implements Serializable
{
    private static final long serialVersionUID = -2457494205644416327L;
    public static final RectangleAnchor CENTER;
    public static final RectangleAnchor TOP;
    public static final RectangleAnchor TOP_LEFT;
    public static final RectangleAnchor TOP_RIGHT;
    public static final RectangleAnchor BOTTOM;
    public static final RectangleAnchor BOTTOM_LEFT;
    public static final RectangleAnchor BOTTOM_RIGHT;
    public static final RectangleAnchor LEFT;
    public static final RectangleAnchor RIGHT;
    private String name;
    
    static {
        CENTER = new RectangleAnchor("RectangleAnchor.CENTER");
        TOP = new RectangleAnchor("RectangleAnchor.TOP");
        TOP_LEFT = new RectangleAnchor("RectangleAnchor.TOP_LEFT");
        TOP_RIGHT = new RectangleAnchor("RectangleAnchor.TOP_RIGHT");
        BOTTOM = new RectangleAnchor("RectangleAnchor.BOTTOM");
        BOTTOM_LEFT = new RectangleAnchor("RectangleAnchor.BOTTOM_LEFT");
        BOTTOM_RIGHT = new RectangleAnchor("RectangleAnchor.BOTTOM_RIGHT");
        LEFT = new RectangleAnchor("RectangleAnchor.LEFT");
        RIGHT = new RectangleAnchor("RectangleAnchor.RIGHT");
    }
    
    private RectangleAnchor(final String name) {
        this.name = name;
    }
    
    public static Point2D coordinates(final Rectangle2D rectangle, final RectangleAnchor anchor) {
        final Point2D result = new Point2D.Double();
        if (anchor == RectangleAnchor.CENTER) {
            result.setLocation(rectangle.getCenterX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.TOP) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.BOTTOM) {
            result.setLocation(rectangle.getCenterX(), rectangle.getMaxY());
        }
        else if (anchor == RectangleAnchor.LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getCenterY());
        }
        else if (anchor == RectangleAnchor.TOP_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.TOP_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMinY());
        }
        else if (anchor == RectangleAnchor.BOTTOM_LEFT) {
            result.setLocation(rectangle.getMinX(), rectangle.getMaxY());
        }
        else if (anchor == RectangleAnchor.BOTTOM_RIGHT) {
            result.setLocation(rectangle.getMaxX(), rectangle.getMaxY());
        }
        return result;
    }
    
    public static Rectangle2D createRectangle(final Size2D dimensions, final double anchorX, final double anchorY, final RectangleAnchor anchor) {
        Rectangle2D result = null;
        final double w = dimensions.getWidth();
        final double h = dimensions.getHeight();
        if (anchor == RectangleAnchor.CENTER) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.TOP) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.BOTTOM) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.LEFT) {
            result = new Rectangle2D.Double(anchorX, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.RIGHT) {
            result = new Rectangle2D.Double(anchorX - w, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.TOP_LEFT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.TOP_RIGHT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.BOTTOM_LEFT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        else if (anchor == RectangleAnchor.BOTTOM_RIGHT) {
            result = new Rectangle2D.Double(anchorX - w / 2.0, anchorY - h / 2.0, w, h);
        }
        return result;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RectangleAnchor)) {
            return false;
        }
        final RectangleAnchor order = (RectangleAnchor)obj;
        return this.name.equals(order.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    private Object readResolve() throws ObjectStreamException {
        RectangleAnchor result = null;
        if (this.equals(RectangleAnchor.CENTER)) {
            result = RectangleAnchor.CENTER;
        }
        else if (this.equals(RectangleAnchor.TOP)) {
            result = RectangleAnchor.TOP;
        }
        else if (this.equals(RectangleAnchor.BOTTOM)) {
            result = RectangleAnchor.BOTTOM;
        }
        else if (this.equals(RectangleAnchor.LEFT)) {
            result = RectangleAnchor.LEFT;
        }
        else if (this.equals(RectangleAnchor.RIGHT)) {
            result = RectangleAnchor.RIGHT;
        }
        else if (this.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.TOP_LEFT;
        }
        else if (this.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.TOP_RIGHT;
        }
        else if (this.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        }
        else if (this.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        }
        return result;
    }
    
    public String toString() {
        return this.name;
    }
}
