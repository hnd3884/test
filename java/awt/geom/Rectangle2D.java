package java.awt.geom;

import java.io.Serializable;

public abstract class Rectangle2D extends RectangularShape
{
    public static final int OUT_LEFT = 1;
    public static final int OUT_TOP = 2;
    public static final int OUT_RIGHT = 4;
    public static final int OUT_BOTTOM = 8;
    
    protected Rectangle2D() {
    }
    
    public abstract void setRect(final double p0, final double p1, final double p2, final double p3);
    
    public void setRect(final Rectangle2D rectangle2D) {
        this.setRect(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    public boolean intersectsLine(double n, double n2, final double n3, final double n4) {
        final int outcode;
        if ((outcode = this.outcode(n3, n4)) == 0) {
            return true;
        }
        int outcode2;
        while ((outcode2 = this.outcode(n, n2)) != 0) {
            if ((outcode2 & outcode) != 0x0) {
                return false;
            }
            if ((outcode2 & 0x5) != 0x0) {
                double x = this.getX();
                if ((outcode2 & 0x4) != 0x0) {
                    x += this.getWidth();
                }
                n2 += (x - n) * (n4 - n2) / (n3 - n);
                n = x;
            }
            else {
                double y = this.getY();
                if ((outcode2 & 0x8) != 0x0) {
                    y += this.getHeight();
                }
                n += (y - n2) * (n3 - n) / (n4 - n2);
                n2 = y;
            }
        }
        return true;
    }
    
    public boolean intersectsLine(final Line2D line2D) {
        return this.intersectsLine(line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2());
    }
    
    public abstract int outcode(final double p0, final double p1);
    
    public int outcode(final Point2D point2D) {
        return this.outcode(point2D.getX(), point2D.getY());
    }
    
    @Override
    public void setFrame(final double n, final double n2, final double n3, final double n4) {
        this.setRect(n, n2, n3, n4);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return (Rectangle2D)this.clone();
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        final double x = this.getX();
        final double y = this.getY();
        return n >= x && n2 >= y && n < x + this.getWidth() && n2 < y + this.getHeight();
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (this.isEmpty() || n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final double x = this.getX();
        final double y = this.getY();
        return n + n3 > x && n2 + n4 > y && n < x + this.getWidth() && n2 < y + this.getHeight();
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        if (this.isEmpty() || n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final double x = this.getX();
        final double y = this.getY();
        return n >= x && n2 >= y && n + n3 <= x + this.getWidth() && n2 + n4 <= y + this.getHeight();
    }
    
    public abstract Rectangle2D createIntersection(final Rectangle2D p0);
    
    public static void intersect(final Rectangle2D rectangle2D, final Rectangle2D rectangle2D2, final Rectangle2D rectangle2D3) {
        final double max = Math.max(rectangle2D.getMinX(), rectangle2D2.getMinX());
        final double max2 = Math.max(rectangle2D.getMinY(), rectangle2D2.getMinY());
        rectangle2D3.setFrame(max, max2, Math.min(rectangle2D.getMaxX(), rectangle2D2.getMaxX()) - max, Math.min(rectangle2D.getMaxY(), rectangle2D2.getMaxY()) - max2);
    }
    
    public abstract Rectangle2D createUnion(final Rectangle2D p0);
    
    public static void union(final Rectangle2D rectangle2D, final Rectangle2D rectangle2D2, final Rectangle2D rectangle2D3) {
        rectangle2D3.setFrameFromDiagonal(Math.min(rectangle2D.getMinX(), rectangle2D2.getMinX()), Math.min(rectangle2D.getMinY(), rectangle2D2.getMinY()), Math.max(rectangle2D.getMaxX(), rectangle2D2.getMaxX()), Math.max(rectangle2D.getMaxY(), rectangle2D2.getMaxY()));
    }
    
    public void add(final double n, final double n2) {
        final double min = Math.min(this.getMinX(), n);
        final double max = Math.max(this.getMaxX(), n);
        final double min2 = Math.min(this.getMinY(), n2);
        this.setRect(min, min2, max - min, Math.max(this.getMaxY(), n2) - min2);
    }
    
    public void add(final Point2D point2D) {
        this.add(point2D.getX(), point2D.getY());
    }
    
    public void add(final Rectangle2D rectangle2D) {
        final double min = Math.min(this.getMinX(), rectangle2D.getMinX());
        final double max = Math.max(this.getMaxX(), rectangle2D.getMaxX());
        final double min2 = Math.min(this.getMinY(), rectangle2D.getMinY());
        this.setRect(min, min2, max - min, Math.max(this.getMaxY(), rectangle2D.getMaxY()) - min2);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new RectIterator(this, affineTransform);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return new RectIterator(this, affineTransform);
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
        if (o instanceof Rectangle2D) {
            final Rectangle2D rectangle2D = (Rectangle2D)o;
            return this.getX() == rectangle2D.getX() && this.getY() == rectangle2D.getY() && this.getWidth() == rectangle2D.getWidth() && this.getHeight() == rectangle2D.getHeight();
        }
        return false;
    }
    
    public static class Float extends Rectangle2D implements Serializable
    {
        public float x;
        public float y;
        public float width;
        public float height;
        private static final long serialVersionUID = 3798716824173675777L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4) {
            this.setRect(n, n2, n3, n4);
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
            return this.width <= 0.0f || this.height <= 0.0f;
        }
        
        public void setRect(final float x, final float y, final float width, final float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        @Override
        public void setRect(final double n, final double n2, final double n3, final double n4) {
            this.x = (float)n;
            this.y = (float)n2;
            this.width = (float)n3;
            this.height = (float)n4;
        }
        
        @Override
        public void setRect(final Rectangle2D rectangle2D) {
            this.x = (float)rectangle2D.getX();
            this.y = (float)rectangle2D.getY();
            this.width = (float)rectangle2D.getWidth();
            this.height = (float)rectangle2D.getHeight();
        }
        
        @Override
        public int outcode(final double n, final double n2) {
            int n3 = 0;
            if (this.width <= 0.0f) {
                n3 |= 0x5;
            }
            else if (n < this.x) {
                n3 |= 0x1;
            }
            else if (n > this.x + (double)this.width) {
                n3 |= 0x4;
            }
            if (this.height <= 0.0f) {
                n3 |= 0xA;
            }
            else if (n2 < this.y) {
                n3 |= 0x2;
            }
            else if (n2 > this.y + (double)this.height) {
                n3 |= 0x8;
            }
            return n3;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Float(this.x, this.y, this.width, this.height);
        }
        
        @Override
        public Rectangle2D createIntersection(final Rectangle2D rectangle2D) {
            Serializable s;
            if (rectangle2D instanceof Float) {
                s = new Float();
            }
            else {
                s = new Double();
            }
            Rectangle2D.intersect(this, rectangle2D, (Rectangle2D)s);
            return (Rectangle2D)s;
        }
        
        @Override
        public Rectangle2D createUnion(final Rectangle2D rectangle2D) {
            Serializable s;
            if (rectangle2D instanceof Float) {
                s = new Float();
            }
            else {
                s = new Double();
            }
            Rectangle2D.union(this, rectangle2D, (Rectangle2D)s);
            return (Rectangle2D)s;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]";
        }
    }
    
    public static class Double extends Rectangle2D implements Serializable
    {
        public double x;
        public double y;
        public double width;
        public double height;
        private static final long serialVersionUID = 7771313791441850493L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4) {
            this.setRect(n, n2, n3, n4);
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
        public void setRect(final double x, final double y, final double width, final double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        
        @Override
        public void setRect(final Rectangle2D rectangle2D) {
            this.x = rectangle2D.getX();
            this.y = rectangle2D.getY();
            this.width = rectangle2D.getWidth();
            this.height = rectangle2D.getHeight();
        }
        
        @Override
        public int outcode(final double n, final double n2) {
            int n3 = 0;
            if (this.width <= 0.0) {
                n3 |= 0x5;
            }
            else if (n < this.x) {
                n3 |= 0x1;
            }
            else if (n > this.x + this.width) {
                n3 |= 0x4;
            }
            if (this.height <= 0.0) {
                n3 |= 0xA;
            }
            else if (n2 < this.y) {
                n3 |= 0x2;
            }
            else if (n2 > this.y + this.height) {
                n3 |= 0x8;
            }
            return n3;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Double(this.x, this.y, this.width, this.height);
        }
        
        @Override
        public Rectangle2D createIntersection(final Rectangle2D rectangle2D) {
            final Double double1 = new Double();
            Rectangle2D.intersect(this, rectangle2D, double1);
            return double1;
        }
        
        @Override
        public Rectangle2D createUnion(final Rectangle2D rectangle2D) {
            final Double double1 = new Double();
            Rectangle2D.union(this, rectangle2D, double1);
            return double1;
        }
        
        @Override
        public String toString() {
            return this.getClass().getName() + "[x=" + this.x + ",y=" + this.y + ",w=" + this.width + ",h=" + this.height + "]";
        }
    }
}
