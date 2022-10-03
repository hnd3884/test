package sun.java2d.marlin;

import java.util.Arrays;
import sun.awt.geom.PathConsumer2D;

final class Stroker implements PathConsumer2D, MarlinConst
{
    private static final int MOVE_TO = 0;
    private static final int DRAWING_OP_TO = 1;
    private static final int CLOSE = 2;
    private static final float ERR_JOIN;
    private static final float ROUND_JOIN_THRESHOLD;
    private static final float C;
    private static final float SQRT_2;
    private PathConsumer2D out;
    private int capStyle;
    private int joinStyle;
    private float lineWidth2;
    private float invHalfLineWidth2Sq;
    private final float[] offset0;
    private final float[] offset1;
    private final float[] offset2;
    private final float[] miter;
    private float miterLimitSq;
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
    private final Helpers.PolyStack reverse;
    private final float[] lp;
    private final float[] rp;
    final RendererContext rdrCtx;
    final Curve curve;
    private float[] clipRect;
    private int cOutCode;
    private int sOutCode;
    private boolean opened;
    private boolean capStart;
    private boolean monotonize;
    private boolean subdivide;
    private final TransformingPathConsumer2D.CurveClipSplitter curveSplitter;
    
    Stroker(final RendererContext rdrCtx) {
        this.offset0 = new float[2];
        this.offset1 = new float[2];
        this.offset2 = new float[2];
        this.miter = new float[2];
        this.lp = new float[8];
        this.rp = new float[8];
        this.cOutCode = 0;
        this.sOutCode = 0;
        this.opened = false;
        this.capStart = false;
        this.subdivide = Stroker.DO_CLIP_SUBDIVIDER;
        this.rdrCtx = rdrCtx;
        this.reverse = ((rdrCtx.stats != null) ? new Helpers.PolyStack(rdrCtx, rdrCtx.stats.stat_str_polystack_types, rdrCtx.stats.stat_str_polystack_curves, rdrCtx.stats.hist_str_polystack_curves, rdrCtx.stats.stat_array_str_polystack_curves, rdrCtx.stats.stat_array_str_polystack_types) : new Helpers.PolyStack(rdrCtx));
        this.curve = rdrCtx.curve;
        this.curveSplitter = rdrCtx.curveClipSplitter;
    }
    
    Stroker init(final PathConsumer2D out, final float n, final int capStyle, final int joinStyle, final float n2, final boolean b) {
        this.out = out;
        this.lineWidth2 = n / 2.0f;
        this.invHalfLineWidth2Sq = 1.0f / (2.0f * this.lineWidth2 * this.lineWidth2);
        this.monotonize = b;
        this.capStyle = capStyle;
        this.joinStyle = joinStyle;
        final float n3 = n2 * this.lineWidth2;
        this.miterLimitSq = n3 * n3;
        this.prev = 2;
        this.rdrCtx.stroking = 1;
        if (this.rdrCtx.doClip) {
            float lineWidth2 = this.lineWidth2;
            if (capStyle == 2) {
                lineWidth2 *= Stroker.SQRT_2;
            }
            if (joinStyle == 0 && lineWidth2 < n3) {
                lineWidth2 = n3;
            }
            final float[] clipRect2;
            final float[] clipRect = clipRect2 = this.rdrCtx.clipRect;
            final int n4 = 0;
            clipRect2[n4] -= lineWidth2;
            final float[] array = clipRect;
            final int n5 = 1;
            array[n5] += lineWidth2;
            final float[] array2 = clipRect;
            final int n6 = 2;
            array2[n6] -= lineWidth2;
            final float[] array3 = clipRect;
            final int n7 = 3;
            array3[n7] += lineWidth2;
            this.clipRect = clipRect;
            if (MarlinConst.DO_LOG_CLIP) {
                MarlinUtils.logInfo("clipRect (stroker): " + Arrays.toString(this.rdrCtx.clipRect));
            }
            if (Stroker.DO_CLIP_SUBDIVIDER) {
                this.subdivide = b;
                this.curveSplitter.init();
            }
            else {
                this.subdivide = false;
            }
        }
        else {
            this.clipRect = null;
            this.cOutCode = 0;
            this.sOutCode = 0;
        }
        return this;
    }
    
