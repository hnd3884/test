package sun.java2d.pisces;

import sun.awt.geom.PathConsumer2D;

final class Dasher implements PathConsumer2D
{
    private final PathConsumer2D out;
    private final float[] dash;
    private final float startPhase;
    private final boolean startDashOn;
    private final int startIdx;
    private boolean starting;
    private boolean needsMoveTo;
    private int idx;
    private boolean dashOn;
    private float phase;
    private float sx;
    private float sy;
    private float x0;
    private float y0;
    private float[] curCurvepts;
    private float[] firstSegmentsBuffer;
    private int firstSegidx;
    private LengthIterator li;
    
    public Dasher(final PathConsumer2D out, final float[] dash, float n) {
        this.firstSegmentsBuffer = new float[7];
        this.firstSegidx = 0;
        this.li = null;
        if (n < 0.0f) {
            throw new IllegalArgumentException("phase < 0 !");
        }
        this.out = out;
        int startIdx = 0;
        this.dashOn = true;
        float n2;
        while (n >= (n2 = dash[startIdx])) {
            n -= n2;
            startIdx = (startIdx + 1) % dash.length;
            this.dashOn = !this.dashOn;
        }
        this.dash = dash;
        final float n3 = n;
        this.phase = n3;
        this.startPhase = n3;
        this.startDashOn = this.dashOn;
        this.startIdx = startIdx;
        this.starting = true;
        this.curCurvepts = new float[16];
    }
    
    @Override
    public void moveTo(final float n, final float n2) {
        if (this.firstSegidx > 0) {
            this.out.moveTo(this.sx, this.sy);
            this.emitFirstSegments();
        }
        this.needsMoveTo = true;
        this.idx = this.startIdx;
        this.dashOn = this.startDashOn;
        this.phase = this.startPhase;
        this.x0 = n;
        this.sx = n;
        this.y0 = n2;
        this.sy = n2;
        this.starting = true;
    }
    
    private void emitSeg(final float[] array, final int n, final int n2) {
        switch (n2) {
            case 8: {
                this.out.curveTo(array[n + 0], array[n + 1], array[n + 2], array[n + 3], array[n + 4], array[n + 5]);
                break;
            }
            case 6: {
                this.out.quadTo(array[n + 0], array[n + 1], array[n + 2], array[n + 3]);
                break;
            }
            case 4: {
                this.out.lineTo(array[n], array[n + 1]);
                break;
            }
        }
    }
    
    private void emitFirstSegments() {
        for (int i = 0; i < this.firstSegidx; i += (int)this.firstSegmentsBuffer[i] - 1) {
            this.emitSeg(this.firstSegmentsBuffer, i + 1, (int)this.firstSegmentsBuffer[i]);
        }
        this.firstSegidx = 0;
    }
    
    private void goTo(final float[] array, final int n, final int n2) {
        final float x0 = array[n + n2 - 4];
        final float y0 = array[n + n2 - 3];
        if (this.dashOn) {
            if (this.starting) {
                (this.firstSegmentsBuffer = Helpers.widenArray(this.firstSegmentsBuffer, this.firstSegidx, n2 - 2 + 1))[this.firstSegidx++] = (float)n2;
                System.arraycopy(array, n, this.firstSegmentsBuffer, this.firstSegidx, n2 - 2);
                this.firstSegidx += n2 - 2;
            }
            else {
                if (this.needsMoveTo) {
                    this.out.moveTo(this.x0, this.y0);
                    this.needsMoveTo = false;
                }
                this.emitSeg(array, n, n2);
            }
        }
        else {
            this.starting = false;
            this.needsMoveTo = true;
        }
        this.x0 = x0;
        this.y0 = y0;
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        final float n3 = n - this.x0;
        final float n4 = n2 - this.y0;
        float n5 = (float)Math.sqrt(n3 * n3 + n4 * n4);
        if (n5 == 0.0f) {
            return;
        }
        final float n6 = n3 / n5;
        final float n7 = n4 / n5;
        float n8;
        while (true) {
            n8 = this.dash[this.idx] - this.phase;
            if (n5 <= n8) {
                break;
            }
            final float n9 = this.dash[this.idx] * n6;
            final float n10 = this.dash[this.idx] * n7;
            if (this.phase == 0.0f) {
                this.curCurvepts[0] = this.x0 + n9;
                this.curCurvepts[1] = this.y0 + n10;
            }
            else {
                final float n11 = n8 / this.dash[this.idx];
                this.curCurvepts[0] = this.x0 + n11 * n9;
                this.curCurvepts[1] = this.y0 + n11 * n10;
            }
            this.goTo(this.curCurvepts, 0, 4);
            n5 -= n8;
            this.idx = (this.idx + 1) % this.dash.length;
            this.dashOn = !this.dashOn;
            this.phase = 0.0f;
        }
        this.curCurvepts[0] = n;
        this.curCurvepts[1] = n2;
        this.goTo(this.curCurvepts, 0, 4);
        this.phase += n5;
        if (n5 == n8) {
            this.phase = 0.0f;
            this.idx = (this.idx + 1) % this.dash.length;
            this.dashOn = !this.dashOn;
        }
    }
    
