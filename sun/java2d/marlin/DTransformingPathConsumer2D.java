package sun.java2d.marlin;

import java.util.Arrays;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

final class DTransformingPathConsumer2D
{
    static final double CLIP_RECT_PADDING = 0.25;
    private final DRendererContext rdrCtx;
    private final ClosedPathDetector cpDetector;
    private final PathClipFilter pathClipper;
    private final Path2DWrapper wp_Path2DWrapper;
    private final DeltaScaleFilter dt_DeltaScaleFilter;
    private final DeltaTransformFilter dt_DeltaTransformFilter;
    private final DeltaScaleFilter iv_DeltaScaleFilter;
    private final DeltaTransformFilter iv_DeltaTransformFilter;
    private final PathTracer tracerInput;
    private final PathTracer tracerCPDetector;
    private final PathTracer tracerFiller;
    private final PathTracer tracerStroker;
    private final PathTracer tracerDasher;
    
    DTransformingPathConsumer2D(final DRendererContext rdrCtx) {
        this.wp_Path2DWrapper = new Path2DWrapper();
        this.dt_DeltaScaleFilter = new DeltaScaleFilter();
        this.dt_DeltaTransformFilter = new DeltaTransformFilter();
        this.iv_DeltaScaleFilter = new DeltaScaleFilter();
        this.iv_DeltaTransformFilter = new DeltaTransformFilter();
        this.tracerInput = new PathTracer("[Input]");
        this.tracerCPDetector = new PathTracer("ClosedPathDetector");
        this.tracerFiller = new PathTracer("Filler");
        this.tracerStroker = new PathTracer("Stroker");
        this.tracerDasher = new PathTracer("Dasher");
        this.rdrCtx = rdrCtx;
        this.cpDetector = new ClosedPathDetector(rdrCtx);
        this.pathClipper = new PathClipFilter(rdrCtx);
    }
    
    DPathConsumer2D wrapPath2D(final Path2D.Double double1) {
        return this.wp_Path2DWrapper.init(double1);
    }
    
    DPathConsumer2D traceInput(final DPathConsumer2D dPathConsumer2D) {
        return this.tracerInput.init(dPathConsumer2D);
    }
    
    DPathConsumer2D traceClosedPathDetector(final DPathConsumer2D dPathConsumer2D) {
        return this.tracerCPDetector.init(dPathConsumer2D);
    }
    
    DPathConsumer2D traceFiller(final DPathConsumer2D dPathConsumer2D) {
        return this.tracerFiller.init(dPathConsumer2D);
    }
    
    DPathConsumer2D traceStroker(final DPathConsumer2D dPathConsumer2D) {
        return this.tracerStroker.init(dPathConsumer2D);
    }
    
    DPathConsumer2D traceDasher(final DPathConsumer2D dPathConsumer2D) {
        return this.tracerDasher.init(dPathConsumer2D);
    }
    
    DPathConsumer2D detectClosedPath(final DPathConsumer2D dPathConsumer2D) {
        return this.cpDetector.init(dPathConsumer2D);
    }
    
    DPathConsumer2D pathClipper(final DPathConsumer2D dPathConsumer2D) {
        return this.pathClipper.init(dPathConsumer2D);
    }
    
