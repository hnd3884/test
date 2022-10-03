package sun.java2d.marlin;

import java.util.Arrays;
import java.awt.geom.AffineTransform;
import sun.awt.geom.PathConsumer2D;
import java.awt.geom.Path2D;

final class TransformingPathConsumer2D
{
    static final float CLIP_RECT_PADDING = 1.0f;
    private final RendererContext rdrCtx;
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
    
    TransformingPathConsumer2D(final RendererContext rdrCtx) {
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
    
    PathConsumer2D wrapPath2D(final Path2D.Float float1) {
        return this.wp_Path2DWrapper.init(float1);
    }
    
    PathConsumer2D traceInput(final PathConsumer2D pathConsumer2D) {
        return this.tracerInput.init(pathConsumer2D);
    }
    
    PathConsumer2D traceClosedPathDetector(final PathConsumer2D pathConsumer2D) {
        return this.tracerCPDetector.init(pathConsumer2D);
    }
    
    PathConsumer2D traceFiller(final PathConsumer2D pathConsumer2D) {
        return this.tracerFiller.init(pathConsumer2D);
    }
    
    PathConsumer2D traceStroker(final PathConsumer2D pathConsumer2D) {
        return this.tracerStroker.init(pathConsumer2D);
    }
    
    PathConsumer2D traceDasher(final PathConsumer2D pathConsumer2D) {
        return this.tracerDasher.init(pathConsumer2D);
    }
    
    PathConsumer2D detectClosedPath(final PathConsumer2D pathConsumer2D) {
        return this.cpDetector.init(pathConsumer2D);
    }
    
    PathConsumer2D pathClipper(final PathConsumer2D pathConsumer2D) {
        return this.pathClipper.init(pathConsumer2D);
    }
    
    PathConsumer2D deltaTransformConsumer(final PathConsumer2D pathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return pathConsumer2D;
        }
        final float n = (float)affineTransform.getScaleX();
        final float n2 = (float)affineTransform.getShearX();
        final float n3 = (float)affineTransform.getShearY();
        final float n4 = (float)affineTransform.getScaleY();
        if (n2 != 0.0f || n3 != 0.0f) {
            if (this.rdrCtx.doClip) {
                this.rdrCtx.clipInvScale = adjustClipInverseDelta(this.rdrCtx.clipRect, n, n2, n3, n4);
            }
            return this.dt_DeltaTransformFilter.init(pathConsumer2D, n, n2, n3, n4);
        }
        if (n == 1.0f && n4 == 1.0f) {
            return pathConsumer2D;
        }
        if (this.rdrCtx.doClip) {
            this.rdrCtx.clipInvScale = adjustClipScale(this.rdrCtx.clipRect, n, n4);
        }
        return this.dt_DeltaScaleFilter.init(pathConsumer2D, n, n4);
    }
    
    private static float adjustClipScale(final float[] array, final float n, final float n2) {
        final float n3 = 1.0f / n2;
        final int n4 = 0;
        array[n4] *= n3;
        final int n5 = 1;
        array[n5] *= n3;
        if (array[1] < array[0]) {
            final float n6 = array[0];
            array[0] = array[1];
            array[1] = n6;
        }
        final float n7 = 1.0f / n;
        final int n8 = 2;
        array[n8] *= n7;
        final int n9 = 3;
        array[n9] *= n7;
        if (array[3] < array[2]) {
            final float n10 = array[2];
            array[2] = array[3];
            array[3] = n10;
        }
        if (MarlinConst.DO_LOG_CLIP) {
            MarlinUtils.logInfo("clipRect (ClipScale): " + Arrays.toString(array));
        }
        return 0.5f * (Math.abs(n7) + Math.abs(n3));
    }
    
    private static float adjustClipInverseDelta(final float[] array, final float n, final float n2, final float n3, final float n4) {
        final float n5 = n * n4 - n2 * n3;
        final float n6 = n4 / n5;
        final float n7 = -n2 / n5;
        final float n8 = -n3 / n5;
        final float n9 = n / n5;
        final float n10 = array[2] * n6 + array[0] * n7;
        final float n11 = array[2] * n8 + array[0] * n9;
        float n13;
        float n12 = n13 = n10;
        float n15;
        float n14 = n15 = n11;
        final float n16 = array[3] * n6 + array[0] * n7;
        final float n17 = array[3] * n8 + array[0] * n9;
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
        final float n18 = array[2] * n6 + array[1] * n7;
        final float n19 = array[2] * n8 + array[1] * n9;
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
        final float n20 = array[3] * n6 + array[1] * n7;
        final float n21 = array[3] * n8 + array[1] * n9;
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
        return 0.5f * ((float)Math.sqrt(n6 * n6 + n7 * n7) + (float)Math.sqrt(n8 * n8 + n9 * n9));
    }
    
