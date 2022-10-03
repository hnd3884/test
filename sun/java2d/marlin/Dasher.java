package sun.java2d.marlin;

import sun.awt.geom.PathConsumer2D;

final class Dasher implements PathConsumer2D, MarlinConst
{
    static final int REC_LIMIT = 16;
    static final float CURVE_LEN_ERR;
    static final float MIN_T_INC = 1.5258789E-5f;
    static final float MAX_CYCLES = 1.6E7f;
    private PathConsumer2D out;
    private float[] dash;
    private int dashLen;
    private float startPhase;
    private boolean startDashOn;
    private int startIdx;
    private boolean starting;
    private boolean needsMoveTo;
    private int idx;
    private boolean dashOn;
    private float phase;
    private float sx0;
    private float sy0;
    private float cx0;
    private float cy0;
    private final float[] curCurvepts;
    final RendererContext rdrCtx;
    boolean recycleDashes;
    private float[] firstSegmentsBuffer;
    private int firstSegidx;
    final FloatArrayCache.Reference dashes_ref;
    final FloatArrayCache.Reference firstSegmentsBuffer_ref;
    private float[] clipRect;
    private int cOutCode;
    private boolean subdivide;
    private final LengthIterator li;
    private final TransformingPathConsumer2D.CurveClipSplitter curveSplitter;
    private float cycleLen;
    private boolean outside;
    private float totalSkipLen;
    
    Dasher(final RendererContext rdrCtx) {
        this.cOutCode = 0;
        this.subdivide = Dasher.DO_CLIP_SUBDIVIDER;
        this.li = new LengthIterator();
        this.rdrCtx = rdrCtx;
        this.dashes_ref = rdrCtx.newDirtyFloatArrayRef(256);
        this.firstSegmentsBuffer_ref = rdrCtx.newDirtyFloatArrayRef(256);
        this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.initial;
        this.curCurvepts = new float[16];
        this.curveSplitter = rdrCtx.curveClipSplitter;
    }
    
    Dasher init(final PathConsumer2D out, final float[] dash, final int dashLen, float n, final boolean recycleDashes) {
        this.out = out;
        int startIdx = 0;
        this.dashOn = true;
        float cycleLen = 0.0f;
        for (int i = 0; i < dashLen; ++i) {
            cycleLen += dash[i];
        }
        this.cycleLen = cycleLen;
        final float n2 = n / cycleLen;
        if (n < 0.0f) {
            if (-n2 >= 1.6E7f) {
                n = 0.0f;
            }
            else {
                final int floor_int = FloatMath.floor_int(-n2);
                if ((floor_int & dashLen & 0x1) != 0x0) {
                    this.dashOn = !this.dashOn;
                }
                for (n += floor_int * cycleLen; n < 0.0f; n += dash[startIdx], this.dashOn = !this.dashOn) {
                    if (--startIdx < 0) {
                        startIdx = dashLen - 1;
                    }
                }
            }
        }
        else if (n > 0.0f) {
            if (n2 >= 1.6E7f) {
                n = 0.0f;
            }
            else {
                final int floor_int2 = FloatMath.floor_int(n2);
                if ((floor_int2 & dashLen & 0x1) != 0x0) {
                    this.dashOn = !this.dashOn;
                }
                float n3;
                for (n -= floor_int2 * cycleLen; n >= (n3 = dash[startIdx]); n -= n3, startIdx = (startIdx + 1) % dashLen, this.dashOn = !this.dashOn) {}
            }
        }
        this.dash = dash;
        this.dashLen = dashLen;
        this.phase = n;
        this.startPhase = n;
        this.startDashOn = this.dashOn;
        this.startIdx = startIdx;
        this.starting = true;
        this.needsMoveTo = false;
        this.firstSegidx = 0;
        this.recycleDashes = recycleDashes;
        if (this.rdrCtx.doClip) {
            this.clipRect = this.rdrCtx.clipRect;
        }
        else {
            this.clipRect = null;
            this.cOutCode = 0;
        }
        return this;
    }
    
    void dispose() {
        if (this.recycleDashes) {
            this.dash = this.dashes_ref.putArray(this.dash);
        }
        this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.putArray(this.firstSegmentsBuffer);
    }
    
