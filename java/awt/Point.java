package java.awt;

import java.beans.Transient;
import java.io.Serializable;
import java.awt.geom.Point2D;

public class Point extends Point2D implements Serializable
{
    public int x;
    public int y;
    private static final long serialVersionUID = -5276940640259749850L;
    
    public Point() {
        this(0, 0);
    }
    
    public Point(final Point point) {
        this(point.x, point.y);
    }
    
    public Point(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public double getX() {
        return this.x;
    }
    
    @Override
    public double getY() {
        return this.y;
    }
    
    @Transient
    public Point getLocation() {
        return new Point(this.x, this.y);
    }
    
    public void setLocation(final Point point) {
        this.setLocation(point.x, point.y);
    }
    
    public void setLocation(final int n, final int n2) {
        this.move(n, n2);
    }
    
    @Override
    public void setLocation(final double n, final double n2) {
        this.x = (int)Math.floor(n + 0.5);
        this.y = (int)Math.floor(n2 + 0.5);
    }
    
    public void move(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public void translate(final int n, final int n2) {
        this.x += n;
        this.y += n2;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Point) {
            final Point point = (Point)o;
            return this.x == point.x && this.y == point.y;
        }
        return super.equals(o);
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + "]";
    }
}