    PathConsumer2D inverseDeltaTransformConsumer(final PathConsumer2D pathConsumer2D, final AffineTransform affineTransform) {
        if (affineTransform == null) {
            return pathConsumer2D;
        }
        final float n = (float)affineTransform.getScaleX();
        final float n2 = (float)affineTransform.getShearX();
        final float n3 = (float)affineTransform.getShearY();
        final float n4 = (float)affineTransform.getScaleY();
        if (n2 != 0.0f || n3 != 0.0f) {
            final float n5 = n * n4 - n2 * n3;
            return this.iv_DeltaTransformFilter.init(pathConsumer2D, n4 / n5, -n2 / n5, -n3 / n5, n / n5);
        }
        if (n == 1.0f && n4 == 1.0f) {
            return pathConsumer2D;
        }
        return this.iv_DeltaScaleFilter.init(pathConsumer2D, 1.0f / n, 1.0f / n4);
    }
    
    static final class DeltaScaleFilter implements PathConsumer2D
    {
        private PathConsumer2D out;
        private float sx;
        private float sy;
        
        DeltaScaleFilter init(final PathConsumer2D out, final float sx, final float sy) {
            this.out = out;
            this.sx = sx;
            this.sy = sy;
            return this;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.sx, n2 * this.sy);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.sx, n2 * this.sy, n3 * this.sx, n4 * this.sy);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
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
    
    static final class DeltaTransformFilter implements PathConsumer2D
    {
        private PathConsumer2D out;
        private float mxx;
        private float mxy;
        private float myx;
        private float myy;
        
