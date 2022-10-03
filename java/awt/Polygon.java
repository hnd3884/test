package java.awt;

import java.awt.geom.PathIterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import sun.awt.geom.Crossings;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.io.Serializable;

public class Polygon implements Shape, Serializable
{
    public int npoints;
    public int[] xpoints;
    public int[] ypoints;
    protected Rectangle bounds;
    private static final long serialVersionUID = -6460061437900069969L;
    private static final int MIN_LENGTH = 4;
    
    public Polygon() {
        this.xpoints = new int[4];
        this.ypoints = new int[4];
    }
    
    public Polygon(final int[] array, final int[] array2, final int npoints) {
        if (npoints > array.length || npoints > array2.length) {
            throw new IndexOutOfBoundsException("npoints > xpoints.length || npoints > ypoints.length");
        }
        if (npoints < 0) {
            throw new NegativeArraySizeException("npoints < 0");
        }
        this.npoints = npoints;
        this.xpoints = Arrays.copyOf(array, npoints);
        this.ypoints = Arrays.copyOf(array2, npoints);
    }
    
    public void reset() {
        this.npoints = 0;
        this.bounds = null;
    }
    
    public void invalidate() {
        this.bounds = null;
    }
    
    public void translate(final int n, final int n2) {
        for (int i = 0; i < this.npoints; ++i) {
            final int[] xpoints = this.xpoints;
            final int n3 = i;
            xpoints[n3] += n;
            final int[] ypoints = this.ypoints;
            final int n4 = i;
            ypoints[n4] += n2;
        }
        if (this.bounds != null) {
            this.bounds.translate(n, n2);
        }
    }
    
