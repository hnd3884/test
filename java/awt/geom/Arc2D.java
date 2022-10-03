package java.awt.geom;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Arc2D extends RectangularShape
{
    public static final int OPEN = 0;
    public static final int CHORD = 1;
    public static final int PIE = 2;
    private int type;
    
    protected Arc2D() {
        this(0);
    }
    
    protected Arc2D(final int arcType) {
        this.setArcType(arcType);
    }
    
    public abstract double getAngleStart();
    
    public abstract double getAngleExtent();
    
    public int getArcType() {
        return this.type;
    }
    
    public Point2D getStartPoint() {
        final double radians = Math.toRadians(-this.getAngleStart());
        return new Point2D.Double(this.getX() + (Math.cos(radians) * 0.5 + 0.5) * this.getWidth(), this.getY() + (Math.sin(radians) * 0.5 + 0.5) * this.getHeight());
    }
    
    public Point2D getEndPoint() {
        final double radians = Math.toRadians(-this.getAngleStart() - this.getAngleExtent());
        return new Point2D.Double(this.getX() + (Math.cos(radians) * 0.5 + 0.5) * this.getWidth(), this.getY() + (Math.sin(radians) * 0.5 + 0.5) * this.getHeight());
    }
    
    public abstract void setArc(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5, final int p6);
    
    public void setArc(final Point2D point2D, final Dimension2D dimension2D, final double n, final double n2, final int n3) {
        this.setArc(point2D.getX(), point2D.getY(), dimension2D.getWidth(), dimension2D.getHeight(), n, n2, n3);
    }
    
    public void setArc(final Rectangle2D rectangle2D, final double n, final double n2, final int n3) {
        this.setArc(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight(), n, n2, n3);
    }
    
    public void setArc(final Arc2D arc2D) {
        this.setArc(arc2D.getX(), arc2D.getY(), arc2D.getWidth(), arc2D.getHeight(), arc2D.getAngleStart(), arc2D.getAngleExtent(), arc2D.type);
    }
    
    public void setArcByCenter(final double n, final double n2, final double n3, final double n4, final double n5, final int n6) {
        this.setArc(n - n3, n2 - n3, n3 * 2.0, n3 * 2.0, n4, n5, n6);
    }
    
    public void setArcByTangent(final Point2D point2D, final Point2D point2D2, final Point2D point2D3, final double n) {
        final double atan2 = Math.atan2(point2D.getY() - point2D2.getY(), point2D.getX() - point2D2.getX());
        double atan3 = Math.atan2(point2D3.getY() - point2D2.getY(), point2D3.getX() - point2D2.getX());
        final double n2 = atan3 - atan2;
        if (n2 > 3.141592653589793) {
            atan3 -= 6.283185307179586;
        }
        else if (n2 < -3.141592653589793) {
            atan3 += 6.283185307179586;
        }
        final double n3 = (atan2 + atan3) / 2.0;
        final double n4 = n / Math.sin(Math.abs(atan3 - n3));
        final double n5 = point2D2.getX() + n4 * Math.cos(n3);
        final double n6 = point2D2.getY() + n4 * Math.sin(n3);
        double n7;
        double n8;
        if (atan2 < atan3) {
            n7 = atan2 - 1.5707963267948966;
            n8 = atan3 + 1.5707963267948966;
        }
        else {
            n7 = atan2 + 1.5707963267948966;
            n8 = atan3 - 1.5707963267948966;
        }
        final double degrees = Math.toDegrees(-n7);
        final double n9 = Math.toDegrees(-n8) - degrees;
        double n10;
        if (n9 < 0.0) {
            n10 = n9 + 360.0;
        }
        else {
            n10 = n9 - 360.0;
        }
        this.setArcByCenter(n5, n6, n, degrees, n10, this.type);
    }
    
    public abstract void setAngleStart(final double p0);
    
    public abstract void setAngleExtent(final double p0);
    
    public void setAngleStart(final Point2D point2D) {
        this.setAngleStart(-Math.toDegrees(Math.atan2(this.getWidth() * (point2D.getY() - this.getCenterY()), this.getHeight() * (point2D.getX() - this.getCenterX()))));
    }
    
    public void setAngles(final double n, final double n2, final double n3, final double n4) {
        final double centerX = this.getCenterX();
        final double centerY = this.getCenterY();
        final double width = this.getWidth();
        final double height = this.getHeight();
        final double atan2 = Math.atan2(width * (centerY - n2), height * (n - centerX));
        double n5 = Math.atan2(width * (centerY - n4), height * (n3 - centerX)) - atan2;
        if (n5 <= 0.0) {
            n5 += 6.283185307179586;
        }
        this.setAngleStart(Math.toDegrees(atan2));
        this.setAngleExtent(Math.toDegrees(n5));
    }
    
    public void setAngles(final Point2D point2D, final Point2D point2D2) {
        this.setAngles(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY());
    }
    
    public void setArcType(final int type) {
        if (type < 0 || type > 2) {
            throw new IllegalArgumentException("invalid type for Arc: " + type);
        }
        this.type = type;
    }
    
    @Override
    public void setFrame(final double n, final double n2, final double n3, final double n4) {
        this.setArc(n, n2, n3, n4, this.getAngleStart(), this.getAngleExtent(), this.type);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        if (this.isEmpty()) {
            return this.makeBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        }
        double max2;
        double max;
        double min2;
        double min;
        if (this.getArcType() == 2) {
            min = (min2 = (max = (max2 = 0.0)));
        }
        else {
            min = (min2 = 1.0);
            max2 = (max = -1.0);
        }
        double angleStart = 0.0;
        for (int i = 0; i < 6; ++i) {
            if (i < 4) {
                angleStart += 90.0;
                if (!this.containsAngle(angleStart)) {
                    continue;
                }
            }
            else if (i == 4) {
                angleStart = this.getAngleStart();
            }
            else {
                angleStart += this.getAngleExtent();
            }
            final double radians = Math.toRadians(-angleStart);
            final double cos = Math.cos(radians);
            final double sin = Math.sin(radians);
            min2 = Math.min(min2, cos);
            min = Math.min(min, sin);
            max = Math.max(max, cos);
            max2 = Math.max(max2, sin);
        }
        final double width = this.getWidth();
        final double height = this.getHeight();
        return this.makeBounds(this.getX() + (min2 * 0.5 + 0.5) * width, this.getY() + (min * 0.5 + 0.5) * height, (max - min2) * 0.5 * width, (max2 - min) * 0.5 * height);
    }
    
    protected abstract Rectangle2D makeBounds(final double p0, final double p1, final double p2, final double p3);
    
    static double normalizeDegrees(double n) {
        if (n > 180.0) {
            if (n <= 540.0) {
                n -= 360.0;
            }
            else {
                n = Math.IEEEremainder(n, 360.0);
                if (n == -180.0) {
                    n = 180.0;
                }
            }
        }
        else if (n <= -180.0) {
            if (n > -540.0) {
                n += 360.0;
            }
            else {
                n = Math.IEEEremainder(n, 360.0);
                if (n == -180.0) {
                    n = 180.0;
                }
            }
        }
        return n;
    }
    
    public boolean containsAngle(double n) {
        double angleExtent = this.getAngleExtent();
        final boolean b = angleExtent < 0.0;
        if (b) {
            angleExtent = -angleExtent;
        }
        if (angleExtent >= 360.0) {
            return true;
        }
        n = normalizeDegrees(n) - normalizeDegrees(this.getAngleStart());
        if (b) {
            n = -n;
        }
        if (n < 0.0) {
            n += 360.0;
        }
        return n >= 0.0 && n < angleExtent;
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
        if (n3 * n3 + n4 * n4 >= 0.25) {
            return false;
        }
        final double abs = Math.abs(this.getAngleExtent());
        if (abs >= 360.0) {
            return true;
        }
        final boolean containsAngle = this.containsAngle(-Math.toDegrees(Math.atan2(n4, n3)));
        if (this.type == 2) {
            return containsAngle;
        }
        if (containsAngle) {
            if (abs >= 180.0) {
                return true;
            }
        }
        else if (abs <= 180.0) {
            return false;
        }
        final double radians = Math.toRadians(-this.getAngleStart());
        final double cos = Math.cos(radians);
        final double sin = Math.sin(radians);
        final double n5 = radians + Math.toRadians(-this.getAngleExtent());
        final double cos2 = Math.cos(n5);
        final double sin2 = Math.sin(n5);
        final boolean b = Line2D.relativeCCW(cos, sin, cos2, sin2, 2.0 * n3, 2.0 * n4) * Line2D.relativeCCW(cos, sin, cos2, sin2, 0.0, 0.0) >= 0;
        return containsAngle ? (!b) : b;
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        final double width = this.getWidth();
        final double height = this.getHeight();
        if (n3 <= 0.0 || n4 <= 0.0 || width <= 0.0 || height <= 0.0) {
            return false;
        }
        final double angleExtent = this.getAngleExtent();
        if (angleExtent == 0.0) {
            return false;
        }
        final double x = this.getX();
        final double y = this.getY();
        final double n5 = x + width;
        final double n6 = y + height;
        final double n7 = n + n3;
        final double n8 = n2 + n4;
        if (n >= n5 || n2 >= n6 || n7 <= x || n8 <= y) {
            return false;
        }
        final double centerX = this.getCenterX();
        final double centerY = this.getCenterY();
        final Point2D startPoint = this.getStartPoint();
        final Point2D endPoint = this.getEndPoint();
        final double x2 = startPoint.getX();
        final double y2 = startPoint.getY();
        final double x3 = endPoint.getX();
        final double y3 = endPoint.getY();
        if (centerY >= n2 && centerY <= n8 && ((x2 < n7 && x3 < n7 && centerX < n7 && n5 > n && this.containsAngle(0.0)) || (x2 > n && x3 > n && centerX > n && x < n7 && this.containsAngle(180.0)))) {
            return true;
        }
        if (centerX >= n && centerX <= n7 && ((y2 > n2 && y3 > n2 && centerY > n2 && y < n8 && this.containsAngle(90.0)) || (y2 < n8 && y3 < n8 && centerY < n8 && n6 > n2 && this.containsAngle(270.0)))) {
            return true;
        }
        final Rectangle2D.Double double1 = new Rectangle2D.Double(n, n2, n3, n4);
        if (this.type == 2 || Math.abs(angleExtent) > 180.0) {
            if (double1.intersectsLine(centerX, centerY, x2, y2) || double1.intersectsLine(centerX, centerY, x3, y3)) {
                return true;
            }
        }
        else if (double1.intersectsLine(x2, y2, x3, y3)) {
            return true;
        }
        return this.contains(n, n2) || this.contains(n + n3, n2) || this.contains(n, n2 + n4) || this.contains(n + n3, n2 + n4);
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return this.contains(n, n2, n3, n4, null);
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight(), rectangle2D);
    }
    
    private boolean contains(final double n, final double n2, final double n3, final double n4, Rectangle2D rectangle2D) {
        if (!this.contains(n, n2) || !this.contains(n + n3, n2) || !this.contains(n, n2 + n4) || !this.contains(n + n3, n2 + n4)) {
            return false;
        }
        if (this.type != 2 || Math.abs(this.getAngleExtent()) <= 180.0) {
            return true;
        }
        if (rectangle2D == null) {
            rectangle2D = new Rectangle2D.Double(n, n2, n3, n4);
        }
        final double n5 = this.getWidth() / 2.0;
        final double n6 = this.getHeight() / 2.0;
        final double n7 = this.getX() + n5;
        final double n8 = this.getY() + n6;
        final double radians = Math.toRadians(-this.getAngleStart());
        if (rectangle2D.intersectsLine(n7, n8, n7 + n5 * Math.cos(radians), n8 + n6 * Math.sin(radians))) {
            return false;
        }
        final double n9 = radians + Math.toRadians(-this.getAngleExtent());
        return !rectangle2D.intersectsLine(n7, n8, n7 + n5 * Math.cos(n9), n8 + n6 * Math.sin(n9));
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new ArcIterator(this, affineTransform);
    }
    
    @Override
    public int hashCode() {
        final long n = java.lang.Double.doubleToLongBits(this.getX()) + java.lang.Double.doubleToLongBits(this.getY()) * 37L + java.lang.Double.doubleToLongBits(this.getWidth()) * 43L + java.lang.Double.doubleToLongBits(this.getHeight()) * 47L + java.lang.Double.doubleToLongBits(this.getAngleStart()) * 53L + java.lang.Double.doubleToLongBits(this.getAngleExtent()) * 59L + this.getArcType() * 61;
        return (int)n ^ (int)(n >> 32);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Arc2D) {
            final Arc2D arc2D = (Arc2D)o;
            return this.getX() == arc2D.getX() && this.getY() == arc2D.getY() && this.getWidth() == arc2D.getWidth() && this.getHeight() == arc2D.getHeight() && this.getAngleStart() == arc2D.getAngleStart() && this.getAngleExtent() == arc2D.getAngleExtent() && this.getArcType() == arc2D.getArcType();
        }
        return false;
    }
    
    public static class Float extends Arc2D implements Serializable
    {
        public float x;
        public float y;
        public float width;
        public float height;
        public float start;
        public float extent;
        private static final long serialVersionUID = 9130893014586380278L;
        
        public Float() {
            super(0);
        }
        
        public Float(final int n) {
            super(n);
        }
        
        public Float(final float x, final float y, final float width, final float height, final float start, final float extent, final int n) {
            super(n);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }
        
        public Float(final Rectangle2D rectangle2D, final float start, final float extent, final int n) {
            super(n);
            this.x = (float)rectangle2D.getX();
            this.y = (float)rectangle2D.getY();
            this.width = (float)rectangle2D.getWidth();
            this.height = (float)rectangle2D.getHeight();
            this.start = start;
            this.extent = extent;
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
        public double getAngleStart() {
            return this.start;
        }
        
        @Override
        public double getAngleExtent() {
            return this.extent;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }
        
        @Override
        public void setArc(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final int arcType) {
            this.setArcType(arcType);
            this.x = (float)n;
            this.y = (float)n2;
            this.width = (float)n3;
            this.height = (float)n4;
            this.start = (float)n5;
            this.extent = (float)n6;
        }
        
        @Override
        public void setAngleStart(final double n) {
            this.start = (float)n;
        }
        
        @Override
        public void setAngleExtent(final double n) {
            this.extent = (float)n;
        }
        
        @Override
        protected Rectangle2D makeBounds(final double n, final double n2, final double n3, final double n4) {
            return new Rectangle2D.Float((float)n, (float)n2, (float)n3, (float)n4);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeByte(this.getArcType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            try {
                this.setArcType(objectInputStream.readByte());
            }
            catch (final IllegalArgumentException ex) {
                throw new InvalidObjectException(ex.getMessage());
            }
        }
    }
    
    public static class Double extends Arc2D implements Serializable
    {
        public double x;
        public double y;
        public double width;
        public double height;
        public double start;
        public double extent;
        private static final long serialVersionUID = 728264085846882001L;
        
        public Double() {
            super(0);
        }
        
        public Double(final int n) {
            super(n);
        }
        
        public Double(final double x, final double y, final double width, final double height, final double start, final double extent, final int n) {
            super(n);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }
        
        public Double(final Rectangle2D rectangle2D, final double start, final double extent, final int n) {
            super(n);
            this.x = rectangle2D.getX();
            this.y = rectangle2D.getY();
            this.width = rectangle2D.getWidth();
            this.height = rectangle2D.getHeight();
            this.start = start;
            this.extent = extent;
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
        public double getAngleStart() {
            return this.start;
        }
        
        @Override
        public double getAngleExtent() {
            return this.extent;
        }
        
        @Override
        public boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }
        
        @Override
        public void setArc(final double x, final double y, final double width, final double height, final double start, final double extent, final int arcType) {
            this.setArcType(arcType);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.start = start;
            this.extent = extent;
        }
        
        @Override
        public void setAngleStart(final double start) {
            this.start = start;
        }
        
        @Override
        public void setAngleExtent(final double extent) {
            this.extent = extent;
        }
        
        @Override
        protected Rectangle2D makeBounds(final double n, final double n2, final double n3, final double n4) {
            return new Rectangle2D.Double(n, n2, n3, n4);
        }
        
        private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeByte(this.getArcType());
        }
        
        private void readObject(final ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
            objectInputStream.defaultReadObject();
            try {
                this.setArcType(objectInputStream.readByte());
            }
            catch (final IllegalArgumentException ex) {
                throw new InvalidObjectException(ex.getMessage());
            }
        }
    }
}