    private void somethingTo(final int n) {
        if (pointCurve(this.curCurvepts, n)) {
            return;
        }
        if (this.li == null) {
            this.li = new LengthIterator(4, 0.01f);
        }
        this.li.initializeIterationOnCurve(this.curCurvepts, n);
        int n2 = 0;
        float n3 = 0.0f;
        float next;
        for (float n4 = this.dash[this.idx] - this.phase; (next = this.li.next(n4)) < 1.0f; n4 = this.dash[this.idx]) {
            if (next != 0.0f) {
                Helpers.subdivideAt((next - n3) / (1.0f - n3), this.curCurvepts, n2, this.curCurvepts, 0, this.curCurvepts, n, n);
                n3 = next;
                this.goTo(this.curCurvepts, 2, n);
                n2 = n;
            }
            this.idx = (this.idx + 1) % this.dash.length;
            this.dashOn = !this.dashOn;
            this.phase = 0.0f;
        }
        this.goTo(this.curCurvepts, n2 + 2, n);
        this.phase += this.li.lastSegLen();
        if (this.phase >= this.dash[this.idx]) {
            this.phase = 0.0f;
            this.idx = (this.idx + 1) % this.dash.length;
            this.dashOn = !this.dashOn;
        }
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
        this.curCurvepts[0] = this.x0;
        this.curCurvepts[1] = this.y0;
        this.curCurvepts[2] = n;
        this.curCurvepts[3] = n2;
        this.curCurvepts[4] = n3;
        this.curCurvepts[5] = n4;
        this.curCurvepts[6] = n5;
        this.curCurvepts[7] = n6;
        this.somethingTo(8);
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float n3, final float n4) {
        this.curCurvepts[0] = this.x0;
        this.curCurvepts[1] = this.y0;
        this.curCurvepts[2] = n;
        this.curCurvepts[3] = n2;
        this.curCurvepts[4] = n3;
        this.curCurvepts[5] = n4;
        this.somethingTo(6);
    }
    
    @Override
    public void closePath() {
        this.lineTo(this.sx, this.sy);
        if (this.firstSegidx > 0) {
            if (!this.dashOn || this.needsMoveTo) {
                this.out.moveTo(this.sx, this.sy);
            }
            this.emitFirstSegments();
        }
        this.moveTo(this.sx, this.sy);
    }
    
    @Override
    public void pathDone() {
        if (this.firstSegidx > 0) {
            this.out.moveTo(this.sx, this.sy);
            this.emitFirstSegments();
        }
        this.out.pathDone();
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Dasher does not use a native consumer");
    }
    
    private static class LengthIterator
    {
        private float[][] recCurveStack;
        private Side[] sides;
        private int curveType;
        private final int limit;
        private final float ERR;
        private final float minTincrement;
        private float nextT;
        private float lenAtNextT;
        private float lastT;
        private float lenAtLastT;
        private float lenAtLastSplit;
        private float lastSegLen;
        private int recLevel;
        private boolean done;
        private float[] curLeafCtrlPolyLengths;
        private int cachedHaveLowAcceleration;
        private float[] nextRoots;
        private float[] flatLeafCoefCache;
        
        public LengthIterator(final int limit, final float err) {
            this.curLeafCtrlPolyLengths = new float[3];
            this.cachedHaveLowAcceleration = -1;
            this.nextRoots = new float[4];
            this.flatLeafCoefCache = new float[] { 0.0f, 0.0f, -1.0f, 0.0f };
            this.limit = limit;
            this.minTincrement = 1.0f / (1 << this.limit);
            this.ERR = err;
            this.recCurveStack = new float[limit + 1][8];
            this.sides = new Side[limit];
            this.nextT = Float.MAX_VALUE;
            this.lenAtNextT = Float.MAX_VALUE;
            this.lenAtLastSplit = Float.MIN_VALUE;
            this.recLevel = Integer.MIN_VALUE;
            this.lastSegLen = Float.MAX_VALUE;
            this.done = true;
        }
        
