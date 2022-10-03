package org.jfree.ui;

import java.io.ObjectStreamException;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public final class RectangleEdge implements Serializable
{
    private static final long serialVersionUID = -7400988293691093548L;
    public static final RectangleEdge TOP;
    public static final RectangleEdge BOTTOM;
    public static final RectangleEdge LEFT;
    public static final RectangleEdge RIGHT;
    private String name;
    
    static {
        TOP = new RectangleEdge("RectangleEdge.TOP");
        BOTTOM = new RectangleEdge("RectangleEdge.BOTTOM");
        LEFT = new RectangleEdge("RectangleEdge.LEFT");
        RIGHT = new RectangleEdge("RectangleEdge.RIGHT");
    }
    
    private RectangleEdge(final String name) {
        this.name = name;
    }
    
    public static double coordinate(final Rectangle2D rectangle, final RectangleEdge edge) {
        double result = 0.0;
        if (edge == RectangleEdge.TOP) {
            result = rectangle.getMinY();
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = rectangle.getMaxY();
        }
        else if (edge == RectangleEdge.LEFT) {
            result = rectangle.getMinX();
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = rectangle.getMaxX();
        }
        return result;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RectangleEdge)) {
            return false;
        }
        final RectangleEdge order = (RectangleEdge)o;
        return this.name.equals(order.name);
    }
    
    public int hashCode() {
        return this.name.hashCode();
    }
    
    public static boolean isLeftOrRight(final RectangleEdge edge) {
        return edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT;
    }
    
    public static boolean isTopOrBottom(final RectangleEdge edge) {
        return edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM;
    }
    
    public static RectangleEdge opposite(final RectangleEdge edge) {
        RectangleEdge result = null;
        if (edge == RectangleEdge.TOP) {
            result = RectangleEdge.BOTTOM;
        }
        else if (edge == RectangleEdge.BOTTOM) {
            result = RectangleEdge.TOP;
        }
        else if (edge == RectangleEdge.LEFT) {
            result = RectangleEdge.RIGHT;
        }
        else if (edge == RectangleEdge.RIGHT) {
            result = RectangleEdge.LEFT;
        }
        return result;
    }
    
    private Object readResolve() throws ObjectStreamException {
        RectangleEdge result = null;
        if (this.equals(RectangleEdge.TOP)) {
            result = RectangleEdge.TOP;
        }
        else if (this.equals(RectangleEdge.BOTTOM)) {
            result = RectangleEdge.BOTTOM;
        }
        else if (this.equals(RectangleEdge.LEFT)) {
            result = RectangleEdge.LEFT;
        }
        else if (this.equals(RectangleEdge.RIGHT)) {
            result = RectangleEdge.RIGHT;
        }
        return result;
    }
    
    public String toString() {
        return this.name;
    }
}