        DeltaTransformFilter init(final PathConsumer2D out, final float mxx, final float mxy, final float myx, final float myy) {
            this.out = out;
            this.mxx = mxx;
            this.mxy = mxy;
            this.myx = myx;
            this.myy = myy;
            return this;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.out.moveTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.out.lineTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.out.quadTo(n * this.mxx + n2 * this.mxy, n * this.myx + n2 * this.myy, n3 * this.mxx + n4 * this.mxy, n3 * this.myx + n4 * this.myy);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
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
    
    static final class Path2DWrapper implements PathConsumer2D
    {
        private Path2D.Float p2d;
        
        Path2DWrapper init(final Path2D.Float p2d) {
            this.p2d = p2d;
            return this;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.p2d.moveTo(n, n2);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
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
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.p2d.curveTo(n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.p2d.quadTo(n, n2, n3, n4);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
    
    static final class ClosedPathDetector implements PathConsumer2D
    {
        private final RendererContext rdrCtx;
        private final Helpers.PolyStack stack;
        private PathConsumer2D out;
        
        ClosedPathDetector(final RendererContext rdrCtx) {
            this.rdrCtx = rdrCtx;
            this.stack = ((rdrCtx.stats != null) ? new Helpers.PolyStack(rdrCtx, rdrCtx.stats.stat_cpd_polystack_types, rdrCtx.stats.stat_cpd_polystack_curves, rdrCtx.stats.hist_cpd_polystack_curves, rdrCtx.stats.stat_array_cpd_polystack_curves, rdrCtx.stats.stat_array_cpd_polystack_types) : new Helpers.PolyStack(rdrCtx));
        }
        
        ClosedPathDetector init(final PathConsumer2D out) {
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
        public void moveTo(final float n, final float n2) {
            this.finish(false);
            this.out.moveTo(n, n2);
        }
        
        private void finish(final boolean closedPath) {
            this.rdrCtx.closedPath = closedPath;
            this.stack.pullAll(this.out);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.stack.pushLine(n, n2);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.stack.pushCubic(n5, n6, n3, n4, n, n2);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            this.stack.pushQuad(n3, n4, n, n2);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
    
    static final class PathClipFilter implements PathConsumer2D
    {
        private PathConsumer2D out;
        private final float[] clipRect;
        private final float[] corners;
        private boolean init_corners;
        private final Helpers.IndexStack stack;
        private int cOutCode;
        private int gOutCode;
        private boolean outside;
        private float cx0;
        private float cy0;
        private float cox0;
        private float coy0;
        private boolean subdivide;
        private final CurveClipSplitter curveSplitter;
        
        PathClipFilter(final RendererContext rendererContext) {
            this.corners = new float[8];
            this.init_corners = false;
            this.cOutCode = 0;
            this.gOutCode = 15;
            this.outside = false;
            this.subdivide = MarlinConst.DO_CLIP_SUBDIVIDER;
            this.clipRect = rendererContext.clipRect;
            this.curveSplitter = rendererContext.curveClipSplitter;
            this.stack = ((rendererContext.stats != null) ? new Helpers.IndexStack(rendererContext, rendererContext.stats.stat_pcf_idxstack_indices, rendererContext.stats.hist_pcf_idxstack_indices, rendererContext.stats.stat_array_pcf_idxstack_indices) : new Helpers.IndexStack(rendererContext));
        }
        
        PathClipFilter init(final PathConsumer2D out) {
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
                    final float[] corners = this.corners;
                    final float[] clipRect = this.clipRect;
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
        public void moveTo(final float cx0, final float cy0) {
            this.finishPath();
            this.cOutCode = Helpers.outcode(cx0, cy0, this.clipRect);
            this.outside = false;
            this.out.moveTo(cx0, cy0);
            this.cx0 = cx0;
            this.cy0 = cy0;
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            final int cOutCode = this.cOutCode;
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
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
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            final int cOutCode = this.cOutCode;
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
            final int outcode2 = Helpers.outcode(n3, n4, this.clipRect);
            final int outcode3 = Helpers.outcode(n5, n6, this.clipRect);
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
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
            final int cOutCode = this.cOutCode;
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
            final int outcode2 = Helpers.outcode(n3, n4, this.clipRect);
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
        static final float LEN_TH;
        static final boolean DO_CHECK_LENGTH;
        private static final boolean TRACE = false;
        private static final int MAX_N_CURVES = 12;
        private final RendererContext rdrCtx;
        private float minLength;
        final float[] clipRect;
        final float[] clipRectPad;
        private boolean init_clipRectPad;
        final float[] middle;
        private final float[] subdivTs;
        private final Curve curve;
        
        CurveClipSplitter(final RendererContext rdrCtx) {
            this.clipRectPad = new float[4];
            this.init_clipRectPad = false;
            this.middle = new float[98];
            this.subdivTs = new float[12];
            this.rdrCtx = rdrCtx;
            this.clipRect = rdrCtx.clipRect;
            this.curve = rdrCtx.curve;
        }
        
        void init() {
            this.init_clipRectPad = true;
            if (CurveClipSplitter.DO_CHECK_LENGTH) {
                this.minLength = ((this.rdrCtx.clipInvScale == 0.0f) ? CurveClipSplitter.LEN_TH : (CurveClipSplitter.LEN_TH * this.rdrCtx.clipInvScale));
                if (MarlinConst.DO_LOG_CLIP) {
                    MarlinUtils.logInfo("CurveClipSplitter.minLength = " + this.minLength);
                }
            }
        }
        
        private void initPaddedClip() {
            final float[] clipRect = this.clipRect;
            final float[] clipRectPad = this.clipRectPad;
            clipRectPad[0] = clipRect[0] - 1.0f;
            clipRectPad[1] = clipRect[1] + 1.0f;
            clipRectPad[2] = clipRect[2] - 1.0f;
            clipRectPad[3] = clipRect[3] + 1.0f;
        }
        
        boolean splitLine(final float n, final float n2, final float n3, final float n4, final int n5, final PathConsumer2D pathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && Helpers.fastLineLen(n, n2, n3, n4) <= this.minLength) {
                return false;
            }
            final float[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            return this.subdivideAtIntersections(4, n5, pathConsumer2D);
        }
        
        boolean splitQuad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final int n7, final PathConsumer2D pathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && Helpers.fastQuadLen(n, n2, n3, n4, n5, n6) <= this.minLength) {
                return false;
            }
            final float[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            return this.subdivideAtIntersections(6, n7, pathConsumer2D);
        }
        
        boolean splitCurve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final int n9, final PathConsumer2D pathConsumer2D) {
            if (CurveClipSplitter.DO_CHECK_LENGTH && Helpers.fastCurvelen(n, n2, n3, n4, n5, n6, n7, n8) <= this.minLength) {
                return false;
            }
            final float[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            middle[6] = n7;
            middle[7] = n8;
            return this.subdivideAtIntersections(8, n9, pathConsumer2D);
        }
        
        private boolean subdivideAtIntersections(final int n, final int n2, final PathConsumer2D pathConsumer2D) {
            final float[] middle = this.middle;
            final float[] subdivTs = this.subdivTs;
            if (this.init_clipRectPad) {
                this.init_clipRectPad = false;
                this.initPaddedClip();
            }
            final int clipPoints = Helpers.findClipPoints(this.curve, middle, subdivTs, n, n2, this.clipRectPad);
            if (clipPoints == 0) {
                return false;
            }
            float n3 = 0.0f;
            for (int i = 0, n4 = 0; i < clipPoints; ++i, n4 += n) {
                final float n5 = subdivTs[i];
                Helpers.subdivideAt((n5 - n3) / (1.0f - n3), middle, n4, middle, n4, n);
                n3 = n5;
            }
            for (int j = 0, n6 = 0; j <= clipPoints; ++j, n6 += n) {
                emitCurrent(n, middle, n6, pathConsumer2D);
            }
            return true;
        }
        
        static void emitCurrent(final int n, final float[] array, final int n2, final PathConsumer2D pathConsumer2D) {
            if (n == 8) {
                pathConsumer2D.curveTo(array[n2 + 2], array[n2 + 3], array[n2 + 4], array[n2 + 5], array[n2 + 6], array[n2 + 7]);
            }
            else if (n == 4) {
                pathConsumer2D.lineTo(array[n2 + 2], array[n2 + 3]);
            }
            else {
                pathConsumer2D.quadTo(array[n2 + 2], array[n2 + 3], array[n2 + 4], array[n2 + 5]);
            }
        }
        
        static {
            LEN_TH = MarlinProperties.getSubdividerMinLength();
            DO_CHECK_LENGTH = (CurveClipSplitter.LEN_TH > 0.0f);
        }
    }
    
    static final class CurveBasicMonotonizer
    {
        private static final int MAX_N_CURVES = 11;
        private float lw2;
        int nbSplits;
        final float[] middle;
        private final float[] subdivTs;
        private final Curve curve;
        
        CurveBasicMonotonizer(final RendererContext rendererContext) {
            this.middle = new float[68];
            this.subdivTs = new float[10];
            this.curve = rendererContext.curve;
        }
        
        void init(final float n) {
            this.lw2 = n * n / 4.0f;
        }
        
        CurveBasicMonotonizer curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
            final float[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            middle[6] = n7;
            middle[7] = n8;
            final float[] subdivTs = this.subdivTs;
            final int subdivPoints = Helpers.findSubdivPoints(this.curve, middle, subdivTs, 8, this.lw2);
            float n9 = 0.0f;
            for (int i = 0, n10 = 0; i < subdivPoints; ++i, n10 += 6) {
                final float n11 = subdivTs[i];
                Helpers.subdivideCubicAt((n11 - n9) / (1.0f - n9), middle, n10, middle, n10, n10 + 6);
                n9 = n11;
            }
            this.nbSplits = subdivPoints;
            return this;
        }
        
        CurveBasicMonotonizer quad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            final float[] middle = this.middle;
            middle[0] = n;
            middle[1] = n2;
            middle[2] = n3;
            middle[3] = n4;
            middle[4] = n5;
            middle[5] = n6;
            final float[] subdivTs = this.subdivTs;
            final int subdivPoints = Helpers.findSubdivPoints(this.curve, middle, subdivTs, 6, this.lw2);
            float n7 = 0.0f;
            for (int i = 0, n8 = 0; i < subdivPoints; ++i, n8 += 4) {
                final float n9 = subdivTs[i];
                Helpers.subdivideQuadAt((n9 - n7) / (1.0f - n7), middle, n8, middle, n8, n8 + 4);
                n7 = n9;
            }
            this.nbSplits = subdivPoints;
            return this;
        }
    }
    
    static final class PathTracer implements PathConsumer2D
    {
        private final String prefix;
        private PathConsumer2D out;
        
        PathTracer(final String s) {
            this.prefix = s + ": ";
        }
        
        PathTracer init(final PathConsumer2D out) {
            this.out = out;
            return this;
        }
        
        @Override
        public void moveTo(final float n, final float n2) {
            this.log("moveTo (" + n + ", " + n2 + ')');
            this.out.moveTo(n, n2);
        }
        
        @Override
        public void lineTo(final float n, final float n2) {
            this.log("lineTo (" + n + ", " + n2 + ')');
            this.out.lineTo(n, n2);
        }
        
        @Override
        public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
            this.log("curveTo P1(" + n + ", " + n2 + ") P2(" + n3 + ", " + n4 + ") P3(" + n5 + ", " + n6 + ')');
            this.out.curveTo(n, n2, n3, n4, n5, n6);
        }
        
        @Override
        public void quadTo(final float n, final float n2, final float n3, final float n4) {
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