    void disableClipping() {
        this.clipRect = null;
        this.cOutCode = 0;
        this.sOutCode = 0;
    }
    
    void dispose() {
        this.reverse.dispose();
        this.opened = false;
        this.capStart = false;
    }
    
    private static void computeOffset(final float n, final float n2, final float n3, final float[] array) {
        final float n4 = n * n + n2 * n2;
        if (n4 == 0.0f) {
            array[1] = (array[0] = 0.0f);
        }
        else {
            final float n5 = (float)Math.sqrt(n4);
            array[0] = n2 * n3 / n5;
            array[1] = -(n * n3) / n5;
        }
    }
    
    private static boolean isCW(final float n, final float n2, final float n3, final float n4) {
        return n * n4 <= n2 * n3;
    }
    
    private void mayDrawRoundJoin(final float n, final float n2, float n3, float n4, float n5, float n6, final boolean b) {
        if ((n3 == 0.0f && n4 == 0.0f) || (n5 == 0.0f && n6 == 0.0f)) {
            return;
        }
        final float n7 = n3 - n5;
        final float n8 = n4 - n6;
        if (n7 * n7 + n8 * n8 < Stroker.ROUND_JOIN_THRESHOLD) {
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
        if (n3 * n5 + n4 * n6 >= 0.0f) {
            this.drawBezApproxForArc(n, n2, n3, n4, n5, n6, b);
        }
        else {
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
        }
    }
    
    private void drawBezApproxForArc(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final boolean b) {
        final float n7 = (n3 * n5 + n4 * n6) * this.invHalfLineWidth2Sq;
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
        final float n5 = Stroker.C * n3;
        final float n6 = Stroker.C * n4;
        this.emitCurveTo(n + n3 - n6, n2 + n4 + n5, n - n4 + n5, n2 + n3 + n6, n - n4, n2 + n3);
        this.emitCurveTo(n - n4 - n5, n2 + n3 - n6, n - n3 - n6, n2 - n4 + n5, n - n3, n2 - n4);
    }
    
    private static void computeMiter(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float[] array) {
        final float n9 = n3 - n;
        final float n10 = n4 - n2;
        final float n11 = n7 - n5;
        final float n12 = n8 - n6;
        final float n13 = (n11 * (n2 - n6) - n12 * (n - n5)) / (n9 * n12 - n11 * n10);
        array[0] = n + n13 * n9;
        array[1] = n2 + n13 * n10;
    }
    
    private static void safeComputeMiter(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float[] array) {
        final float n9 = n3 - n;
        final float n10 = n4 - n2;
        final float n11 = n7 - n5;
        final float n12 = n8 - n6;
        final float n13 = n9 * n12 - n11 * n10;
        if (n13 == 0.0f) {
            array[2] = (n + n5) / 2.0f;
            array[3] = (n2 + n6) / 2.0f;
        }
        else {
            final float n14 = (n11 * (n2 - n6) - n12 * (n - n5)) / n13;
            array[2] = n + n14 * n9;
            array[3] = n2 + n14 * n10;
        }
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
        computeMiter(n3 - n + n7, n4 - n2 + n8, n3 + n7, n4 + n8, n5 + n3 + n9, n6 + n4 + n10, n3 + n9, n4 + n10, this.miter);
        final float n11 = this.miter[0];
        final float n12 = this.miter[1];
        if ((n11 - n3) * (n11 - n3) + (n12 - n4) * (n12 - n4) < this.miterLimitSq) {
            this.emitLineTo(n11, n12, b);
        }
    }
    
    @Override
    public void moveTo(final float sx0, final float sy0) {
        this._moveTo(sx0, sy0, this.cOutCode);
        this.sx0 = sx0;
        this.sy0 = sy0;
        this.sdx = 1.0f;
        this.sdy = 0.0f;
        this.opened = false;
        this.capStart = false;
        if (this.clipRect != null) {
            final int outcode = Helpers.outcode(sx0, sy0, this.clipRect);
            this.cOutCode = outcode;
            this.sOutCode = outcode;
        }
    }
    
    private void _moveTo(final float n, final float n2, final int n3) {
        if (this.prev == 0) {
            this.cx0 = n;
            this.cy0 = n2;
        }
        else {
            if (this.prev == 1) {
                this.finish(n3);
            }
            this.prev = 0;
            this.cx0 = n;
            this.cy0 = n2;
            this.cdx = 1.0f;
            this.cdy = 0.0f;
        }
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        this.lineTo(n, n2, false);
    }
    
    private void lineTo(final float cx0, final float cy0, final boolean b) {
        final int cOutCode = this.cOutCode;
        if (!b && this.clipRect != null) {
            final int outcode = Helpers.outcode(cx0, cy0, this.clipRect);
            final int n = cOutCode | outcode;
            if (n != 0) {
                if ((cOutCode & outcode) != 0x0) {
                    this.cOutCode = outcode;
                    this._moveTo(cx0, cy0, cOutCode);
                    this.opened = true;
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    final boolean splitLine = this.curveSplitter.splitLine(this.cx0, this.cy0, cx0, cy0, n, this);
                    this.subdivide = true;
                    if (splitLine) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode;
        }
        float cdx = cx0 - this.cx0;
        final float cdy = cy0 - this.cy0;
        if (cdx == 0.0f && cdy == 0.0f) {
            cdx = 1.0f;
        }
        computeOffset(cdx, cdy, this.lineWidth2, this.offset0);
        final float cmx = this.offset0[0];
        final float cmy = this.offset0[1];
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, cdx, cdy, this.cmx, this.cmy, cmx, cmy, cOutCode);
        this.emitLineTo(this.cx0 + cmx, this.cy0 + cmy);
        this.emitLineTo(cx0 + cmx, cy0 + cmy);
        this.emitLineToRev(this.cx0 - cmx, this.cy0 - cmy);
        this.emitLineToRev(cx0 - cmx, cy0 - cmy);
        this.prev = 1;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cmx = cmx;
        this.cmy = cmy;
    }
    
    @Override
    public void closePath() {
        if (this.prev == 1 || this.opened) {
            if ((this.sOutCode & this.cOutCode) == 0x0) {
                if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
                    this.lineTo(this.sx0, this.sy0, true);
                }
                this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, this.sdx, this.sdy, this.cmx, this.cmy, this.smx, this.smy, this.sOutCode);
                this.emitLineTo(this.sx0 + this.smx, this.sy0 + this.smy);
                if (this.opened) {
                    this.emitLineTo(this.sx0 - this.smx, this.sy0 - this.smy);
                }
                else {
                    this.emitMoveTo(this.sx0 - this.smx, this.sy0 - this.smy);
                }
            }
            this.emitReverse();
            this.prev = 2;
            if (this.opened) {
                this.opened = false;
            }
            else {
                this.emitClose();
            }
            return;
        }
        if (this.prev == 2) {
            return;
        }
        this.emitMoveTo(this.cx0, this.cy0 - this.lineWidth2);
        this.sdx = 1.0f;
        this.sdy = 0.0f;
        this.cdx = 1.0f;
        this.cdy = 0.0f;
        this.smx = 0.0f;
        this.smy = -this.lineWidth2;
        this.cmx = 0.0f;
        this.cmy = -this.lineWidth2;
        this.finish(this.cOutCode);
    }
    
