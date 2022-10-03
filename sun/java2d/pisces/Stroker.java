package sun.java2d.pisces;

import java.util.Arrays;
import java.util.Iterator;
import sun.awt.geom.PathConsumer2D;

final class Stroker implements PathConsumer2D
{
    private static final int MOVE_TO = 0;
    private static final int DRAWING_OP_TO = 1;
    private static final int CLOSE = 2;
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;
    private final PathConsumer2D out;
    private final int capStyle;
    private final int joinStyle;
    private final float lineWidth2;
    private final float[][] offset;
    private final float[] miter;
    private final float miterLimitSq;
    private int prev;
    private float sx0;
    private float sy0;
    private float sdx;
    private float sdy;
    private float cx0;
    private float cy0;
    private float cdx;
    private float cdy;
    private float smx;
    private float smy;
    private float cmx;
    private float cmy;
    private final PolyStack reverse;
    private static final float ROUND_JOIN_THRESHOLD = 0.015258789f;
    private float[] middle;
    private float[] lp;
    private float[] rp;
    private static final int MAX_N_CURVES = 11;
    private float[] subdivTs;
    private static Curve c;
    
    public Stroker(final PathConsumer2D out, final float n, final int capStyle, final int joinStyle, final float n2) {
        this.offset = new float[3][2];
        this.miter = new float[2];
        this.reverse = new PolyStack();
        this.middle = new float[16];
        this.lp = new float[8];
        this.rp = new float[8];
        this.subdivTs = new float[10];
        this.out = out;
        this.lineWidth2 = n / 2.0f;
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        final float n3 = n2 * this.lineWidth2;
        this.miterLimitSq = n3 * n3;
        this.prev = 2;
    }
    
    private static void computeOffset(final float n, final float n2, final float n3, final float[] array) {
        final float n4 = (float)Math.sqrt(n * n + n2 * n2);
        if (n4 == 0.0f) {
            array[0] = (array[1] = 0.0f);
        }
        else {
            array[0] = n2 * n3 / n4;
            array[1] = -(n * n3) / n4;
        }
    }
    
    private static boolean isCW(final float n, final float n2, final float n3, final float n4) {
        return n * n4 <= n2 * n3;
    }
    
    private void drawRoundJoin(final float n, final float n2, float n3, float n4, float n5, float n6, final boolean b, final float n7) {
        if ((n3 == 0.0f && n4 == 0.0f) || (n5 == 0.0f && n6 == 0.0f)) {
            return;
        }
        final float n8 = n3 - n5;
        final float n9 = n4 - n6;
        if (n8 * n8 + n9 * n9 < n7) {
            return;
        }
        if (b) {
            n3 = -n3;
            n4 = -n4;
            n5 = -n5;
            n6 = -n6;
        }
        this.drawRoundJoin(n, n2, n3, n4, n5, n6, b);
    }
    
    private void drawRoundJoin(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        switch ((n3 * n5 + n4 * n6 >= 0.0f) ? 1 : 2) {
            case 1: {
                this.drawBezApproxForArc(n, n2, n3, n4, n5, n6, b);
                break;
            }
            case 2: {
                final float n7 = n6 - n4;
                final float n8 = n3 - n5;
                final float n9 = this.lineWidth2 / (float)Math.sqrt(n7 * n7 + n8 * n8);
                float n10 = n7 * n9;
                float n11 = n8 * n9;
                if (b) {
                    n10 = -n10;
                    n11 = -n11;
                }
                this.drawBezApproxForArc(n, n2, n3, n4, n10, n11, b);
                this.drawBezApproxForArc(n, n2, n10, n11, n5, n6, b);
                break;
            }
        }
    }
    
