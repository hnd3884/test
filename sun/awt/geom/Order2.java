package sun.awt.geom;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

final class Order2 extends Curve
{
    private double x0;
    private double y0;
    private double cx0;
    private double cy0;
    private double x1;
    private double y1;
    private double xmin;
    private double xmax;
    private double xcoeff0;
    private double xcoeff1;
    private double xcoeff2;
    private double ycoeff0;
    private double ycoeff1;
    private double ycoeff2;
    
    public static void insert(final Vector vector, final double[] array, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final int n7) {
        if (getHorizontalParams(n2, n4, n6, array) == 0) {
            addInstance(vector, n, n2, n3, n4, n5, n6, n7);
            return;
        }
        final double n8 = array[0];
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
        array[4] = n5;
        array[5] = n6;
        split(array, 0, n8);
        final int n9 = (n7 == 1) ? 0 : 4;
        final int n10 = 4 - n9;
        addInstance(vector, array[n9], array[n9 + 1], array[n9 + 2], array[n9 + 3], array[n9 + 4], array[n9 + 5], n7);
        addInstance(vector, array[n10], array[n10 + 1], array[n10 + 2], array[n10 + 3], array[n10 + 4], array[n10 + 5], n7);
    }
    
    public static void addInstance(final Vector vector, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final int n7) {
        if (n2 > n6) {
            vector.add(new Order2(n5, n6, n3, n4, n, n2, -n7));
        }
        else if (n6 > n2) {
            vector.add(new Order2(n, n2, n3, n4, n5, n6, n7));
        }
    }
    
    public static int getHorizontalParams(double n, final double n2, double n3, final double[] array) {
        if (n <= n2 && n2 <= n3) {
            return 0;
        }
        n -= n2;
        n3 -= n2;
        final double n4 = n + n3;
        if (n4 == 0.0) {
            return 0;
        }
        final double n5 = n / n4;
        if (n5 <= 0.0 || n5 >= 1.0) {
            return 0;
        }
        array[0] = n5;
        return 1;
    }
    
    public static void split(final double[] array, final int n, final double n2) {
        final double n3 = array[n + 8] = array[n + 4];
        final double n4 = array[n + 9] = array[n + 5];
        final double n5 = array[n + 2];
        final double n6 = array[n + 3];
        final double n7 = n5 + (n3 - n5) * n2;
        final double n8 = n6 + (n4 - n6) * n2;
        final double n9 = array[n + 0];
        final double n10 = array[n + 1];
        final double n11 = n9 + (n5 - n9) * n2;
        final double n12 = n10 + (n6 - n10) * n2;
        final double n13 = n11 + (n7 - n11) * n2;
        final double n14 = n12 + (n8 - n12) * n2;
        array[n + 2] = n11;
        array[n + 3] = n12;
        array[n + 4] = n13;
        array[n + 5] = n14;
        array[n + 6] = n7;
        array[n + 7] = n8;
    }
    
    public Order2(final double n, final double n2, final double cx0, double cy0, final double x1, final double y1, final int n3) {
        super(n3);
        if (cy0 < n2) {
            cy0 = n2;
        }
        else if (cy0 > y1) {
            cy0 = y1;
        }
        this.x0 = n;
        this.y0 = n2;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.x1 = x1;
        this.y1 = y1;
        this.xmin = Math.min(Math.min(n, x1), cx0);
        this.xmax = Math.max(Math.max(n, x1), cx0);
        this.xcoeff0 = n;
        this.xcoeff1 = cx0 + cx0 - n - n;
        this.xcoeff2 = n - cx0 - cx0 + x1;
        this.ycoeff0 = n2;
        this.ycoeff1 = cy0 + cy0 - n2 - n2;
        this.ycoeff2 = n2 - cy0 - cy0 + y1;
    }
    
    @Override
    public int getOrder() {
        return 2;
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
        return this.cx0;
    }
    