        public void initializeIterationOnCurve(final float[] array, final int curveType) {
            System.arraycopy(array, 0, this.recCurveStack[0], 0, curveType);
            this.curveType = curveType;
            this.recLevel = 0;
            this.lastT = 0.0f;
            this.lenAtLastT = 0.0f;
            this.nextT = 0.0f;
            this.lenAtNextT = 0.0f;
            this.goLeft();
            this.lenAtLastSplit = 0.0f;
            if (this.recLevel > 0) {
                this.sides[0] = Side.LEFT;
                this.done = false;
            }
            else {
                this.sides[0] = Side.RIGHT;
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
                if (!Helpers.within(n3, n4, n * n4) || !Helpers.within(n2, n4, n * n4)) {
                    this.cachedHaveLowAcceleration = 0;
                    return false;
                }
            }
            this.cachedHaveLowAcceleration = 1;
            return true;
        }
        
        public float next(final float lastSegLen) {
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
                if (this.flatLeafCoefCache[2] < 0.0f) {
                    final float n2 = 0.0f + this.curLeafCtrlPolyLengths[0];
                    final float n3 = n2 + this.curLeafCtrlPolyLengths[1];
                    if (this.curveType == 8) {
                        final float n4 = n3 + this.curLeafCtrlPolyLengths[2];
                        this.flatLeafCoefCache[0] = 3.0f * (n2 - n3) + n4;
                        this.flatLeafCoefCache[1] = 3.0f * (n3 - 2.0f * n2);
                        this.flatLeafCoefCache[2] = 3.0f * n2;
                        this.flatLeafCoefCache[3] = -n4;
                    }
                    else if (this.curveType == 6) {
                        this.flatLeafCoefCache[0] = 0.0f;
                        this.flatLeafCoefCache[1] = n3 - 2.0f * n2;
                        this.flatLeafCoefCache[2] = 2.0f * n2;
                        this.flatLeafCoefCache[3] = -n3;
                    }
                }
                if (Helpers.cubicRootsInAB(this.flatLeafCoefCache[0], this.flatLeafCoefCache[1], this.flatLeafCoefCache[2], n * this.flatLeafCoefCache[3], this.nextRoots, 0, 0.0f, 1.0f) == 1 && !Float.isNaN(this.nextRoots[0])) {
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
        
        public float lastSegLen() {
            return this.lastSegLen;
        }
        
        private void goToNextLeaf() {
            --this.recLevel;
            while (this.sides[this.recLevel] == Side.RIGHT) {
                if (this.recLevel == 0) {
                    this.done = true;
                    return;
                }
                --this.recLevel;
            }
            this.sides[this.recLevel] = Side.RIGHT;
            System.arraycopy(this.recCurveStack[this.recLevel], 0, this.recCurveStack[this.recLevel + 1], 0, this.curveType);
            ++this.recLevel;
            this.goLeft();
        }
        
        private void goLeft() {
            final float onLeaf = this.onLeaf();
            if (onLeaf >= 0.0f) {
                this.lastT = this.nextT;
                this.lenAtLastT = this.lenAtNextT;
                this.nextT += (1 << this.limit - this.recLevel) * this.minTincrement;
                this.lenAtNextT += onLeaf;
                this.flatLeafCoefCache[2] = -1.0f;
                this.cachedHaveLowAcceleration = -1;
            }
            else {
                Helpers.subdivide(this.recCurveStack[this.recLevel], 0, this.recCurveStack[this.recLevel + 1], 0, this.recCurveStack[this.recLevel], 0, this.curveType);
                this.sides[this.recLevel] = Side.LEFT;
                ++this.recLevel;
                this.goLeft();
            }
        }
        
        private float onLeaf() {
            final float[] array = this.recCurveStack[this.recLevel];
            float n = 0.0f;
            float n2 = array[0];
            float n3 = array[1];
            for (int i = 2; i < this.curveType; i += 2) {
                final float n4 = array[i];
                final float n5 = array[i + 1];
                final float linelen = Helpers.linelen(n2, n3, n4, n5);
                n += linelen;
                this.curLeafCtrlPolyLengths[i / 2 - 1] = linelen;
                n2 = n4;
                n3 = n5;
            }
            final float linelen2 = Helpers.linelen(array[0], array[1], array[this.curveType - 2], array[this.curveType - 1]);
            if (n - linelen2 < this.ERR || this.recLevel == this.limit) {
                return (n + linelen2) / 2.0f;
            }
            return -1.0f;
        }
        
        private enum Side
        {
            LEFT, 
            RIGHT;
        }
    }
}