    private void drawBezApproxForArc(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        final float n7 = (n3 * n5 + n4 * n6) / (2.0f * this.lineWidth2 * this.lineWidth2);
        if (n7 >= 0.5f) {
            return;
        }
        float n8 = (float)(1.3333333333333333 * Math.sqrt(0.5 - n7) / (1.0 + Math.sqrt(n7 + 0.5)));
        if (b) {
            n8 = -n8;
        }
        final float n9 = n + n3;
        final float n10 = n2 + n4;
        final float n11 = n9 - n8 * n4;
        final float n12 = n10 + n8 * n3;
        final float n13 = n + n5;
        final float n14 = n2 + n6;
        this.emitCurveTo(n9, n10, n11, n12, n13 + n8 * n6, n14 - n8 * n5, n13, n14, b);
    }
    
    private void drawRoundCap(final float n, final float n2, final float n3, final float n4) {
        this.emitCurveTo(n + n3, n2 + n4, n + n3 - 0.5522848f * n4, n2 + n4 + 0.5522848f * n3, n - n4 + 0.5522848f * n3, n2 + n3 + 0.5522848f * n4, n - n4, n2 + n3, false);
        this.emitCurveTo(n - n4, n2 + n3, n - n4 - 0.5522848f * n3, n2 + n3 - 0.5522848f * n4, n - n3 - 0.5522848f * n4, n2 - n4 + 0.5522848f * n3, n - n3, n2 - n4, false);
    }
    
    private void computeIntersection(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float[] array, int n9) {
        final float n10 = n3 - n;
        final float n11 = n4 - n2;
        final float n12 = n7 - n5;
        final float n13 = n8 - n6;
        final float n14 = (n12 * (n2 - n6) - n13 * (n - n5)) / (n10 * n13 - n12 * n11);
        array[n9++] = n + n14 * n10;
        array[n9] = n2 + n14 * n11;
    }
    
    private void drawMiter(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, float n7, float n8, float n9, float n10, final boolean b) {
        if ((n9 == n7 && n10 == n8) || (n == 0.0f && n2 == 0.0f) || (n5 == 0.0f && n6 == 0.0f)) {
            return;
        }
        if (b) {
            n7 = -n7;
            n8 = -n8;
            n9 = -n9;
            n10 = -n10;
        }
        this.computeIntersection(n3 - n + n7, n4 - n2 + n8, n3 + n7, n4 + n8, n5 + n3 + n9, n6 + n4 + n10, n3 + n9, n4 + n10, this.miter, 0);
        if ((this.miter[0] - n3) * (this.miter[0] - n3) + (this.miter[1] - n4) * (this.miter[1] - n4) < this.miterLimitSq) {
            this.emitLineTo(this.miter[0], this.miter[1], b);
        }
    }
    
    @Override
    public void moveTo(final float n, final float n2) {
        if (this.prev == 1) {
            this.finish();
        }
        this.cx0 = n;
        this.sx0 = n;
        this.cy0 = n2;
        this.sy0 = n2;
        final float n3 = 1.0f;
        this.sdx = n3;
        this.cdx = n3;
        final float n4 = 0.0f;
        this.sdy = n4;
        this.cdy = n4;
        this.prev = 0;
    }
    