    private void emitReverse() {
        this.reverse.popAll(this.out);
    }
    
    @Override
    public void pathDone() {
        if (this.prev == 1) {
            this.finish(this.cOutCode);
        }
        this.out.pathDone();
        this.prev = 2;
        this.dispose();
    }
    
    private void finish(final int n) {
        if (!this.rdrCtx.closedPath) {
            if (n == 0) {
                if (this.capStyle == 1) {
                    this.drawRoundCap(this.cx0, this.cy0, this.cmx, this.cmy);
                }
                else if (this.capStyle == 2) {
                    this.emitLineTo(this.cx0 - this.cmy + this.cmx, this.cy0 + this.cmx + this.cmy);
                    this.emitLineTo(this.cx0 - this.cmy - this.cmx, this.cy0 + this.cmx - this.cmy);
                }
            }
            this.emitReverse();
            if (!this.capStart) {
                this.capStart = true;
                if (this.sOutCode == 0) {
                    if (this.capStyle == 1) {
                        this.drawRoundCap(this.sx0, this.sy0, -this.smx, -this.smy);
                    }
                    else if (this.capStyle == 2) {
                        this.emitLineTo(this.sx0 + this.smy - this.smx, this.sy0 - this.smx - this.smy);
                        this.emitLineTo(this.sx0 + this.smy + this.smx, this.sy0 - this.smx + this.smy);
                    }
                }
            }
        }
        else {
            this.emitReverse();
        }
        this.emitClose();
    }
    
