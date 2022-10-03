package sun.java2d.marlin;

final class Curve
{
    float ax;
    float ay;
    float bx;
    float by;
    float cx;
    float cy;
    float dx;
    float dy;
    float dax;
    float day;
    float dbx;
    float dby;
    
    void set(final float[] array, final int n) {
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
    
    void set(final float dx, final float dy, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = 3.0f * (n3 - n);
        final float n8 = 3.0f * (n4 - n2);
        final float cx = 3.0f * (n - dx);
        final float cy = 3.0f * (n2 - dy);
        this.ax = n5 - dx - n7;
        this.ay = n6 - dy - n8;
        this.bx = n7 - cx;
        this.by = n8 - cy;
        this.cx = cx;
        this.cy = cy;
        this.dx = dx;
        this.dy = dy;
        this.dax = 3.0f * this.ax;
        this.day = 3.0f * this.ay;
        this.dbx = 2.0f * this.bx;
        this.dby = 2.0f * this.by;
    }
    
    void set(final float dx, final float dy, final float n, final float n2, final float n3, final float n4) {
        final float n5 = n - dx;
        final float n6 = n2 - dy;
        this.ax = 0.0f;
        this.ay = 0.0f;
        this.bx = n3 - n - n5;
        this.by = n4 - n2 - n6;
        this.cx = 2.0f * n5;
        this.cy = 2.0f * n6;
        this.dx = dx;
        this.dy = dy;
        this.dax = 0.0f;
        this.day = 0.0f;
        this.dbx = 2.0f * this.bx;
        this.dby = 2.0f * this.by;
    }
    
    void set(final float dx, final float dy, final float n, final float n2) {
        final float cx = n - dx;
        final float cy = n2 - dy;
        this.ax = 0.0f;
        this.ay = 0.0f;
        this.bx = 0.0f;
        this.by = 0.0f;
        this.cx = cx;
        this.cy = cy;
        this.dx = dx;
        this.dy = dy;
        this.dax = 0.0f;
        this.day = 0.0f;
        this.dbx = 0.0f;
        this.dby = 0.0f;
    }
    
    int dxRoots(final float[] array, final int n) {
        return Helpers.quadraticRoots(this.dax, this.dbx, this.cx, array, n);
    }
    
    int dyRoots(final float[] array, final int n) {
        return Helpers.quadraticRoots(this.day, this.dby, this.cy, array, n);
    }
    
    int infPoints(final float[] array, final int n) {
        return Helpers.quadraticRoots(this.dax * this.dby - this.dbx * this.day, 2.0f * (this.cy * this.dax - this.day * this.cx), this.cy * this.dbx - this.cx * this.dby, array, n);
    }
    
    int xPoints(final float[] array, final int n, final float n2) {
        return Helpers.cubicRootsInAB(this.ax, this.bx, this.cx, this.dx - n2, array, n, 0.0f, 1.0f);
    }
    
    int yPoints(final float[] array, final int n, final float n2) {
        return Helpers.cubicRootsInAB(this.ay, this.by, this.cy, this.dy - n2, array, n, 0.0f, 1.0f);
    }
    
    private int perpendiculardfddf(final float[] array, final int n) {
        assert array.length >= n + 4;
        return Helpers.cubicRootsInAB(2.0f * (this.dax * this.dax + this.day * this.day), 3.0f * (this.dax * this.dbx + this.day * this.dby), 2.0f * (this.dax * this.cx + this.day * this.cy) + this.dbx * this.dbx + this.dby * this.dby, this.dbx * this.cx + this.dby * this.cy, array, n, 0.0f, 1.0f);
    }
    
    int rootsOfROCMinusW(final float[] array, final int n, final float n2, final float n3) {
        assert n <= 6 && array.length >= 10;
        int n4 = n;
        final int n5 = n + this.perpendiculardfddf(array, n);
        array[n5] = 1.0f;
        float n6 = 0.0f;
        float n7 = this.ROCsq(n6) - n2;
        for (int i = n; i <= n5; ++i) {
            final float n8 = array[i];
            final float n9 = this.ROCsq(n8) - n2;
            if (n7 == 0.0f) {
                array[n4++] = n6;
            }
            else if (n9 * n7 < 0.0f) {
                array[n4++] = this.falsePositionROCsqMinusX(n6, n8, n2, n3);
            }
            n6 = n8;
            n7 = n9;
        }
        return n4 - n;
    }
    
    private static float eliminateInf(final float n) {
        return (n == Float.POSITIVE_INFINITY) ? Float.MAX_VALUE : ((n == Float.NEGATIVE_INFINITY) ? Float.MIN_VALUE : n);
    }
    
    private float falsePositionROCsqMinusX(final float n, final float n2, final float n3, final float n4) {
        int n5 = 0;
        float n6 = n2;
        float eliminateInf = eliminateInf(this.ROCsq(n6) - n3);
        float n7 = n;
        float eliminateInf2 = eliminateInf(this.ROCsq(n7) - n3);
        float n8 = n7;
        for (int n9 = 0; n9 < 100 && Math.abs(n6 - n7) > n4 * Math.abs(n6 + n7); ++n9) {
            n8 = (eliminateInf2 * n6 - eliminateInf * n7) / (eliminateInf2 - eliminateInf);
            final float n10 = this.ROCsq(n8) - n3;
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
                if (n10 * eliminateInf2 <= 0.0f) {
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
    
    private static boolean sameSign(final float n, final float n2) {
        return (n < 0.0f && n2 < 0.0f) || (n > 0.0f && n2 > 0.0f);
    }
    
    private float ROCsq(final float n) {
        final float n2 = n * (n * this.dax + this.dbx) + this.cx;
        final float n3 = n * (n * this.day + this.dby) + this.cy;
        final float n4 = 2.0f * this.dax * n + this.dbx;
        final float n5 = 2.0f * this.day * n + this.dby;
        final float n6 = n2 * n2 + n3 * n3;
        final float n7 = n4 * n4 + n5 * n5;
        final float n8 = n4 * n2 + n5 * n3;
        return n6 * (n6 * n6 / (n6 * n7 - n8 * n8));
    }
}
