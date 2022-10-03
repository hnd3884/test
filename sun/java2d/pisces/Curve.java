package sun.java2d.pisces;

import java.util.Iterator;

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
        switch (n) {
            case 8: {
                this.set(array[0], array[1], array[2], array[3], array[4], array[5], array[6], array[7]);
                break;
            }
            case 6: {
                this.set(array[0], array[1], array[2], array[3], array[4], array[5]);
                break;
            }
            default: {
                throw new InternalError("Curves can only be cubic or quadratic");
            }
        }
    }
    
    void set(final float dx, final float dy, final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.ax = 3.0f * (n - n3) + n5 - dx;
        this.ay = 3.0f * (n2 - n4) + n6 - dy;
        this.bx = 3.0f * (dx - 2.0f * n + n3);
        this.by = 3.0f * (dy - 2.0f * n2 + n4);
        this.cx = 3.0f * (n - dx);
        this.cy = 3.0f * (n2 - dy);
        this.dx = dx;
        this.dy = dy;
        this.dax = 3.0f * this.ax;
        this.day = 3.0f * this.ay;
        this.dbx = 2.0f * this.bx;
        this.dby = 2.0f * this.by;
    }
    
    void set(final float dx, final float dy, final float n, final float n2, final float n3, final float n4) {
        final float n5 = 0.0f;
        this.ay = n5;
        this.ax = n5;
        this.bx = dx - 2.0f * n + n3;
        this.by = dy - 2.0f * n2 + n4;
        this.cx = 2.0f * (n - dx);
        this.cy = 2.0f * (n2 - dy);
        this.dx = dx;
        this.dy = dy;
        this.dax = 0.0f;
        this.day = 0.0f;
        this.dbx = 2.0f * this.bx;
        this.dby = 2.0f * this.by;
    }
    
    float xat(final float n) {
        return n * (n * (n * this.ax + this.bx) + this.cx) + this.dx;
    }
    
    float yat(final float n) {
        return n * (n * (n * this.ay + this.by) + this.cy) + this.dy;
    }
    
    float dxat(final float n) {
        return n * (n * this.dax + this.dbx) + this.cx;
    }
    
    float dyat(final float n) {
        return n * (n * this.day + this.dby) + this.cy;
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
    
    private int perpendiculardfddf(final float[] array, final int n) {
        assert array.length >= n + 4;
        return Helpers.cubicRootsInAB(2.0f * (this.dax * this.dax + this.day * this.day), 3.0f * (this.dax * this.dbx + this.day * this.dby), 2.0f * (this.dax * this.cx + this.day * this.cy) + this.dbx * this.dbx + this.dby * this.dby, this.dbx * this.cx + this.dby * this.cy, array, n, 0.0f, 1.0f);
    }
    
    int rootsOfROCMinusW(final float[] array, final int n, final float n2, final float n3) {
        assert n <= 6 && array.length >= 10;
        int n4 = n;
        int perpendiculardfddf = this.perpendiculardfddf(array, n);
        float n5 = 0.0f;
        float n6 = this.ROCsq(n5) - n2 * n2;
        array[n + perpendiculardfddf] = 1.0f;
        ++perpendiculardfddf;
        for (int i = n; i < n + perpendiculardfddf; ++i) {
            final float n7 = array[i];
            final float n8 = this.ROCsq(n7) - n2 * n2;
            if (n6 == 0.0f) {
                array[n4++] = n5;
            }
            else if (n8 * n6 < 0.0f) {
                array[n4++] = this.falsePositionROCsqMinusX(n5, n7, n2 * n2, n3);
            }
            n5 = n7;
            n6 = n8;
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
    
    private static boolean sameSign(final double n, final double n2) {
        return (n < 0.0 && n2 < 0.0) || (n > 0.0 && n2 > 0.0);
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
    
    static Iterator<Integer> breakPtsAtTs(final float[] array, final int n, final float[] array2, final int n2) {
        assert array.length >= 2 * n && n2 <= array2.length;
        return new Iterator<Integer>() {
            final Integer i0 = 0;
            final Integer itype = n;
            int nextCurveIdx = 0;
            Integer curCurveOff = this.i0;
            float prevT = 0.0f;
            
            @Override
            public boolean hasNext() {
                return this.nextCurveIdx < n2 + 1;
            }
            
            @Override
            public Integer next() {
                Integer n;
                if (this.nextCurveIdx < n2) {
                    final float prevT = array2[this.nextCurveIdx];
                    Helpers.subdivideAt((prevT - this.prevT) / (1.0f - this.prevT), array, this.curCurveOff, array, 0, array, n, n);
                    this.prevT = prevT;
                    n = this.i0;
                    this.curCurveOff = this.itype;
                }
                else {
                    n = this.curCurveOff;
                }
                ++this.nextCurveIdx;
                return n;
            }
            
            @Override
            public void remove() {
            }
        };
    }
}