    public double getCY0() {
        return this.cy0;
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
    public double TforY(final double n) {
        if (n <= this.y0) {
            return 0.0;
        }
        if (n >= this.y1) {
            return 1.0;
        }
        return TforY(n, this.ycoeff0, this.ycoeff1, this.ycoeff2);
    }
    
    public static double TforY(final double n, double n2, final double n3, final double n4) {
        n2 -= n;
        if (n4 == 0.0) {
            final double n5 = -n2 / n3;
            if (n5 >= 0.0 && n5 <= 1.0) {
                return n5;
            }
        }
        else {
            final double n6 = n3 * n3 - 4.0 * n4 * n2;
            if (n6 >= 0.0) {
                double sqrt = Math.sqrt(n6);
                if (n3 < 0.0) {
                    sqrt = -sqrt;
                }
                final double n7 = (n3 + sqrt) / -2.0;
                final double n8 = n7 / n4;
                if (n8 >= 0.0 && n8 <= 1.0) {
                    return n8;
                }
                if (n7 != 0.0) {
                    final double n9 = n2 / n7;
                    if (n9 >= 0.0 && n9 <= 1.0) {
                        return n9;
                    }
                }
            }
        }
        return (0.0 < (n2 + (n2 + n3 + n4)) / 2.0) ? 0.0 : 1.0;
    }
    
    @Override
    public double XforT(final double n) {
        return (this.xcoeff2 * n + this.xcoeff1) * n + this.xcoeff0;
    }
    
    @Override
    public double YforT(final double n) {
        return (this.ycoeff2 * n + this.ycoeff1) * n + this.ycoeff0;
    }
    
    @Override
    public double dXforT(final double n, final int n2) {
        switch (n2) {
            case 0: {
                return (this.xcoeff2 * n + this.xcoeff1) * n + this.xcoeff0;
            }
            case 1: {
                return 2.0 * this.xcoeff2 * n + this.xcoeff1;
            }
            case 2: {
                return 2.0 * this.xcoeff2;
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
                return (this.ycoeff2 * n + this.ycoeff1) * n + this.ycoeff0;
            }
            case 1: {
                return 2.0 * this.ycoeff2 * n + this.ycoeff1;
            }
            case 2: {
                return 2.0 * this.ycoeff2;
            }
            default: {
                return 0.0;
            }
        }
    }
    
    @Override
    public double nextVertical(final double n, final double n2) {
        final double n3 = -this.xcoeff1 / (2.0 * this.xcoeff2);
        if (n3 > n && n3 < n2) {
            return n3;
        }
        return n2;
    }
    
    @Override
    public void enlarge(final Rectangle2D rectangle2D) {
        rectangle2D.add(this.x0, this.y0);
        final double n = -this.xcoeff1 / (2.0 * this.xcoeff2);
        if (n > 0.0 && n < 1.0) {
            rectangle2D.add(this.XforT(n), this.YforT(n));
        }
        rectangle2D.add(this.x1, this.y1);
    }
    
    @Override
    public Curve getSubCurve(final double n, final double n2, final int n3) {
        double tforY;
        if (n <= this.y0) {
            if (n2 >= this.y1) {
                return this.getWithDirection(n3);
            }
            tforY = 0.0;
        }
        else {
            tforY = TforY(n, this.ycoeff0, this.ycoeff1, this.ycoeff2);
        }
        double tforY2;
        if (n2 >= this.y1) {
            tforY2 = 1.0;
        }
        else {
            tforY2 = TforY(n2, this.ycoeff0, this.ycoeff1, this.ycoeff2);
        }
        final double[] array = { this.x0, this.y0, this.cx0, this.cy0, this.x1, this.y1, 0.0, 0.0, 0.0, 0.0 };
        if (tforY2 < 1.0) {
            split(array, 0, tforY2);
        }
        int n4;
        if (tforY <= 0.0) {
            n4 = 0;
        }
        else {
            split(array, 0, tforY / tforY2);
            n4 = 4;
        }
        return new Order2(array[n4 + 0], n, array[n4 + 2], array[n4 + 3], array[n4 + 4], n2, n3);
    }
    
    @Override
    public Curve getReversedCurve() {
        return new Order2(this.x0, this.y0, this.cx0, this.cy0, this.x1, this.y1, -this.direction);
    }
    
    @Override
    public int getSegment(final double[] array) {
        array[0] = this.cx0;
        array[1] = this.cy0;
        if (this.direction == 1) {
            array[2] = this.x1;
            array[3] = this.y1;
        }
        else {
            array[2] = this.x0;
            array[3] = this.y0;
        }
        return 2;
    }
    
    @Override
    public String controlPointString() {
        return "(" + Curve.round(this.cx0) + ", " + Curve.round(this.cy0) + "), ";
    }
}
