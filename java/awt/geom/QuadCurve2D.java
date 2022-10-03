package java.awt.geom;

import java.io.Serializable;
import java.awt.Rectangle;
import java.awt.Shape;

public abstract class QuadCurve2D implements Shape, Cloneable
{
    private static final int BELOW = -2;
    private static final int LOWEDGE = -1;
    private static final int INSIDE = 0;
    private static final int HIGHEDGE = 1;
    private static final int ABOVE = 2;
    
    protected QuadCurve2D() {
    }
    
    public abstract double getX1();
    
    public abstract double getY1();
    
    public abstract Point2D getP1();
    
    public abstract double getCtrlX();
    
    public abstract double getCtrlY();
    
    public abstract Point2D getCtrlPt();
    
    public abstract double getX2();
    
    public abstract double getY2();
    
    public abstract Point2D getP2();
    
    public abstract void setCurve(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5);
    
    public void setCurve(final double[] array, final int n) {
        this.setCurve(array[n + 0], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5]);
    }
    
    public void setCurve(final Point2D point2D, final Point2D point2D2, final Point2D point2D3) {
        this.setCurve(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY(), point2D3.getX(), point2D3.getY());
    }
    
    public void setCurve(final Point2D[] array, final int n) {
        this.setCurve(array[n + 0].getX(), array[n + 0].getY(), array[n + 1].getX(), array[n + 1].getY(), array[n + 2].getX(), array[n + 2].getY());
    }
    
    public void setCurve(final QuadCurve2D quadCurve2D) {
        this.setCurve(quadCurve2D.getX1(), quadCurve2D.getY1(), quadCurve2D.getCtrlX(), quadCurve2D.getCtrlY(), quadCurve2D.getX2(), quadCurve2D.getY2());
    }
    
    public static double getFlatnessSq(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return Line2D.ptSegDistSq(n, n2, n5, n6, n3, n4);
    }
    
    public static double getFlatness(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        return Line2D.ptSegDist(n, n2, n5, n6, n3, n4);
    }
    
    public static double getFlatnessSq(final double[] array, final int n) {
        return Line2D.ptSegDistSq(array[n + 0], array[n + 1], array[n + 4], array[n + 5], array[n + 2], array[n + 3]);
    }
    
    public static double getFlatness(final double[] array, final int n) {
        return Line2D.ptSegDist(array[n + 0], array[n + 1], array[n + 4], array[n + 5], array[n + 2], array[n + 3]);
    }
    
    public double getFlatnessSq() {
        return Line2D.ptSegDistSq(this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getCtrlX(), this.getCtrlY());
    }
    
    public double getFlatness() {
        return Line2D.ptSegDist(this.getX1(), this.getY1(), this.getX2(), this.getY2(), this.getCtrlX(), this.getCtrlY());
    }
    
    public void subdivide(final QuadCurve2D quadCurve2D, final QuadCurve2D quadCurve2D2) {
        subdivide(this, quadCurve2D, quadCurve2D2);
    }
    
    public static void subdivide(final QuadCurve2D quadCurve2D, final QuadCurve2D quadCurve2D2, final QuadCurve2D quadCurve2D3) {
        final double x1 = quadCurve2D.getX1();
        final double y1 = quadCurve2D.getY1();
        final double ctrlX = quadCurve2D.getCtrlX();
        final double ctrlY = quadCurve2D.getCtrlY();
        final double x2 = quadCurve2D.getX2();
        final double y2 = quadCurve2D.getY2();
        final double n = (x1 + ctrlX) / 2.0;
        final double n2 = (y1 + ctrlY) / 2.0;
        final double n3 = (x2 + ctrlX) / 2.0;
        final double n4 = (y2 + ctrlY) / 2.0;
        final double n5 = (n + n3) / 2.0;
        final double n6 = (n2 + n4) / 2.0;
        if (quadCurve2D2 != null) {
            quadCurve2D2.setCurve(x1, y1, n, n2, n5, n6);
        }
        if (quadCurve2D3 != null) {
            quadCurve2D3.setCurve(n5, n6, n3, n4, x2, y2);
        }
    }
    
    public static void subdivide(final double[] array, final int n, final double[] array2, final int n2, final double[] array3, final int n3) {
        final double n4 = array[n + 0];
        final double n5 = array[n + 1];
        final double n6 = array[n + 2];
        final double n7 = array[n + 3];
        final double n8 = array[n + 4];
        final double n9 = array[n + 5];
        if (array2 != null) {
            array2[n2 + 0] = n4;
            array2[n2 + 1] = n5;
        }
        if (array3 != null) {
            array3[n3 + 4] = n8;
            array3[n3 + 5] = n9;
        }
        final double n10 = (n4 + n6) / 2.0;
        final double n11 = (n5 + n7) / 2.0;
        final double n12 = (n8 + n6) / 2.0;
        final double n13 = (n9 + n7) / 2.0;
        final double n14 = (n10 + n12) / 2.0;
        final double n15 = (n11 + n13) / 2.0;
        if (array2 != null) {
            array2[n2 + 2] = n10;
            array2[n2 + 3] = n11;
            array2[n2 + 4] = n14;
            array2[n2 + 5] = n15;
        }
        if (array3 != null) {
            array3[n3 + 0] = n14;
            array3[n3 + 1] = n15;
            array3[n3 + 2] = n12;
            array3[n3 + 3] = n13;
        }
    }
    
    public static int solveQuadratic(final double[] array) {
        return solveQuadratic(array, array);
    }
    
    public static int solveQuadratic(final double[] array, final double[] array2) {
        final double n = array[2];
        final double n2 = array[1];
        final double n3 = array[0];
        int n4 = 0;
        if (n == 0.0) {
            if (n2 == 0.0) {
                return -1;
            }
            array2[n4++] = -n3 / n2;
        }
        else {
            final double n5 = n2 * n2 - 4.0 * n * n3;
            if (n5 < 0.0) {
                return 0;
            }
            double sqrt = Math.sqrt(n5);
            if (n2 < 0.0) {
                sqrt = -sqrt;
            }
            final double n6 = (n2 + sqrt) / -2.0;
            array2[n4++] = n6 / n;
            if (n6 != 0.0) {
                array2[n4++] = n3 / n6;
            }
        }
        return n4;
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        final double x1 = this.getX1();
        final double y1 = this.getY1();
        final double ctrlX = this.getCtrlX();
        final double ctrlY = this.getCtrlY();
        final double x2 = this.getX2();
        final double y2 = this.getY2();
        final double n3 = x1 - 2.0 * ctrlX + x2;
        final double n4 = y1 - 2.0 * ctrlY + y2;
        final double n5 = n - x1;
        final double n6 = n2 - y1;
        final double n7 = x2 - x1;
        final double n8 = y2 - y1;
        final double n9 = (n5 * n4 - n6 * n3) / (n7 * n4 - n8 * n3);
        if (n9 < 0.0 || n9 > 1.0 || n9 != n9) {
            return false;
        }
        final double n10 = n3 * n9 * n9 + 2.0 * (ctrlX - x1) * n9 + x1;
        final double n11 = n4 * n9 * n9 + 2.0 * (ctrlY - y1) * n9 + y1;
        final double n12 = n7 * n9 + x1;
        final double n13 = n8 * n9 + y1;
        return (n >= n10 && n < n12) || (n >= n12 && n < n10) || (n2 >= n11 && n2 < n13) || (n2 >= n13 && n2 < n11);
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    private static void fillEqn(final double[] array, final double n, final double n2, final double n3, final double n4) {
        array[0] = n2 - n;
        array[1] = n3 + n3 - n2 - n2;
        array[2] = n2 - n3 - n3 + n4;
    }
    
    private static int evalQuadratic(final double[] array, final int n, final boolean b, final boolean b2, final double[] array2, final double n2, final double n3, final double n4) {
        int n5 = 0;
        for (final double n6 : array) {
            Label_0129: {
                if (b) {
                    if (n6 < 0.0) {
                        break Label_0129;
                    }
                }
                else if (n6 <= 0.0) {
                    break Label_0129;
                }
                if (b2) {
                    if (n6 > 1.0) {
                        break Label_0129;
                    }
                }
                else if (n6 >= 1.0) {
                    break Label_0129;
                }
                if (array2 == null || array2[1] + 2.0 * array2[2] * n6 != 0.0) {
                    final double n7 = 1.0 - n6;
                    array[n5++] = n2 * n7 * n7 + 2.0 * n3 * n6 * n7 + n4 * n6 * n6;
                }
            }
        }
        return n5;
    }
    
    private static int getTag(final double n, final double n2, final double n3) {
        if (n <= n2) {
            return (n < n2) ? -2 : -1;
        }
        if (n >= n3) {
            return (n > n3) ? 2 : 1;
        }
        return 0;
    }
    
    private static boolean inwards(final int n, final int n2, final int n3) {
        switch (n) {
            default: {
                return false;
            }
            case -1: {
                return n2 >= 0 || n3 >= 0;
            }
            case 0: {
                return true;
            }
            case 1: {
                return n2 <= 0 || n3 <= 0;
            }
        }
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final double x1 = this.getX1();
        final double y1 = this.getY1();
        final int tag = getTag(x1, n, n + n3);
        final int tag2 = getTag(y1, n2, n2 + n4);
        if (tag == 0 && tag2 == 0) {
            return true;
        }
        final double x2 = this.getX2();
        final double y2 = this.getY2();
        final int tag3 = getTag(x2, n, n + n3);
        final int tag4 = getTag(y2, n2, n2 + n4);
        if (tag3 == 0 && tag4 == 0) {
            return true;
        }
        final double ctrlX = this.getCtrlX();
        final double ctrlY = this.getCtrlY();
        final int tag5 = getTag(ctrlX, n, n + n3);
        final int tag6 = getTag(ctrlY, n2, n2 + n4);
        if (tag < 0 && tag3 < 0 && tag5 < 0) {
            return false;
        }
        if (tag2 < 0 && tag4 < 0 && tag6 < 0) {
            return false;
        }
        if (tag > 0 && tag3 > 0 && tag5 > 0) {
            return false;
        }
        if (tag2 > 0 && tag4 > 0 && tag6 > 0) {
            return false;
        }
        if (inwards(tag, tag3, tag5) && inwards(tag2, tag4, tag6)) {
            return true;
        }
        if (inwards(tag3, tag, tag5) && inwards(tag4, tag2, tag6)) {
            return true;
        }
        final boolean b = tag * tag3 <= 0;
        final boolean b2 = tag2 * tag4 <= 0;
        if (tag == 0 && tag3 == 0 && b2) {
            return true;
        }
        if (tag2 == 0 && tag4 == 0 && b) {
            return true;
        }
        final double[] array = new double[3];
        final double[] array2 = new double[3];
        if (!b2) {
            fillEqn(array, (tag2 < 0) ? n2 : (n2 + n4), y1, ctrlY, y2);
            return solveQuadratic(array, array2) == 2 && evalQuadratic(array2, 2, true, true, null, x1, ctrlX, x2) == 2 && getTag(array2[0], n, n + n3) * getTag(array2[1], n, n + n3) <= 0;
        }
        if (!b) {
            fillEqn(array, (tag < 0) ? n : (n + n3), x1, ctrlX, x2);
            return solveQuadratic(array, array2) == 2 && evalQuadratic(array2, 2, true, true, null, y1, ctrlY, y2) == 2 && getTag(array2[0], n2, n2 + n4) * getTag(array2[1], n2, n2 + n4) <= 0;
        }
        final double n5 = x2 - x1;
        final double n6 = y2 - y1;
        final double n7 = y2 * x1 - x2 * y1;
        int tag7;
        if (tag2 == 0) {
            tag7 = tag;
        }
        else {
            tag7 = getTag((n7 + n5 * ((tag2 < 0) ? n2 : (n2 + n4))) / n6, n, n + n3);
        }
        int tag8;
        if (tag4 == 0) {
            tag8 = tag3;
        }
        else {
            tag8 = getTag((n7 + n5 * ((tag4 < 0) ? n2 : (n2 + n4))) / n6, n, n + n3);
        }
        if (tag7 * tag8 <= 0) {
            return true;
        }
        final int n8 = (tag7 * tag <= 0) ? tag2 : tag4;
        fillEqn(array, (tag8 < 0) ? n : (n + n3), x1, ctrlX, x2);
        evalQuadratic(array2, solveQuadratic(array, array2), true, true, null, y1, ctrlY, y2);
        return n8 * getTag(array2[0], n2, n2 + n4) <= 0;
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        return n3 > 0.0 && n4 > 0.0 && this.contains(n, n2) && this.contains(n + n3, n2) && this.contains(n + n3, n2 + n4) && this.contains(n, n2 + n4);
    }
    
    @Override
    public boolean contains(final Rectangle2D rectangle2D) {
        return this.contains(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public Rectangle getBounds() {
        return this.getBounds2D().getBounds();
    }
    
    @Override
    public PathIterator getPathIterator(final AffineTransform affineTransform) {
        return new QuadIterator(this, affineTransform);
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
    
    public static class Float extends QuadCurve2D implements Serializable
    {
        public float x1;
        public float y1;
        public float ctrlx;
        public float ctrly;
        public float x2;
        public float y2;
        private static final long serialVersionUID = -8511188402130719609L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.setCurve(n, n2, n3, n4, n5, n6);
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
        public double getCtrlX() {
            return this.ctrlx;
        }
        
        @Override
        public double getCtrlY() {
            return this.ctrly;
        }
        
        @Override
        public Point2D getCtrlPt() {
            return new Point2D.Float(this.ctrlx, this.ctrly);
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
        public void setCurve(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.x1 = (float)n;
            this.y1 = (float)n2;
            this.ctrlx = (float)n3;
            this.ctrly = (float)n4;
            this.x2 = (float)n5;
            this.y2 = (float)n6;
        }
        
        public void setCurve(final float x1, final float y1, final float ctrlx, final float ctrly, final float x2, final float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx = ctrlx;
            this.ctrly = ctrly;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            final float min = Math.min(Math.min(this.x1, this.x2), this.ctrlx);
            final float min2 = Math.min(Math.min(this.y1, this.y2), this.ctrly);
            return new Rectangle2D.Float(min, min2, Math.max(Math.max(this.x1, this.x2), this.ctrlx) - min, Math.max(Math.max(this.y1, this.y2), this.ctrly) - min2);
        }
    }
    
    public static class Double extends QuadCurve2D implements Serializable
    {
        public double x1;
        public double y1;
        public double ctrlx;
        public double ctrly;
        public double x2;
        public double y2;
        private static final long serialVersionUID = 4217149928428559721L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.setCurve(n, n2, n3, n4, n5, n6);
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
        public double getCtrlX() {
            return this.ctrlx;
        }
        
        @Override
        public double getCtrlY() {
            return this.ctrly;
        }
        
        @Override
        public Point2D getCtrlPt() {
            return new Point2D.Double(this.ctrlx, this.ctrly);
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
        public void setCurve(final double x1, final double y1, final double ctrlx, final double ctrly, final double x2, final double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx = ctrlx;
            this.ctrly = ctrly;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            final double min = Math.min(Math.min(this.x1, this.x2), this.ctrlx);
            final double min2 = Math.min(Math.min(this.y1, this.y2), this.ctrly);
            return new Rectangle2D.Double(min, min2, Math.max(Math.max(this.x1, this.x2), this.ctrlx) - min, Math.max(Math.max(this.y1, this.y2), this.ctrly) - min2);
        }
    }
}
