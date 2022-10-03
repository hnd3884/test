package sun.awt.geom;

import java.awt.geom.Rectangle2D;
import java.awt.geom.QuadCurve2D;
import java.util.Vector;

final class Order3 extends Curve
{
    private double x0;
    private double y0;
    private double cx0;
    private double cy0;
    private double cx1;
    private double cy1;
    private double x1;
    private double y1;
    private double xmin;
    private double xmax;
    private double xcoeff0;
    private double xcoeff1;
    private double xcoeff2;
    private double xcoeff3;
    private double ycoeff0;
    private double ycoeff1;
    private double ycoeff2;
    private double ycoeff3;
    private double TforY1;
    private double YforT1;
    private double TforY2;
    private double YforT2;
    private double TforY3;
    private double YforT3;
    
    public static void insert(final Vector vector, final double[] array, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final int n9) {
        int i = getHorizontalParams(n2, n4, n6, n8, array);
        if (i == 0) {
            addInstance(vector, n, n2, n3, n4, n5, n6, n7, n8, n9);
            return;
        }
        array[3] = n;
        array[4] = n2;
        array[5] = n3;
        array[6] = n4;
        array[7] = n5;
        array[8] = n6;
        array[9] = n7;
        array[10] = n8;
        double n10 = array[0];
        if (i > 1 && n10 > array[1]) {
            array[0] = array[1];
            array[1] = n10;
            n10 = array[0];
        }
        split(array, 3, n10);
        if (i > 1) {
            split(array, 9, (array[1] - n10) / (1.0 - n10));
        }
        int n11 = 3;
        if (n9 == -1) {
            n11 += i * 6;
        }
        while (i >= 0) {
            addInstance(vector, array[n11 + 0], array[n11 + 1], array[n11 + 2], array[n11 + 3], array[n11 + 4], array[n11 + 5], array[n11 + 6], array[n11 + 7], n9);
            --i;
            if (n9 == 1) {
                n11 += 6;
            }
            else {
                n11 -= 6;
            }
        }
    }
    
    public static void addInstance(final Vector vector, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final int n9) {
        if (n2 > n8) {
            vector.add(new Order3(n7, n8, n5, n6, n3, n4, n, n2, -n9));
        }
        else if (n8 > n2) {
            vector.add(new Order3(n, n2, n3, n4, n5, n6, n7, n8, n9));
        }
    }
    
    public static int getHorizontalParams(final double n, double n2, double n3, double n4, final double[] array) {
        if (n <= n2 && n2 <= n3 && n3 <= n4) {
            return 0;
        }
        n4 -= n3;
        n3 -= n2;
        n2 -= n;
        array[1] = (n3 - (array[0] = n2)) * 2.0;
        array[2] = n4 - n3 - n3 + n2;
        final int solveQuadratic = QuadCurve2D.solveQuadratic(array, array);
        int n5 = 0;
        for (int i = 0; i < solveQuadratic; ++i) {
            final double n6 = array[i];
            if (n6 > 0.0 && n6 < 1.0) {
                if (n5 < i) {
                    array[n5] = n6;
                }
                ++n5;
            }
        }
        return n5;
    }
    
    public static void split(final double[] array, final int n, final double n2) {
        final double n3 = array[n + 12] = array[n + 6];
        final double n4 = array[n + 13] = array[n + 7];
        final double n5 = array[n + 4];
        final double n6 = array[n + 5];
        final double n7 = n5 + (n3 - n5) * n2;
        final double n8 = n6 + (n4 - n6) * n2;
        final double n9 = array[n + 0];
        final double n10 = array[n + 1];
        final double n11 = array[n + 2];
        final double n12 = array[n + 3];
        final double n13 = n9 + (n11 - n9) * n2;
        final double n14 = n10 + (n12 - n10) * n2;
        final double n15 = n11 + (n5 - n11) * n2;
        final double n16 = n12 + (n6 - n12) * n2;
        final double n17 = n15 + (n7 - n15) * n2;
        final double n18 = n16 + (n8 - n16) * n2;
        final double n19 = n13 + (n15 - n13) * n2;
        final double n20 = n14 + (n16 - n14) * n2;
        array[n + 2] = n13;
        array[n + 3] = n14;
        array[n + 4] = n19;
        array[n + 5] = n20;
        array[n + 6] = n19 + (n17 - n19) * n2;
        array[n + 7] = n20 + (n18 - n20) * n2;
        array[n + 8] = n17;
        array[n + 9] = n18;
        array[n + 10] = n7;
        array[n + 11] = n8;
    }
    
