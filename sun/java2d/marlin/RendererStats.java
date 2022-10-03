package sun.java2d.marlin;

import java.lang.ref.PhantomReference;
import java.util.Iterator;
import java.security.AccessController;
import java.util.Vector;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import sun.java2d.marlin.stats.Monitor;
import sun.java2d.marlin.stats.Histogram;
import sun.java2d.marlin.stats.StatLong;

public final class RendererStats implements MarlinConst
{
    final String name;
    final StatLong stat_cache_rowAA;
    final StatLong stat_cache_rowAAChunk;
    final StatLong stat_cache_tiles;
    final StatLong stat_rdr_addLine;
    final StatLong stat_rdr_addLine_skip;
    final StatLong stat_rdr_curveBreak;
    final StatLong stat_rdr_curveBreak_dec;
    final StatLong stat_rdr_curveBreak_inc;
    final StatLong stat_rdr_quadBreak;
    final StatLong stat_rdr_quadBreak_dec;
    final StatLong stat_rdr_edges;
    final StatLong stat_rdr_edges_count;
    final StatLong stat_rdr_edges_resizes;
    final StatLong stat_rdr_activeEdges;
    final StatLong stat_rdr_activeEdges_updates;
    final StatLong stat_rdr_activeEdges_adds;
    final StatLong stat_rdr_activeEdges_adds_high;
    final StatLong stat_rdr_crossings_updates;
    final StatLong stat_rdr_crossings_sorts;
    final StatLong stat_rdr_crossings_bsearch;
    final StatLong stat_rdr_crossings_msorts;
    final StatLong stat_str_polystack_curves;
    final StatLong stat_str_polystack_types;
    final StatLong stat_cpd_polystack_curves;
    final StatLong stat_cpd_polystack_types;
    final StatLong stat_pcf_idxstack_indices;
    final StatLong stat_array_dasher_dasher;
    final StatLong stat_array_dasher_firstSegmentsBuffer;
    final StatLong stat_array_marlincache_rowAAChunk;
    final StatLong stat_array_marlincache_touchedTile;
    final StatLong stat_array_renderer_alphaline;
    final StatLong stat_array_renderer_crossings;
    final StatLong stat_array_renderer_aux_crossings;
    final StatLong stat_array_renderer_edgeBuckets;
    final StatLong stat_array_renderer_edgeBucketCounts;
    final StatLong stat_array_renderer_edgePtrs;
    final StatLong stat_array_renderer_aux_edgePtrs;
    final StatLong stat_array_str_polystack_curves;
    final StatLong stat_array_str_polystack_types;
    final StatLong stat_array_cpd_polystack_curves;
    final StatLong stat_array_cpd_polystack_types;
    final StatLong stat_array_pcf_idxstack_indices;
    final Histogram hist_rdr_edges_count;
    final Histogram hist_rdr_crossings;
    final Histogram hist_rdr_crossings_ratio;
    final Histogram hist_rdr_crossings_adds;
    final Histogram hist_rdr_crossings_msorts;
    final Histogram hist_rdr_crossings_msorts_adds;
    final Histogram hist_str_polystack_curves;
    final Histogram hist_tile_generator_alpha;
    final Histogram hist_tile_generator_encoding;
    final Histogram hist_tile_generator_encoding_dist;
    final Histogram hist_tile_generator_encoding_ratio;
    final Histogram hist_tile_generator_encoding_runLen;
    final Histogram hist_cpd_polystack_curves;
    final Histogram hist_pcf_idxstack_indices;
    final StatLong[] statistics;
    final Monitor mon_pre_getAATileGenerator;
    final Monitor mon_rdr_addLine;
    final Monitor mon_rdr_endRendering;
    final Monitor mon_rdr_endRendering_Y;
    final Monitor mon_rdr_copyAARow;
    final Monitor mon_pipe_renderTiles;
    final Monitor mon_ptg_getAlpha;
    final Monitor mon_debug;
    final Monitor[] monitors;
    long totalOffHeapInitial;
    long totalOffHeap;
    long totalOffHeapMax;
    ArrayCacheConst.CacheStats[] cacheStats;
    
    static RendererStats createInstance(final Object o, final String s) {
        final RendererStats rendererStats = new RendererStats(s);
        RendererStatsHolder.getInstance().add(o, rendererStats);
        return rendererStats;
    }
    
    public static void dumpStats() {
        RendererStatsHolder.dumpStats();
    }
    
