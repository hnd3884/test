package sun.java2d.marlin;

final class DDasher implements DPathConsumer2D, MarlinConst
{
    static final int REC_LIMIT = 16;
    static final double CURVE_LEN_ERR;
    static final double MIN_T_INC = 1.52587890625E-5;
    static final double MAX_CYCLES = 1.6E7;
    private DPathConsumer2D out;
    private double[] dash;
    private int dashLen;
    private double startPhase;
    private boolean startDashOn;
    private int startIdx;
    private boolean starting;
    private boolean needsMoveTo;
    private int idx;
    private boolean dashOn;
    private double phase;
    private double sx0;
    private double sy0;
    private double cx0;
    private double cy0;
    private final double[] curCurvepts;
    final DRendererContext rdrCtx;
    boolean recycleDashes;
    private double[] firstSegmentsBuffer;
    private int firstSegidx;
    final DoubleArrayCache.Reference dashes_ref;
    final DoubleArrayCache.Reference firstSegmentsBuffer_ref;
    private double[] clipRect;
    private int cOutCode;
    private boolean subdivide;
    private final LengthIterator li;
    private final DTransformingPathConsumer2D.CurveClipSplitter curveSplitter;
    private double cycleLen;
    private boolean outside;
    private double totalSkipLen;
    
    DDasher(final DRendererContext rdrCtx) {
        this.cOutCode = 0;
        this.subdivide = DDasher.DO_CLIP_SUBDIVIDER;
        this.li = new LengthIterator();
        this.rdrCtx = rdrCtx;
        this.dashes_ref = rdrCtx.newDirtyDoubleArrayRef(256);
        this.firstSegmentsBuffer_ref = rdrCtx.newDirtyDoubleArrayRef(256);
        this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.initial;
        this.curCurvepts = new double[16];
        this.curveSplitter = rdrCtx.curveClipSplitter;
    }
    