    void calculateBounds(final int[] array, final int[] array2, final int n) {
        int min = Integer.MAX_VALUE;
        int min2 = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        int max2 = Integer.MIN_VALUE;
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i];
            min = Math.min(min, n2);
            max = Math.max(max, n2);
            final int n3 = array2[i];
            min2 = Math.min(min2, n3);
            max2 = Math.max(max2, n3);
        }
        this.bounds = new Rectangle(min, min2, max - min, max2 - min2);
    }
    
    void updateBounds(final int x, final int y) {
        if (x < this.bounds.x) {
            this.bounds.width += this.bounds.x - x;
            this.bounds.x = x;
        }
        else {
            this.bounds.width = Math.max(this.bounds.width, x - this.bounds.x);
        }
        if (y < this.bounds.y) {
            this.bounds.height += this.bounds.y - y;
            this.bounds.y = y;
        }
        else {
            this.bounds.height = Math.max(this.bounds.height, y - this.bounds.y);
        }
    }
    
    public void addPoint(final int n, final int n2) {
        if (this.npoints >= this.xpoints.length || this.npoints >= this.ypoints.length) {
            int highestOneBit = this.npoints * 2;
            if (highestOneBit < 4) {
                highestOneBit = 4;
            }
            else if ((highestOneBit & highestOneBit - 1) != 0x0) {
                highestOneBit = Integer.highestOneBit(highestOneBit);
            }
            this.xpoints = Arrays.copyOf(this.xpoints, highestOneBit);
            this.ypoints = Arrays.copyOf(this.ypoints, highestOneBit);
        }
        this.xpoints[this.npoints] = n;
        this.ypoints[this.npoints] = n2;
        ++this.npoints;
        if (this.bounds != null) {
            this.updateBounds(n, n2);
        }
    }
    
    @Override
    public Rectangle getBounds() {
        return this.getBoundingBox();
    }
    
    @Deprecated
    public Rectangle getBoundingBox() {
        if (this.npoints == 0) {
            return new Rectangle();
        }
        if (this.bounds == null) {
            this.calculateBounds(this.xpoints, this.ypoints, this.npoints);
        }
        return this.bounds.getBounds();
    }
    
    public boolean contains(final Point point) {
        return this.contains(point.x, point.y);
    }
    
    public boolean contains(final int n, final int n2) {
        return this.contains(n, (double)n2);
    }
    
    @Deprecated
    public boolean inside(final int n, final int n2) {
        return this.contains(n, (double)n2);
    }
    
    @Override
    public Rectangle2D getBounds2D() {
        return this.getBounds();
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        if (this.npoints <= 2 || !this.getBoundingBox().contains(n, n2)) {
            return false;
        }
        int n3 = 0;
        int n4 = this.xpoints[this.npoints - 1];
        int n5 = this.ypoints[this.npoints - 1];
        for (int i = 0; i < this.npoints; ++i) {
            final int n6 = this.xpoints[i];
            final int n7 = this.ypoints[i];
            Label_0260: {
                if (n7 != n5) {
                    int n8;
                    if (n6 < n4) {
                        if (n >= n4) {
                            break Label_0260;
                        }
                        n8 = n6;
                    }
                    else {
                        if (n >= n6) {
                            break Label_0260;
                        }
                        n8 = n4;
                    }
                    double n9;
                    double n10;
                    if (n7 < n5) {
                        if (n2 < n7) {
                            break Label_0260;
                        }
                        if (n2 >= n5) {
                            break Label_0260;
                        }
                        if (n < n8) {
                            ++n3;
                            break Label_0260;
                        }
                        n9 = n - n6;
                        n10 = n2 - n7;
                    }
                    else {
                        if (n2 < n5) {
                            break Label_0260;
                        }
                        if (n2 >= n7) {
                            break Label_0260;
                        }
                        if (n < n8) {
                            ++n3;
                            break Label_0260;
                        }
                        n9 = n - n4;
                        n10 = n2 - n5;
                    }
                    if (n9 < n10 / (n5 - n7) * (n4 - n6)) {
                        ++n3;
                    }
                }
            }
            n4 = n6;
            n5 = n7;
        }
        return (n3 & 0x1) != 0x0;
    }
    
    private Crossings getCrossings(final double n, final double n2, final double n3, final double n4) {
        final Crossings.EvenOdd evenOdd = new Crossings.EvenOdd(n, n2, n3, n4);
        int n5 = this.xpoints[this.npoints - 1];
        int n6 = this.ypoints[this.npoints - 1];
        for (int i = 0; i < this.npoints; ++i) {
            final int n7 = this.xpoints[i];
            final int n8 = this.ypoints[i];
            if (evenOdd.accumulateLine(n5, n6, n7, n8)) {
                return null;
            }
            n5 = n7;
            n6 = n8;
        }
        return evenOdd;
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (this.npoints <= 0 || !this.getBoundingBox().intersects(n, n2, n3, n4)) {
            return false;
        }
        final Crossings crossings = this.getCrossings(n, n2, n + n3, n2 + n4);
        return crossings == null || !crossings.isEmpty();
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        if (this.npoints <= 0 || !this.getBoundingBox().intersects(n, n2, n3, n4)) {
            return false;
        }
        final Crossings crossings = this.getCrossings(n, n2, n + n3, n2 + n4);
        return crossings != null && crossings.covers(n2, n2 + n4);
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new PolygonPathIterator(this, affineTransform);
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform, final double n) {
        return this.getPathIterator(affineTransform);
    }
    
    class PolygonPathIterator implements PathIterator
    {
        Polygon poly;
        AffineTransform transform;
        int index;
        
        public PolygonPathIterator(final Polygon poly, final AffineTransform transform) {
            this.poly = poly;
            this.transform = transform;
            if (poly.npoints == 0) {
                this.index = 1;
            }
        }
        
        @Override
        public int getWindingRule() {
            return 0;
        }
        
        @Override
        public boolean isDone() {
            return this.index > this.poly.npoints;
        }
        
        @Override
        public void next() {
            ++this.index;
        }
        
        @Override
        public int currentSegment(final float[] array) {
            if (this.index >= this.poly.npoints) {
                return 4;
            }
            array[0] = (float)this.poly.xpoints[this.index];
            array[1] = (float)this.poly.ypoints[this.index];
            if (this.transform != null) {
                this.transform.transform(array, 0, array, 0, 1);
            }
            return (this.index != 0) ? 1 : 0;
        }
        
        @Override
        public int currentSegment(final double[] array) {
            if (this.index >= this.poly.npoints) {
                return 4;
            }
            array[0] = this.poly.xpoints[this.index];
            array[1] = this.poly.ypoints[this.index];
            if (this.transform != null) {
                this.transform.transform(array, 0, array, 0, 1);
            }
            return (this.index != 0) ? 1 : 0;
        }
    }
}