    public Order3(final double n, final double yforT1, final double cx0, double cy0, final double cx2, double cy2, final double x1, final double y1, final int n2) {
        super(n2);
        if (cy0 < yforT1) {
            cy0 = yforT1;
        }
        if (cy2 > y1) {
            cy2 = y1;
        }
        this.x0 = n;
        this.y0 = yforT1;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.cx1 = cx2;
        this.cy1 = cy2;
        this.x1 = x1;
        this.y1 = y1;
        this.xmin = Math.min(Math.min(n, x1), Math.min(cx0, cx2));
        this.xmax = Math.max(Math.max(n, x1), Math.max(cx0, cx2));
        this.xcoeff0 = n;
        this.xcoeff1 = (cx0 - n) * 3.0;
        this.xcoeff2 = (cx2 - cx0 - cx0 + n) * 3.0;
        this.xcoeff3 = x1 - (cx2 - cx0) * 3.0 - n;
        this.ycoeff0 = yforT1;
        this.ycoeff1 = (cy0 - yforT1) * 3.0;
        this.ycoeff2 = (cy2 - cy0 - cy0 + yforT1) * 3.0;
        this.ycoeff3 = y1 - (cy2 - cy0) * 3.0 - yforT1;
        this.YforT3 = yforT1;
        this.YforT2 = yforT1;
        this.YforT1 = yforT1;
    }
    
    @Override
    public int getOrder() {
        return 3;
    }
    
    @Override
    public double getXTop() {
        return this.x0;
    }
    
    @Override
    public double getYTop() {
        return this.y0;
    }
    
    @Override
    public double getXBot() {
        return this.x1;
    }
    
    @Override
    public double getYBot() {
        return this.y1;
    }
    
    @Override
    public double getXMin() {
        return this.xmin;
    }
    
    @Override
    public double getXMax() {
        return this.xmax;
    }
    
    @Override
    public double getX0() {
        return (this.direction == 1) ? this.x0 : this.x1;
    }
    
    @Override
    public double getY0() {
        return (this.direction == 1) ? this.y0 : this.y1;
    }
    
    public double getCX0() {
        return (this.direction == 1) ? this.cx0 : this.cx1;
    }
    
    public double getCY0() {
        return (this.direction == 1) ? this.cy0 : this.cy1;
    }
    
    public double getCX1() {
        return (this.direction == -1) ? this.cx0 : this.cx1;
    }
    
    public double getCY1() {
        return (this.direction == -1) ? this.cy0 : this.cy1;
    }
    
    @Override
    public double getX1() {
        return (this.direction == -1) ? this.x0 : this.x1;
    }
    
    @Override
    public double getY1() {
        return (this.direction == -1) ? this.y0 : this.y1;
    }
    