    DDasher init(final DPathConsumer2D out, final double[] dash, final int dashLen, double n, final boolean recycleDashes) {
        this.out = out;
        int startIdx = 0;
        this.dashOn = true;
        double cycleLen = 0.0;
        for (int i = 0; i < dashLen; ++i) {
            cycleLen += dash[i];
        }
        this.cycleLen = cycleLen;
        final double n2 = n / cycleLen;
        if (n < 0.0) {
            if (-n2 >= 1.6E7) {
                n = 0.0;
            }
            else {
                final int floor_int = FloatMath.floor_int(-n2);
                if ((floor_int & dashLen & 0x1) != 0x0) {
                    this.dashOn = !this.dashOn;
                }
                for (n += floor_int * cycleLen; n < 0.0; n += dash[startIdx], this.dashOn = !this.dashOn) {
                    if (--startIdx < 0) {
                        startIdx = dashLen - 1;
                    }
                }
            }
        }
        else if (n > 0.0) {
            if (n2 >= 1.6E7) {
                n = 0.0;
            }
            else {
                final int floor_int2 = FloatMath.floor_int(n2);
                if ((floor_int2 & dashLen & 0x1) != 0x0) {
                    this.dashOn = !this.dashOn;
                }
                double n3;
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
    
    double[] copyDashArray(final float[] array) {
        final int length = array.length;
        double[] array2;
        if (length <= 256) {
            array2 = this.dashes_ref.initial;
        }
        else {
            if (DDasher.DO_STATS) {
                this.rdrCtx.stats.stat_array_dasher_dasher.add(length);
            }
            array2 = this.dashes_ref.getArray(length);
        }
        for (int i = 0; i < length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    @Override
    public void moveTo(final double n, final double n2) {
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
            this.cOutCode = DHelpers.outcode(n, n2, this.clipRect);
            this.outside = false;
            this.totalSkipLen = 0.0;
        }
    }
    
    private void emitSeg(final double[] array, final int n, final int n2) {
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
        final double[] firstSegmentsBuffer = this.firstSegmentsBuffer;
        int n;
        for (int i = 0; i < this.firstSegidx; i += n - 1) {
            n = (int)firstSegmentsBuffer[i];
            this.emitSeg(firstSegmentsBuffer, i + 1, n);
        }
        this.firstSegidx = 0;
    }
    
    private void goTo(final double[] array, final int n, final int n2, final boolean b) {
        final int n3 = n + n2;
        final double cx0 = array[n3 - 4];
        final double cy0 = array[n3 - 3];
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
    
    private void goTo_starting(final double[] array, final int n, final int n2) {
        int n3 = n2 - 1;
        int firstSegidx = this.firstSegidx;
        double[] firstSegmentsBuffer = this.firstSegmentsBuffer;
        if (firstSegidx + n3 > firstSegmentsBuffer.length) {
            if (DDasher.DO_STATS) {
                this.rdrCtx.stats.stat_array_dasher_firstSegmentsBuffer.add(firstSegidx + n3);
            }
            firstSegmentsBuffer = (this.firstSegmentsBuffer = this.firstSegmentsBuffer_ref.widenArray(firstSegmentsBuffer, firstSegidx, firstSegidx + n3));
        }
        firstSegmentsBuffer[firstSegidx++] = n2;
        --n3;
        System.arraycopy(array, n, firstSegmentsBuffer, firstSegidx, n3);
        this.firstSegidx = firstSegidx + n3;
    }
    
    @Override
    public void lineTo(final double n, final double n2) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
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
    
    private void _lineTo(final double n, final double n2) {
        final double n3 = n - this.cx0;
        final double n4 = n2 - this.cy0;
        final double n5 = n3 * n3 + n4 * n4;
        if (n5 == 0.0) {
            return;
        }
        double sqrt = Math.sqrt(n5);
        final double n6 = n3 / sqrt;
        final double n7 = n4 / sqrt;
        final double[] curCurvepts = this.curCurvepts;
        final double[] dash = this.dash;
        final int dashLen = this.dashLen;
        int idx = this.idx;
        boolean dashOn = this.dashOn;
        double phase = this.phase;
        double n9;
        while (true) {
            final double n8 = dash[idx];
            n9 = n8 - phase;
            if (sqrt <= n9) {
                break;
            }
            if (phase == 0.0) {
                curCurvepts[0] = this.cx0 + n8 * n6;
                curCurvepts[1] = this.cy0 + n8 * n7;
            }
            else {
                curCurvepts[0] = this.cx0 + n9 * n6;
                curCurvepts[1] = this.cy0 + n9 * n7;
            }
            this.goTo(curCurvepts, 0, 4, dashOn);
            sqrt -= n9;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
            phase = 0.0;
        }
        curCurvepts[0] = n;
        curCurvepts[1] = n2;
        this.goTo(curCurvepts, 0, 4, dashOn);
        double phase2 = phase + sqrt;
        if (sqrt == n9) {
            phase2 = 0.0;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
        }
        this.idx = idx;
        this.dashOn = dashOn;
        this.phase = phase2;
    }
    
    private void skipLineTo(final double cx0, final double cy0) {
        final double n = cx0 - this.cx0;
        final double n2 = cy0 - this.cy0;
        double sqrt = n * n + n2 * n2;
        if (sqrt != 0.0) {
            sqrt = Math.sqrt(sqrt);
        }
        this.outside = true;
        this.totalSkipLen += sqrt;
        this.needsMoveTo = true;
        this.starting = false;
        this.cx0 = cx0;
        this.cy0 = cy0;
    }
    
    public void skipLen() {
        double totalSkipLen = this.totalSkipLen;
        this.totalSkipLen = 0.0;
        final double[] dash = this.dash;
        final int dashLen = this.dashLen;
        int idx = this.idx;
        int dashOn = this.dashOn ? 1 : 0;
        double phase = this.phase;
        final long n = (long)Math.floor(totalSkipLen / this.cycleLen) - 2L;
        if (n > 0L) {
            totalSkipLen -= this.cycleLen * n;
            final long n2 = n * dashLen;
            idx = (int)(n2 + idx) % dashLen;
            dashOn = (((n2 + dashOn & 0x1L) == 0x1L) ? 1 : 0);
        }
        double n3;
        while (true) {
            n3 = dash[idx] - phase;
            if (totalSkipLen <= n3) {
                break;
            }
            totalSkipLen -= n3;
            idx = (idx + 1) % dashLen;
            dashOn = ((dashOn != 0) ? 0 : 1);
            phase = 0.0;
        }
        double phase2 = phase + totalSkipLen;
        if (totalSkipLen == n3) {
            phase2 = 0.0;
            idx = (idx + 1) % dashLen;
            dashOn = ((dashOn != 0) ? 0 : 1);
        }
        this.idx = idx;
        this.dashOn = (dashOn != 0);
        this.phase = phase2;
    }
    
    private void somethingTo(final int n) {
        final double[] curCurvepts = this.curCurvepts;
        if (pointCurve(curCurvepts, n)) {
            return;
        }
        final LengthIterator li = this.li;
        final double[] dash = this.dash;
        final int dashLen = this.dashLen;
        li.initializeIterationOnCurve(curCurvepts, n);
        int idx = this.idx;
        boolean dashOn = this.dashOn;
        double phase = this.phase;
        int n2 = 0;
        double n3 = 0.0;
        double next;
        for (double n4 = dash[idx] - phase; (next = li.next(n4)) < 1.0; n4 = dash[idx]) {
            if (next != 0.0) {
                DHelpers.subdivideAt((next - n3) / (1.0 - n3), curCurvepts, n2, curCurvepts, 0, n);
                n3 = next;
                this.goTo(curCurvepts, 2, n, dashOn);
                n2 = n;
            }
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
            phase = 0.0;
        }
        this.goTo(curCurvepts, n2 + 2, n, dashOn);
        double phase2 = phase + li.lastSegLen();
        if (phase2 >= dash[idx]) {
            phase2 = 0.0;
            idx = (idx + 1) % dashLen;
            dashOn = !dashOn;
        }
        this.idx = idx;
        this.dashOn = dashOn;
        this.phase = phase2;
        li.reset();
    }
    
    private void skipSomethingTo(final int n) {
        final double[] curCurvepts = this.curCurvepts;
        if (pointCurve(curCurvepts, n)) {
            return;
        }
        final LengthIterator li = this.li;
        li.initializeIterationOnCurve(curCurvepts, n);
        final double totalLength = li.totalLength();
        this.outside = true;
        this.totalSkipLen += totalLength;
        this.needsMoveTo = true;
        this.starting = false;
    }
    
    private static boolean pointCurve(final double[] array, final int n) {
        for (int i = 2; i < n; ++i) {
            if (array[i] != array[i - 2]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
            final int outcode2 = DHelpers.outcode(n3, n4, this.clipRect);
            final int outcode3 = DHelpers.outcode(n5, n6, this.clipRect);
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
    
    private void _curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
        final double[] curCurvepts = this.curCurvepts;
        final DTransformingPathConsumer2D.CurveBasicMonotonizer curve = this.rdrCtx.monotonizer.curve(this.cx0, this.cy0, n, n2, n3, n4, n5, n6);
        final int nbSplits = curve.nbSplits;
        final double[] middle = curve.middle;
        for (int i = 0, n7 = 0; i <= nbSplits; ++i, n7 += 6) {
            System.arraycopy(middle, n7, curCurvepts, 0, 8);
            this.somethingTo(8);
        }
    }
    
    private void skipCurveTo(final double n, final double n2, final double n3, final double n4, final double cx0, final double cy0) {
        final double[] curCurvepts = this.curCurvepts;
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
    public void quadTo(final double n, final double n2, final double n3, final double n4) {
        final int cOutCode = this.cOutCode;
        if (this.clipRect != null) {
            final int outcode = DHelpers.outcode(n, n2, this.clipRect);
            final int outcode2 = DHelpers.outcode(n3, n4, this.clipRect);
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
    
    private void _quadTo(final double n, final double n2, final double n3, final double n4) {
        final double[] curCurvepts = this.curCurvepts;
        final DTransformingPathConsumer2D.CurveBasicMonotonizer quad = this.rdrCtx.monotonizer.quad(this.cx0, this.cy0, n, n2, n3, n4);
        final int nbSplits = quad.nbSplits;
        final double[] middle = quad.middle;
        for (int i = 0, n5 = 0; i <= nbSplits; ++i, n5 += 4) {
            System.arraycopy(middle, n5, curCurvepts, 0, 8);
            this.somethingTo(6);
        }
    }
    
    private void skipQuadTo(final double n, final double n2, final double cx0, final double cy0) {
        final double[] curCurvepts = this.curCurvepts;
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
        throw new InternalError("DDasher does not use a native consumer");
    }
    
    static {
        CURVE_LEN_ERR = MarlinProperties.getCurveLengthError();
    }
    
    static final class LengthIterator
    {
        private final double[][] recCurveStack;
        private final boolean[] sidesRight;
        private int curveType;
        private double nextT;
        private double lenAtNextT;
        private double lastT;
        private double lenAtLastT;
        private double lenAtLastSplit;
        private double lastSegLen;
        private int recLevel;
        private boolean done;
        private final double[] curLeafCtrlPolyLengths;
        private int cachedHaveLowAcceleration;
        private final double[] nextRoots;
        private final double[] flatLeafCoefCache;
        
        LengthIterator() {
            this.curLeafCtrlPolyLengths = new double[3];
            this.cachedHaveLowAcceleration = -1;
            this.nextRoots = new double[4];
            this.flatLeafCoefCache = new double[] { 0.0, 0.0, -1.0, 0.0 };
            this.recCurveStack = new double[17][8];
            this.sidesRight = new boolean[16];
            this.nextT = Double.MAX_VALUE;
            this.lenAtNextT = Double.MAX_VALUE;
            this.lenAtLastSplit = Double.MIN_VALUE;
            this.recLevel = Integer.MIN_VALUE;
            this.lastSegLen = Double.MAX_VALUE;
            this.done = true;
        }
        
        void reset() {
        }
        
        void initializeIterationOnCurve(final double[] array, final int curveType) {
            System.arraycopy(array, 0, this.recCurveStack[0], 0, 8);
            this.curveType = curveType;
            this.recLevel = 0;
            this.lastT = 0.0;
            this.lenAtLastT = 0.0;
            this.nextT = 0.0;
            this.lenAtNextT = 0.0;
            this.goLeft();
            this.lenAtLastSplit = 0.0;
            if (this.recLevel > 0) {
                this.sidesRight[0] = false;
                this.done = false;
            }
            else {
                this.sidesRight[0] = true;
                this.done = true;
            }
            this.lastSegLen = 0.0;
        }
        
        private boolean haveLowAcceleration(final double n) {
            if (this.cachedHaveLowAcceleration != -1) {
                return this.cachedHaveLowAcceleration == 1;
            }
            final double n2 = this.curLeafCtrlPolyLengths[0];
            final double n3 = this.curLeafCtrlPolyLengths[1];
            if (!DHelpers.within(n2, n3, n * n3)) {
                this.cachedHaveLowAcceleration = 0;
                return false;
            }
            if (this.curveType == 8) {
                final double n4 = this.curLeafCtrlPolyLengths[2];
                final double n5 = n * n4;
                if (!DHelpers.within(n3, n4, n5) || !DHelpers.within(n2, n4, n5)) {
                    this.cachedHaveLowAcceleration = 0;
                    return false;
                }
            }
            this.cachedHaveLowAcceleration = 1;
            return true;
        }
        
        double next(final double lastSegLen) {
            final double lenAtLastSplit = this.lenAtLastSplit + lastSegLen;
            while (this.lenAtNextT < lenAtLastSplit) {
                if (this.done) {
                    this.lastSegLen = this.lenAtNextT - this.lenAtLastSplit;
                    return 1.0;
                }
                this.goToNextLeaf();
            }
            this.lenAtLastSplit = lenAtLastSplit;
            double n = (lenAtLastSplit - this.lenAtLastT) / (this.lenAtNextT - this.lenAtLastT);
            if (!this.haveLowAcceleration(0.05)) {
                final double[] flatLeafCoefCache = this.flatLeafCoefCache;
                if (flatLeafCoefCache[2] < 0.0) {
                    final double n2 = this.curLeafCtrlPolyLengths[0];
                    final double n3 = n2 + this.curLeafCtrlPolyLengths[1];
                    if (this.curveType == 8) {
                        final double n4 = n3 + this.curLeafCtrlPolyLengths[2];
                        flatLeafCoefCache[0] = 3.0 * (n2 - n3) + n4;
                        flatLeafCoefCache[1] = 3.0 * (n3 - 2.0 * n2);
                        flatLeafCoefCache[2] = 3.0 * n2;
                        flatLeafCoefCache[3] = -n4;
                    }
                    else if (this.curveType == 6) {
                        flatLeafCoefCache[0] = 0.0;
                        flatLeafCoefCache[1] = n3 - 2.0 * n2;
                        flatLeafCoefCache[2] = 2.0 * n2;
                        flatLeafCoefCache[3] = -n3;
                    }
                }
                if (DHelpers.cubicRootsInAB(flatLeafCoefCache[0], flatLeafCoefCache[1], flatLeafCoefCache[2], n * flatLeafCoefCache[3], this.nextRoots, 0, 0.0, 1.0) == 1 && !Double.isNaN(this.nextRoots[0])) {
                    n = this.nextRoots[0];
                }
            }
            double n5 = n * (this.nextT - this.lastT) + this.lastT;
            if (n5 >= 1.0) {
                n5 = 1.0;
                this.done = true;
            }
            this.lastSegLen = lastSegLen;
            return n5;
        }
        
        double totalLength() {
            while (!this.done) {
                this.goToNextLeaf();
            }
            this.reset();
            return this.lenAtNextT;
        }
        
        double lastSegLen() {
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
            final double onLeaf = this.onLeaf();
            if (onLeaf >= 0.0) {
                this.lastT = this.nextT;
                this.lenAtLastT = this.lenAtNextT;
                this.nextT += (1 << 16 - this.recLevel) * 1.52587890625E-5;
                this.lenAtNextT += onLeaf;
                this.flatLeafCoefCache[2] = -1.0;
                this.cachedHaveLowAcceleration = -1;
            }
            else {
                DHelpers.subdivide(this.recCurveStack[this.recLevel], this.recCurveStack[this.recLevel + 1], this.recCurveStack[this.recLevel], this.curveType);
                this.sidesRight[this.recLevel] = false;
                ++this.recLevel;
                this.goLeft();
            }
        }
        
        private double onLeaf() {
            final double[] array = this.recCurveStack[this.recLevel];
            final int curveType = this.curveType;
            double n = 0.0;
            double n2 = array[0];
            double n3 = array[1];
            for (int i = 2; i < curveType; i += 2) {
                final double n4 = array[i];
                final double n5 = array[i + 1];
                final double linelen = DHelpers.linelen(n2, n3, n4, n5);
                n += linelen;
                this.curLeafCtrlPolyLengths[(i >> 1) - 1] = linelen;
                n2 = n4;
                n3 = n5;
            }
            final double linelen2 = DHelpers.linelen(array[0], array[1], n2, n3);
            if (n - linelen2 < DDasher.CURVE_LEN_ERR || this.recLevel == 16) {
                return (n + linelen2) / 2.0;
            }
            return -1.0;
        }
    }
}