    @Override
    public void lineTo(final float cx0, final float cy0) {
        float cdx = cx0 - this.cx0;
        final float cdy = cy0 - this.cy0;
        if (cdx == 0.0f && cdy == 0.0f) {
            cdx = 1.0f;
        }
        computeOffset(cdx, cdy, this.lineWidth2, this.offset[0]);
        final float cmx = this.offset[0][0];
        final float cmy = this.offset[0][1];
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, cdx, cdy, this.cmx, this.cmy, cmx, cmy);
        this.emitLineTo(this.cx0 + cmx, this.cy0 + cmy);
        this.emitLineTo(cx0 + cmx, cy0 + cmy);
        this.emitLineTo(this.cx0 - cmx, this.cy0 - cmy, true);
        this.emitLineTo(cx0 - cmx, cy0 - cmy, true);
        this.cmx = cmx;
        this.cmy = cmy;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.prev = 1;
    }
    
    @Override
    public void closePath() {
        if (this.prev == 1) {
            if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
                this.lineTo(this.sx0, this.sy0);
            }
            this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, this.sdx, this.sdy, this.cmx, this.cmy, this.smx, this.smy);
            this.emitLineTo(this.sx0 + this.smx, this.sy0 + this.smy);
            this.emitMoveTo(this.sx0 - this.smx, this.sy0 - this.smy);
            this.emitReverse();
            this.prev = 2;
            this.emitClose();
            return;
        }
        if (this.prev == 2) {
            return;
        }
        this.emitMoveTo(this.cx0, this.cy0 - this.lineWidth2);
        final float n = 0.0f;
        this.smx = n;
        this.cmx = n;
        final float n2 = -this.lineWidth2;
        this.smy = n2;
        this.cmy = n2;
        final float n3 = 1.0f;
        this.sdx = n3;
        this.cdx = n3;
        final float n4 = 0.0f;
        this.sdy = n4;
        this.cdy = n4;
        this.finish();
    }
    
    private void emitReverse() {
        while (!this.reverse.isEmpty()) {
            this.reverse.pop(this.out);
        }
    }
    
    @Override
    public void pathDone() {
        if (this.prev == 1) {
            this.finish();
        }
        this.out.pathDone();
        this.prev = 2;
    }
    
    private void finish() {
        if (this.capStyle == 1) {
            this.drawRoundCap(this.cx0, this.cy0, this.cmx, this.cmy);
        }
        else if (this.capStyle == 2) {
            this.emitLineTo(this.cx0 - this.cmy + this.cmx, this.cy0 + this.cmx + this.cmy);
            this.emitLineTo(this.cx0 - this.cmy - this.cmx, this.cy0 + this.cmx - this.cmy);
        }
        this.emitReverse();
        if (this.capStyle == 1) {
            this.drawRoundCap(this.sx0, this.sy0, -this.smx, -this.smy);
        }
        else if (this.capStyle == 2) {
            this.emitLineTo(this.sx0 + this.smy - this.smx, this.sy0 - this.smx - this.smy);
            this.emitLineTo(this.sx0 + this.smy + this.smx, this.sy0 - this.smx + this.smy);
        }
        this.emitClose();
    }
    
    private void emitMoveTo(final float n, final float n2) {
        this.out.moveTo(n, n2);
    }
    
    private void emitLineTo(final float n, final float n2) {
        this.out.lineTo(n, n2);
    }
    
    private void emitLineTo(final float n, final float n2, final boolean b) {
        if (b) {
            this.reverse.pushLine(n, n2);
        }
        else {
            this.emitLineTo(n, n2);
        }
    }
    
    private void emitQuadTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        if (b) {
            this.reverse.pushQuad(n, n2, n3, n4);
        }
        else {
            this.out.quadTo(n3, n4, n5, n6);
        }
    }
    
    private void emitCurveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final boolean b) {
        if (b) {
            this.reverse.pushCubic(n, n2, n3, n4, n5, n6);
        }
        else {
            this.out.curveTo(n3, n4, n5, n6, n7, n8);
        }
    }
    
    private void emitClose() {
        this.out.closePath();
    }
    
    private void drawJoin(final float n, final float n2, final float n3, final float n4, final float sdx, final float sdy, final float n5, final float n6, final float smx, final float smy) {
        if (this.prev != 1) {
            this.emitMoveTo(n3 + smx, n4 + smy);
            this.sdx = sdx;
            this.sdy = sdy;
            this.smx = smx;
            this.smy = smy;
        }
        else {
            final boolean cw = isCW(n, n2, sdx, sdy);
            if (this.joinStyle == 0) {
                this.drawMiter(n, n2, n3, n4, sdx, sdy, n5, n6, smx, smy, cw);
            }
            else if (this.joinStyle == 1) {
                this.drawRoundJoin(n3, n4, n5, n6, smx, smy, cw, 0.015258789f);
            }
            this.emitLineTo(n3, n4, !cw);
        }
        this.prev = 1;
    }
    
    private static boolean within(final float n, final float n2, final float n3, final float n4, final float n5) {
        assert n5 > 0.0f : "";
        return Helpers.within(n, n3, n5) && Helpers.within(n2, n4, n5);
    }
    
    private void getLineOffsets(final float n, final float n2, final float n3, final float n4, final float[] array, final float[] array2) {
        computeOffset(n3 - n, n4 - n2, this.lineWidth2, this.offset[0]);
        array[0] = n + this.offset[0][0];
        array[1] = n2 + this.offset[0][1];
        array[2] = n3 + this.offset[0][0];
        array[3] = n4 + this.offset[0][1];
        array2[0] = n - this.offset[0][0];
        array2[1] = n2 - this.offset[0][1];
        array2[2] = n3 - this.offset[0][0];
        array2[3] = n4 - this.offset[0][1];
    }
    
    private int computeOffsetCubic(final float[] array, final int n, final float[] array2, final float[] array3) {
        final float n2 = array[n + 0];
        final float n3 = array[n + 1];
        final float n4 = array[n + 2];
        final float n5 = array[n + 3];
        final float n6 = array[n + 4];
        final float n7 = array[n + 5];
        final float n8 = array[n + 6];
        final float n9 = array[n + 7];
        float n10 = n8 - n6;
        float n11 = n9 - n7;
        float n12 = n4 - n2;
        float n13 = n5 - n3;
        final boolean within = within(n2, n3, n4, n5, 6.0f * Math.ulp(n5));
        final boolean within2 = within(n6, n7, n8, n9, 6.0f * Math.ulp(n9));
        if (within && within2) {
            this.getLineOffsets(n2, n3, n8, n9, array2, array3);
            return 4;
        }
        if (within) {
            n12 = n6 - n2;
            n13 = n7 - n3;
        }
        else if (within2) {
            n10 = n8 - n4;
            n11 = n9 - n5;
        }
        final float n14 = n12 * n10 + n13 * n11;
        final float n15 = n14 * n14;
        if (Helpers.within(n15, (n12 * n12 + n13 * n13) * (n10 * n10 + n11 * n11), 4.0f * Math.ulp(n15))) {
            this.getLineOffsets(n2, n3, n8, n9, array2, array3);
            return 4;
        }
        final float n16 = 0.125f * (n2 + 3.0f * (n4 + n6) + n8);
        final float n17 = 0.125f * (n3 + 3.0f * (n5 + n7) + n9);
        final float n18 = n6 + n8 - n2 - n4;
        final float n19 = n7 + n9 - n3 - n5;
        computeOffset(n12, n13, this.lineWidth2, this.offset[0]);
        computeOffset(n18, n19, this.lineWidth2, this.offset[1]);
        computeOffset(n10, n11, this.lineWidth2, this.offset[2]);
        final float n20 = n2 + this.offset[0][0];
        final float n21 = n3 + this.offset[0][1];
        final float n22 = n16 + this.offset[1][0];
        final float n23 = n17 + this.offset[1][1];
        final float n24 = n8 + this.offset[2][0];
        final float n25 = n9 + this.offset[2][1];
        final float n26 = 4.0f / (3.0f * (n12 * n11 - n13 * n10));
        final float n27 = 2.0f * n22 - n20 - n24;
        final float n28 = 2.0f * n23 - n21 - n25;
        final float n29 = n26 * (n11 * n27 - n10 * n28);
        final float n30 = n26 * (n12 * n28 - n13 * n27);
        final float n31 = n20 + n29 * n12;
        final float n32 = n21 + n29 * n13;
        final float n33 = n24 + n30 * n10;
        final float n34 = n25 + n30 * n11;
        array2[0] = n20;
        array2[1] = n21;
        array2[2] = n31;
        array2[3] = n32;
        array2[4] = n33;
        array2[5] = n34;
        array2[6] = n24;
        array2[7] = n25;
        final float n35 = n2 - this.offset[0][0];
        final float n36 = n3 - this.offset[0][1];
        final float n37 = n22 - 2.0f * this.offset[1][0];
        final float n38 = n23 - 2.0f * this.offset[1][1];
        final float n39 = n8 - this.offset[2][0];
        final float n40 = n9 - this.offset[2][1];
        final float n41 = 2.0f * n37 - n35 - n39;
        final float n42 = 2.0f * n38 - n36 - n40;
        final float n43 = n26 * (n11 * n41 - n10 * n42);
        final float n44 = n26 * (n12 * n42 - n13 * n41);
        final float n45 = n35 + n43 * n12;
        final float n46 = n36 + n43 * n13;
        final float n47 = n39 + n44 * n10;
        final float n48 = n40 + n44 * n11;
        array3[0] = n35;
        array3[1] = n36;
        array3[2] = n45;
        array3[3] = n46;
        array3[4] = n47;
        array3[5] = n48;
        array3[6] = n39;
        array3[7] = n40;
        return 8;
    }
    
    private int computeOffsetQuad(final float[] array, final int n, final float[] array2, final float[] array3) {
        final float n2 = array[n + 0];
        final float n3 = array[n + 1];
        final float n4 = array[n + 2];
        final float n5 = array[n + 3];
        final float n6 = array[n + 4];
        final float n7 = array[n + 5];
        final float n8 = n6 - n4;
        final float n9 = n7 - n5;
        final float n10 = n4 - n2;
        final float n11 = n5 - n3;
        computeOffset(n10, n11, this.lineWidth2, this.offset[0]);
        computeOffset(n8, n9, this.lineWidth2, this.offset[1]);
        array2[0] = n2 + this.offset[0][0];
        array2[1] = n3 + this.offset[0][1];
        array2[4] = n6 + this.offset[1][0];
        array2[5] = n7 + this.offset[1][1];
        array3[0] = n2 - this.offset[0][0];
        array3[1] = n3 - this.offset[0][1];
        array3[4] = n6 - this.offset[1][0];
        array3[5] = n7 - this.offset[1][1];
        final float n12 = array2[0];
        final float n13 = array2[1];
        final float n14 = array2[4];
        final float n15 = array2[5];
        this.computeIntersection(n12, n13, n12 + n10, n13 + n11, n14, n15, n14 - n8, n15 - n9, array2, 2);
        final float n16 = array2[2];
        final float n17 = array2[3];
        if (isFinite(n16) && isFinite(n17)) {
            array3[2] = 2.0f * n4 - n16;
            array3[3] = 2.0f * n5 - n17;
            return 6;
        }
        final float n18 = array3[0];
        final float n19 = array3[1];
        final float n20 = array3[4];
        final float n21 = array3[5];
        this.computeIntersection(n18, n19, n18 + n10, n19 + n11, n20, n21, n20 - n8, n21 - n9, array3, 2);
        final float n22 = array3[2];
        final float n23 = array3[3];
        if (!isFinite(n22) || !isFinite(n23)) {
            this.getLineOffsets(n2, n3, n6, n7, array2, array3);
            return 4;
        }
        array2[2] = 2.0f * n4 - n22;
        array2[3] = 2.0f * n5 - n23;
        return 6;
    }
    
    private static boolean isFinite(final float n) {
        return Float.NEGATIVE_INFINITY < n && n < Float.POSITIVE_INFINITY;
    }
    
    private static int findSubdivPoints(final float[] array, final float[] array2, final int n, final float n2) {
        final float n3 = array[2] - array[0];
        final float n4 = array[3] - array[1];
        if (n4 != 0.0f && n3 != 0.0f) {
            final float n5 = (float)Math.sqrt(n3 * n3 + n4 * n4);
            final float n6 = n3 / n5;
            final float n7 = n4 / n5;
            final float n8 = n6 * array[0] + n7 * array[1];
            final float n9 = n6 * array[1] - n7 * array[0];
            final float n10 = n6 * array[2] + n7 * array[3];
            final float n11 = n6 * array[3] - n7 * array[2];
            final float n12 = n6 * array[4] + n7 * array[5];
            final float n13 = n6 * array[5] - n7 * array[4];
            switch (n) {
                case 8: {
                    Stroker.c.set(n8, n9, n10, n11, n12, n13, n6 * array[6] + n7 * array[7], n6 * array[7] - n7 * array[6]);
                    break;
                }
                case 6: {
                    Stroker.c.set(n8, n9, n10, n11, n12, n13);
                    break;
                }
            }
        }
        else {
            Stroker.c.set(array, n);
        }
        final int n14 = 0;
        final int n15 = n14 + Stroker.c.dxRoots(array2, n14);
        int n16 = n15 + Stroker.c.dyRoots(array2, n15);
        if (n == 8) {
            n16 += Stroker.c.infPoints(array2, n16);
        }
        final int filterOutNotInAB = Helpers.filterOutNotInAB(array2, 0, n16 + Stroker.c.rootsOfROCMinusW(array2, n16, n2, 1.0E-4f), 1.0E-4f, 0.9999f);
        Helpers.isort(array2, 0, filterOutNotInAB);
        return filterOutNotInAB;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.middle[0] = this.cx0;
        this.middle[1] = this.cy0;
        this.middle[2] = n;
        this.middle[3] = n2;
        this.middle[4] = n3;
        this.middle[5] = n4;
        this.middle[6] = n5;
        this.middle[7] = n6;
        final float cx0 = this.middle[6];
        final float cy0 = this.middle[7];
        float n7 = this.middle[2] - this.middle[0];
        float n8 = this.middle[3] - this.middle[1];
        float cdx = this.middle[6] - this.middle[4];
        float cdy = this.middle[7] - this.middle[5];
        final boolean b = n7 == 0.0f && n8 == 0.0f;
        final boolean b2 = cdx == 0.0f && cdy == 0.0f;
        if (b) {
            n7 = this.middle[4] - this.middle[0];
            n8 = this.middle[5] - this.middle[1];
            if (n7 == 0.0f && n8 == 0.0f) {
                n7 = this.middle[6] - this.middle[0];
                n8 = this.middle[7] - this.middle[1];
            }
        }
        if (b2) {
            cdx = this.middle[6] - this.middle[2];
            cdy = this.middle[7] - this.middle[3];
            if (cdx == 0.0f && cdy == 0.0f) {
                cdx = this.middle[6] - this.middle[0];
                cdy = this.middle[7] - this.middle[1];
            }
        }
        if (n7 == 0.0f && n8 == 0.0f) {
            this.lineTo(this.middle[0], this.middle[1]);
            return;
        }
        if (Math.abs(n7) < 0.1f && Math.abs(n8) < 0.1f) {
            final float n9 = (float)Math.sqrt(n7 * n7 + n8 * n8);
            n7 /= n9;
            n8 /= n9;
        }
        if (Math.abs(cdx) < 0.1f && Math.abs(cdy) < 0.1f) {
            final float n10 = (float)Math.sqrt(cdx * cdx + cdy * cdy);
            cdx /= n10;
            cdy /= n10;
        }
        computeOffset(n7, n8, this.lineWidth2, this.offset[0]);
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, n7, n8, this.cmx, this.cmy, this.offset[0][0], this.offset[0][1]);
        final int subdivPoints = findSubdivPoints(this.middle, this.subdivTs, 8, this.lineWidth2);
        int computeOffsetCubic = 0;
        final Iterator<Integer> breakPtsAtTs = Curve.breakPtsAtTs(this.middle, 8, this.subdivTs, subdivPoints);
        while (breakPtsAtTs.hasNext()) {
            computeOffsetCubic = this.computeOffsetCubic(this.middle, breakPtsAtTs.next(), this.lp, this.rp);
            this.emitLineTo(this.lp[0], this.lp[1]);
            switch (computeOffsetCubic) {
                case 8: {
                    this.emitCurveTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], this.lp[6], this.lp[7], false);
                    this.emitCurveTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], this.rp[6], this.rp[7], true);
                    break;
                }
                case 4: {
                    this.emitLineTo(this.lp[2], this.lp[3]);
                    this.emitLineTo(this.rp[0], this.rp[1], true);
                    break;
                }
            }
            this.emitLineTo(this.rp[computeOffsetCubic - 2], this.rp[computeOffsetCubic - 1], true);
        }
        this.cmx = (this.lp[computeOffsetCubic - 2] - this.rp[computeOffsetCubic - 2]) / 2.0f;
        this.cmy = (this.lp[computeOffsetCubic - 1] - this.rp[computeOffsetCubic - 1]) / 2.0f;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.prev = 1;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float n3, final float n4) {
        this.middle[0] = this.cx0;
        this.middle[1] = this.cy0;
        this.middle[2] = n;
        this.middle[3] = n2;
        this.middle[4] = n3;
        this.middle[5] = n4;
        final float cx0 = this.middle[4];
        final float cy0 = this.middle[5];
        float n5 = this.middle[2] - this.middle[0];
        float n6 = this.middle[3] - this.middle[1];
        float cdx = this.middle[4] - this.middle[2];
        float cdy = this.middle[5] - this.middle[3];
        if ((n5 == 0.0f && n6 == 0.0f) || (cdx == 0.0f && cdy == 0.0f)) {
            cdx = (n5 = this.middle[4] - this.middle[0]);
            cdy = (n6 = this.middle[5] - this.middle[1]);
        }
        if (n5 == 0.0f && n6 == 0.0f) {
            this.lineTo(this.middle[0], this.middle[1]);
            return;
        }
        if (Math.abs(n5) < 0.1f && Math.abs(n6) < 0.1f) {
            final float n7 = (float)Math.sqrt(n5 * n5 + n6 * n6);
            n5 /= n7;
            n6 /= n7;
        }
        if (Math.abs(cdx) < 0.1f && Math.abs(cdy) < 0.1f) {
            final float n8 = (float)Math.sqrt(cdx * cdx + cdy * cdy);
            cdx /= n8;
            cdy /= n8;
        }
        computeOffset(n5, n6, this.lineWidth2, this.offset[0]);
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, n5, n6, this.cmx, this.cmy, this.offset[0][0], this.offset[0][1]);
        final int subdivPoints = findSubdivPoints(this.middle, this.subdivTs, 6, this.lineWidth2);
        int computeOffsetQuad = 0;
        final Iterator<Integer> breakPtsAtTs = Curve.breakPtsAtTs(this.middle, 6, this.subdivTs, subdivPoints);
        while (breakPtsAtTs.hasNext()) {
            computeOffsetQuad = this.computeOffsetQuad(this.middle, breakPtsAtTs.next(), this.lp, this.rp);
            this.emitLineTo(this.lp[0], this.lp[1]);
            switch (computeOffsetQuad) {
                case 6: {
                    this.emitQuadTo(this.lp[0], this.lp[1], this.lp[2], this.lp[3], this.lp[4], this.lp[5], false);
                    this.emitQuadTo(this.rp[0], this.rp[1], this.rp[2], this.rp[3], this.rp[4], this.rp[5], true);
                    break;
                }
                case 4: {
                    this.emitLineTo(this.lp[2], this.lp[3]);
                    this.emitLineTo(this.rp[0], this.rp[1], true);
                    break;
                }
            }
            this.emitLineTo(this.rp[computeOffsetQuad - 2], this.rp[computeOffsetQuad - 1], true);
        }
        this.cmx = (this.lp[computeOffsetQuad - 2] - this.rp[computeOffsetQuad - 2]) / 2.0f;
        this.cmy = (this.lp[computeOffsetQuad - 1] - this.rp[computeOffsetQuad - 1]) / 2.0f;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.prev = 1;
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Stroker doesn't use a native consumer");
    }
    
    static {
        Stroker.c = new Curve();
    }
    
    private static final class PolyStack
    {
        float[] curves;
        int end;
        int[] curveTypes;
        int numCurves;
        private static final int INIT_SIZE = 50;
        
        PolyStack() {
            this.curves = new float[400];
            this.curveTypes = new int[50];
            this.end = 0;
            this.numCurves = 0;
        }
        
        public boolean isEmpty() {
            return this.numCurves == 0;
        }
        
        private void ensureSpace(final int n) {
            if (this.end + n >= this.curves.length) {
                this.curves = Arrays.copyOf(this.curves, (this.end + n) * 2);
            }
            if (this.numCurves >= this.curveTypes.length) {
                this.curveTypes = Arrays.copyOf(this.curveTypes, this.numCurves * 2);
            }
        }
        
        public void pushCubic(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.ensureSpace(6);
            this.curveTypes[this.numCurves++] = 8;
            this.curves[this.end++] = n5;
            this.curves[this.end++] = n6;
            this.curves[this.end++] = n3;
            this.curves[this.end++] = n4;
            this.curves[this.end++] = n;
            this.curves[this.end++] = n2;
        }
        
        public void pushQuad(final float n, final float n2, final float n3, final float n4) {
            this.ensureSpace(4);
            this.curveTypes[this.numCurves++] = 6;
            this.curves[this.end++] = n3;
            this.curves[this.end++] = n4;
            this.curves[this.end++] = n;
            this.curves[this.end++] = n2;
        }
        
        public void pushLine(final float n, final float n2) {
            this.ensureSpace(2);
            this.curveTypes[this.numCurves++] = 4;
            this.curves[this.end++] = n;
            this.curves[this.end++] = n2;
        }
        
        public int pop(final float[] array) {
            final int n = this.curveTypes[this.numCurves - 1];
            --this.numCurves;
            this.end -= n - 2;
            System.arraycopy(this.curves, this.end, array, 0, n - 2);
            return n;
        }
        
        public void pop(final PathConsumer2D pathConsumer2D) {
            --this.numCurves;
            final int n = this.curveTypes[this.numCurves];
            this.end -= n - 2;
            switch (n) {
                case 8: {
                    pathConsumer2D.curveTo(this.curves[this.end + 0], this.curves[this.end + 1], this.curves[this.end + 2], this.curves[this.end + 3], this.curves[this.end + 4], this.curves[this.end + 5]);
                    break;
                }
                case 6: {
                    pathConsumer2D.quadTo(this.curves[this.end + 0], this.curves[this.end + 1], this.curves[this.end + 2], this.curves[this.end + 3]);
                    break;
                }
                case 4: {
                    pathConsumer2D.lineTo(this.curves[this.end], this.curves[this.end + 1]);
                    break;
                }
            }
        }
        
        @Override
        public String toString() {
            String s = "";
            int i = this.numCurves;
            int end = this.end;
            while (i > 0) {
                --i;
                final int n = this.curveTypes[this.numCurves];
                end -= n - 2;
                switch (n) {
                    case 8: {
                        s += "cubic: ";
                        break;
                    }
                    case 6: {
                        s += "quad: ";
                        break;
                    }
                    case 4: {
                        s += "line: ";
                        break;
                    }
                }
                s = s + Arrays.toString(Arrays.copyOfRange(this.curves, end, end + n - 2)) + "\n";
            }
            return s;
        }
    }
}
