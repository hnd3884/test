package java.awt.geom;

import java.io.Serializable;
import java.awt.Rectangle;
import sun.awt.geom.Curve;
import java.util.Arrays;
import java.awt.Shape;

public abstract class CubicCurve2D implements Shape, Cloneable
{
    protected CubicCurve2D() {
    }
    
    public abstract double getX1();
    
    public abstract double getY1();
    
    public abstract Point2D getP1();
    
    public abstract double getCtrlX1();
    
    public abstract double getCtrlY1();
    
    public abstract Point2D getCtrlP1();
    
    public abstract double getCtrlX2();
    
    public abstract double getCtrlY2();
    
    public abstract Point2D getCtrlP2();
    
    public abstract double getX2();
    
    public abstract double getY2();
    
    public abstract Point2D getP2();
    
    public abstract void setCurve(final double p0, final double p1, final double p2, final double p3, final double p4, final double p5, final double p6, final double p7);
    
    public void setCurve(final double[] array, final int n) {
        this.setCurve(array[n + 0], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5], array[n + 6], array[n + 7]);
    }
    
    public void setCurve(final Point2D point2D, final Point2D point2D2, final Point2D point2D3, final Point2D point2D4) {
        this.setCurve(point2D.getX(), point2D.getY(), point2D2.getX(), point2D2.getY(), point2D3.getX(), point2D3.getY(), point2D4.getX(), point2D4.getY());
    }
    
    public void setCurve(final Point2D[] array, final int n) {
        this.setCurve(array[n + 0].getX(), array[n + 0].getY(), array[n + 1].getX(), array[n + 1].getY(), array[n + 2].getX(), array[n + 2].getY(), array[n + 3].getX(), array[n + 3].getY());
    }
    
    public void setCurve(final CubicCurve2D cubicCurve2D) {
        this.setCurve(cubicCurve2D.getX1(), cubicCurve2D.getY1(), cubicCurve2D.getCtrlX1(), cubicCurve2D.getCtrlY1(), cubicCurve2D.getCtrlX2(), cubicCurve2D.getCtrlY2(), cubicCurve2D.getX2(), cubicCurve2D.getY2());
    }
    
    public static double getFlatnessSq(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        return Math.max(Line2D.ptSegDistSq(n, n2, n7, n8, n3, n4), Line2D.ptSegDistSq(n, n2, n7, n8, n5, n6));
    }
    
    public static double getFlatness(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
        return Math.sqrt(getFlatnessSq(n, n2, n3, n4, n5, n6, n7, n8));
    }
    
    public static double getFlatnessSq(final double[] array, final int n) {
        return getFlatnessSq(array[n + 0], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5], array[n + 6], array[n + 7]);
    }
    
    public static double getFlatness(final double[] array, final int n) {
        return getFlatness(array[n + 0], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5], array[n + 6], array[n + 7]);
    }
    
    public double getFlatnessSq() {
        return getFlatnessSq(this.getX1(), this.getY1(), this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), this.getX2(), this.getY2());
    }
    
    public double getFlatness() {
        return getFlatness(this.getX1(), this.getY1(), this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), this.getX2(), this.getY2());
    }
    
    public void subdivide(final CubicCurve2D cubicCurve2D, final CubicCurve2D cubicCurve2D2) {
        subdivide(this, cubicCurve2D, cubicCurve2D2);
    }
    
    public static void subdivide(final CubicCurve2D cubicCurve2D, final CubicCurve2D cubicCurve2D2, final CubicCurve2D cubicCurve2D3) {
        final double x1 = cubicCurve2D.getX1();
        final double y1 = cubicCurve2D.getY1();
        final double ctrlX1 = cubicCurve2D.getCtrlX1();
        final double ctrlY1 = cubicCurve2D.getCtrlY1();
        final double ctrlX2 = cubicCurve2D.getCtrlX2();
        final double ctrlY2 = cubicCurve2D.getCtrlY2();
        final double x2 = cubicCurve2D.getX2();
        final double y2 = cubicCurve2D.getY2();
        final double n = (ctrlX1 + ctrlX2) / 2.0;
        final double n2 = (ctrlY1 + ctrlY2) / 2.0;
        final double n3 = (x1 + ctrlX1) / 2.0;
        final double n4 = (y1 + ctrlY1) / 2.0;
        final double n5 = (x2 + ctrlX2) / 2.0;
        final double n6 = (y2 + ctrlY2) / 2.0;
        final double n7 = (n3 + n) / 2.0;
        final double n8 = (n4 + n2) / 2.0;
        final double n9 = (n5 + n) / 2.0;
        final double n10 = (n6 + n2) / 2.0;
        final double n11 = (n7 + n9) / 2.0;
        final double n12 = (n8 + n10) / 2.0;
        if (cubicCurve2D2 != null) {
            cubicCurve2D2.setCurve(x1, y1, n3, n4, n7, n8, n11, n12);
        }
        if (cubicCurve2D3 != null) {
            cubicCurve2D3.setCurve(n11, n12, n9, n10, n5, n6, x2, y2);
        }
    }
    
    public static void subdivide(final double[] array, final int n, final double[] array2, final int n2, final double[] array3, final int n3) {
        final double n4 = array[n + 0];
        final double n5 = array[n + 1];
        final double n6 = array[n + 2];
        final double n7 = array[n + 3];
        final double n8 = array[n + 4];
        final double n9 = array[n + 5];
        final double n10 = array[n + 6];
        final double n11 = array[n + 7];
        if (array2 != null) {
            array2[n2 + 0] = n4;
            array2[n2 + 1] = n5;
        }
        if (array3 != null) {
            array3[n3 + 6] = n10;
            array3[n3 + 7] = n11;
        }
        final double n12 = (n4 + n6) / 2.0;
        final double n13 = (n5 + n7) / 2.0;
        final double n14 = (n10 + n8) / 2.0;
        final double n15 = (n11 + n9) / 2.0;
        final double n16 = (n6 + n8) / 2.0;
        final double n17 = (n7 + n9) / 2.0;
        final double n18 = (n12 + n16) / 2.0;
        final double n19 = (n13 + n17) / 2.0;
        final double n20 = (n14 + n16) / 2.0;
        final double n21 = (n15 + n17) / 2.0;
        final double n22 = (n18 + n20) / 2.0;
        final double n23 = (n19 + n21) / 2.0;
        if (array2 != null) {
            array2[n2 + 2] = n12;
            array2[n2 + 3] = n13;
            array2[n2 + 4] = n18;
            array2[n2 + 5] = n19;
            array2[n2 + 6] = n22;
            array2[n2 + 7] = n23;
        }
        if (array3 != null) {
            array3[n3 + 0] = n22;
            array3[n3 + 1] = n23;
            array3[n3 + 2] = n20;
            array3[n3 + 3] = n21;
            array3[n3 + 4] = n14;
            array3[n3 + 5] = n15;
        }
    }
    
    public static int solveCubic(final double[] array) {
        return solveCubic(array, array);
    }
    
    public static int solveCubic(double[] array, final double[] array2) {
        final double n = array[3];
        if (n == 0.0) {
            return QuadCurve2D.solveQuadratic(array, array2);
        }
        final double n2 = array[2] / n;
        final double n3 = array[1] / n;
        final double n4 = array[0] / n;
        final double n5 = n2 * n2;
        final double n6 = 0.3333333333333333 * (-0.3333333333333333 * n5 + n3);
        final double n7 = 0.5 * (0.07407407407407407 * n2 * n5 - 0.3333333333333333 * n2 * n3 + n4);
        final double n8 = n6 * n6 * n6;
        final double n9 = n7 * n7 + n8;
        final double n10 = 0.3333333333333333 * n2;
        int fixRoots;
        if (n9 < 0.0) {
            final double n11 = 0.3333333333333333 * Math.acos(-n7 / Math.sqrt(-n8));
            final double n12 = 2.0 * Math.sqrt(-n6);
            if (array2 == array) {
                array = Arrays.copyOf(array, 4);
            }
            array2[0] = n12 * Math.cos(n11);
            array2[1] = -n12 * Math.cos(n11 + 1.0471975511965976);
            array2[2] = -n12 * Math.cos(n11 - 1.0471975511965976);
            fixRoots = 3;
            for (int i = 0; i < fixRoots; ++i) {
                final int n13 = i;
                array2[n13] -= n10;
            }
        }
        else {
            final double sqrt = Math.sqrt(n9);
            final double cbrt = Math.cbrt(sqrt - n7);
            final double n14 = -Math.cbrt(sqrt + n7);
            final double n15 = cbrt + n14;
            fixRoots = 1;
            final double n16 = 1.2E9 * Math.ulp(Math.abs(n15) + Math.abs(n10));
            if (iszero(n9, n16) || within(cbrt, n14, n16)) {
                if (array2 == array) {
                    array = Arrays.copyOf(array, 4);
                }
                array2[1] = -(n15 / 2.0) - n10;
                fixRoots = 2;
            }
            array2[0] = n15 - n10;
        }
        if (fixRoots > 1) {
            fixRoots = fixRoots(array, array2, fixRoots);
        }
        if (fixRoots > 2 && (array2[2] == array2[1] || array2[2] == array2[0])) {
            --fixRoots;
        }
        if (fixRoots > 1 && array2[1] == array2[0]) {
            array2[1] = array2[--fixRoots];
        }
        return fixRoots;
    }
    
    private static int fixRoots(final double[] array, final double[] array2, final int n) {
        final double[] array3 = { array[1], 2.0 * array[2], 3.0 * array[3] };
        int solveQuadratic = QuadCurve2D.solveQuadratic(array3, array3);
        if (solveQuadratic == 2 && array3[0] == array3[1]) {
            --solveQuadratic;
        }
        if (solveQuadratic == 2 && array3[0] > array3[1]) {
            final double n2 = array3[0];
            array3[0] = array3[1];
            array3[1] = n2;
        }
        if (n == 3) {
            final double rootUpperBound = getRootUpperBound(array);
            final double n3 = -rootUpperBound;
            Arrays.sort(array2, 0, n);
            if (solveQuadratic == 2) {
                array2[0] = refineRootWithHint(array, n3, array3[0], array2[0]);
                array2[1] = refineRootWithHint(array, array3[0], array3[1], array2[1]);
                array2[2] = refineRootWithHint(array, array3[1], rootUpperBound, array2[2]);
                return 3;
            }
            if (solveQuadratic == 1) {
                final double n4 = array[3];
                final double n5 = -n4;
                final double n6 = array3[0];
                final double solveEqn = solveEqn(array, 3, n6);
                if (oppositeSigns(n5, solveEqn)) {
                    array2[0] = bisectRootWithHint(array, n3, n6, array2[0]);
                }
                else if (oppositeSigns(solveEqn, n4)) {
                    array2[0] = bisectRootWithHint(array, n6, rootUpperBound, array2[2]);
                }
                else {
                    array2[0] = n6;
                }
            }
            else if (solveQuadratic == 0) {
                array2[0] = bisectRootWithHint(array, n3, rootUpperBound, array2[1]);
            }
        }
        else if (n == 2 && solveQuadratic == 2) {
            final double n7 = array2[0];
            final double n8 = array2[1];
            final double n9 = array3[0];
            final double n10 = array3[1];
            final double n11 = (Math.abs(n9 - n7) > Math.abs(n10 - n7)) ? n9 : n10;
            final double solveEqn2 = solveEqn(array, 3, n11);
            if (iszero(solveEqn2, 1.0E7 * Math.ulp(n11))) {
                array2[1] = ((Math.abs(solveEqn(array, 3, n8)) < Math.abs(solveEqn2)) ? n8 : n11);
                return 2;
            }
        }
        return 1;
    }
    
    private static double refineRootWithHint(final double[] array, final double n, final double n2, double n3) {
        if (!inInterval(n3, n, n2)) {
            return n3;
        }
        final double[] array2 = { array[1], 2.0 * array[2], 3.0 * array[3] };
        final double n4 = n3;
        for (int i = 0; i < 3; ++i) {
            final double solveEqn = solveEqn(array2, 2, n3);
            final double solveEqn2 = solveEqn(array, 3, n3);
            final double n5 = n3 + -(solveEqn2 / solveEqn);
            if (solveEqn == 0.0 || solveEqn2 == 0.0) {
                break;
            }
            if (n3 == n5) {
                break;
            }
            n3 = n5;
        }
        if (within(n3, n4, 1000.0 * Math.ulp(n4)) && inInterval(n3, n, n2)) {
            return n3;
        }
        return n4;
    }
    
    private static double bisectRootWithHint(final double[] array, double n, double n2, final double n3) {
        double min = Math.min(Math.abs(n3 - n) / 64.0, 0.0625);
        double min2 = Math.min(Math.abs(n3 - n2) / 64.0, 0.0625);
        double n4;
        double n5;
        double n6;
        double n7;
        for (n4 = n3 - min, n5 = n3 + min2, n6 = solveEqn(array, 3, n4), n7 = solveEqn(array, 3, n5); oppositeSigns(n6, n7); n6 = solveEqn(array, 3, n4), n7 = solveEqn(array, 3, n5)) {
            if (n4 >= n5) {
                return n4;
            }
            n = n4;
            n2 = n5;
            min /= 64.0;
            min2 /= 64.0;
            n4 = n3 - min;
            n5 = n3 + min2;
        }
        if (n6 == 0.0) {
            return n4;
        }
        if (n7 == 0.0) {
            return n5;
        }
        return bisectRoot(array, n, n2);
    }
    
    private static double bisectRoot(final double[] array, double n, double n2) {
        double solveEqn = solveEqn(array, 3, n);
        double n3;
        for (n3 = n + (n2 - n) / 2.0; n3 != n && n3 != n2; n3 = n + (n2 - n) / 2.0) {
            final double solveEqn2 = solveEqn(array, 3, n3);
            if (solveEqn2 == 0.0) {
                return n3;
            }
            if (oppositeSigns(solveEqn, solveEqn2)) {
                n2 = n3;
            }
            else {
                solveEqn = solveEqn2;
                n = n3;
            }
        }
        return n3;
    }
    
    private static boolean inInterval(final double n, final double n2, final double n3) {
        return n2 <= n && n <= n3;
    }
    
    private static boolean within(final double n, final double n2, final double n3) {
        final double n4 = n2 - n;
        return n4 <= n3 && n4 >= -n3;
    }
    
    private static boolean iszero(final double n, final double n2) {
        return within(n, 0.0, n2);
    }
    
    private static boolean oppositeSigns(final double n, final double n2) {
        return (n < 0.0 && n2 > 0.0) || (n > 0.0 && n2 < 0.0);
    }
    
    private static double solveEqn(final double[] array, int n, final double n2) {
        double n3 = array[n];
        while (--n >= 0) {
            n3 = n3 * n2 + array[n];
        }
        return n3;
    }
    
    private static double getRootUpperBound(final double[] array) {
        final double n = 1.0 + Math.max(Math.max(Math.abs(array[2]), Math.abs(array[1])), Math.abs(array[0])) / Math.abs(array[3]);
        return n + (Math.ulp(n) + 1.0);
    }
    
    @Override
    public boolean contains(final double n, final double n2) {
        if (n * 0.0 + n2 * 0.0 != 0.0) {
            return false;
        }
        final double x1 = this.getX1();
        final double y1 = this.getY1();
        final double x2 = this.getX2();
        final double y2 = this.getY2();
        return (Curve.pointCrossingsForLine(n, n2, x1, y1, x2, y2) + Curve.pointCrossingsForCubic(n, n2, x1, y1, this.getCtrlX1(), this.getCtrlY1(), this.getCtrlX2(), this.getCtrlY2(), x2, y2, 0) & 0x1) == 0x1;
    }
    
    @Override
    public boolean contains(final Point2D point2D) {
        return this.contains(point2D.getX(), point2D.getY());
    }
    
    @Override
    public boolean intersects(final double n, final double n2, final double n3, final double n4) {
        return n3 > 0.0 && n4 > 0.0 && this.rectCrossings(n, n2, n3, n4) != 0;
    }
    
    @Override
    public boolean intersects(final Rectangle2D rectangle2D) {
        return this.intersects(rectangle2D.getX(), rectangle2D.getY(), rectangle2D.getWidth(), rectangle2D.getHeight());
    }
    
    @Override
    public boolean contains(final double n, final double n2, final double n3, final double n4) {
        if (n3 <= 0.0 || n4 <= 0.0) {
            return false;
        }
        final int rectCrossings = this.rectCrossings(n, n2, n3, n4);
        return rectCrossings != 0 && rectCrossings != Integer.MIN_VALUE;
    }
    
    private int rectCrossings(final double n, final double n2, final double n3, final double n4) {
        int rectCrossingsForLine = 0;
        if (this.getX1() != this.getX2() || this.getY1() != this.getY2()) {
            rectCrossingsForLine = Curve.rectCrossingsForLine(rectCrossingsForLine, n, n2, n + n3, n2 + n4, this.getX1(), this.getY1(), this.getX2(), this.getY2());
            if (rectCrossingsForLine == Integer.MIN_VALUE) {
                return rectCrossingsForLine;
            }
        }
        return Curve.rectCrossingsForCubic(rectCrossingsForLine, n, n2, n + n3, n2 + n4, this.getX2(), this.getY2(), this.getCtrlX2(), this.getCtrlY2(), this.getCtrlX1(), this.getCtrlY1(), this.getX1(), this.getY1(), 0);
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
        return new CubicIterator(this, affineTransform);
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
    
    public static class Float extends CubicCurve2D implements Serializable
    {
        public float x1;
        public float y1;
        public float ctrlx1;
        public float ctrly1;
        public float ctrlx2;
        public float ctrly2;
        public float x2;
        public float y2;
        private static final long serialVersionUID = -1272015596714244385L;
        
        public Float() {
        }
        
        public Float(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            this.setCurve(n, n2, n3, n4, n5, n6, n7, n8);
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
        public double getCtrlX1() {
            return this.ctrlx1;
        }
        
        @Override
        public double getCtrlY1() {
            return this.ctrly1;
        }
        
        @Override
        public Point2D getCtrlP1() {
            return new Point2D.Float(this.ctrlx1, this.ctrly1);
        }
        
        @Override
        public double getCtrlX2() {
            return this.ctrlx2;
        }
        
        @Override
        public double getCtrlY2() {
            return this.ctrly2;
        }
        
        @Override
        public Point2D getCtrlP2() {
            return new Point2D.Float(this.ctrlx2, this.ctrly2);
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
        public void setCurve(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
            this.x1 = (float)n;
            this.y1 = (float)n2;
            this.ctrlx1 = (float)n3;
            this.ctrly1 = (float)n4;
            this.ctrlx2 = (float)n5;
            this.ctrly2 = (float)n6;
            this.x2 = (float)n7;
            this.y2 = (float)n8;
        }
        
        public void setCurve(final float x1, final float y1, final float ctrlx1, final float ctrly1, final float ctrlx2, final float ctrly2, final float x2, final float y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx1 = ctrlx1;
            this.ctrly1 = ctrly1;
            this.ctrlx2 = ctrlx2;
            this.ctrly2 = ctrly2;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            final float min = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
            final float min2 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
            return new Rectangle2D.Float(min, min2, Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2)) - min, Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2)) - min2);
        }
    }
    
    public static class Double extends CubicCurve2D implements Serializable
    {
        public double x1;
        public double y1;
        public double ctrlx1;
        public double ctrly1;
        public double ctrlx2;
        public double ctrly2;
        public double x2;
        public double y2;
        private static final long serialVersionUID = -4202960122839707295L;
        
        public Double() {
        }
        
        public Double(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
            this.setCurve(n, n2, n3, n4, n5, n6, n7, n8);
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
        public double getCtrlX1() {
            return this.ctrlx1;
        }
        
        @Override
        public double getCtrlY1() {
            return this.ctrly1;
        }
        
        @Override
        public Point2D getCtrlP1() {
            return new Point2D.Double(this.ctrlx1, this.ctrly1);
        }
        
        @Override
        public double getCtrlX2() {
            return this.ctrlx2;
        }
        
        @Override
        public double getCtrlY2() {
            return this.ctrly2;
        }
        
        @Override
        public Point2D getCtrlP2() {
            return new Point2D.Double(this.ctrlx2, this.ctrly2);
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
        public void setCurve(final double x1, final double y1, final double ctrlx1, final double ctrly1, final double ctrlx2, final double ctrly2, final double x2, final double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.ctrlx1 = ctrlx1;
            this.ctrly1 = ctrly1;
            this.ctrlx2 = ctrlx2;
            this.ctrly2 = ctrly2;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        @Override
        public Rectangle2D getBounds2D() {
            final double min = Math.min(Math.min(this.x1, this.x2), Math.min(this.ctrlx1, this.ctrlx2));
            final double min2 = Math.min(Math.min(this.y1, this.y2), Math.min(this.ctrly1, this.ctrly2));
            return new Rectangle2D.Double(min, min2, Math.max(Math.max(this.x1, this.x2), Math.max(this.ctrlx1, this.ctrlx2)) - min, Math.max(Math.max(this.y1, this.y2), Math.max(this.ctrly1, this.ctrly2)) - min2);
        }
    }
}