    private RendererStats(final String name) {
        this.stat_cache_rowAA = new StatLong("cache.rowAA");
        this.stat_cache_rowAAChunk = new StatLong("cache.rowAAChunk");
        this.stat_cache_tiles = new StatLong("cache.tiles");
        this.stat_rdr_addLine = new StatLong("renderer.addLine");
        this.stat_rdr_addLine_skip = new StatLong("renderer.addLine.skip");
        this.stat_rdr_curveBreak = new StatLong("renderer.curveBreakIntoLinesAndAdd");
        this.stat_rdr_curveBreak_dec = new StatLong("renderer.curveBreakIntoLinesAndAdd.dec");
        this.stat_rdr_curveBreak_inc = new StatLong("renderer.curveBreakIntoLinesAndAdd.inc");
        this.stat_rdr_quadBreak = new StatLong("renderer.quadBreakIntoLinesAndAdd");
        this.stat_rdr_quadBreak_dec = new StatLong("renderer.quadBreakIntoLinesAndAdd.dec");
        this.stat_rdr_edges = new StatLong("renderer.edges");
        this.stat_rdr_edges_count = new StatLong("renderer.edges.count");
        this.stat_rdr_edges_resizes = new StatLong("renderer.edges.resize");
        this.stat_rdr_activeEdges = new StatLong("renderer.activeEdges");
        this.stat_rdr_activeEdges_updates = new StatLong("renderer.activeEdges.updates");
        this.stat_rdr_activeEdges_adds = new StatLong("renderer.activeEdges.adds");
        this.stat_rdr_activeEdges_adds_high = new StatLong("renderer.activeEdges.adds_high");
        this.stat_rdr_crossings_updates = new StatLong("renderer.crossings.updates");
        this.stat_rdr_crossings_sorts = new StatLong("renderer.crossings.sorts");
        this.stat_rdr_crossings_bsearch = new StatLong("renderer.crossings.bsearch");
        this.stat_rdr_crossings_msorts = new StatLong("renderer.crossings.msorts");
        this.stat_str_polystack_curves = new StatLong("stroker.polystack.curves");
        this.stat_str_polystack_types = new StatLong("stroker.polystack.types");
        this.stat_cpd_polystack_curves = new StatLong("closedPathDetector.polystack.curves");
        this.stat_cpd_polystack_types = new StatLong("closedPathDetector.polystack.types");
        this.stat_pcf_idxstack_indices = new StatLong("pathClipFilter.stack.indices");
        this.stat_array_dasher_dasher = new StatLong("array.dasher.dasher.d_float");
        this.stat_array_dasher_firstSegmentsBuffer = new StatLong("array.dasher.firstSegmentsBuffer.d_float");
        this.stat_array_marlincache_rowAAChunk = new StatLong("array.marlincache.rowAAChunk.resize");
        this.stat_array_marlincache_touchedTile = new StatLong("array.marlincache.touchedTile.int");
        this.stat_array_renderer_alphaline = new StatLong("array.renderer.alphaline.int");
        this.stat_array_renderer_crossings = new StatLong("array.renderer.crossings.int");
        this.stat_array_renderer_aux_crossings = new StatLong("array.renderer.aux_crossings.int");
        this.stat_array_renderer_edgeBuckets = new StatLong("array.renderer.edgeBuckets.int");
        this.stat_array_renderer_edgeBucketCounts = new StatLong("array.renderer.edgeBucketCounts.int");
        this.stat_array_renderer_edgePtrs = new StatLong("array.renderer.edgePtrs.int");
        this.stat_array_renderer_aux_edgePtrs = new StatLong("array.renderer.aux_edgePtrs.int");
        this.stat_array_str_polystack_curves = new StatLong("array.stroker.polystack.curves.d_float");
        this.stat_array_str_polystack_types = new StatLong("array.stroker.polystack.curveTypes.d_byte");
        this.stat_array_cpd_polystack_curves = new StatLong("array.closedPathDetector.polystack.curves.d_float");
        this.stat_array_cpd_polystack_types = new StatLong("array.closedPathDetector.polystack.curveTypes.d_byte");
        this.stat_array_pcf_idxstack_indices = new StatLong("array.pathClipFilter.stack.indices.d_int");
        this.hist_rdr_edges_count = new Histogram("renderer.edges.count");
        this.hist_rdr_crossings = new Histogram("renderer.crossings");
        this.hist_rdr_crossings_ratio = new Histogram("renderer.crossings.ratio");
        this.hist_rdr_crossings_adds = new Histogram("renderer.crossings.adds");
        this.hist_rdr_crossings_msorts = new Histogram("renderer.crossings.msorts");
        this.hist_rdr_crossings_msorts_adds = new Histogram("renderer.crossings.msorts.adds");
        this.hist_str_polystack_curves = new Histogram("stroker.polystack.curves");
        this.hist_tile_generator_alpha = new Histogram("tile_generator.alpha");
        this.hist_tile_generator_encoding = new Histogram("tile_generator.encoding");
        this.hist_tile_generator_encoding_dist = new Histogram("tile_generator.encoding.dist");
        this.hist_tile_generator_encoding_ratio = new Histogram("tile_generator.encoding.ratio");
        this.hist_tile_generator_encoding_runLen = new Histogram("tile_generator.encoding.runLen");
        this.hist_cpd_polystack_curves = new Histogram("closedPathDetector.polystack.curves");
        this.hist_pcf_idxstack_indices = new Histogram("pathClipFilter.stack.indices");
        this.statistics = new StatLong[] { this.stat_cache_rowAA, this.stat_cache_rowAAChunk, this.stat_cache_tiles, this.stat_rdr_addLine, this.stat_rdr_addLine_skip, this.stat_rdr_curveBreak, this.stat_rdr_curveBreak_dec, this.stat_rdr_curveBreak_inc, this.stat_rdr_quadBreak, this.stat_rdr_quadBreak_dec, this.stat_rdr_edges, this.stat_rdr_edges_count, this.stat_rdr_edges_resizes, this.stat_rdr_activeEdges, this.stat_rdr_activeEdges_updates, this.stat_rdr_activeEdges_adds, this.stat_rdr_activeEdges_adds_high, this.stat_rdr_crossings_updates, this.stat_rdr_crossings_sorts, this.stat_rdr_crossings_bsearch, this.stat_rdr_crossings_msorts, this.stat_str_polystack_types, this.stat_str_polystack_curves, this.stat_cpd_polystack_curves, this.stat_cpd_polystack_types, this.stat_pcf_idxstack_indices, this.hist_rdr_edges_count, this.hist_rdr_crossings, this.hist_rdr_crossings_ratio, this.hist_rdr_crossings_adds, this.hist_rdr_crossings_msorts, this.hist_rdr_crossings_msorts_adds, this.hist_tile_generator_alpha, this.hist_tile_generator_encoding, this.hist_tile_generator_encoding_dist, this.hist_tile_generator_encoding_ratio, this.hist_tile_generator_encoding_runLen, this.hist_str_polystack_curves, this.hist_cpd_polystack_curves, this.hist_pcf_idxstack_indices, this.stat_array_dasher_dasher, this.stat_array_dasher_firstSegmentsBuffer, this.stat_array_marlincache_rowAAChunk, this.stat_array_marlincache_touchedTile, this.stat_array_renderer_alphaline, this.stat_array_renderer_crossings, this.stat_array_renderer_aux_crossings, this.stat_array_renderer_edgeBuckets, this.stat_array_renderer_edgeBucketCounts, this.stat_array_renderer_edgePtrs, this.stat_array_renderer_aux_edgePtrs, this.stat_array_str_polystack_curves, this.stat_array_str_polystack_types, this.stat_array_cpd_polystack_curves, this.stat_array_cpd_polystack_types, this.stat_array_pcf_idxstack_indices };
        this.mon_pre_getAATileGenerator = new Monitor("MarlinRenderingEngine.getAATileGenerator()");
        this.mon_rdr_addLine = new Monitor("Renderer.addLine()");
        this.mon_rdr_endRendering = new Monitor("Renderer.endRendering()");
        this.mon_rdr_endRendering_Y = new Monitor("Renderer._endRendering(Y)");
        this.mon_rdr_copyAARow = new Monitor("Renderer.copyAARow()");
        this.mon_pipe_renderTiles = new Monitor("AAShapePipe.renderTiles()");
        this.mon_ptg_getAlpha = new Monitor("MarlinTileGenerator.getAlpha()");
        this.mon_debug = new Monitor("DEBUG()");
        this.monitors = new Monitor[] { this.mon_pre_getAATileGenerator, this.mon_rdr_addLine, this.mon_rdr_endRendering, this.mon_rdr_endRendering_Y, this.mon_rdr_copyAARow, this.mon_pipe_renderTiles, this.mon_ptg_getAlpha, this.mon_debug };
        this.totalOffHeapInitial = 0L;
        this.totalOffHeap = 0L;
        this.totalOffHeapMax = 0L;
        this.cacheStats = null;
        this.name = name;
    }
    