    @Override
    public double TforY(final double yforT1) {
        if (yforT1 <= this.y0) {
            return 0.0;
        }
        if (yforT1 >= this.y1) {
            return 1.0;
        }
        if (yforT1 == this.YforT1) {
            return this.TforY1;
        }
        if (yforT1 == this.YforT2) {
            return this.TforY2;
        }
        if (yforT1 == this.YforT3) {
            return this.TforY3;
        }
        if (this.ycoeff3 == 0.0) {
            return Order2.TforY(yforT1, this.ycoeff0, this.ycoeff1, this.ycoeff2);
        }
        final double n = this.ycoeff2 / this.ycoeff3;
        final double n2 = this.ycoeff1 / this.ycoeff3;
        final double n3 = (this.ycoeff0 - yforT1) / this.ycoeff3;
        final double n4 = (n * n - 3.0 * n2) / 9.0;
        double n5 = (2.0 * n * n * n - 9.0 * n * n2 + 27.0 * n3) / 54.0;
        final double n6 = n5 * n5;
        final double n7 = n4 * n4 * n4;
        final double n8 = n / 3.0;
        double tforY1;
        if (n6 < n7) {
            final double acos = Math.acos(n5 / Math.sqrt(n7));
            final double n9 = -2.0 * Math.sqrt(n4);
            tforY1 = this.refine(n, n2, n3, yforT1, n9 * Math.cos(acos / 3.0) - n8);
            if (tforY1 < 0.0) {
                tforY1 = this.refine(n, n2, n3, yforT1, n9 * Math.cos((acos + 6.283185307179586) / 3.0) - n8);
            }
            if (tforY1 < 0.0) {
                tforY1 = this.refine(n, n2, n3, yforT1, n9 * Math.cos((acos - 6.283185307179586) / 3.0) - n8);
            }
        }
        else {
            final boolean b = n5 < 0.0;
            final double sqrt = Math.sqrt(n6 - n7);
            if (b) {
                n5 = -n5;
            }
            double pow = Math.pow(n5 + sqrt, 0.3333333333333333);
            if (!b) {
                pow = -pow;
            }
            tforY1 = this.refine(n, n2, n3, yforT1, pow + ((pow == 0.0) ? 0.0 : (n4 / pow)) - n8);
        }
        if (tforY1 < 0.0) {
            double n10 = 0.0;
            double n11 = 1.0;
            while (true) {
                tforY1 = (n10 + n11) / 2.0;
                if (tforY1 == n10) {
                    break;
                }
                if (tforY1 == n11) {
                    break;
                }
                final double yforT2 = this.YforT(tforY1);
                if (yforT2 < yforT1) {
                    n10 = tforY1;
                }
                else {
                    if (yforT2 <= yforT1) {
                        break;
                    }
                    n11 = tforY1;
                }
            }
        }
        if (tforY1 >= 0.0) {
            this.TforY3 = this.TforY2;
            this.YforT3 = this.YforT2;
            this.TforY2 = this.TforY1;
            this.YforT2 = this.YforT1;
            this.TforY1 = tforY1;
            this.YforT1 = yforT1;
        }
        return tforY1;
    }
    
    public double refine(final double n, final double n2, final double n3, final double n4, double n5) {
        if (n5 < -0.1 || n5 > 1.1) {
            return -1.0;
        }
        double n6 = this.YforT(n5);
        double n7;
        double n8;
        if (n6 < n4) {
            n7 = n5;
            n8 = 1.0;
        }
        else {
            n7 = 0.0;
            n8 = n5;
        }
        int n9 = 1;
        while (n6 != n4) {
            if (n9 == 0) {
                final double n10 = (n7 + n8) / 2.0;
                if (n10 == n7) {
                    break;
                }
                if (n10 == n8) {
                    break;
                }
                n5 = n10;
            }
            else {
                final double dYforT = this.dYforT(n5, 1);
                if (dYforT == 0.0) {
                    n9 = 0;
                    continue;
                }
                final double n11 = n5 + (n4 - n6) / dYforT;
                if (n11 == n5 || n11 <= n7 || n11 >= n8) {
                    n9 = 0;
                    continue;
                }
                n5 = n11;
            }
            n6 = this.YforT(n5);
            if (n6 < n4) {
                n7 = n5;
            }
            else {
                if (n6 <= n4) {
                    break;
                }
                n8 = n5;
            }
        }
        return (n5 > 1.0) ? -1.0 : n5;
    }
    
    @Override
    public double XforY(final double n) {
        if (n <= this.y0) {
            return this.x0;
        }
        if (n >= this.y1) {
            return this.x1;
        }
        return this.XforT(this.TforY(n));
    }
    
    @Override
    public double XforT(final double n) {
        return ((this.xcoeff3 * n + this.xcoeff2) * n + this.xcoeff1) * n + this.xcoeff0;
    }
    
    @Override
    public double YforT(final double n) {
        return ((this.ycoeff3 * n + this.ycoeff2) * n + this.ycoeff1) * n + this.ycoeff0;
    }
    
