package java.awt.geom;

import java.io.Serializable;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class Line2D implements Shape, Cloneable
{
    protected Line2D() {
    }
    
    public abstract double getX1();
    
    public abstract double getY1();
    
    public abstract Point2D getP1();
    
    public abstract double getX2();
    
    public abstract double getY2();
    
    public abstract Point2D getP2();
    
    public abstract void setLine(final double p0, final double p1, final double p2, final double p3);
    
    public void setLine(final Point2D point2D, final Point2D point2D2) {
        this.setLine(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY());
    }
    
    public void setLine(final Line2D line2D) {
        this.setLine(line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2());
    }
    
    public static int relativeCCW(final double n, final double n2, double n3, double n4, double n5, double n6) {
        n3 -= n;
        n4 -= n2;
        n5 -= n;
        n6 -= n2;
        double n7 = n5 * n4 - n6 * n3;
        if (n7 == 0.0) {
            n7 = n5 * n3 + n6 * n4;
            if (n7 > 0.0) {
                n5 -= n3;
                n6 -= n4;
                n7 = n5 * n3 + n6 * n4;
                if (n7 < 0.0) {
                    n7 = 0.0;
                }
            }
        }
        return (n7 < 0.0) ? -1 : (n7 > 0.0);
    }
    
    public int relativeCCW(final double n, final double n2) {
        return relativeCCW(this.getX1(), this.getY1(), this.getX2(), this.getY2(), n, n2);
    }
    
    public int relativeCCW(final Point2D point2D) {
        return relativeCCW(this.getX1(), this.getY1(), this.getX2(), this.getY2(), point2D.getX(), point2D.getY());
    }
    
    public static boolean linesIntersect(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        return relativeCCW(n, n2, n3, n4, n5, n6) * relativeCCW(n, n2, n3, n4, n7, n8) <= 0 && relativeCCW(n5, n6, n7, n8, n, n2) * relativeCCW(n5, n6, n7, n8, n3, n4) <= 0;
    }
    
    public boolean intersectsLine(final double n, final double n2, final double n3, final double n4) {
        return linesIntersect(n, n2, n3, n4, this.getX1(), this.getY1(), this.getX2(), this.getY2());
    }
    
    public boolean intersectsLine(final Line2D line2D) {
        return linesIntersect(line2D.getX1(), line2D.getY1(), line2D.getX2(), line2D.getY2(), this.getX1(), this.getY1(), this.getX2(), this.getY2());
    }
    
    public static double ptSegDistSq(final double n, final double n2, double n3, double n4, double n5, double n6) {
        n3 -= n;
        n4 -= n2;
        n5 -= n;
        n6 -= n2;
        double n7;
        if (n5 * n3 + n6 * n4 <= 0.0) {
            n7 = 0.0;
        }
        else {
            n5 = n3 - n5;
            n6 = n4 - n6;
            final double n8 = n5 * n3 + n6 * n4;
            if (n8 <= 0.0) {
                n7 = 0.0;
            }
            else {
                n7 = n8 * n8 / (n3 * n3 + n4 * n4);
            }
        }
        double n9 = n5 * n5 + n6 * n6 - n7;
        if (n9 < 0.0) {
            n9 = 0.0;
        }
        return n9;
    }
    
    public static double ptSegDist(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return Math.sqrt(ptSegDistSq(n, n2, n3, n4, n5, n6));
    }
    
    public double ptSegDistSq(final double n, final double n2) {
        return ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), n, n2);
    }
    
    public double ptSegDistSq(final Point2D point2D) {
        return ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), point2D.getX(), point2D.getY());
    }
    
    public double ptSegDist(final double n, final double n2) {
        return ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), n, n2);
    }
    
    public double ptSegDist(final Point2D point2D) {
        return ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), point2D.getX(), point2D.getY());
    }
    
    public static double ptLineDistSq(final double n, final double n2, double n3, double n4, double n5, double n6) {
        n3 -= n;
        n4 -= n2;
        n5 -= n;
        n6 -= n2;
        final double n7 = n5 * n3 + n6 * n4;
        double n8 = n5 * n5 + n6 * n6 - n7 * n7 / (n3 * n3 + n4 * n4);
        if (n8 < 0.0) {
            n8 = 0.0;
        }
        return n8;
    }
    
    public static double ptLineDist(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return Math.sqrt(ptLineDistSq(n, n2, n3, n4, n5, n6));
    }
    
    public double ptLineDistSq(final double n, final double n2) {
        return ptLineDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), n, n2);
    }
    
    public double ptLineDistSq(final Point2D point2D) {
        return ptLineDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), point2D.getX(), point2D.getY());
    }
    
    public double ptLineDist(final double n, final double n2) {
        return ptLineDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), n, n2);
    }
    
    public double ptLineDist(final Point2D point2D) {
        return ptLineDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), point2D.getX(), point2D.getY());
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        return false;
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return false;
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        return this.intersects(new Rectangle2D.Double(n, n2, n3, n4));
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return rectangle2D.intersectsLine(this.getX1(), this.getY1(), this.getX2(), this.getY2());
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return false;
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return false;
    }
    
    @Override
    public Rectangle getBounds() {
        return this.getBounds2D().getBounds();
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new LineIterator(this, affineTransform);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return new LineIterator(this, affineTransform);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    public static class Float extends Line2D implements Serializable
    {
        public float x1;
        public float y1;
        public float x2;
        public float y2;
        private static final long serialVersionUID = 6161772511649436349L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4) {
            this.setLine(n, n2, n3, n4);
        }
        
        public Float(final Point2D point2D, final Point2D point2D2) {
            this.setLine(point2D, point2D2);
        }
        
        @Override
        public double getX1() {
            return this.x1;
        }
        
        @Override
        public double getY1() {
            return this.y1;
        }
        
        @Override
        public Point2D getP1() {
            return new Point2D.Float(this.x1, this.y1);
        }
        
        @Override
        public double getX2() {
            return this.x2;
        }
        
        @Override
        public double getY2() {
            return this.y2;
        }
        
        @Override
        public Point2D getP2() {
            return new Point2D.Float(this.x2, this.y2);
        }
        
        @Override
        public void setLine(final double n, final double n2, final double n3, final double n4) {
            this.x1 = (float)n;
            this.y1 = (float)n2;
            this.x2 = (float)n3;
            this.y2 = (float)n4;
        }
        
        public void setLine(final float x1, final float y1, final float x2, final float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            float n;
            float n2;
            if (this.x1 < this.x2) {
                n = this.x1;
                n2 = this.x2 - this.x1;
            }
            else {
                n = this.x2;
                n2 = this.x1 - this.x2;
            }
            float n3;
            float n4;
            if (this.y1 < this.y2) {
                n3 = this.y1;
                n4 = this.y2 - this.y1;
            }
            else {
                n3 = this.y2;
                n4 = this.y1 - this.y2;
            }
            return new Rectangle2D.Float(n, n3, n2, n4);
        }
    }
    
    public static class Double extends Line2D implements Serializable
    {
        public double x1;
        public double y1;
        public double x2;
        public double y2;
        private static final long serialVersionUID = 7979627399746467499L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4) {
            this.setLine(n, n2, n3, n4);
        }
        
        public Double(final Point2D point2D, final Point2D point2D2) {
            this.setLine(point2D, point2D2);
        }
        
        @Override
        public double getX1() {
            return this.x1;
        }
        
        @Override
        public double getY1() {
            return this.y1;
        }
        
        @Override
        public Point2D getP1() {
            return new Point2D.Double(this.x1, this.y1);
        }
        
        @Override
        public double getX2() {
            return this.x2;
        }
        
        @Override
        public double getY2() {
            return this.y2;
        }
        
        @Override
        public Point2D getP2() {
            return new Point2D.Double(this.x2, this.y2);
        }
        
        @Override
        public void setLine(final double x1, final double y1, final double x2, final double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            double n;
            double n2;
            if (this.x1 < this.x2) {
                n = this.x1;
                n2 = this.x2 - this.x1;
            }
            else {
                n = this.x2;
                n2 = this.x1 - this.x2;
            }
            double n3;
            double n4;
            if (this.y1 < this.y2) {
                n3 = this.y1;
                n4 = this.y2 - this.y1;
            }
            else {
                n3 = this.y2;
                n4 = this.y1 - this.y2;
            }
            return new Rectangle2D.Double(n, n3, n2, n4);
        }
    }
}
