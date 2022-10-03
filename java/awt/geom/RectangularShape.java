package java.awt.geom;

import java.awt.Rectangle;
import java.beans.Transient;
import java.awt.Shape;

public abstract class RectangularShape implements Shape, Cloneable
{
    protected RectangularShape() {
    }
    
    public abstract double getX();
    
    public abstract double getY();
    
    public abstract double getWidth();
    
    public abstract double getHeight();
    
    public double getMinX() {
        return this.getX();
    }
    
    public double getMinY() {
        return this.getY();
    }
    
    public double getMaxX() {
        return this.getX() + this.getWidth();
    }
    
    public double getMaxY() {
        return this.getY() + this.getHeight();
    }
    
    public double getCenterX() {
        return this.getX() + this.getWidth() / 2.0;
    }
    
    public double getCenterY() {
        return this.getY() + this.getHeight() / 2.0;
    }
    
    @Transient
    public Rectangle2D getFrame() {
        return new Rectangle2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }
    
    public abstract boolean isEmpty();
    
    public abstract void setFrame(final double p0, final double p1, final double p2, final double p3);
    
    public void setFrame(final Point2D point2D, final Dimension2D dimension2D) {
        this.setFrame(point2D.getX(), point2D.getY(), dimension2D.getWidth(), dimension2D.getHeight());
    }
    
    public void setFrame(final Rectangle2D rectangle2D) {
        this.setFrame(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    public void setFrameFromDiagonal(double n, double n2, double n3, double n4) {
        if (n3 < n) {
            final double n5 = n;
            n = n3;
            n3 = n5;
        }
        if (n4 < n2) {
            final double n6 = n2;
            n2 = n4;
            n4 = n6;
        }
        this.setFrame(n, n2, n3 - n, n4 - n2);
    }
    
    public void setFrameFromDiagonal(final Point2D point2D, final Point2D point2D2) {
        this.setFrameFromDiagonal(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY());
    }
    
    public void setFrameFromCenter(final double n, final double n2, final double n3, final double n4) {
        final double abs = Math.abs(n3 - n);
        final double abs2 = Math.abs(n4 - n2);
        this.setFrame(n - abs, n2 - abs2, abs * 2.0, abs2 * 2.0);
    }
    
    public void setFrameFromCenter(final Point2D point2D, final Point2D point2D2) {
        this.setFrameFromCenter(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY());
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public Rectangle getBounds() {
        final double width = this.getWidth();
        final double height = this.getHeight();
        if (width < 0.0 || height < 0.0) {
            return new Rectangle();
        }
        final double x = this.getX();
        final double y = this.getY();
        final double floor = Math.floor(x);
        final double floor2 = Math.floor(y);
        return new Rectangle((int)floor, (int)floor2, (int)(Math.ceil(x + width) - floor), (int)(Math.ceil(y + height) - floor2));
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return new FlatteningPathIterator(this.getPathIterator(affineTransform), n);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
}