    void dump() {
        MarlinUtils.logInfo("RendererContext: " + this.name);
        if (RendererStats.DO_STATS) {
            for (final StatLong statLong : this.statistics) {
                if (statLong.count != 0L) {
                    MarlinUtils.logInfo(statLong.toString());
                    statLong.reset();
                }
            }
            MarlinUtils.logInfo("OffHeap footprint: initial: " + this.totalOffHeapInitial + " bytes - max: " + this.totalOffHeapMax + " bytes");
            this.totalOffHeapMax = 0L;
            MarlinUtils.logInfo("Array caches for RendererContext: " + this.name);
            long totalOffHeapInitial = this.totalOffHeapInitial;
            long n = 0L;
            if (this.cacheStats != null) {
                for (final ArrayCacheConst.CacheStats cacheStats2 : this.cacheStats) {
                    n += cacheStats2.dumpStats();
                    totalOffHeapInitial += cacheStats2.getTotalInitialBytes();
                    cacheStats2.reset();
                }
            }
            MarlinUtils.logInfo("Heap footprint: initial: " + totalOffHeapInitial + " bytes - cache: " + n + " bytes");
        }
    }
    
    static final class RendererStatsHolder
    {
        private static volatile RendererStatsHolder SINGLETON;
        private final ConcurrentLinkedQueue<RendererStats> allStats;
        private static final ReferenceQueue<Object> REF_QUEUE;
        private static final Vector<RendererStatsReference> REF_LIST;
        
