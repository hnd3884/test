package sun.java2d.marlin;

import sun.awt.geom.PathConsumer2D;
import java.awt.geom.Path2D;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import sun.java2d.ReentrantContext;

final class DRendererContext extends ReentrantContext implements IRendererContext
{
    private static final AtomicInteger CTX_COUNT;
    private final Object cleanerObj;
    boolean dirty;
    final double[] double6;
    final DCurve curve;
    final DMarlinRenderingEngine.NormalizingPathIterator nPCPathIterator;
    final DMarlinRenderingEngine.NormalizingPathIterator nPQPathIterator;
    final DTransformingPathConsumer2D transformerPC2D;
    private WeakReference<Path2D.Double> refPath2D;
    final DRenderer renderer;
    final DStroker stroker;
    final DCollinearSimplifier simplifier;
    final DPathSimplifier pathSimplifier;
    final DDasher dasher;
    final MarlinTileGenerator ptg;
    final MarlinCache cache;
    int stroking;
    boolean doClip;
    boolean closedPath;
    final double[] clipRect;
    double clipInvScale;
    final DTransformingPathConsumer2D.CurveBasicMonotonizer monotonizer;
    final DTransformingPathConsumer2D.CurveClipSplitter curveClipSplitter;
    private final IntArrayCache cleanIntCache;
    private final IntArrayCache dirtyIntCache;
    private final DoubleArrayCache dirtyDoubleCache;
    private final ByteArrayCache dirtyByteCache;
    final RendererStats stats;
    final PathConsumer2DAdapter p2dAdapter;
    
    static DRendererContext createContext() {
        return new DRendererContext("ctx" + Integer.toString(DRendererContext.CTX_COUNT.getAndIncrement()));
    }
    
    DRendererContext(final String s) {
        this.dirty = false;
        this.double6 = new double[6];
        this.curve = new DCurve();
        this.refPath2D = null;
        this.simplifier = new DCollinearSimplifier();
        this.pathSimplifier = new DPathSimplifier();
        this.stroking = 0;
        this.doClip = false;
        this.closedPath = false;
        this.clipRect = new double[4];
        this.clipInvScale = 0.0;
        this.cleanIntCache = new IntArrayCache(true, 5);
        this.dirtyIntCache = new IntArrayCache(false, 5);
        this.dirtyDoubleCache = new DoubleArrayCache(false, 4);
        this.dirtyByteCache = new ByteArrayCache(false, 2);
        this.p2dAdapter = new PathConsumer2DAdapter();
        if (DRendererContext.LOG_CREATE_CONTEXT) {
            MarlinUtils.logInfo("new RendererContext = " + s);
        }
        this.cleanerObj = new Object();
        if (!DRendererContext.DO_STATS) {
            this.stats = null;
        }
        else {
            this.stats = RendererStats.createInstance(this.cleanerObj, s);
            this.stats.cacheStats = new ArrayCacheConst.CacheStats[] { this.cleanIntCache.stats, this.dirtyIntCache.stats, this.dirtyDoubleCache.stats, this.dirtyByteCache.stats };
        }
        this.nPCPathIterator = new DMarlinRenderingEngine.NormalizingPathIterator.NearestPixelCenter(this.double6);
        this.nPQPathIterator = new DMarlinRenderingEngine.NormalizingPathIterator.NearestPixelQuarter(this.double6);
        this.monotonizer = new DTransformingPathConsumer2D.CurveBasicMonotonizer(this);
        this.curveClipSplitter = new DTransformingPathConsumer2D.CurveClipSplitter(this);
        this.transformerPC2D = new DTransformingPathConsumer2D(this);
        this.cache = new MarlinCache(this);
        this.renderer = new DRenderer(this);
        this.ptg = new MarlinTileGenerator(this.stats, this.renderer, this.cache);
        this.stroker = new DStroker(this);
        this.dasher = new DDasher(this);
    }
    
    void dispose() {
        if (DRendererContext.DO_STATS) {
            if (this.stats.totalOffHeap > this.stats.totalOffHeapMax) {
                this.stats.totalOffHeapMax = this.stats.totalOffHeap;
            }
            this.stats.totalOffHeap = 0L;
        }
        this.stroking = 0;
        this.doClip = false;
        this.closedPath = false;
        this.clipInvScale = 0.0;
        if (this.dirty) {
            this.nPCPathIterator.dispose();
            this.nPQPathIterator.dispose();
            this.dasher.dispose();
            this.stroker.dispose();
            this.dirty = false;
        }
    }
    
    Path2D.Double getPath2D() {
        Path2D.Double double1 = (this.refPath2D != null) ? this.refPath2D.get() : null;
        if (double1 == null) {
            double1 = new Path2D.Double(1, DRendererContext.INITIAL_EDGES_COUNT);
            this.refPath2D = new WeakReference<Path2D.Double>(double1);
        }
        double1.reset();
        return double1;
    }
    
    @Override
    public RendererStats stats() {
        return this.stats;
    }
    
    @Override
    public OffHeapArray newOffHeapArray(final long n) {
        if (DRendererContext.DO_STATS) {
            final RendererStats stats = this.stats;
            stats.totalOffHeapInitial += n;
        }
        return new OffHeapArray(this.cleanerObj, n);
    }
    
    @Override
    public IntArrayCache.Reference newCleanIntArrayRef(final int n) {
        return this.cleanIntCache.createRef(n);
    }
    
    IntArrayCache.Reference newDirtyIntArrayRef(final int n) {
        return this.dirtyIntCache.createRef(n);
    }
    
    DoubleArrayCache.Reference newDirtyDoubleArrayRef(final int n) {
        return this.dirtyDoubleCache.createRef(n);
    }
    
    ByteArrayCache.Reference newDirtyByteArrayRef(final int n) {
        return this.dirtyByteCache.createRef(n);
    }
    
    static {
        CTX_COUNT = new AtomicInteger(1);
    }
    
    static final class PathConsumer2DAdapter implements DPathConsumer2D
    {
        private PathConsumer2D out;
        
        PathConsumer2DAdapter init(final PathConsumer2D out) {
            this.out = out;
            return this;
        }
        
        @Override
        public void moveTo(final double n, final double n2) {
            this.out.moveTo((float)n, (float)n2);
        }
        
        @Override
        public void lineTo(final double n, final double n2) {
            this.out.lineTo((float)n, (float)n2);
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
        public void curveTo(final double n, final double n2, final double n3, final double n4, final double n5, final double n6) {
            this.out.curveTo((float)n, (float)n2, (float)n3, (float)n4, (float)n5, (float)n6);
        }
        
        @Override
        public void quadTo(final double n, final double n2, final double n3, final double n4) {
            this.out.quadTo((float)n, (float)n2, (float)n3, (float)n4);
        }
        
        @Override
        public long getNativeConsumer() {
            throw new InternalError("Not using a native peer");
        }
    }
}
