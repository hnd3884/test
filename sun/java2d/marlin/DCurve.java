package sun.java2d.marlin;

final class DCurve
{
    double ax;
    double ay;
    double bx;
    double by;
    double cx;
    double cy;
    double dx;
    double dy;
    double dax;
    double day;
    double dbx;
    double dby;
    
    void set(final double[] array, final int n) {
        if (n == 8) {
            this.set(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
        }
        else if (n == 4) {
            this.set(array[0], array[1], array[2], array[3]);
        }
        else {
            this.set(array[0], array[1], array[2], array[3], array[4], array[5]);
        }
    }
    
    void set(final double dx, final double dy, final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        final double n7 = 3.0 * (n3 - n);
        final double n8 = 3.0 * (n4 - n2);
        final double cx = 3.0 * (n - dx);
        final double cy = 3.0 * (n2 - dy);
        this.ax = n5 - dx - n7;
        this.ay = n6 - dy - n8;
        this.bx = n7 - cx;
        this.by = n8 - cy;
        this.cx = cx;
        this.cy = cy;
        this.dx = dx;
        this.dy = dy;
        this.dax = 3.0 * this.ax;
        this.day = 3.0 * this.ay;
        this.dbx = 2.0 * this.bx;
        this.dby = 2.0 * this.by;
    }
    
    void set(final double dx, final double dy, final double n, final double n2, final double n3, final double n4) {
        final double n5 = n - dx;
        final double n6 = n2 - dy;
        this.ax = 0.0;
        this.ay = 0.0;
        this.bx = n3 - n - n5;
        this.by = n4 - n2 - n6;
        this.cx = 2.0 * n5;
        this.cy = 2.0 * n6;
        this.dx = dx;
        this.dy = dy;
        this.dax = 0.0;
        this.day = 0.0;
        this.dbx = 2.0 * this.bx;
        this.dby = 2.0 * this.by;
    }
    
    void set(final double dx, final double dy, final double n, final double n2) {
        final double cx = n - dx;
        final double cy = n2 - dy;
        this.ax = 0.0;
        this.ay = 0.0;
        this.bx = 0.0;
        this.by = 0.0;
        this.cx = cx;
        this.cy = cy;
        this.dx = dx;
        this.dy = dy;
        this.dax = 0.0;
        this.day = 0.0;
        this.dbx = 0.0;
        this.dby = 0.0;
    }
    
    int dxRoots(final double[] array, final int n) {
        return DHelpers.quadraticRoots(this.dax, this.dbx, this.cx, array, n);
    }
    
    int dyRoots(final double[] array, final int n) {
        return DHelpers.quadraticRoots(this.day, this.dby, this.cy, array, n);
    }
    
    int infPoints(final double[] array, final int n) {
        return DHelpers.quadraticRoots(this.dax * this.dby - this.dbx * this.day, 2.0 * (this.cy * this.dax - this.day * this.cx), this.cy * this.dbx - this.cx * this.dby, array, n);
    }
    
    int xPoints(final double[] array, final int n, final double n2) {
        return DHelpers.cubicRootsInAB(this.ax, this.bx, this.cx, this.dx - n2, array, n, 0.0, 1.0);
    }
    
    int yPoints(final double[] array, final int n, final double n2) {
        return DHelpers.cubicRootsInAB(this.ay, this.by, this.cy, this.dy - n2, array, n, 0.0, 1.0);
    }
    
    private int perpendiculardfddf(final double[] array, final int n) {
        assert array.length >= n + 4;
        return DHelpers.cubicRootsInAB(2.0 * (this.dax * this.dax + this.day * this.day), 3.0 * (this.dax * this.dbx + this.day * this.dby), 2.0 * (this.dax * this.cx + this.day * this.cy) + this.dbx * this.dbx + this.dby * this.dby, this.dbx * this.cx + this.dby * this.cy, array, n, 0.0, 1.0);
    }
    
    int rootsOfROCMinusW(final double[] array, final int n, final double n2, final double n3) {
        assert n <= 6 && array.length >= 10;
        int n4 = n;
        final int n5 = n + this.perpendiculardfddf(array, n);
        array[n5] = 1.0;
        double n6 = 0.0;
        double n7 = this.ROCsq(n6) - n2;
        for (int i = n; i <= n5; ++i) {
            final double n8 = array[i];
            final double n9 = this.ROCsq(n8) - n2;
            if (n7 == 0.0) {
                array[n4++] = n6;
            }
            else if (n9 * n7 < 0.0) {
                array[n4++] = this.falsePositionROCsqMinusX(n6, n8, n2, n3);
            }
            n6 = n8;
            n7 = n9;
        }
        return n4 - n;
    }
    
    private static double eliminateInf(final double n) {
        return (n == Double.POSITIVE_INFINITY) ? Double.MAX_VALUE : ((n == Double.NEGATIVE_INFINITY) ? Double.MIN_VALUE : n);
    }
    
    private double falsePositionROCsqMinusX(final double n, final double n2, final double n3, final double n4) {
        int n5 = 0;
        double n6 = n2;
        double eliminateInf = eliminateInf(this.ROCsq(n6) - n3);
        double n7 = n;
        double eliminateInf2 = eliminateInf(this.ROCsq(n7) - n3);
        double n8 = n7;
        for (int n9 = 0; n9 < 100 && Math.abs(n6 - n7) > n4 * Math.abs(n6 + n7); ++n9) {
            n8 = (eliminateInf2 * n6 - eliminateInf * n7) / (eliminateInf2 - eliminateInf);
            final double n10 = this.ROCsq(n8) - n3;
            if (sameSign(n10, eliminateInf)) {
                eliminateInf = n10;
                n6 = n8;
                if (n5 < 0) {
                    eliminateInf2 /= 1 << -n5;
                    --n5;
                }
                else {
                    n5 = -1;
                }
            }
            else {
                if (n10 * eliminateInf2 <= 0.0) {
                    break;
                }
                eliminateInf2 = n10;
                n7 = n8;
                if (n5 > 0) {
                    eliminateInf /= 1 << n5;
                    ++n5;
                }
                else {
                    n5 = 1;
                }
            }
        }
        return n8;
    }
    
    private static boolean sameSign(final double n, final double n2) {
        return (n < 0.0 && n2 < 0.0) || (n > 0.0 && n2 > 0.0);
    }
    
    private double ROCsq(final double n) {
        final double n2 = n * (n * this.dax + this.dbx) + this.cx;
        final double n3 = n * (n * this.day + this.dby) + this.cy;
        final double n4 = 2.0 * this.dax * n + this.dbx;
        final double n5 = 2.0 * this.day * n + this.dby;
        final double n6 = n2 * n2 + n3 * n3;
        final double n7 = n4 * n4 + n5 * n5;
        final double n8 = n4 * n2 + n5 * n3;
        return n6 * (n6 * n6 / (n6 * n7 - n8 * n8));
    }
}