    private void emitMoveTo(final float n, final float n2) {
        this.out.moveTo(n, n2);
    }
    
    private void emitLineTo(final float n, final float n2) {
        this.out.lineTo(n, n2);
    }
    
    private void emitLineToRev(final float n, final float n2) {
        this.reverse.pushLine(n, n2);
    }
    
    private void emitLineTo(final float n, final float n2, final boolean b) {
        if (b) {
            this.emitLineToRev(n, n2);
        }
        else {
            this.emitLineTo(n, n2);
        }
    }
    
    private void emitQuadTo(final float n, final float n2, final float n3, final float n4) {
        this.out.quadTo(n, n2, n3, n4);
    }
    
    private void emitQuadToRev(final float n, final float n2, final float n3, final float n4) {
        this.reverse.pushQuad(n, n2, n3, n4);
    }
    
    private void emitCurveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.out.curveTo(n, n2, n3, n4, n5, n6);
    }
    
    private void emitCurveToRev(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.reverse.pushCubic(n, n2, n3, n4, n5, n6);
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
    
    private void drawJoin(final float n, final float n2, final float n3, final float n4, final float sdx, final float sdy, final float n5, final float n6, final float smx, final float smy, final int n7) {
        if (this.prev != 1) {
            this.emitMoveTo(n3 + smx, n4 + smy);
            if (!this.opened) {
                this.sdx = sdx;
                this.sdy = sdy;
                this.smx = smx;
                this.smy = smy;
            }
        }
        else {
            final boolean cw = isCW(n, n2, sdx, sdy);
            if (n7 == 0) {
                if (this.joinStyle == 0) {
                    this.drawMiter(n, n2, n3, n4, sdx, sdy, n5, n6, smx, smy, cw);
                }
                else if (this.joinStyle == 1) {
                    this.mayDrawRoundJoin(n3, n4, n5, n6, smx, smy, cw);
                }
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
        computeOffset(n3 - n, n4 - n2, this.lineWidth2, this.offset0);
        final float n5 = this.offset0[0];
        final float n6 = this.offset0[1];
        array[0] = n + n5;
        array[1] = n2 + n6;
        array[2] = n3 + n5;
        array[3] = n4 + n6;
        array2[0] = n - n5;
        array2[1] = n2 - n6;
        array2[2] = n3 - n5;
        array2[3] = n4 - n6;
    }
    
    private int computeOffsetCubic(final float[] array, final int n, final float[] array2, final float[] array3) {
        final float n2 = array[n];
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
        final float n16 = (n2 + 3.0f * (n4 + n6) + n8) / 8.0f;
        final float n17 = (n3 + 3.0f * (n5 + n7) + n9) / 8.0f;
        final float n18 = n6 + n8 - n2 - n4;
        final float n19 = n7 + n9 - n3 - n5;
        computeOffset(n12, n13, this.lineWidth2, this.offset0);
        computeOffset(n18, n19, this.lineWidth2, this.offset1);
        computeOffset(n10, n11, this.lineWidth2, this.offset2);
        final float n20 = n2 + this.offset0[0];
        final float n21 = n3 + this.offset0[1];
        final float n22 = n16 + this.offset1[0];
        final float n23 = n17 + this.offset1[1];
        final float n24 = n8 + this.offset2[0];
        final float n25 = n9 + this.offset2[1];
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
        final float n35 = n2 - this.offset0[0];
        final float n36 = n3 - this.offset0[1];
        final float n37 = n22 - 2.0f * this.offset1[0];
        final float n38 = n23 - 2.0f * this.offset1[1];
        final float n39 = n8 - this.offset2[0];
        final float n40 = n9 - this.offset2[1];
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
        final float n2 = array[n];
        final float n3 = array[n + 1];
        final float n4 = array[n + 2];
        final float n5 = array[n + 3];
        final float n6 = array[n + 4];
        final float n7 = array[n + 5];
        final float n8 = n6 - n4;
        final float n9 = n7 - n5;
        final float n10 = n4 - n2;
        final float n11 = n5 - n3;
        final boolean within = within(n2, n3, n4, n5, 6.0f * Math.ulp(n5));
        final boolean within2 = within(n4, n5, n6, n7, 6.0f * Math.ulp(n7));
        if (within || within2) {
            this.getLineOffsets(n2, n3, n6, n7, array2, array3);
            return 4;
        }
        final float n12 = n10 * n8 + n11 * n9;
        final float n13 = n12 * n12;
        if (Helpers.within(n13, (n10 * n10 + n11 * n11) * (n8 * n8 + n9 * n9), 4.0f * Math.ulp(n13))) {
            this.getLineOffsets(n2, n3, n6, n7, array2, array3);
            return 4;
        }
        computeOffset(n10, n11, this.lineWidth2, this.offset0);
        computeOffset(n8, n9, this.lineWidth2, this.offset1);
        final float n14 = n2 + this.offset0[0];
        final float n15 = n3 + this.offset0[1];
        final float n16 = n6 + this.offset1[0];
        final float n17 = n7 + this.offset1[1];
        safeComputeMiter(n14, n15, n14 + n10, n15 + n11, n16, n17, n16 - n8, n17 - n9, array2);
        array2[0] = n14;
        array2[1] = n15;
        array2[4] = n16;
        array2[5] = n17;
        final float n18 = n2 - this.offset0[0];
        final float n19 = n3 - this.offset0[1];
        final float n20 = n6 - this.offset1[0];
        final float n21 = n7 - this.offset1[1];
        safeComputeMiter(n18, n19, n18 + n10, n19 + n11, n20, n21, n20 - n8, n21 - n9, array3);
        array3[0] = n18;
        array3[1] = n19;
        array3[4] = n20;
        array3[5] = n21;
        return 6;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
            final int outcode2 = Helpers.outcode(n3, n4, this.clipRect);
            final int outcode3 = Helpers.outcode(n5, n6, this.clipRect);
            final int n7 = cOutCode | outcode | outcode2 | outcode3;
            if (n7 != 0) {
                if ((cOutCode & outcode & outcode2 & outcode3) != 0x0) {
                    this.cOutCode = outcode3;
                    this._moveTo(n5, n6, cOutCode);
                    this.opened = true;
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    final boolean splitCurve = this.curveSplitter.splitCurve(this.cx0, this.cy0, n, n2, n3, n4, n5, n6, n7, this);
                    this.subdivide = true;
                    if (splitCurve) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode3;
        }
        this._curveTo(n, n2, n3, n4, n5, n6, cOutCode);
    }
    
    private void _curveTo(final float n, final float n2, final float n3, final float n4, final float cx0, final float cy0, final int cOutCode) {
        float n5 = n - this.cx0;
        float n6 = n2 - this.cy0;
        float cdx = cx0 - n3;
        float cdy = cy0 - n4;
        if (n5 == 0.0f && n6 == 0.0f) {
            n5 = n3 - this.cx0;
            n6 = n4 - this.cy0;
            if (n5 == 0.0f && n6 == 0.0f) {
                n5 = cx0 - this.cx0;
                n6 = cy0 - this.cy0;
            }
        }
        if (cdx == 0.0f && cdy == 0.0f) {
            cdx = cx0 - n;
            cdy = cy0 - n2;
            if (cdx == 0.0f && cdy == 0.0f) {
                cdx = cx0 - this.cx0;
                cdy = cy0 - this.cy0;
            }
        }
        if (n5 == 0.0f && n6 == 0.0f) {
            if (this.clipRect != null) {
                this.cOutCode = cOutCode;
            }
            this.lineTo(this.cx0, this.cy0);
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
        computeOffset(n5, n6, this.lineWidth2, this.offset0);
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, n5, n6, this.cmx, this.cmy, this.offset0[0], this.offset0[1], cOutCode);
        int nbSplits = 0;
        final float[] lp = this.lp;
        float[] middle;
        if (this.monotonize) {
            final TransformingPathConsumer2D.CurveBasicMonotonizer curve = this.rdrCtx.monotonizer.curve(this.cx0, this.cy0, n, n2, n3, n4, cx0, cy0);
            nbSplits = curve.nbSplits;
            middle = curve.middle;
        }
        else {
            middle = lp;
            middle[0] = this.cx0;
            middle[1] = this.cy0;
            middle[2] = n;
            middle[3] = n2;
            middle[4] = n3;
            middle[5] = n4;
            middle[6] = cx0;
            middle[7] = cy0;
        }
        final float[] rp = this.rp;
        int computeOffsetCubic = 0;
        for (int i = 0, n9 = 0; i <= nbSplits; ++i, n9 += 6) {
            computeOffsetCubic = this.computeOffsetCubic(middle, n9, lp, rp);
            this.emitLineTo(lp[0], lp[1]);
            switch (computeOffsetCubic) {
                case 8: {
                    this.emitCurveTo(lp[2], lp[3], lp[4], lp[5], lp[6], lp[7]);
                    this.emitCurveToRev(rp[0], rp[1], rp[2], rp[3], rp[4], rp[5]);
                    break;
                }
                case 4: {
                    this.emitLineTo(lp[2], lp[3]);
                    this.emitLineToRev(rp[0], rp[1]);
                    break;
                }
            }
            this.emitLineToRev(rp[computeOffsetCubic - 2], rp[computeOffsetCubic - 1]);
        }
        this.prev = 1;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cmx = (lp[computeOffsetCubic - 2] - rp[computeOffsetCubic - 2]) / 2.0f;
        this.cmy = (lp[computeOffsetCubic - 1] - rp[computeOffsetCubic - 1]) / 2.0f;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float n3, final float n4) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
            final int outcode2 = Helpers.outcode(n3, n4, this.clipRect);
            final int n5 = cOutCode | outcode | outcode2;
            if (n5 != 0) {
                if ((cOutCode & outcode & outcode2) != 0x0) {
                    this.cOutCode = outcode2;
                    this._moveTo(n3, n4, cOutCode);
                    this.opened = true;
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    final boolean splitQuad = this.curveSplitter.splitQuad(this.cx0, this.cy0, n, n2, n3, n4, n5, this);
                    this.subdivide = true;
                    if (splitQuad) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode2;
        }
        this._quadTo(n, n2, n3, n4, cOutCode);
    }
    
    private void _quadTo(final float n, final float n2, final float cx0, final float cy0, final int cOutCode) {
        float n3 = n - this.cx0;
        float n4 = n2 - this.cy0;
        float cdx = cx0 - n;
        float cdy = cy0 - n2;
        if ((n3 == 0.0f && n4 == 0.0f) || (cdx == 0.0f && cdy == 0.0f)) {
            cdx = (n3 = cx0 - this.cx0);
            cdy = (n4 = cy0 - this.cy0);
        }
        if (n3 == 0.0f && n4 == 0.0f) {
            if (this.clipRect != null) {
                this.cOutCode = cOutCode;
            }
            this.lineTo(this.cx0, this.cy0);
            return;
        }
        if (Math.abs(n3) < 0.1f && Math.abs(n4) < 0.1f) {
            final float n5 = (float)Math.sqrt(n3 * n3 + n4 * n4);
            n3 /= n5;
            n4 /= n5;
        }
        if (Math.abs(cdx) < 0.1f && Math.abs(cdy) < 0.1f) {
            final float n6 = (float)Math.sqrt(cdx * cdx + cdy * cdy);
            cdx /= n6;
            cdy /= n6;
        }
        computeOffset(n3, n4, this.lineWidth2, this.offset0);
        this.drawJoin(this.cdx, this.cdy, this.cx0, this.cy0, n3, n4, this.cmx, this.cmy, this.offset0[0], this.offset0[1], cOutCode);
        int nbSplits = 0;
        final float[] lp = this.lp;
        float[] middle;
        if (this.monotonize) {
            final TransformingPathConsumer2D.CurveBasicMonotonizer quad = this.rdrCtx.monotonizer.quad(this.cx0, this.cy0, n, n2, cx0, cy0);
            nbSplits = quad.nbSplits;
            middle = quad.middle;
        }
        else {
            middle = lp;
            middle[0] = this.cx0;
            middle[1] = this.cy0;
            middle[2] = n;
            middle[3] = n2;
            middle[4] = cx0;
            middle[5] = cy0;
        }
        final float[] rp = this.rp;
        int computeOffsetQuad = 0;
        for (int i = 0, n7 = 0; i <= nbSplits; ++i, n7 += 4) {
            computeOffsetQuad = this.computeOffsetQuad(middle, n7, lp, rp);
            this.emitLineTo(lp[0], lp[1]);
            switch (computeOffsetQuad) {
                case 6: {
                    this.emitQuadTo(lp[2], lp[3], lp[4], lp[5]);
                    this.emitQuadToRev(rp[0], rp[1], rp[2], rp[3]);
                    break;
                }
                case 4: {
                    this.emitLineTo(lp[2], lp[3]);
                    this.emitLineToRev(rp[0], rp[1]);
                    break;
                }
            }
            this.emitLineToRev(rp[computeOffsetQuad - 2], rp[computeOffsetQuad - 1]);
        }
        this.prev = 1;
        this.cx0 = cx0;
        this.cy0 = cy0;
        this.cdx = cdx;
        this.cdy = cdy;
        this.cmx = (lp[computeOffsetQuad - 2] - rp[computeOffsetQuad - 2]) / 2.0f;
        this.cmy = (lp[computeOffsetQuad - 1] - rp[computeOffsetQuad - 1]) / 2.0f;
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Stroker doesn't use a native consumer");
    }
    
    static {
        ERR_JOIN = 1.0f / Stroker.MIN_SUBPIXELS;
        ROUND_JOIN_THRESHOLD = Stroker.ERR_JOIN * Stroker.ERR_JOIN;
        C = (float)(4.0 * (Math.sqrt(2.0) - 1.0) / 3.0);
        SQRT_2 = (float)Math.sqrt(2.0);
    }
}