    DPathConsumer2D deltaTransformConsumer(final DPathConsumer2D dPathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return dPathConsumer2D;
        }
        final double scaleX = affineTransform.getScaleX();
        final double shearX = affineTransform.getShearX();
        final double shearY = affineTransform.getShearY();
        final double scaleY = affineTransform.getScaleY();
        if (shearX != 0.0 || shearY != 0.0) {
            if (this.rdrCtx.doClip) {
                this.rdrCtx.clipInvScale = adjustClipInverseDelta(this.rdrCtx.clipRect, scaleX, shearX, shearY, scaleY);
            }
            return this.dt_DeltaTransformFilter.init(dPathConsumer2D, scaleX, shearX, shearY, scaleY);
        }
        if (scaleX == 1.0 && scaleY == 1.0) {
            return dPathConsumer2D;
        }
        if (this.rdrCtx.doClip) {
            this.rdrCtx.clipInvScale = adjustClipScale(this.rdrCtx.clipRect, scaleX, scaleY);
        }
        return this.dt_DeltaScaleFilter.init(dPathConsumer2D, scaleX, scaleY);
    }
    
    private static double adjustClipScale(final double[] array, final double n, final double n2) {
        final double n3 = 1.0 / n2;
        final int n4 = 0;
        array[n4] *= n3;
        final int n5 = 1;
        array[n5] *= n3;
        if (array[1] < array[0]) {
            final double n6 = array[0];
            array[0] = array[1];
            array[1] = n6;
        }
        final double n7 = 1.0 / n;
        final int n8 = 2;
        array[n8] *= n7;
        final int n9 = 3;
        array[n9] *= n7;
        if (array[3] < array[2]) {
            final double n10 = array[2];
            array[2] = array[3];
            array[3] = n10;
        }
        if (MarlinConst.DO_LOG_CLIP) {
            MarlinUtils.logInfo("clipRect (ClipScale): " + Arrays.toString(array));
        }
        return 0.5 * (Math.abs(n7) + Math.abs(n3));
    }
    
    private static double adjustClipInverseDelta(final double[] array, final double n, final double n2, final double n3, final double n4) {
        final double n5 = n * n4 - n2 * n3;
        final double n6 = n4 / n5;
        final double n7 = -n2 / n5;
        final double n8 = -n3 / n5;
        final double n9 = n / n5;
        final double n10 = array[2] * n6 + array[0] * n7;
        final double n11 = array[2] * n8 + array[0] * n9;
        double n13;
        double n12 = n13 = n10;
        double n15;
        double n14 = n15 = n11;
        final double n16 = array[3] * n6 + array[0] * n7;
        final double n17 = array[3] * n8 + array[0] * n9;
        if (n16 < n13) {
            n13 = n16;
        }
        else if (n16 > n12) {
            n12 = n16;
        }
        if (n17 < n15) {
            n15 = n17;
        }
        else if (n17 > n14) {
            n14 = n17;
        }
        final double n18 = array[2] * n6 + array[1] * n7;
        final double n19 = array[2] * n8 + array[1] * n9;
        if (n18 < n13) {
            n13 = n18;
        }
        else if (n18 > n12) {
            n12 = n18;
        }
        if (n19 < n15) {
            n15 = n19;
        }
        else if (n19 > n14) {
            n14 = n19;
        }
        final double n20 = array[3] * n6 + array[1] * n7;
        final double n21 = array[3] * n8 + array[1] * n9;
        if (n20 < n13) {
            n13 = n20;
        }
        else if (n20 > n12) {
            n12 = n20;
        }
        if (n21 < n15) {
            n15 = n21;
        }
        else if (n21 > n14) {
            n14 = n21;
        }
        array[0] = n15;
        array[1] = n14;
        array[2] = n13;
        array[3] = n12;
        if (MarlinConst.DO_LOG_CLIP) {
            MarlinUtils.logInfo("clipRect (ClipInverseDelta): " + Arrays.toString(array));
        }
        return 0.5 * (Math.sqrt(n6 * n6 + n7 * n7) + Math.sqrt(n8 * n8 + n9 * n9));
    }
    
    DPathConsumer2D inverseDeltaTransformConsumer(final DPathConsumer2D dPathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return dPathConsumer2D;
        }
        final double scaleX = affineTransform.getScaleX();
        final double shearX = affineTransform.getShearX();
        final double shearY = affineTransform.getShearY();
        final double scaleY = affineTransform.getScaleY();
        if (shearX != 0.0 || shearY != 0.0) {
            final double n = scaleX * scaleY - shearX * shearY;
            return this.iv_DeltaTransformFilter.init(dPathConsumer2D, scaleY / n, -shearX / n, -shearY / n, scaleX / n);
        }
        if (scaleX == 1.0 && scaleY == 1.0) {
            return dPathConsumer2D;
        }
        return this.iv_DeltaScaleFilter.init(dPathConsumer2D, 1.0 / scaleX, 1.0 / scaleY);
    }
    
    static final class DeltaScaleFilter implements DPathConsumer2D
    {
        private DPathConsumer2D out;
        private double sx;
        private double sy;
        
        DeltaScaleFilter init(final DPathConsumer2D out, final double sx, final double sy) {
            this.out = out;
            this.sx = sx;
            this.sy = sy;
            return this;
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.out.moveTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.out.lineTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.out.quadTo(n * this.sx, n2 * this.sy, n3 * this.sx, n4 * this.sy);
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.out.curveTo(n * this.sx, n2 * this.sy, n3 * this.sx, n4 * this.sy, n5 * this.sx, n6 * this.sy);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class DeltaTransformFilter implements DPathConsumer2D
    {
        private DPathConsumer2D out;
        private double mxx;
        private double mxy;
        private double myx;
        private double myy;
        
        DeltaTransformFilter init(final DPathConsumer2D out, final double mxx, final double mxy, final double myx, final double myy) {
            this.out = out;
            this.mxx = mxx;
            this.mxy = mxy;
            this.myx = myx;
            this.myy = myy;
            return this;
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.out.moveTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.out.lineTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.out.quadTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy, n3 * this.mxx + n4 * this.mxy, n3 * this.myx + n4 * this.myy);
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.out.curveTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy, n3 * this.mxx + n4 * this.mxy, n3 * this.myx + n4 * this.myy, n5 * this.mxx + n6 * this.mxy, n5 * this.myx + n6 * this.myy);
        }
        
        @Override
        public void closePath() {
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.out.pathDone();
        }
        
        @Override
        public long getNativeConsumer() {
            return 0L;
        }
    }
    
    static final class Path2DWrapper implements DPathConsumer2D
    {
        private Path2D.Double p2d;
        
        Path2DWrapper init(final Path2D.Double p2d) {
            this.p2d = p2d;
            return this;
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.p2d.moveTo(n, n2);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.p2d.lineTo(n, n2);
        }
        
        @Override
        public void closePath() {
            this.p2d.closePath();
        }
        
        @Override
        public void pathDone() {
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.p2d.curveTo(n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.p2d.quadTo(n, n2, n3, n4);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
    
    static final class ClosedPathDetector implements DPathConsumer2D
    {
        private final DRendererContext rdrCtx;
        private final DHelpers.PolyStack stack;
        private DPathConsumer2D out;
        
        ClosedPathDetector(final DRendererContext rdrCtx) {
            this.rdrCtx = rdrCtx;
            this.stack = ((rdrCtx.stats != null) ? new DHelpers.PolyStack(rdrCtx, rdrCtx.stats.stat_cpd_polystack_types, rdrCtx.stats.stat_cpd_polystack_curves, rdrCtx.stats.hist_cpd_polystack_curves, rdrCtx.stats.stat_array_cpd_polystack_curves, rdrCtx.stats.stat_array_cpd_polystack_types) : new DHelpers.PolyStack(rdrCtx));
        }
        
        ClosedPathDetector init(final DPathConsumer2D out) {
            this.out = out;
            return this;
        }
        
        void dispose() {
            this.stack.dispose();
        }
        
        @Override
        public void pathDone() {
            this.finish(false);
            this.out.pathDone();
            this.dispose();
        }
        
        @Override
        public void closePath() {
            this.finish(true);
            this.out.closePath();
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.finish(false);
            this.out.moveTo(n, n2);
        }
        
        private void finish(final boolean closedPath) {
            this.rdrCtx.closedPath = closedPath;
            this.stack.pullAll(this.out);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.stack.pushLine(n, n2);
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.stack.pushCubic(n5, n6, n3, n4, n, n2);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.stack.pushQuad(n3, n4, n, n2);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
    
    static final class PathClipFilter implements DPathConsumer2D
    {
        private DPathConsumer2D out;
        private final double[] clipRect;
        private final double[] corners;
        private boolean init_corners;
        private final DHelpers.IndexStack stack;
        private int cOutCode;
        private int gOutCode;
        private boolean outside;
        private double cx0;
        private double cy0;
        private double cox0;
        private double coy0;
        private boolean subdivide;
        private final CurveClipSplitter curveSplitter;
        
        PathClipFilter(final DRendererContext dRendererContext) {
            this.corners = new double[8];
            this.init_corners = false;
            this.cOutCode = 0;
            this.gOutCode = 15;
            this.outside = false;
            this.subdivide = MarlinConst.DO_CLIP_SUBDIVIDER;
            this.clipRect = dRendererContext.clipRect;
            this.curveSplitter = dRendererContext.curveClipSplitter;
            this.stack = ((dRendererContext.stats != null) ? new DHelpers.IndexStack(dRendererContext, dRendererContext.stats.stat_pcf_idxstack_indices, dRendererContext.stats.hist_pcf_idxstack_indices, dRendererContext.stats.stat_array_pcf_idxstack_indices) : new DHelpers.IndexStack(dRendererContext));
        }
        
        PathClipFilter init(final DPathConsumer2D out) {
            this.out = out;
            if (MarlinConst.DO_CLIP_SUBDIVIDER) {
                this.curveSplitter.init();
            }
            this.init_corners = true;
            this.gOutCode = 15;
            return this;
        }
        
        void dispose() {
            this.stack.dispose();
        }
        
        private void finishPath() {
            if (this.outside) {
                if (this.gOutCode == 0) {
                    this.finish();
                }
                else {
                    this.outside = false;
                    this.stack.reset();
                }
            }
        }
        
        private void finish() {
            this.outside = false;
            if (!this.stack.isEmpty()) {
                if (this.init_corners) {
                    this.init_corners = false;
                    final double[] corners = this.corners;
                    final double[] clipRect = this.clipRect;
                    corners[0] = clipRect[2];
                    corners[1] = clipRect[0];
                    corners[2] = clipRect[2];
                    corners[3] = clipRect[1];
                    corners[4] = clipRect[3];
                    corners[5] = clipRect[0];
                    corners[6] = clipRect[3];
                    corners[7] = clipRect[1];
                }
                this.stack.pullAll(this.corners, this.out);
            }
            this.out.lineTo(this.cox0, this.coy0);
            this.cx0 = this.cox0;
            this.cy0 = this.coy0;
        }
        
        @Override
        public void pathDone() {
            this.finishPath();
            this.out.pathDone();
            this.dispose();
        }
        
        @Override
        public void closePath() {
            this.finishPath();
            this.out.closePath();
        }
        
        @Override
        public void moveTo(final double cx0, final double cy0) {
            this.finishPath();
            this.cOutCode = DHelpers.outcode(cx0, cy0, this.clipRect);
            this.outside = false;
            this.out.moveTo(cx0, cy0);
            this.cx0 = cx0;
            this.cy0 = cy0;
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            final int cOutCode = this.cOutCode;
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
            final int n3 = cOutCode | outcode;
            if (n3 != 0) {
                final int n4 = cOutCode & outcode;
                if (n4 != 0) {
                    this.cOutCode = outcode;
                    this.gOutCode &= n4;
                    this.outside = true;
                    this.cox0 = n;
                    this.coy0 = n2;
                    this.clip(n4, cOutCode, outcode);
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    boolean b;
                    if (this.outside) {
                        b = this.curveSplitter.splitLine(this.cox0, this.coy0, n, n2, n3, this);
                    }
                    else {
                        b = this.curveSplitter.splitLine(this.cx0, this.cy0, n, n2, n3, this);
                    }
                    this.subdivide = true;
                    if (b) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode;
            this.gOutCode = 0;
            if (this.outside) {
                this.finish();
            }
            this.out.lineTo(n, n2);
            this.cx0 = n;
            this.cy0 = n2;
        }
        
        private void clip(final int n, final int n2, final int n3) {
            if (n2 != n3 && (n & 0xC) != 0x0) {
                final int n4 = n2 | n3;
                final int n5 = n4 & 0x3;
                final int n6 = ((n4 & 0xC) == 0x4) ? 0 : 2;
                switch (n5) {
                    case 1: {
                        this.stack.push(n6);
                        return;
                    }
                    case 2: {
                        this.stack.push(n6 + 1);
                        return;
                    }
                    default: {
                        if ((n2 & 0x1) != 0x0) {
                            this.stack.push(n6);
                            this.stack.push(n6 + 1);
                            break;
                        }
                        this.stack.push(n6 + 1);
                        this.stack.push(n6);
                        break;
                    }
                }
            }
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            final int cOutCode = this.cOutCode;
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
            final int outcode2 = DHelpers.outcode(n3, n4, this.clipRect);
            final int outcode3 = DHelpers.outcode(n5, n6, this.clipRect);
            final int n7 = cOutCode | outcode | outcode2 | outcode3;
            if (n7 != 0) {
                final int n8 = cOutCode & outcode & outcode2 & outcode3;
                if (n8 != 0) {
                    this.cOutCode = outcode3;
                    this.gOutCode &= n8;
                    this.outside = true;
                    this.cox0 = n5;
                    this.coy0 = n6;
                    this.clip(n8, cOutCode, outcode3);
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    boolean b;
                    if (this.outside) {
                        b = this.curveSplitter.splitCurve(this.cox0, this.coy0, n, n2, n3, n4, n5, n6, n7, this);
                    }
                    else {
                        b = this.curveSplitter.splitCurve(this.cx0, this.cy0, n, n2, n3, n4, n5, n6, n7, this);
                    }
                    this.subdivide = true;
                    if (b) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode3;
            this.gOutCode = 0;
            if (this.outside) {
                this.finish();
            }
            this.out.curveTo(n, n2, n3, n4, n5, n6);
            this.cx0 = n5;
            this.cy0 = n6;
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            final int cOutCode = this.cOutCode;
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
            final int outcode2 = DHelpers.outcode(n3, n4, this.clipRect);
            final int n5 = cOutCode | outcode | outcode2;
            if (n5 != 0) {
                final int n6 = cOutCode & outcode & outcode2;
                if (n6 != 0) {
                    this.cOutCode = outcode2;
                    this.gOutCode &= n6;
                    this.outside = true;
                    this.cox0 = n3;
                    this.coy0 = n4;
                    this.clip(n6, cOutCode, outcode2);
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    boolean b;
                    if (this.outside) {
                        b = this.curveSplitter.splitQuad(this.cox0, this.coy0, n, n2, n3, n4, n5, this);
                    }
                    else {
                        b = this.curveSplitter.splitQuad(this.cx0, this.cy0, n, n2, n3, n4, n5, this);
                    }
                    this.subdivide = true;
                    if (b) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode2;
            this.gOutCode = 0;
            if (this.outside) {
                this.finish();
            }
            this.out.quadTo(n, n2, n3, n4);
            this.cx0 = n3;
            this.cy0 = n4;
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
    
    static final class CurveClipSplitter
    {
        static final double LEN_TH;
        static final boolean DO_CHECK_LENGTH;
        private static final boolean TRACE = false;
        private static final int MAX_N_CURVES = 12;
        private final DRendererContext rdrCtx;
        private double minLength;
        final double[] clipRect;
        final double[] clipRectPad;
        private boolean init_clipRectPad;
        final double[] middle;
        private final double[] subdivTs;
        private final DCurve curve;
        
        CurveClipSplitter(final DRendererContext rdrCtx) {
            this.clipRectPad = new double[4];
            this.init_clipRectPad = false;
            this.middle = new double[98];
            this.subdivTs = new double[12];
            this.rdrCtx = rdrCtx;
            this.clipRect = rdrCtx.clipRect;
            this.curve = rdrCtx.curve;
        }
        
        void init() {
            this.init_clipRectPad = true;
            if (CurveClipSplitter.DO_CHECK_LENGTH) {
                this.minLength = ((this.rdrCtx.clipInvScale == 0.0) ? CurveClipSplitter.LEN_TH : (CurveClipSplitter.LEN_TH * this.rdrCtx.clipInvScale));
                if (MarlinConst.DO_LOG_CLIP) {
                    MarlinUtils.logInfo("CurveClipSplitter.minLength = " + this.minLength);
                }
            }
        }
        
        private void initPaddedClip() {
            final double[] clipRect = this.clipRect;
            final double[] clipRectPad = this.clipRectPad;
            clipRectPad[0] = clipRect[0] - 0.25;
            clipRectPad[1] = clipRect[1] + 0.25;
            clipRectPad[2] = clipRect[2] - 0.25;
            clipRectPad[3] = clipRect[3] + 0.25;
        }
        
        boolean splitLine(final double n, final double n2, final double n3, final double n4, final int n5, final DPathConsumer2D dPathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && DHelpers.fastLineLen(n, n2, n3, n4) <= this.minLength) {
                return false;
            }
            final double[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            return this.subdivideAtIntersections(4, n5, dPathConsumer2D);
        }
        
        boolean splitQuad(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final int n7, final DPathConsumer2D dPathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && DHelpers.fastQuadLen(n, n2, n3, n4, n5, n6) <= this.minLength) {
                return false;
            }
            final double[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            return this.subdivideAtIntersections(6, n7, dPathConsumer2D);
        }
        
        boolean splitCurve(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final int n9, final DPathConsumer2D dPathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && DHelpers.fastCurvelen(n, n2, n3, n4, n5, n6, n7, n8) <= this.minLength) {
                return false;
            }
            final double[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            middle[6] = n7;
            middle[7] = n8;
            return this.subdivideAtIntersections(8, n9, dPathConsumer2D);
        }
        
        private boolean subdivideAtIntersections(final int n, final int n2, final DPathConsumer2D dPathConsumer2D) {
            final double[] middle = this.middle;
            final double[] subdivTs = this.subdivTs;
            if (this.init_clipRectPad) {
                this.init_clipRectPad = false;
                this.initPaddedClip();
            }
            final int clipPoints = DHelpers.findClipPoints(this.curve, middle, subdivTs, n, n2, this.clipRectPad);
            if (clipPoints == 0) {
                return false;
            }
            double n3 = 0.0;
            for (int i = 0, n4 = 0; i < clipPoints; ++i, n4 += n) {
                final double n5 = subdivTs[i];
                DHelpers.subdivideAt((n5 - n3) / (1.0 - n3), middle, n4, middle, n4, n);
                n3 = n5;
            }
            for (int j = 0, n6 = 0; j <= clipPoints; ++j, n6 += n) {
                emitCurrent(n, middle, n6, dPathConsumer2D);
            }
            return true;
        }
        
        static void emitCurrent(final int n, final double[] array, final int n2, final DPathConsumer2D dPathConsumer2D) {
            if (n == 8) {
                dPathConsumer2D.curveTo(array[n2 + 2], array[n2 + 3], array[n2 + 4], array[n2 + 5], array[n2 + 6], array[n2 + 7]);
            }
            else if (n == 4) {
                dPathConsumer2D.lineTo(array[n2 + 2], array[n2 + 3]);
            }
            else {
                dPathConsumer2D.quadTo(array[n2 + 2], array[n2 + 3], array[n2 + 4], array[n2 + 5]);
            }
        }
        
        static {
            LEN_TH = MarlinProperties.getSubdividerMinLength();
            DO_CHECK_LENGTH = (CurveClipSplitter.LEN_TH > 0.0);
        }
    }
    
    static final class CurveBasicMonotonizer
    {
        private static final int MAX_N_CURVES = 11;
        private double lw2;
        int nbSplits;
        final double[] middle;
        private final double[] subdivTs;
        private final DCurve curve;
        
        CurveBasicMonotonizer(final DRendererContext dRendererContext) {
            this.middle = new double[68];
            this.subdivTs = new double[10];
            this.curve = dRendererContext.curve;
        }
        
        void init(final double n) {
            this.lw2 = n * n / 4.0;
        }
        
        CurveBasicMonotonizer curve(final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
            final double[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            middle[6] = n7;
            middle[7] = n8;
            final double[] subdivTs = this.subdivTs;
            final int subdivPoints = DHelpers.findSubdivPoints(this.curve, middle, subdivTs, 8, this.lw2);
            double n9 = 0.0;
            for (int i = 0, n10 = 0; i < subdivPoints; ++i, n10 += 6) {
                final double n11 = subdivTs[i];
                DHelpers.subdivideCubicAt((n11 - n9) / (1.0 - n9), middle, n10, middle, n10, n10 + 6);
                n9 = n11;
            }
            this.nbSplits = subdivPoints;
            return this;
        }
        
        CurveBasicMonotonizer quad(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            final double[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            final double[] subdivTs = this.subdivTs;
            final int subdivPoints = DHelpers.findSubdivPoints(this.curve, middle, subdivTs, 6, this.lw2);
            double n7 = 0.0;
            for (int i = 0, n8 = 0; i < subdivPoints; ++i, n8 += 4) {
                final double n9 = subdivTs[i];
                DHelpers.subdivideQuadAt((n9 - n7) / (1.0 - n7), middle, n8, middle, n8, n8 + 4);
                n7 = n9;
            }
            this.nbSplits = subdivPoints;
            return this;
        }
    }
    
    static final class PathTracer implements DPathConsumer2D
    {
        private final String prefix;
        private DPathConsumer2D out;
        
        PathTracer(final String s) {
            this.prefix = s + ": ";
        }
        
        PathTracer init(final DPathConsumer2D out) {
            this.out = out;
            return this;
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.log("moveTo (" + n + ", " + n2 + ')');
            this.out.moveTo(n, n2);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.log("lineTo (" + n + ", " + n2 + ')');
            this.out.lineTo(n, n2);
        }
        
        @Override
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.log("curveTo P1(" + n + ", " + n2 + ") P2(" + n3 + ", " + n4 + ") P3(" + n5 + ", " + n6 + ')');
            this.out.curveTo(n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.log("quadTo P1(" + n + ", " + n2 + ") P2(" + n3 + ", " + n4 + ')');
            this.out.quadTo(n, n2, n3, n4);
        }
        
        @Override
        public void closePath() {
            this.log("closePath");
            this.out.closePath();
        }
        
        @Override
        public void pathDone() {
            this.log("pathDone");
            this.out.pathDone();
        }
        
        private void log(final String s) {
            MarlinUtils.logInfo(this.prefix + s);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
}
