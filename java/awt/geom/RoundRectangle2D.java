package java.awt.geom;

import java.io.Serializable;

public abstract class RoundRectangle2D extends RectangularShape
{
    protected RoundRectangle2D() {
    }
    
    public abstract double getArcWidth();
    
    public abstract double getArcHeight();
    
    public abstract void setRoundRect(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5);
    
    public void setRoundRect(final RoundRectangle2D roundRectangle2D) {
        this.setRoundRect(roundRectangle2D.getX(), roundRectangle2D.getY(), roundRectangle2D.getWidth(), roundRectangle2D.getHeight(), roundRectangle2D.getArcWidth(), roundRectangle2D.getArcHeight());
    }
    
    @Override
    public void setFrame(final double n, final double n2, final double n3, final double n4) {
        this.setRoundRect(n, n2, n3, n4, this.getArcWidth(), this.getArcHeight());
    }
    
    @Override
    public boolean contains(double n, double n2) {
        if (this.isEmpty()) {
            return false;
        }
        final double x = this.getX();
        final double y = this.getY();
        final double n3 = x + this.getWidth();
        final double n4 = y + this.getHeight();
        if (n < x || n2 < y || n >= n3 || n2 >= n4) {
            return false;
        }
        final double n5 = Math.min(this.getWidth(), Math.abs(this.getArcWidth())) / 2.0;
        final double n6 = Math.min(this.getHeight(), Math.abs(this.getArcHeight())) / 2.0;
        double n7;
        if (n >= (n7 = x + n5) && n < (n7 = n3 - n5)) {
            return true;
        }
        double n8;
        if (n2 >= (n8 = y + n6) && n2 < (n8 = n4 - n6)) {
            return true;
        }
        n = (n - n7) / n5;
        n2 = (n2 - n8) / n6;
        return n * n + n2 * n2 <= 1.0;
    }
    
    private int classify(final double n, final double n2, final double n3, final double n4) {
        if (n < n2) {
            return 0;
        }
        if (n < n2 + n4) {
            return 1;
        }
        if (n < n3 - n4) {
            return 2;
        }
        if (n < n3) {
            return 3;
        }
        return 4;
    }
    
    @Override
    public boolean intersects(double n, double n2, final double n3, final double n4) {
        if (this.isEmpty() || n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final double x = this.getX();
        final double y = this.getY();
        final double n5 = x + this.getWidth();
        final double n6 = y + this.getHeight();
        if (n + n3 <= x || n >= n5 || n2 + n4 <= y || n2 >= n6) {
            return false;
        }
        final double n7 = Math.min(this.getWidth(), Math.abs(this.getArcWidth())) / 2.0;
        final double n8 = Math.min(this.getHeight(), Math.abs(this.getArcHeight())) / 2.0;
        final int classify = this.classify(n, x, n5, n7);
        final int classify2 = this.classify(n + n3, x, n5, n7);
        final int classify3 = this.classify(n2, y, n6, n8);
        final int classify4 = this.classify(n2 + n4, y, n6, n8);
        if (classify == 2 || classify2 == 2 || classify3 == 2 || classify4 == 2) {
            return true;
        }
        if ((classify < 2 && classify2 > 2) || (classify3 < 2 && classify4 > 2)) {
            return true;
        }
        n = (n = ((classify2 == 1) ? (n + n3 - (x + n7)) : (n - (n5 - n7))));
        n2 = (n2 = ((classify4 == 1) ? (n2 + n4 - (y + n8)) : (n2 - (n6 - n8))));
        n /= n7;
        n2 /= n8;
        return n * n + n2 * n2 <= 1.0;
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return !this.isEmpty() && n3 > 0.0 && n4 > 0.0 && this.contains(n, n2) && this.contains(n + n3, n2) && this.contains(n, n2 + n4) && this.contains(n + n3, n2 + n4);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new RoundRectIterator(this, affineTransform);
    }
    
    @Override
    public int hashCode() {
        final long n = java.lang.Double.doubleToLongBits(this.getX()) + java.lang.Double.doubleToLongBits(this.getY()) * 37L + java.lang.Double.doubleToLongBits(this.getWidth()) * 43L + java.lang.Double.doubleToLongBits(this.getHeight()) * 47L + java.lang.Double.doubleToLongBits(this.getArcWidth()) * 53L + java.lang.Double.doubleToLongBits(this.getArcHeight()) * 59L;
        return (int)n ^ (int)(n >> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RoundRectangle2D) {
            final RoundRectangle2D roundRectangle2D = (RoundRectangle2D)o;
            return this.getX() == roundRectangle2D.getX() && this.getY() == roundRectangle2D.getY() && this.getWidth() == roundRectangle2D.getWidth() && this.getHeight() == roundRectangle2D.getHeight() && this.getArcWidth() == roundRectangle2D.getArcWidth() && this.getArcHeight() == roundRectangle2D.getArcHeight();
        }
        return false;
    }
    
    public static class Float extends RoundRectangle2D implements Serializable
    {
        public float x;
        public float y;
        public float width;
        public float height;
        public float arcwidth;
        public float archeight;
        private static final long serialVersionUID = -3423150618393866922L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.setRoundRect(n, n2, n3, n4, n5, n6);
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
        public double getArcWidth() {
            return this.arcwidth;
        }
        
        @Override
        public double getArcHeight() {
            return this.archeight;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0f || this.height <= 0.0f;
        }
        
        public void setRoundRect(final float x, final float y, final float width, final float height, final float arcwidth, final float archeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.arcwidth = arcwidth;
            this.archeight = archeight;
        }
        
        @Override
        public void setRoundRect(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.x = (float)n;
            this.y = (float)n2;
            this.width = (float)n3;
            this.height = (float)n4;
            this.arcwidth = (float)n5;
            this.archeight = (float)n6;
        }
        
        @Override
        public void setRoundRect(final RoundRectangle2D roundRectangle2D) {
            this.x = (float)roundRectangle2D.getX();
            this.y = (float)roundRectangle2D.getY();
            this.width = (float)roundRectangle2D.getWidth();
            this.height = (float)roundRectangle2D.getHeight();
            this.arcwidth = (float)roundRectangle2D.getArcWidth();
            this.archeight = (float)roundRectangle2D.getArcHeight();
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Float(this.x, this.y, this.width, this.height);
        }
    }
    
    public static class Double extends RoundRectangle2D implements Serializable
    {
        public double x;
        public double y;
        public double width;
        public double height;
        public double arcwidth;
        public double archeight;
        private static final long serialVersionUID = 1048939333485206117L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.setRoundRect(n, n2, n3, n4, n5, n6);
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
        public double getArcWidth() {
            return this.arcwidth;
        }
        
        @Override
        public double getArcHeight() {
            return this.archeight;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }
        
        @Override
        public void setRoundRect(final double x, final double y, final double width, final double height, final double arcwidth, final double archeight) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.arcwidth = arcwidth;
            this.archeight = archeight;
        }
        
        @Override
        public void setRoundRect(final RoundRectangle2D roundRectangle2D) {
            this.x = roundRectangle2D.getX();
            this.y = roundRectangle2D.getY();
            this.width = roundRectangle2D.getWidth();
            this.height = roundRectangle2D.getHeight();
            this.arcwidth = roundRectangle2D.getArcWidth();
            this.archeight = roundRectangle2D.getArcHeight();
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(this.x, this.y, this.width, this.height);
        }
    }
}