    float[] copyDashArray(final float[] array) {
        final int length = array.length;
        float[] array2;
        if (length <= 256) {
            array2 = this.dashes_ref.initial;
        }
        else {
            if (Dasher.DO_STATS) {
                this.rdrCtx.stats.stat_array_dasher_dasher.add(length);
            }
            array2 = this.dashes_ref.getArray(length);
        }
        System.arraycopy(array, 0, array2, 0, length);
        return array2;
    }
    
    @Override
    public void moveTo(final float n, final float n2) {
        if (this.firstSegidx != 0) {
            this.out.moveTo(this.sx0, this.sy0);
            this.emitFirstSegments();
        }
        this.needsMoveTo = true;
        this.idx = this.startIdx;
        this.dashOn = this.startDashOn;
        this.phase = this.startPhase;
        this.cx0 = n;
        this.cy0 = n2;
        this.sx0 = n;
        this.sy0 = n2;
        this.starting = true;
        if (this.clipRect != null) {
            this.cOutCode = Helpers.outcode(n, n2, this.clipRect);
            this.outside = false;
            this.totalSkipLen = 0.0f;
        }
    }
    
    private void emitSeg(final float[] array, final int n, final int n2) {
        switch (n2) {
            case 8: {
                this.out.curveTo(array[n], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5]);
                return;
            }
            case 6: {
                this.out.quadTo(array[n], array[n + 1], array[n + 2], array[n + 3]);
                return;
            }
            case 4: {
                this.out.lineTo(array[n], array[n + 1]);
            }
            default: {}
        }
    }
    
    private void emitFirstSegments() {
        final float[] firstSegmentsBuffer = this.firstSegmentsBuffer;
        int n;
        for (int i = 0; i < this.firstSegidx; i += n - 1) {
            n = (int)firstSegmentsBuffer[i];
            this.emitSeg(firstSegmentsBuffer, i + 1, n);
        }
        this.firstSegidx = 0;
    }
    
    private void goTo(final float[] array, final int n, final int n2, final boolean b) {
        final int n3 = n + n2;
        final float cx0 = array[n3 - 4];
        final float cy0 = array[n3 - 3];
        if (b) {
            if (this.starting) {
                this.goTo_starting(array, n, n2);
            }
            else {
                if (this.needsMoveTo) {
                    this.needsMoveTo = false;
                    this.out.moveTo(this.cx0, this.cy0);
                }
                this.emitSeg(array, n, n2);
            }
        }
        else {
            if (this.starting) {
                this.starting = false;
            }
            this.needsMoveTo = true;
        }
        this.cx0 = cx0;
        this.cy0 = cy0;
    }
    
    private void goTo_starting(final float[] array, final int n, final int n2) {
        int n3 = n2 - 1;
        int firstSegidx = this.firstSegidx;
        float[] firstSegmentsBuffer = this.firstSegmentsBuffer;
        if (firstSegidx + n3 > firstSegmentsBuffer.length) {
            if (Dasher.DO_STATS) {
                this.rdrCtx.stats.stat_array_dasher_firstSegmentsBuffer.add(firstSegidx + n3);
            }
            firstSegmentsBuffer = (this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.widenArray(firstSegmentsBuffer, firstSegidx, firstSegidx + n3));
        }
        firstSegmentsBuffer[firstSegidx++] = (float)n2;
        --n3;
        System.arraycopy(array, n, firstSegmentsBuffer, firstSegidx, n3);
        this.firstSegidx = firstSegidx + n3;
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = Helpers.outcode(n, n2, this.clipRect);
            final int n3 = cOutCode | outcode;
            if (n3 != 0) {
                if ((cOutCode & outcode) != 0x0) {
                    this.cOutCode = outcode;
                    this.skipLineTo(n, n2);
                    return;
                }
                if (this.subdivide) {
                    this.subdivide = false;
                    final boolean splitLine = this.curveSplitter.splitLine(this.cx0, this.cy0, n, n2, n3, this);
                    this.subdivide = true;
                    if (splitLine) {
                        return;
                    }
                }
            }
            this.cOutCode = outcode;
            if (this.outside) {
                this.outside = false;
                this.skipLen();
            }
        }
        this._lineTo(n, n2);
    }
    
    private void _lineTo(final float n, final float n2) {
        final float n3 = n - this.cx0;
        final float n4 = n2 - this.cy0;
        final float n5 = n3 * n3 + n4 * n4;
        if (n5 == 0.0f) {
            return;
        }
        float n6 = (float)Math.sqrt(n5);
        final float n7 = n3 / n6;
        final float n8 = n4 / n6;
        final float[] curCurvepts = this.curCurvepts;
        final float[] dash = this.dash;
        final int dashLen = this.dashLen;
        int idx = this.idx;
        boolean dashOn = this.dashOn;
        float phase = this.phase;
        float n10;
        while (true) {
            final float n9 = dash[idx];
            n10 = n9 - phase;
            if (n6 <= n10) {
                break;
            }
            if (phase == 0.0f) {
                curCurvepts[0] = this.cx0 + n9 * n7;
                curCurvepts[1] = this.cy0 + n9 * n8;
            }
            else {
                curCurvepts[0] = this.cx0 + n10 * n7;
                curCurvepts[1] = this.cy0 + n10 * n8;
            }
            this.goTo(curCurvepts, 0, 4, dashOn);
            n6 -= n10;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
            phase = 0.0f;
        }
        curCurvepts[0] = n;
        curCurvepts[1] = n2;
        this.goTo(curCurvepts, 0, 4, dashOn);
        float phase2 = phase + n6;
        if (n6 == n10) {
            phase2 = 0.0f;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
        }
        this.idx = idx;
        this.dashOn = dashOn;
        this.phase = phase2;
    }
    
    private void skipLineTo(final float cx0, final float cy0) {
        final float n = cx0 - this.cx0;
        final float n2 = cy0 - this.cy0;
        float n3 = n * n + n2 * n2;
        if (n3 != 0.0f) {
            n3 = (float)Math.sqrt(n3);
        }
        this.outside = true;
        this.totalSkipLen += n3;
        this.needsMoveTo = true;
        this.starting = false;
        this.cx0 = cx0;
        this.cy0 = cy0;
    }
    
    public void skipLen() {
        float totalSkipLen = this.totalSkipLen;
        this.totalSkipLen = 0.0f;
        final float[] dash = this.dash;
        final int dashLen = this.dashLen;
        int idx = this.idx;
        int dashOn = this.dashOn ? 1 : 0;
        float phase = this.phase;
        final long n = (long)Math.floor(totalSkipLen / this.cycleLen) - 2L;
        if (n > 0L) {
            totalSkipLen -= this.cycleLen * n;
            final long n2 = n * dashLen;
            idx = (int)(n2 + idx) % dashLen;
            dashOn = (((n2 + dashOn & 0x1L) == 0x1L) ? 1 : 0);
        }
        float n3;
        while (true) {
            n3 = dash[idx] - phase;
            if (totalSkipLen <= n3) {
                break;
            }
            totalSkipLen -= n3;
            idx = (idx + 1) % dashLen;
            dashOn = ((dashOn != 0) ? 0 : 1);
            phase = 0.0f;
        }
        float phase2 = phase + totalSkipLen;
        if (totalSkipLen == n3) {
            phase2 = 0.0f;
            idx = (idx + 1) % dashLen;
            dashOn = ((dashOn != 0) ? 0 : 1);
        }
        this.idx = idx;
        this.dashOn = (dashOn != 0);
        this.phase = phase2;
    }
    
    private void somethingTo(final int n) {
        final float[] curCurvepts = this.curCurvepts;
        if (pointCurve(curCurvepts, n)) {
            return;
        }
        final LengthIterator li = this.li;
        final float[] dash = this.dash;
        final int dashLen = this.dashLen;
        li.initializeIterationOnCurve(curCurvepts, n);
        int idx = this.idx;
        boolean dashOn = this.dashOn;
        float phase = this.phase;
        int n2 = 0;
        float n3 = 0.0f;
        float next;
        for (float n4 = dash[idx] - phase; (next = li.next(n4)) < 1.0f; n4 = dash[idx]) {
            if (next != 0.0f) {
                Helpers.subdivideAt((next - n3) / (1.0f - n3), curCurvepts, n2, curCurvepts, 0, n);
                n3 = next;
                this.goTo(curCurvepts, 2, n, dashOn);
                n2 = n;
            }
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
            phase = 0.0f;
        }
        this.goTo(curCurvepts, n2 + 2, n, dashOn);
        float phase2 = phase + li.lastSegLen();
        if (phase2 >= dash[idx]) {
            phase2 = 0.0f;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
        }
        this.idx = idx;
        this.dashOn = dashOn;
        this.phase = phase2;
        li.reset();
    }
    
    private void skipSomethingTo(final int n) {
        final float[] curCurvepts = this.curCurvepts;
        if (pointCurve(curCurvepts, n)) {
            return;
        }
        final LengthIterator li = this.li;
        li.initializeIterationOnCurve(curCurvepts, n);
        final float totalLength = li.totalLength();
        this.outside = true;
        this.totalSkipLen += totalLength;
        this.needsMoveTo = true;
        this.starting = false;
    }
    
    private static boolean pointCurve(final float[] array, final int n) {
        for (int i = 2; i < n; ++i) {
            if (array[i] != array[i - 2]) {
                return false;
            }
        }
        return true;
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
                    this.skipCurveTo(n, n2, n3, n4, n5, n6);
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
            if (this.outside) {
                this.outside = false;
                this.skipLen();
            }
        }
        this._curveTo(n, n2, n3, n4, n5, n6);
    }
    
    private void _curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float[] curCurvepts = this.curCurvepts;
        final TransformingPathConsumer2D.CurveBasicMonotonizer curve = this.rdrCtx.monotonizer.curve(this.cx0, this.cy0, n, n2, n3, n4, n5, n6);
        final int nbSplits = curve.nbSplits;
        final float[] middle = curve.middle;
        for (int i = 0, n7 = 0; i <= nbSplits; ++i, n7 += 6) {
            System.arraycopy(middle, n7, curCurvepts, 0, 8);
            this.somethingTo(8);
        }
    }
    
    private void skipCurveTo(final float n, final float n2, final float n3, final float n4, final float cx0, final float cy0) {
        final float[] curCurvepts = this.curCurvepts;
        curCurvepts[0] = this.cx0;
        curCurvepts[1] = this.cy0;
        curCurvepts[2] = n;
        curCurvepts[3] = n2;
        curCurvepts[4] = n3;
        curCurvepts[5] = n4;
        curCurvepts[6] = cx0;
        curCurvepts[7] = cy0;
        this.skipSomethingTo(8);
        this.cx0 = cx0;
        this.cy0 = cy0;
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
                    this.skipQuadTo(n, n2, n3, n4);
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
            if (this.outside) {
                this.outside = false;
                this.skipLen();
            }
        }
        this._quadTo(n, n2, n3, n4);
    }
    
    private void _quadTo(final float n, final float n2, final float n3, final float n4) {
        final float[] curCurvepts = this.curCurvepts;
        final TransformingPathConsumer2D.CurveBasicMonotonizer quad = this.rdrCtx.monotonizer.quad(this.cx0, this.cy0, n, n2, n3, n4);
        final int nbSplits = quad.nbSplits;
        final float[] middle = quad.middle;
        for (int i = 0, n5 = 0; i <= nbSplits; ++i, n5 += 4) {
            System.arraycopy(middle, n5, curCurvepts, 0, 8);
            this.somethingTo(6);
        }
    }
    
    private void skipQuadTo(final float n, final float n2, final float cx0, final float cy0) {
        final float[] curCurvepts = this.curCurvepts;
        curCurvepts[0] = this.cx0;
        curCurvepts[1] = this.cy0;
        curCurvepts[2] = n;
        curCurvepts[3] = n2;
        curCurvepts[4] = cx0;
        curCurvepts[5] = cy0;
        this.skipSomethingTo(6);
        this.cx0 = cx0;
        this.cy0 = cy0;
    }
    
    @Override
    public void closePath() {
        if (this.cx0 != this.sx0 || this.cy0 != this.sy0) {
            this.lineTo(this.sx0, this.sy0);
        }
        if (this.firstSegidx != 0) {
            if (!this.dashOn || this.needsMoveTo) {
                this.out.moveTo(this.sx0, this.sy0);
            }
            this.emitFirstSegments();
        }
        this.moveTo(this.sx0, this.sy0);
    }
    
    @Override
    public void pathDone() {
        if (this.firstSegidx != 0) {
            this.out.moveTo(this.sx0, this.sy0);
            this.emitFirstSegments();
        }
        this.out.pathDone();
        this.dispose();
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Dasher does not use a native consumer");
    }
    
    static {
        CURVE_LEN_ERR = MarlinProperties.getCurveLengthError();
    }
    
    static final class LengthIterator
    {
        private final float[][] recCurveStack;
        private final boolean[] sidesRight;
        private int curveType;
        private float nextT;
        private float lenAtNextT;
        private float lastT;
        private float lenAtLastT;
        private float lenAtLastSplit;
        private float lastSegLen;
        private int recLevel;
        private boolean done;
        private final float[] curLeafCtrlPolyLengths;
        private int cachedHaveLowAcceleration;
        private final float[] nextRoots;
        private final float[] flatLeafCoefCache;
        
        LengthIterator() {
            this.curLeafCtrlPolyLengths = new float[3];
            this.cachedHaveLowAcceleration = -1;
            this.nextRoots = new float[4];
            this.flatLeafCoefCache = new float[] { 0.0f, 0.0f, -1.0f, 0.0f };
            this.recCurveStack = new float[17][8];
            this.sidesRight = new boolean[16];
            this.nextT = Float.MAX_VALUE;
            this.lenAtNextT = Float.MAX_VALUE;
            this.lenAtLastSplit = Float.MIN_VALUE;
            this.recLevel = Integer.MIN_VALUE;
            this.lastSegLen = Float.MAX_VALUE;
            this.done = true;
        }
        
        void reset() {
        }
        
        void initializeIterationOnCurve(final float[] array, final int curveType) {
            System.arraycopy(array, 0, this.recCurveStack[0], 0, 8);
            this.curveType = curveType;
            this.recLevel = 0;
            this.lastT = 0.0f;
            this.lenAtLastT = 0.0f;
            this.nextT = 0.0f;
            this.lenAtNextT = 0.0f;
            this.goLeft();
            this.lenAtLastSplit = 0.0f;
            if (this.recLevel > 0) {
                this.sidesRight[0] = false;
                this.done = false;
            }
            else {
                this.sidesRight[0] = true;
                this.done = true;
            }
            this.lastSegLen = 0.0f;
        }
        
        private boolean haveLowAcceleration(final float n) {
            if (this.cachedHaveLowAcceleration != -1) {
                return this.cachedHaveLowAcceleration == 1;
            }
            final float n2 = this.curLeafCtrlPolyLengths[0];
            final float n3 = this.curLeafCtrlPolyLengths[1];
            if (!Helpers.within(n2, n3, n * n3)) {
                this.cachedHaveLowAcceleration = 0;
                return false;
            }
            if (this.curveType == 8) {
                final float n4 = this.curLeafCtrlPolyLengths[2];
                final float n5 = n * n4;
                if (!Helpers.within(n3, n4, n5) || !Helpers.within(n2, n4, n5)) {
                    this.cachedHaveLowAcceleration = 0;
                    return false;
                }
            }
            this.cachedHaveLowAcceleration = 1;
            return true;
        }
        
        float next(final float lastSegLen) {
            final float lenAtLastSplit = this.lenAtLastSplit + lastSegLen;
            while (this.lenAtNextT < lenAtLastSplit) {
                if (this.done) {
                    this.lastSegLen = this.lenAtNextT - this.lenAtLastSplit;
                    return 1.0f;
                }
                this.goToNextLeaf();
            }
            this.lenAtLastSplit = lenAtLastSplit;
            float n = (lenAtLastSplit - this.lenAtLastT) / (this.lenAtNextT - this.lenAtLastT);
            if (!this.haveLowAcceleration(0.05f)) {
                final float[] flatLeafCoefCache = this.flatLeafCoefCache;
                if (flatLeafCoefCache[2] < 0.0f) {
                    final float n2 = this.curLeafCtrlPolyLengths[0];
                    final float n3 = n2 + this.curLeafCtrlPolyLengths[1];
                    if (this.curveType == 8) {
                        final float n4 = n3 + this.curLeafCtrlPolyLengths[2];
                        flatLeafCoefCache[0] = 3.0f * (n2 - n3) + n4;
                        flatLeafCoefCache[1] = 3.0f * (n3 - 2.0f * n2);
                        flatLeafCoefCache[2] = 3.0f * n2;
                        flatLeafCoefCache[3] = -n4;
                    }
                    else if (this.curveType == 6) {
                        flatLeafCoefCache[0] = 0.0f;
                        flatLeafCoefCache[1] = n3 - 2.0f * n2;
                        flatLeafCoefCache[2] = 2.0f * n2;
                        flatLeafCoefCache[3] = -n3;
                    }
                }
                if (Helpers.cubicRootsInAB(flatLeafCoefCache[0], flatLeafCoefCache[1], flatLeafCoefCache[2], n * flatLeafCoefCache[3], this.nextRoots, 0, 0.0f, 1.0f) == 1 && !Float.isNaN(this.nextRoots[0])) {
                    n = this.nextRoots[0];
                }
            }
            float n5 = n * (this.nextT - this.lastT) + this.lastT;
            if (n5 >= 1.0f) {
                n5 = 1.0f;
                this.done = true;
            }
            this.lastSegLen = lastSegLen;
            return n5;
        }
        
        float totalLength() {
            while (!this.done) {
                this.goToNextLeaf();
            }
            this.reset();
            return this.lenAtNextT;
        }
        
        float lastSegLen() {
            return this.lastSegLen;
        }
        
        private void goToNextLeaf() {
            final boolean[] sidesRight = this.sidesRight;
            int recLevel = this.recLevel;
            --recLevel;
            while (sidesRight[recLevel]) {
                if (recLevel == 0) {
                    this.recLevel = 0;
                    this.done = true;
                    return;
                }
                --recLevel;
            }
            sidesRight[recLevel] = true;
            System.arraycopy(this.recCurveStack[recLevel++], 0, this.recCurveStack[recLevel], 0, 8);
            this.recLevel = recLevel;
            this.goLeft();
        }
        
        private void goLeft() {
            final float onLeaf = this.onLeaf();
            if (onLeaf >= 0.0f) {
                this.lastT = this.nextT;
                this.lenAtLastT = this.lenAtNextT;
                this.nextT += (1 << 16 - this.recLevel) * 1.5258789E-5f;
                this.lenAtNextT += onLeaf;
                this.flatLeafCoefCache[2] = -1.0f;
                this.cachedHaveLowAcceleration = -1;
            }
            else {
                Helpers.subdivide(this.recCurveStack[this.recLevel], this.recCurveStack[this.recLevel + 1], this.recCurveStack[this.recLevel], this.curveType);
                this.sidesRight[this.recLevel] = false;
                ++this.recLevel;
                this.goLeft();
            }
        }
        
        private float onLeaf() {
            final float[] array = this.recCurveStack[this.recLevel];
            final int curveType = this.curveType;
            float n = 0.0f;
            float n2 = array[0];
            float n3 = array[1];
            for (int i = 2; i < curveType; i += 2) {
                final float n4 = array[i];
                final float n5 = array[i + 1];
                final float linelen = Helpers.linelen(n2, n3, n4, n5);
                n += linelen;
                this.curLeafCtrlPolyLengths[(i >> 1) - 1] = linelen;
                n2 = n4;
                n3 = n5;
            }
            final float linelen2 = Helpers.linelen(array[0], array[1], n2, n3);
            if (n - linelen2 < Dasher.CURVE_LEN_ERR || this.recLevel == 16) {
                return (n + linelen2) / 2.0f;
            }
            return -1.0f;
        }
    }
}
