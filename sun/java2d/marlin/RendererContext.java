package sun.java2d.marlin;

import java.awt.geom.Path2D;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;
import sun.java2d.ReentrantContext;

final class RendererContext extends ReentrantContext implements IRendererContext
{
    private static final AtomicInteger CTX_COUNT;
    private final Object cleanerObj;
    boolean dirty;
    final float[] float6;
    final Curve curve;
    final MarlinRenderingEngine.NormalizingPathIterator nPCPathIterator;
    final MarlinRenderingEngine.NormalizingPathIterator nPQPathIterator;
    final TransformingPathConsumer2D transformerPC2D;
    private WeakReference<Path2D.Float> refPath2D;
    final Renderer renderer;
    final Stroker stroker;
    final CollinearSimplifier simplifier;
    final PathSimplifier pathSimplifier;
    final Dasher dasher;
    final MarlinTileGenerator ptg;
    final MarlinCache cache;
    int stroking;
    boolean doClip;
    boolean closedPath;
    final float[] clipRect;
    float clipInvScale;
    final TransformingPathConsumer2D.CurveBasicMonotonizer monotonizer;
    final TransformingPathConsumer2D.CurveClipSplitter curveClipSplitter;
    private final IntArrayCache cleanIntCache;
    private final IntArrayCache dirtyIntCache;
    private final FloatArrayCache dirtyFloatCache;
    private final ByteArrayCache dirtyByteCache;
    final RendererStats stats;
    
    static RendererContext createContext() {
        return new RendererContext("ctx" + Integer.toString(RendererContext.CTX_COUNT.getAndIncrement()));
    }
    
    RendererContext(final String s) {
        this.dirty = false;
        this.float6 = new float[6];
        this.curve = new Curve();
        this.refPath2D = null;
        this.simplifier = new CollinearSimplifier();
        this.pathSimplifier = new PathSimplifier();
        this.stroking = 0;
        this.doClip = false;
        this.closedPath = false;
        this.clipRect = new float[4];
        this.clipInvScale = 0.0f;
        this.cleanIntCache = new IntArrayCache(true, 5);
        this.dirtyIntCache = new IntArrayCache(false, 5);
        this.dirtyFloatCache = new FloatArrayCache(false, 4);
        this.dirtyByteCache = new ByteArrayCache(false, 2);
        if (RendererContext.LOG_CREATE_CONTEXT) {
            MarlinUtils.logInfo("new RendererContext = " + s);
        }
        this.cleanerObj = new Object();
        if (!RendererContext.DO_STATS) {
            this.stats = null;
        }
        else {
            this.stats = RendererStats.createInstance(this.cleanerObj, s);
            this.stats.cacheStats = new ArrayCacheConst.CacheStats[] { this.cleanIntCache.stats, this.dirtyIntCache.stats, this.dirtyFloatCache.stats, this.dirtyByteCache.stats };
        }
        this.nPCPathIterator = new MarlinRenderingEngine.NormalizingPathIterator.NearestPixelCenter(this.float6);
        this.nPQPathIterator = new MarlinRenderingEngine.NormalizingPathIterator.NearestPixelQuarter(this.float6);
        this.monotonizer = new TransformingPathConsumer2D.CurveBasicMonotonizer(this);
        this.curveClipSplitter = new TransformingPathConsumer2D.CurveClipSplitter(this);
        this.transformerPC2D = new TransformingPathConsumer2D(this);
        this.cache = new MarlinCache(this);
        this.renderer = new Renderer(this);
        this.ptg = new MarlinTileGenerator(this.stats, this.renderer, this.cache);
        this.stroker = new Stroker(this);
        this.dasher = new Dasher(this);
    }
    
    void dispose() {
        if (RendererContext.DO_STATS) {
            if (this.stats.totalOffHeap > this.stats.totalOffHeapMax) {
                this.stats.totalOffHeapMax = this.stats.totalOffHeap;
            }
            this.stats.totalOffHeap = 0L;
        }
        this.stroking = 0;
        this.doClip = false;
        this.closedPath = false;
        this.clipInvScale = 0.0f;
        if (this.dirty) {
            this.nPCPathIterator.dispose();
            this.nPQPathIterator.dispose();
            this.dasher.dispose();
            this.stroker.dispose();
            this.dirty = false;
        }
    }
    
    Path2D.Float getPath2D() {
        Path2D.Float float1 = (this.refPath2D != null) ? this.refPath2D.get() : null;
        if (float1 == null) {
            float1 = new Path2D.Float(1, RendererContext.INITIAL_EDGES_COUNT);
            this.refPath2D = new WeakReference<Path2D.Float>(float1);
        }
        float1.reset();
        return float1;
    }
    
    @Override
    public RendererStats stats() {
        return this.stats;
    }
    
    @Override
    public OffHeapArray newOffHeapArray(final long n) {
        if (RendererContext.DO_STATS) {
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
    
    FloatArrayCache.Reference newDirtyFloatArrayRef(final int n) {
        return this.dirtyFloatCache.createRef(n);
    }
    
    ByteArrayCache.Reference newDirtyByteArrayRef(final int n) {
        return this.dirtyByteCache.createRef(n);
    }
    
    static {
        CTX_COUNT = new AtomicInteger(1);
    }
}