        static synchronized RendererStatsHolder getInstance() {
            if (RendererStatsHolder.SINGLETON == null) {
                RendererStatsHolder.SINGLETON = new RendererStatsHolder();
            }
            return RendererStatsHolder.SINGLETON;
        }
        
        static void dumpStats() {
            if (RendererStatsHolder.SINGLETON != null) {
                RendererStatsHolder.SINGLETON.dump();
            }
        }
        
        private RendererStatsHolder() {
            this.allStats = new ConcurrentLinkedQueue<RendererStats>();
            AccessController.doPrivileged(() -> {
                MarlinUtils.getRootThreadGroup();
                final ThreadGroup threadGroup;
                new Thread(threadGroup, new RendererStatsDisposer(), "MarlinRenderer Disposer");
                final Thread thread2;
                thread2.setContextClassLoader(null);
                thread2.setDaemon(true);
                thread2.setPriority(8);
                thread2.start();
                return null;
            });
        }
        
        void add(final Object o, final RendererStats rendererStats) {
            this.allStats.add(rendererStats);
            RendererStatsHolder.REF_LIST.add(new RendererStatsReference(o, rendererStats));
        }
        
        void remove(final RendererStats rendererStats) {
            rendererStats.dump();
            this.allStats.remove(rendererStats);
        }
        
        void dump() {
            final Iterator<RendererStats> iterator = this.allStats.iterator();
            while (iterator.hasNext()) {
                iterator.next().dump();
            }
        }
        
        static {
            RendererStatsHolder.SINGLETON = null;
            REF_QUEUE = new ReferenceQueue<Object>();
            REF_LIST = new Vector<RendererStatsReference>(32);
        }
        
        static final class RendererStatsReference extends PhantomReference<Object>
        {
            private final RendererStats stats;
            
            RendererStatsReference(final Object o, final RendererStats stats) {
                super(o, RendererStatsHolder.REF_QUEUE);
                this.stats = stats;
            }
            
            void dispose() {
                RendererStatsHolder.getInstance().remove(this.stats);
            }
        }
        
        static final class RendererStatsDisposer implements Runnable
        {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        final RendererStatsReference rendererStatsReference = (RendererStatsReference)RendererStatsHolder.REF_QUEUE.remove();
                        rendererStatsReference.dispose();
                        RendererStatsHolder.REF_LIST.remove(rendererStatsReference);
                    }
                    catch (final InterruptedException ex) {
                        MarlinUtils.logException("RendererStatsDisposer interrupted:", ex);
                    }
                }
            }
        }
    }
}
