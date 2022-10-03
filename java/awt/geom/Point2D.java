package java.awt.geom;

import java.io.Serializable;

public abstract class Point2D implements Cloneable
{
    protected Point2D() {
    }
    
    public abstract double getX();
    
    public abstract double getY();
    
    public abstract void setLocation(final double p0, final double p1);
    
    public void setLocation(final Point2D point2D) {
        this.setLocation(point2D.getX(), point2D.getY());
    }
    
    public static double distanceSq(double n, double n2, final double n3, final double n4) {
        n -= n3;
        n2 -= n4;
        return n * n + n2 * n2;
    }
    
    public static double distance(double n, double n2, final double n3, final double n4) {
        n -= n3;
        n2 -= n4;
        return Math.sqrt(n * n + n2 * n2);
    }
    
    public double distanceSq(double n, double n2) {
        n -= this.getX();
        n2 -= this.getY();
        return n * n + n2 * n2;
    }
    
    public double distanceSq(final Point2D point2D) {
        final double n = point2D.getX() - this.getX();
        final double n2 = point2D.getY() - this.getY();
        return n * n + n2 * n2;
    }
    
    public double distance(double n, double n2) {
        n -= this.getX();
        n2 -= this.getY();
        return Math.sqrt(n * n + n2 * n2);
    }
    
    public double distance(final Point2D point2D) {
        final double n = point2D.getX() - this.getX();
        final double n2 = point2D.getY() - this.getY();
        return Math.sqrt(n * n + n2 * n2);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public int hashCode() {
        final long n = java.lang.Double.doubleToLongBits(this.getX()) ^ java.lang.Double.doubleToLongBits(this.getY()) * 31L;
        return (int)n ^ (int)(n >> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof Point2D) {
            final Point2D point2D = (Point2D)o;
            return this.getX() == point2D.getX() && this.getY() == point2D.getY();
        }
        return super.equals(o);
    }
    
    public static class Float extends Point2D implements Serializable
    {
        public float x;
        public float y;
        private static final long serialVersionUID = -2870572449815403710L;
        
        public Float() {
        }
        
        public Float(final float x, final float y) {
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
        
        @Override
        public void setLocation(final double n, final double n2) {
            this.x = (float)n;
            this.y = (float)n2;
        }
        
        public void setLocation(final float x, final float y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "Point2D.Float[" + this.x + ", " + this.y + "]";
        }
    }
    
    public static class Double extends Point2D implements Serializable
    {
        public double x;
        public double y;
        private static final long serialVersionUID = 6150783262733311327L;
        
        public Double() {
        }
        
        public Double(final double x, final double y) {
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
        
        @Override
        public void setLocation(final double x, final double y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public String toString() {
            return "Point2D.Double[" + this.x + ", " + this.y + "]";
        }
    }
}
