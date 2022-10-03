package java.awt.geom;

import java.io.Serializable;

public abstract class Ellipse2D extends RectangularShape
{
    protected Ellipse2D() {
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        final double width = this.getWidth();
        if (width <= 0.0) {
            return false;
        }
        final double n3 = (n - this.getX()) / width - 0.5;
        final double height = this.getHeight();
        if (height <= 0.0) {
            return false;
        }
        final double n4 = (n2 - this.getY()) / height - 0.5;
        return n3 * n3 + n4 * n4 < 0.25;
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final double width = this.getWidth();
        if (width <= 0.0) {
            return false;
        }
        final double n5 = (n - this.getX()) / width - 0.5;
        final double n6 = n5 + n3 / width;
        final double height = this.getHeight();
        if (height <= 0.0) {
            return false;
        }
        final double n7 = (n2 - this.getY()) / height - 0.5;
        final double n8 = n7 + n4 / height;
        double n9;
        if (n5 > 0.0) {
            n9 = n5;
        }
        else if (n6 < 0.0) {
            n9 = n6;
        }
        else {
            n9 = 0.0;
        }
        double n10;
        if (n7 > 0.0) {
            n10 = n7;
        }
        else if (n8 < 0.0) {
            n10 = n8;
        }
        else {
            n10 = 0.0;
        }
        return n9 * n9 + n10 * n10 < 0.25;
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return this.contains(n, n2) && this.contains(n + n3, n2) && this.contains(n, n2 + n4) && this.contains(n + n3, n2 + n4);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new EllipseIterator(this, affineTransform);
    }
    
    @Override
    public int hashCode() {
        final long n = java.lang.Double.doubleToLongBits(this.getX()) + java.lang.Double.doubleToLongBits(this.getY()) * 37L + java.lang.Double.doubleToLongBits(this.getWidth()) * 43L + java.lang.Double.doubleToLongBits(this.getHeight()) * 47L;
        return (int)n ^ (int)(n >> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Ellipse2D) {
            final Ellipse2D ellipse2D = (Ellipse2D)o;
            return this.getX() == ellipse2D.getX() && this.getY() == ellipse2D.getY() && this.getWidth() == ellipse2D.getWidth() && this.getHeight() == ellipse2D.getHeight();
        }
        return false;
    }
    
    public static class Float extends Ellipse2D implements Serializable
    {
        public float x;
        public float y;
        public float width;
        public float height;
        private static final long serialVersionUID = -6633761252372475977L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4) {
            this.setFrame(n, n2, n3, n4);
        }
        
        @Override
        public double getX() {
            return this.x;
        }
        
        @Override
        public double getY() {
            return this.y;
        }
        
        @Override
        public double getWidth() {
            return this.width;
        }
        
        @Override
        public double getHeight() {
            return this.height;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }
        
        public void setFrame(final float x, final float y, final float width, final float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        @Override
        public void setFrame(final double n, final double n2, final double n3, final double n4) {
            this.x = (float)n;
            this.y = (float)n2;
            this.width = (float)n3;
            this.height = (float)n4;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
        }
    }
    
    public static class Double extends Ellipse2D implements Serializable
    {
        public double x;
        public double y;
        public double width;
        public double height;
        private static final long serialVersionUID = 5555464816372320683L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4) {
            this.setFrame(n, n2, n3, n4);
        }
        
        @Override
        public double getX() {
            return this.x;
        }
        
        @Override
        public double getY() {
            return this.y;
        }
        
        @Override
        public double getWidth() {
            return this.width;
        }
        
        @Override
        public double getHeight() {
            return this.height;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }
        
        @Override
        public void setFrame(final double x, final double y, final double width, final double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
        }
    }
}