    @Override
    public double dXforT(final double n, final int n2) {
        switch (n2) {
            case 0: {
                return ((this.xcoeff3 * n + this.xcoeff2) * n + this.xcoeff1) * n + this.xcoeff0;
            }
            case 1: {
                return (3.0 * this.xcoeff3 * n + 2.0 * this.xcoeff2) * n + this.xcoeff1;
            }
            case 2: {
                return 6.0 * this.xcoeff3 * n + 2.0 * this.xcoeff2;
            }
            case 3: {
                return 6.0 * this.xcoeff3;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    @Override
    public double dYforT(final double n, final int n2) {
        switch (n2) {
            case 0: {
                return ((this.ycoeff3 * n + this.ycoeff2) * n + this.ycoeff1) * n + this.ycoeff0;
            }
            case 1: {
                return (3.0 * this.ycoeff3 * n + 2.0 * this.ycoeff2) * n + this.ycoeff1;
            }
            case 2: {
                return 6.0 * this.ycoeff3 * n + 2.0 * this.ycoeff2;
            }
            case 3: {
                return 6.0 * this.ycoeff3;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    @Override
    public double nextVertical(final double n, double n2) {
        final double[] array = { this.xcoeff1, 2.0 * this.xcoeff2, 3.0 * this.xcoeff3 };
        for (int solveQuadratic = QuadCurve2D.solveQuadratic(array, array), i = 0; i < solveQuadratic; ++i) {
            if (array[i] > n && array[i] < n2) {
                n2 = array[i];
            }
        }
        return n2;
    }
    
    @Override
    public void enlarge(final Rectangle2D rectangle2D) {
        rectangle2D.add(this.x0, this.y0);
        final double[] array = { this.xcoeff1, 2.0 * this.xcoeff2, 3.0 * this.xcoeff3 };
        for (int solveQuadratic = QuadCurve2D.solveQuadratic(array, array), i = 0; i < solveQuadratic; ++i) {
            final double n = array[i];
            if (n > 0.0 && n < 1.0) {
                rectangle2D.add(this.XforT(n), this.YforT(n));
            }
        }
        rectangle2D.add(this.x1, this.y1);
    }
    
    @Override
    public Curve getSubCurve(final double n, final double n2, final int n3) {
        if (n <= this.y0 && n2 >= this.y1) {
            return this.getWithDirection(n3);
        }
        final double[] array = new double[14];
        double tforY = this.TforY(n);
        double tforY2 = this.TforY(n2);
        array[0] = this.x0;
        array[1] = this.y0;
        array[2] = this.cx0;
        array[3] = this.cy0;
        array[4] = this.cx1;
        array[5] = this.cy1;
        array[6] = this.x1;
        array[7] = this.y1;
        if (tforY > tforY2) {
            final double n4 = tforY;
            tforY = tforY2;
            tforY2 = n4;
        }
        if (tforY2 < 1.0) {
            split(array, 0, tforY2);
        }
        int n5;
        if (tforY <= 0.0) {
            n5 = 0;
        }
        else {
            split(array, 0, tforY / tforY2);
            n5 = 6;
        }
        return new Order3(array[n5 + 0], n, array[n5 + 2], array[n5 + 3], array[n5 + 4], array[n5 + 5], array[n5 + 6], n2, n3);
    }
    
    @Override
    public Curve getReversedCurve() {
        return new Order3(this.x0, this.y0, this.cx0, this.cy0, this.cx1, this.cy1, this.x1, this.y1, -this.direction);
    }
    
    @Override
    public int getSegment(final double[] array) {
        if (this.direction == 1) {
            array[0] = this.cx0;
            array[1] = this.cy0;
            array[2] = this.cx1;
            array[3] = this.cy1;
            array[4] = this.x1;
            array[5] = this.y1;
        }
        else {
            array[0] = this.cx1;
            array[1] = this.cy1;
            array[2] = this.cx0;
            array[3] = this.cy0;
            array[4] = this.x0;
            array[5] = this.y0;
        }
        return 3;
    }
    
    @Override
    public String controlPointString() {
        return "(" + Curve.round(this.getCX0()) + ", " + Curve.round(this.getCY0()) + "), " + "(" + Curve.round(this.getCX1()) + ", " + Curve.round(this.getCY1()) + "), ";
    }
}
