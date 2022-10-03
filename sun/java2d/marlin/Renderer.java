package sun.java2d.marlin;

import sun.misc.Unsafe;
import sun.awt.geom.PathConsumer2D;

final class Renderer implements PathConsumer2D, MarlinRenderer
{
    static final boolean DISABLE_RENDER = false;
    static final boolean ENABLE_BLOCK_FLAGS;
    static final boolean ENABLE_BLOCK_FLAGS_HEURISTICS;
    private static final int ALL_BUT_LSB = -2;
    private static final int ERR_STEP_MAX = Integer.MAX_VALUE;
    private static final double POWER_2_TO_32 = 4.294967296E9;
    static final float SUBPIXEL_SCALE_X;
    static final float SUBPIXEL_SCALE_Y;
    static final int SUBPIXEL_MASK_X;
    static final int SUBPIXEL_MASK_Y;
    static final float RDR_OFFSET_X;
    static final float RDR_OFFSET_Y;
    private static final int SUBPIXEL_TILE;
    static final int INITIAL_BUCKET_ARRAY;
    static final int INITIAL_CROSSING_COUNT;
    public static final long OFF_CURX_OR = 0L;
    public static final long OFF_ERROR;
    public static final long OFF_BUMP_X;
    public static final long OFF_BUMP_ERR;
    public static final long OFF_NEXT;
    public static final long OFF_YMAX;
    public static final int SIZEOF_EDGE_BYTES;
    private static final float CUB_DEC_ERR_SUBPIX;
    private static final float CUB_INC_ERR_SUBPIX;
    public static final float SCALE_DY;
    public static final float CUB_DEC_BND;
    public static final float CUB_INC_BND;
    public static final int CUB_COUNT_LG = 2;
    private static final int CUB_COUNT = 4;
    private static final int CUB_COUNT_2 = 16;
    private static final int CUB_COUNT_3 = 64;
    private static final float CUB_INV_COUNT = 0.25f;
    private static final float CUB_INV_COUNT_2 = 0.0625f;
    private static final float CUB_INV_COUNT_3 = 0.015625f;
    private static final float QUAD_DEC_ERR_SUBPIX;
    public static final float QUAD_DEC_BND;
    private int[] crossings;
    private int[] aux_crossings;
    private int edgeCount;
    private int[] edgePtrs;
    private int[] aux_edgePtrs;
    private int activeEdgeMaxUsed;
    private final IntArrayCache.Reference crossings_ref;
    private final IntArrayCache.Reference edgePtrs_ref;
    private final IntArrayCache.Reference aux_crossings_ref;
    private final IntArrayCache.Reference aux_edgePtrs_ref;
    private int edgeMinY;
    private int edgeMaxY;
    private float edgeMinX;
    private float edgeMaxX;
    private final OffHeapArray edges;
    private int[] edgeBuckets;
    private int[] edgeBucketCounts;
    private int buckets_minY;
    private int buckets_maxY;
    private final IntArrayCache.Reference edgeBuckets_ref;
    private final IntArrayCache.Reference edgeBucketCounts_ref;
    final MarlinCache cache;
    private int boundsMinX;
    private int boundsMinY;
    private int boundsMaxX;
    private int boundsMaxY;
    private int windingRule;
    private float x0;
    private float y0;
    private float sx0;
    private float sy0;
    final RendererContext rdrCtx;
    private final Curve curve;
    private int[] alphaLine;
    private final IntArrayCache.Reference alphaLine_ref;
    private boolean enableBlkFlags;
    private boolean prevUseBlkFlags;
    private int[] blkFlags;
    private final IntArrayCache.Reference blkFlags_ref;
    private int bbox_spminX;
    private int bbox_spmaxX;
    private int bbox_spminY;
    private int bbox_spmaxY;
    
    private void quadBreakIntoLinesAndAdd(float n, float n2, final Curve curve, final float n3, final float n4) {
        int n5 = 1;
        float n6 = Math.abs(curve.dbx) + Math.abs(curve.dby) * Renderer.SCALE_DY;
        while (n6 >= Renderer.QUAD_DEC_BND) {
            n6 /= 4.0f;
            n5 <<= 1;
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.stat_rdr_quadBreak_dec.add(n5);
            }
        }
        final int n7;
        if ((n7 = n5) > 1) {
            final float n8 = 1.0f / n5;
            final float n9 = n8 * n8;
            final float n10 = curve.dbx * n9;
            final float n11 = curve.dby * n9;
            float n12 = curve.bx * n9 + curve.cx * n8;
            float n13 = curve.by * n9 + curve.cy * n8;
            float n14 = n;
            float n15 = n2;
            while (--n5 > 0) {
                n14 += n12;
                n15 += n13;
                this.addLine(n, n2, n14, n15);
                n = n14;
                n2 = n15;
                n12 += n10;
                n13 += n11;
            }
        }
        this.addLine(n, n2, n3, n4);
        if (Renderer.DO_STATS) {
            this.rdrCtx.stats.stat_rdr_quadBreak.add(n7);
        }
    }
    
    private void curveBreakIntoLinesAndAdd(float n, float n2, final Curve curve, final float n3, final float n4) {
        int i = 4;
        float n5 = 2.0f * curve.dax * 0.015625f;
        float n6 = 2.0f * curve.day * 0.015625f;
        float n7 = n5 + curve.dbx * 0.0625f;
        float n8 = n6 + curve.dby * 0.0625f;
        float n9 = curve.ax * 0.015625f + curve.bx * 0.0625f + curve.cx * 0.25f;
        float n10 = curve.ay * 0.015625f + curve.by * 0.0625f + curve.cy * 0.25f;
        final int n11 = 0;
        final float cub_DEC_BND = Renderer.CUB_DEC_BND;
        final float cub_INC_BND = Renderer.CUB_INC_BND;
        final float scale_DY = Renderer.SCALE_DY;
        float n12 = n;
        float n13 = n2;
        while (i > 0) {
            while (i % 2 == 0 && Math.abs(n7) + Math.abs(n8) * scale_DY <= cub_INC_BND) {
                n9 = 2.0f * n9 + n7;
                n10 = 2.0f * n10 + n8;
                n7 = 4.0f * (n7 + n5);
                n8 = 4.0f * (n8 + n6);
                n5 *= 8.0f;
                n6 *= 8.0f;
                i >>= 1;
                if (Renderer.DO_STATS) {
                    this.rdrCtx.stats.stat_rdr_curveBreak_inc.add(i);
                }
            }
            while (Math.abs(n7) + Math.abs(n8) * scale_DY >= cub_DEC_BND) {
                n5 /= 8.0f;
                n6 /= 8.0f;
                n7 = n7 / 4.0f - n5;
                n8 = n8 / 4.0f - n6;
                n9 = (n9 - n7) / 2.0f;
                n10 = (n10 - n8) / 2.0f;
                i <<= 1;
                if (Renderer.DO_STATS) {
                    this.rdrCtx.stats.stat_rdr_curveBreak_dec.add(i);
                }
            }
            if (--i == 0) {
                break;
            }
            n12 += n9;
            n13 += n10;
            n9 += n7;
            n10 += n8;
            n7 += n5;
            n8 += n6;
            this.addLine(n, n2, n12, n13);
            n = n12;
            n2 = n13;
        }
        this.addLine(n, n2, n3, n4);
        if (Renderer.DO_STATS) {
            this.rdrCtx.stats.stat_rdr_curveBreak.add(n11 + 1);
        }
    }
    
    private void addLine(float n, float n2, float n3, float n4) {
        if (Renderer.DO_STATS) {
            this.rdrCtx.stats.stat_rdr_addLine.add(1);
        }
        boolean b = true;
        if (n4 < n2) {
            b = false;
            final float n5 = n4;
            n4 = n2;
            n2 = n5;
            final float n6 = n3;
            n3 = n;
            n = n6;
        }
        final int max = FloatMath.max(FloatMath.ceil_int(n2), this.boundsMinY);
        final int min = FloatMath.min(FloatMath.ceil_int(n4), this.boundsMaxY);
        if (max >= min) {
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.stat_rdr_addLine_skip.add(1);
            }
            return;
        }
        if (max < this.edgeMinY) {
            this.edgeMinY = max;
        }
        if (min > this.edgeMaxY) {
            this.edgeMaxY = min;
        }
        final double n7 = n;
        final double n8 = n2;
        final double n9 = (n7 - n3) / (n8 - n4);
        if (n9 >= 0.0) {
            if (n < this.edgeMinX) {
                this.edgeMinX = n;
            }
            if (n3 > this.edgeMaxX) {
                this.edgeMaxX = n3;
            }
        }
        else {
            if (n3 < this.edgeMinX) {
                this.edgeMinX = n3;
            }
            if (n > this.edgeMaxX) {
                this.edgeMaxX = n;
            }
        }
        final int sizeof_EDGE_BYTES = Renderer.SIZEOF_EDGE_BYTES;
        final OffHeapArray edges = this.edges;
        final int used = edges.used;
        if (edges.length - used < sizeof_EDGE_BYTES) {
            final long newLargeSize = ArrayCacheConst.getNewLargeSize(edges.length, used + sizeof_EDGE_BYTES);
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.stat_rdr_edges_resizes.add(newLargeSize);
            }
            edges.resize(newLargeSize);
        }
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long n10 = edges.address + used;
        final long n11 = (long)(4.294967296E9 * (n7 + (max - n8) * n9)) + 2147483647L;
        unsafe.putInt(n10, ((int)(n11 >> 31) & 0xFFFFFFFE) | (b ? 1 : 0));
        final long n12 = n10 + 4L;
        unsafe.putInt(n12, (int)n11 >>> 1);
        final long n13 = n12 + 4L;
        final long n14 = (long)(4.294967296E9 * n9);
        unsafe.putInt(n13, (int)(n14 >> 31) & 0xFFFFFFFE);
        final long n15 = n13 + 4L;
        unsafe.putInt(n15, (int)n14 >>> 1);
        final long n16 = n15 + 4L;
        final int[] edgeBuckets = this.edgeBuckets;
        final int[] edgeBucketCounts = this.edgeBucketCounts;
        final int boundsMinY = this.boundsMinY;
        final int n17 = max - boundsMinY;
        unsafe.putInt(n16, edgeBuckets[n17]);
        unsafe.putInt(n16 + 4L, min);
        edgeBuckets[n17] = used;
        final int[] array = edgeBucketCounts;
        final int n18 = n17;
        array[n18] += 2;
        final int[] array2 = edgeBucketCounts;
        final int n19 = min - boundsMinY;
        array2[n19] |= 0x1;
        final OffHeapArray offHeapArray = edges;
        offHeapArray.used += sizeof_EDGE_BYTES;
    }
    
    Renderer(final RendererContext rdrCtx) {
        this.edgeMinY = Integer.MAX_VALUE;
        this.edgeMaxY = Integer.MIN_VALUE;
        this.edgeMinX = Float.POSITIVE_INFINITY;
        this.edgeMaxX = Float.NEGATIVE_INFINITY;
        this.enableBlkFlags = false;
        this.prevUseBlkFlags = false;
        this.rdrCtx = rdrCtx;
        this.curve = rdrCtx.curve;
        this.cache = rdrCtx.cache;
        this.edges = rdrCtx.newOffHeapArray(Renderer.INITIAL_EDGES_CAPACITY);
        this.edgeBuckets_ref = rdrCtx.newCleanIntArrayRef(Renderer.INITIAL_BUCKET_ARRAY);
        this.edgeBucketCounts_ref = rdrCtx.newCleanIntArrayRef(Renderer.INITIAL_BUCKET_ARRAY);
        this.edgeBuckets = this.edgeBuckets_ref.initial;
        this.edgeBucketCounts = this.edgeBucketCounts_ref.initial;
        this.alphaLine_ref = rdrCtx.newCleanIntArrayRef(Renderer.INITIAL_AA_ARRAY);
        this.alphaLine = this.alphaLine_ref.initial;
        this.crossings_ref = rdrCtx.newDirtyIntArrayRef(Renderer.INITIAL_CROSSING_COUNT);
        this.aux_crossings_ref = rdrCtx.newDirtyIntArrayRef(Renderer.INITIAL_CROSSING_COUNT);
        this.edgePtrs_ref = rdrCtx.newDirtyIntArrayRef(Renderer.INITIAL_CROSSING_COUNT);
        this.aux_edgePtrs_ref = rdrCtx.newDirtyIntArrayRef(Renderer.INITIAL_CROSSING_COUNT);
        this.crossings = this.crossings_ref.initial;
        this.aux_crossings = this.aux_crossings_ref.initial;
        this.edgePtrs = this.edgePtrs_ref.initial;
        this.aux_edgePtrs = this.aux_edgePtrs_ref.initial;
        this.blkFlags_ref = rdrCtx.newCleanIntArrayRef(256);
        this.blkFlags = this.blkFlags_ref.initial;
    }
    
    Renderer init(final int n, final int n2, final int n3, final int n4, final int windingRule) {
        this.windingRule = windingRule;
        this.boundsMinX = n << Renderer.SUBPIXEL_LG_POSITIONS_X;
        this.boundsMaxX = n + n3 << Renderer.SUBPIXEL_LG_POSITIONS_X;
        this.boundsMinY = n2 << Renderer.SUBPIXEL_LG_POSITIONS_Y;
        this.boundsMaxY = n2 + n4 << Renderer.SUBPIXEL_LG_POSITIONS_Y;
        if (Renderer.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("boundsXY = [" + this.boundsMinX + " ... " + this.boundsMaxX + "[ [" + this.boundsMinY + " ... " + this.boundsMaxY + "[");
        }
        final int n5 = this.boundsMaxY - this.boundsMinY + 1;
        if (n5 > Renderer.INITIAL_BUCKET_ARRAY) {
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.stat_array_renderer_edgeBuckets.add(n5);
                this.rdrCtx.stats.stat_array_renderer_edgeBucketCounts.add(n5);
            }
            this.edgeBuckets = this.edgeBuckets_ref.getArray(n5);
            this.edgeBucketCounts = this.edgeBucketCounts_ref.getArray(n5);
        }
        this.edgeMinY = Integer.MAX_VALUE;
        this.edgeMaxY = Integer.MIN_VALUE;
        this.edgeMinX = Float.POSITIVE_INFINITY;
        this.edgeMaxX = Float.NEGATIVE_INFINITY;
        this.edgeCount = 0;
        this.activeEdgeMaxUsed = 0;
        this.edges.used = 0;
        return this;
    }
    
    void dispose() {
        if (Renderer.DO_STATS) {
            this.rdrCtx.stats.stat_rdr_activeEdges.add(this.activeEdgeMaxUsed);
            this.rdrCtx.stats.stat_rdr_edges.add(this.edges.used);
            this.rdrCtx.stats.stat_rdr_edges_count.add(this.edges.used / Renderer.SIZEOF_EDGE_BYTES);
            this.rdrCtx.stats.hist_rdr_edges_count.add(this.edges.used / Renderer.SIZEOF_EDGE_BYTES);
            final RendererStats stats = this.rdrCtx.stats;
            stats.totalOffHeap += this.edges.length;
        }
        this.crossings = this.crossings_ref.putArray(this.crossings);
        this.aux_crossings = this.aux_crossings_ref.putArray(this.aux_crossings);
        this.edgePtrs = this.edgePtrs_ref.putArray(this.edgePtrs);
        this.aux_edgePtrs = this.aux_edgePtrs_ref.putArray(this.aux_edgePtrs);
        this.alphaLine = this.alphaLine_ref.putArray(this.alphaLine, 0, 0);
        this.blkFlags = this.blkFlags_ref.putArray(this.blkFlags, 0, 0);
        if (this.edgeMinY != Integer.MAX_VALUE) {
            if (this.rdrCtx.dirty) {
                this.buckets_minY = 0;
                this.buckets_maxY = this.boundsMaxY - this.boundsMinY;
            }
            this.edgeBuckets = this.edgeBuckets_ref.putArray(this.edgeBuckets, this.buckets_minY, this.buckets_maxY);
            this.edgeBucketCounts = this.edgeBucketCounts_ref.putArray(this.edgeBucketCounts, this.buckets_minY, this.buckets_maxY + 1);
        }
        else {
            this.edgeBuckets = this.edgeBuckets_ref.putArray(this.edgeBuckets, 0, 0);
            this.edgeBucketCounts = this.edgeBucketCounts_ref.putArray(this.edgeBucketCounts, 0, 0);
        }
        if (this.edges.length != Renderer.INITIAL_EDGES_CAPACITY) {
            this.edges.resize(Renderer.INITIAL_EDGES_CAPACITY);
        }
        MarlinRenderingEngine.returnRendererContext(this.rdrCtx);
    }
    
    private static float tosubpixx(final float n) {
        return Renderer.SUBPIXEL_SCALE_X * n;
    }
    
    private static float tosubpixy(final float n) {
        return Renderer.SUBPIXEL_SCALE_Y * n - 0.5f;
    }
    
    @Override
    public void moveTo(final float n, final float n2) {
        this.closePath();
        final float tosubpixx = tosubpixx(n);
        final float tosubpixy = tosubpixy(n2);
        this.sx0 = tosubpixx;
        this.sy0 = tosubpixy;
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        final float tosubpixx = tosubpixx(n);
        final float tosubpixy = tosubpixy(n2);
        this.addLine(this.x0, this.y0, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float tosubpixx = tosubpixx(n5);
        final float tosubpixy = tosubpixy(n6);
        this.curve.set(this.x0, this.y0, tosubpixx(n), tosubpixy(n2), tosubpixx(n3), tosubpixy(n4), tosubpixx, tosubpixy);
        this.curveBreakIntoLinesAndAdd(this.x0, this.y0, this.curve, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float n3, final float n4) {
        final float tosubpixx = tosubpixx(n3);
        final float tosubpixy = tosubpixy(n4);
        this.curve.set(this.x0, this.y0, tosubpixx(n), tosubpixy(n2), tosubpixx, tosubpixy);
        this.quadBreakIntoLinesAndAdd(this.x0, this.y0, this.curve, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void closePath() {
        if (this.x0 != this.sx0 || this.y0 != this.sy0) {
            this.addLine(this.x0, this.y0, this.sx0, this.sy0);
            this.x0 = this.sx0;
            this.y0 = this.sy0;
        }
    }
    
    @Override
    public void pathDone() {
        this.closePath();
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Renderer does not use a native consumer.");
    }
    
    private void _endRendering(final int n, final int n2) {
        final int bbox_spminX = this.bbox_spminX;
        final int bbox_spmaxX = this.bbox_spmaxX;
        final boolean b = this.windingRule == 0;
        final int[] alphaLine = this.alphaLine;
        final MarlinCache cache = this.cache;
        final OffHeapArray edges = this.edges;
        final int[] edgeBuckets = this.edgeBuckets;
        final int[] edgeBucketCounts = this.edgeBucketCounts;
        int[] crossings = this.crossings;
        int[] edgePtrs = this.edgePtrs;
        int[] aux_crossings = this.aux_crossings;
        int[] aux_edgePtrs = this.aux_edgePtrs;
        final long off_ERROR = Renderer.OFF_ERROR;
        final long off_BUMP_X = Renderer.OFF_BUMP_X;
        final long off_BUMP_ERR = Renderer.OFF_BUMP_ERR;
        final long off_NEXT = Renderer.OFF_NEXT;
        final long off_YMAX = Renderer.OFF_YMAX;
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = edges.address;
        final int subpixel_LG_POSITIONS_X = Renderer.SUBPIXEL_LG_POSITIONS_X;
        final int subpixel_LG_POSITIONS_Y = Renderer.SUBPIXEL_LG_POSITIONS_Y;
        final int subpixel_MASK_X = Renderer.SUBPIXEL_MASK_X;
        final int subpixel_MASK_Y = Renderer.SUBPIXEL_MASK_Y;
        final int subpixel_POSITIONS_X = Renderer.SUBPIXEL_POSITIONS_X;
        int n3 = Integer.MAX_VALUE;
        int n4 = Integer.MIN_VALUE;
        int i = n;
        int n5 = i - this.boundsMinY;
        int j = this.edgeCount;
        int n6 = edgePtrs.length;
        int n7 = crossings.length;
        int activeEdgeMaxUsed = this.activeEdgeMaxUsed;
        int n8 = 0;
        final int[] blkFlags = this.blkFlags;
        final int block_SIZE_LG = Renderer.BLOCK_SIZE_LG;
        final int block_SIZE = Renderer.BLOCK_SIZE;
        final boolean b2 = Renderer.ENABLE_BLOCK_FLAGS_HEURISTICS && this.enableBlkFlags;
        boolean prevUseBlkFlags = this.prevUseBlkFlags;
        final int stroking = this.rdrCtx.stroking;
        int n9 = -1;
        while (i < n2) {
            final int n10 = edgeBucketCounts[n5];
            int n11 = j;
            if (n10 != 0) {
                if (Renderer.DO_STATS) {
                    this.rdrCtx.stats.stat_rdr_activeEdges_updates.add(j);
                }
                if ((n10 & 0x1) != 0x0) {
                    final long n12 = address + off_YMAX;
                    int k = 0;
                    int n13 = 0;
                    while (k < j) {
                        final int n14 = edgePtrs[k];
                        if (unsafe.getInt(n12 + n14) > i) {
                            edgePtrs[n13++] = n14;
                        }
                        ++k;
                    }
                    j = (n11 = n13);
                }
                n8 = n10 >> 1;
                if (n8 != 0) {
                    if (Renderer.DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_activeEdges_adds.add(n8);
                        if (n8 > 10) {
                            this.rdrCtx.stats.stat_rdr_activeEdges_adds_high.add(n8);
                        }
                    }
                    final int n15 = j + n8;
                    if (n6 < n15) {
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_array_renderer_edgePtrs.add(n15);
                        }
                        edgePtrs = (this.edgePtrs = this.edgePtrs_ref.widenArray(edgePtrs, j, n15));
                        n6 = edgePtrs.length;
                        this.aux_edgePtrs_ref.putArray(aux_edgePtrs);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_array_renderer_aux_edgePtrs.add(n15);
                        }
                        aux_edgePtrs = (this.aux_edgePtrs = this.aux_edgePtrs_ref.getArray(ArrayCacheConst.getNewSize(j, n15)));
                    }
                    final long n16 = address + off_NEXT;
                    int int1 = edgeBuckets[n5];
                    while (j < n15) {
                        edgePtrs[j] = int1;
                        int1 = unsafe.getInt(n16 + int1);
                        ++j;
                    }
                    if (n7 < j) {
                        this.crossings_ref.putArray(crossings);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_array_renderer_crossings.add(j);
                        }
                        crossings = (this.crossings = this.crossings_ref.getArray(j));
                        this.aux_crossings_ref.putArray(aux_crossings);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_array_renderer_aux_crossings.add(j);
                        }
                        aux_crossings = (this.aux_crossings = this.aux_crossings_ref.getArray(j));
                        n7 = crossings.length;
                    }
                    if (Renderer.DO_STATS && j > activeEdgeMaxUsed) {
                        activeEdgeMaxUsed = j;
                    }
                }
            }
            if (j != 0) {
                if (n8 < 10 || j < 40) {
                    if (Renderer.DO_STATS) {
                        this.rdrCtx.stats.hist_rdr_crossings.add(j);
                        this.rdrCtx.stats.hist_rdr_crossings_adds.add(n8);
                    }
                    final boolean b3 = j >= 20;
                    int n17 = Integer.MIN_VALUE;
                    for (int l = 0; l < j; ++l) {
                        final int n18 = edgePtrs[l];
                        final long n19 = address + n18;
                        final int int2;
                        final int n20 = (int2 = unsafe.getInt(n19)) + unsafe.getInt(n19 + off_BUMP_X);
                        final int n21 = unsafe.getInt(n19 + off_ERROR) + unsafe.getInt(n19 + off_BUMP_ERR);
                        unsafe.putInt(n19, n20 - (n21 >> 30 & 0xFFFFFFFE));
                        unsafe.putInt(n19 + off_ERROR, n21 & Integer.MAX_VALUE);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_rdr_crossings_updates.add(j);
                        }
                        if (int2 < n17) {
                            if (Renderer.DO_STATS) {
                                this.rdrCtx.stats.stat_rdr_crossings_sorts.add(l);
                            }
                            if (b3 && l >= n11) {
                                if (Renderer.DO_STATS) {
                                    this.rdrCtx.stats.stat_rdr_crossings_bsearch.add(l);
                                }
                                int n22 = 0;
                                int n23 = l - 1;
                                do {
                                    final int n24 = n22 + n23 >> 1;
                                    if (crossings[n24] < int2) {
                                        n22 = n24 + 1;
                                    }
                                    else {
                                        n23 = n24 - 1;
                                    }
                                } while (n22 <= n23);
                                for (int n25 = l - 1; n25 >= n22; --n25) {
                                    crossings[n25 + 1] = crossings[n25];
                                    edgePtrs[n25 + 1] = edgePtrs[n25];
                                }
                                crossings[n22] = int2;
                                edgePtrs[n22] = n18;
                            }
                            else {
                                int n26 = l - 1;
                                crossings[l] = crossings[n26];
                                edgePtrs[l] = edgePtrs[n26];
                                while (--n26 >= 0 && crossings[n26] > int2) {
                                    crossings[n26 + 1] = crossings[n26];
                                    edgePtrs[n26 + 1] = edgePtrs[n26];
                                }
                                crossings[n26 + 1] = int2;
                                edgePtrs[n26 + 1] = n18;
                            }
                        }
                        else {
                            n17 = (crossings[l] = int2);
                        }
                    }
                }
                else {
                    if (Renderer.DO_STATS) {
                        this.rdrCtx.stats.stat_rdr_crossings_msorts.add(j);
                        this.rdrCtx.stats.hist_rdr_crossings_ratio.add(1000 * n8 / j);
                        this.rdrCtx.stats.hist_rdr_crossings_msorts.add(j);
                        this.rdrCtx.stats.hist_rdr_crossings_msorts_adds.add(n8);
                    }
                    int n27 = Integer.MIN_VALUE;
                    for (int n28 = 0; n28 < j; ++n28) {
                        final int n29 = edgePtrs[n28];
                        final long n30 = address + n29;
                        final int int3;
                        final int n31 = (int3 = unsafe.getInt(n30)) + unsafe.getInt(n30 + off_BUMP_X);
                        final int n32 = unsafe.getInt(n30 + off_ERROR) + unsafe.getInt(n30 + off_BUMP_ERR);
                        unsafe.putInt(n30, n31 - (n32 >> 30 & 0xFFFFFFFE));
                        unsafe.putInt(n30 + off_ERROR, n32 & Integer.MAX_VALUE);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.stat_rdr_crossings_updates.add(j);
                        }
                        if (n28 >= n11) {
                            crossings[n28] = int3;
                        }
                        else if (int3 < n27) {
                            if (Renderer.DO_STATS) {
                                this.rdrCtx.stats.stat_rdr_crossings_sorts.add(n28);
                            }
                            int n33 = n28 - 1;
                            aux_crossings[n28] = aux_crossings[n33];
                            aux_edgePtrs[n28] = aux_edgePtrs[n33];
                            while (--n33 >= 0 && aux_crossings[n33] > int3) {
                                aux_crossings[n33 + 1] = aux_crossings[n33];
                                aux_edgePtrs[n33 + 1] = aux_edgePtrs[n33];
                            }
                            aux_crossings[n33 + 1] = int3;
                            aux_edgePtrs[n33 + 1] = n29;
                        }
                        else {
                            n27 = (aux_crossings[n28] = int3);
                            aux_edgePtrs[n28] = n29;
                        }
                    }
                    MergeSort.mergeSortNoCopy(crossings, edgePtrs, aux_crossings, aux_edgePtrs, j, n11);
                }
                n8 = 0;
                final int n34 = crossings[0];
                final int n35 = n34 >> 1;
                if (n35 < n3) {
                    n3 = n35;
                }
                final int n36 = crossings[j - 1] >> 1;
                if (n36 > n4) {
                    n4 = n36;
                }
                int n38;
                int n37 = n38 = n35;
                int n39 = ((n34 & 0x1) << 1) - 1;
                if (b) {
                    int n40 = n39;
                    for (int n41 = 1; n41 < j; ++n41) {
                        final int n42 = crossings[n41];
                        final int n43 = n42 >> 1;
                        final int n44 = ((n42 & 0x1) << 1) - 1;
                        if ((n40 & 0x1) != 0x0) {
                            final int n45 = (n38 > bbox_spminX) ? n38 : bbox_spminX;
                            int n46;
                            if (n43 < bbox_spmaxX) {
                                n46 = n43;
                            }
                            else {
                                n46 = bbox_spmaxX;
                                n41 = j;
                            }
                            if (n45 < n46) {
                                final int n47 = n45 - bbox_spminX;
                                final int n48 = n46 - bbox_spminX;
                                final int n49 = n47 >> subpixel_LG_POSITIONS_X;
                                if (n49 == n48 - 1 >> subpixel_LG_POSITIONS_X) {
                                    final int n50 = n48 - n47;
                                    final int[] array = alphaLine;
                                    final int n51 = n49;
                                    array[n51] += n50;
                                    final int[] array2 = alphaLine;
                                    final int n52 = n49 + 1;
                                    array2[n52] -= n50;
                                    if (prevUseBlkFlags) {
                                        blkFlags[n49 >> block_SIZE_LG] = 1;
                                    }
                                }
                                else {
                                    final int n53 = n47 & subpixel_MASK_X;
                                    final int[] array3 = alphaLine;
                                    final int n54 = n49;
                                    array3[n54] += subpixel_POSITIONS_X - n53;
                                    final int[] array4 = alphaLine;
                                    final int n55 = n49 + 1;
                                    array4[n55] += n53;
                                    final int n56 = n48 >> subpixel_LG_POSITIONS_X;
                                    final int n57 = n48 & subpixel_MASK_X;
                                    final int[] array5 = alphaLine;
                                    final int n58 = n56;
                                    array5[n58] -= subpixel_POSITIONS_X - n57;
                                    final int[] array6 = alphaLine;
                                    final int n59 = n56 + 1;
                                    array6[n59] -= n57;
                                    if (prevUseBlkFlags) {
                                        blkFlags[n56 >> block_SIZE_LG] = (blkFlags[n49 >> block_SIZE_LG] = 1);
                                    }
                                }
                            }
                        }
                        n40 += n44;
                        n38 = n43;
                    }
                }
                else {
                    int n60 = 1;
                    int n61 = 0;
                    while (true) {
                        n61 += n39;
                        if (n61 != 0) {
                            if (n38 > n37) {
                                n38 = n37;
                            }
                        }
                        else {
                            final int n62 = (n38 > bbox_spminX) ? n38 : bbox_spminX;
                            int n63;
                            if (n37 < bbox_spmaxX) {
                                n63 = n37;
                            }
                            else {
                                n63 = bbox_spmaxX;
                                n60 = j;
                            }
                            if (n62 < n63) {
                                final int n64 = n62 - bbox_spminX;
                                final int n65 = n63 - bbox_spminX;
                                final int n66 = n64 >> subpixel_LG_POSITIONS_X;
                                if (n66 == n65 - 1 >> subpixel_LG_POSITIONS_X) {
                                    final int n67 = n65 - n64;
                                    final int[] array7 = alphaLine;
                                    final int n68 = n66;
                                    array7[n68] += n67;
                                    final int[] array8 = alphaLine;
                                    final int n69 = n66 + 1;
                                    array8[n69] -= n67;
                                    if (prevUseBlkFlags) {
                                        blkFlags[n66 >> block_SIZE_LG] = 1;
                                    }
                                }
                                else {
                                    final int n70 = n64 & subpixel_MASK_X;
                                    final int[] array9 = alphaLine;
                                    final int n71 = n66;
                                    array9[n71] += subpixel_POSITIONS_X - n70;
                                    final int[] array10 = alphaLine;
                                    final int n72 = n66 + 1;
                                    array10[n72] += n70;
                                    final int n73 = n65 >> subpixel_LG_POSITIONS_X;
                                    final int n74 = n65 & subpixel_MASK_X;
                                    final int[] array11 = alphaLine;
                                    final int n75 = n73;
                                    array11[n75] -= subpixel_POSITIONS_X - n74;
                                    final int[] array12 = alphaLine;
                                    final int n76 = n73 + 1;
                                    array12[n76] -= n74;
                                    if (prevUseBlkFlags) {
                                        blkFlags[n73 >> block_SIZE_LG] = (blkFlags[n66 >> block_SIZE_LG] = 1);
                                    }
                                }
                            }
                            n38 = Integer.MAX_VALUE;
                        }
                        if (n60 == j) {
                            break;
                        }
                        final int n77 = crossings[n60];
                        n37 = n77 >> 1;
                        n39 = ((n77 & 0x1) << 1) - 1;
                        ++n60;
                    }
                }
            }
            if ((i & subpixel_MASK_Y) == subpixel_MASK_Y) {
                n9 = i >> subpixel_LG_POSITIONS_Y;
                final int n78 = FloatMath.max(n3, bbox_spminX) >> subpixel_LG_POSITIONS_X;
                final int n79 = FloatMath.min(n4, bbox_spmaxX) >> subpixel_LG_POSITIONS_X;
                if (n79 >= n78) {
                    this.copyAARow(alphaLine, n9, n78, n79 + 1, prevUseBlkFlags);
                    if (b2) {
                        final int n80 = n79 - n78;
                        prevUseBlkFlags = (n80 > block_SIZE && n80 > (j >> stroking) - 1 << block_SIZE_LG);
                        if (Renderer.DO_STATS) {
                            this.rdrCtx.stats.hist_tile_generator_encoding_dist.add(n80 / FloatMath.max(1, (j >> stroking) - 1));
                        }
                    }
                }
                else {
                    cache.clearAARow(n9);
                }
                n3 = Integer.MAX_VALUE;
                n4 = Integer.MIN_VALUE;
            }
            ++i;
            ++n5;
        }
        final int n81 = --i >> subpixel_LG_POSITIONS_Y;
        final int n82 = FloatMath.max(n3, bbox_spminX) >> subpixel_LG_POSITIONS_X;
        final int n83 = FloatMath.min(n4, bbox_spmaxX) >> subpixel_LG_POSITIONS_X;
        if (n83 >= n82) {
            this.copyAARow(alphaLine, n81, n82, n83 + 1, prevUseBlkFlags);
        }
        else if (n81 != n9) {
            cache.clearAARow(n81);
        }
        this.edgeCount = j;
        this.prevUseBlkFlags = prevUseBlkFlags;
        if (Renderer.DO_STATS) {
            this.activeEdgeMaxUsed = activeEdgeMaxUsed;
        }
    }
    
    boolean endRendering() {
        if (this.edgeMinY == Integer.MAX_VALUE) {
            return false;
        }
        final int max = FloatMath.max(FloatMath.ceil_int(this.edgeMinX - 0.5f), this.boundsMinX);
        final int min = FloatMath.min(FloatMath.ceil_int(this.edgeMaxX - 0.5f), this.boundsMaxX);
        final int edgeMinY = this.edgeMinY;
        final int edgeMaxY = this.edgeMaxY;
        this.buckets_minY = edgeMinY - this.boundsMinY;
        this.buckets_maxY = edgeMaxY - this.boundsMinY;
        if (Renderer.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("edgesXY = [" + this.edgeMinX + " ... " + this.edgeMaxX + "[ [" + this.edgeMinY + " ... " + this.edgeMaxY + "[");
            MarlinUtils.logInfo("spXY    = [" + max + " ... " + min + "[ [" + edgeMinY + " ... " + edgeMaxY + "[");
        }
        if (max >= min || edgeMinY >= edgeMaxY) {
            return false;
        }
        final int n = max >> Renderer.SUBPIXEL_LG_POSITIONS_X;
        final int n2 = min + Renderer.SUBPIXEL_MASK_X >> Renderer.SUBPIXEL_LG_POSITIONS_X;
        final int n3 = edgeMinY >> Renderer.SUBPIXEL_LG_POSITIONS_Y;
        final int n4 = edgeMaxY + Renderer.SUBPIXEL_MASK_Y >> Renderer.SUBPIXEL_LG_POSITIONS_Y;
        this.cache.init(n, n3, n2, n4);
        if (Renderer.ENABLE_BLOCK_FLAGS) {
            this.enableBlkFlags = this.cache.useRLE;
            this.prevUseBlkFlags = (this.enableBlkFlags && !Renderer.ENABLE_BLOCK_FLAGS_HEURISTICS);
            if (this.enableBlkFlags) {
                final int n5 = (n2 - n >> Renderer.BLOCK_SIZE_LG) + 2;
                if (n5 > 256) {
                    this.blkFlags = this.blkFlags_ref.getArray(n5);
                }
            }
        }
        this.bbox_spminX = n << Renderer.SUBPIXEL_LG_POSITIONS_X;
        this.bbox_spmaxX = n2 << Renderer.SUBPIXEL_LG_POSITIONS_X;
        this.bbox_spminY = edgeMinY;
        this.bbox_spmaxY = edgeMaxY;
        if (Renderer.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("pXY       = [" + n + " ... " + n2 + "[ [" + n3 + " ... " + n4 + "[");
            MarlinUtils.logInfo("bbox_spXY = [" + this.bbox_spminX + " ... " + this.bbox_spmaxX + "[ [" + this.bbox_spminY + " ... " + this.bbox_spmaxY + "[");
        }
        final int n6 = n2 - n + 2;
        if (n6 > Renderer.INITIAL_AA_ARRAY) {
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.stat_array_renderer_alphaline.add(n6);
            }
            this.alphaLine = this.alphaLine_ref.getArray(n6);
        }
        this.endRendering(n3);
        return true;
    }
    
    void endRendering(final int n) {
        final int n2 = n << Renderer.SUBPIXEL_LG_POSITIONS_Y;
        final int max = FloatMath.max(this.bbox_spminY, n2);
        if (max < this.bbox_spmaxY) {
            final int min = FloatMath.min(this.bbox_spmaxY, n2 + Renderer.SUBPIXEL_TILE);
            this.cache.resetTileLine(n);
            this._endRendering(max, min);
        }
    }
    
    void copyAARow(final int[] array, final int n, final int n2, final int n3, final boolean b) {
        if (b) {
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.hist_tile_generator_encoding.add(1);
            }
            this.cache.copyAARowRLE_WithBlockFlags(this.blkFlags, array, n, n2, n3);
        }
        else {
            if (Renderer.DO_STATS) {
                this.rdrCtx.stats.hist_tile_generator_encoding.add(0);
            }
            this.cache.copyAARowNoRLE(array, n, n2, n3);
        }
    }
    
    static {
        ENABLE_BLOCK_FLAGS = MarlinProperties.isUseTileFlags();
        ENABLE_BLOCK_FLAGS_HEURISTICS = MarlinProperties.isUseTileFlagsWithHeuristics();
        SUBPIXEL_SCALE_X = (float)Renderer.SUBPIXEL_POSITIONS_X;
        SUBPIXEL_SCALE_Y = (float)Renderer.SUBPIXEL_POSITIONS_Y;
        SUBPIXEL_MASK_X = Renderer.SUBPIXEL_POSITIONS_X - 1;
        SUBPIXEL_MASK_Y = Renderer.SUBPIXEL_POSITIONS_Y - 1;
        RDR_OFFSET_X = 0.5f / Renderer.SUBPIXEL_SCALE_X;
        RDR_OFFSET_Y = 0.5f / Renderer.SUBPIXEL_SCALE_Y;
        SUBPIXEL_TILE = Renderer.TILE_H << Renderer.SUBPIXEL_LG_POSITIONS_Y;
        INITIAL_BUCKET_ARRAY = Renderer.INITIAL_PIXEL_HEIGHT * Renderer.SUBPIXEL_POSITIONS_Y;
        INITIAL_CROSSING_COUNT = Renderer.INITIAL_EDGES_COUNT >> 2;
        OFF_ERROR = 0L + OffHeapArray.SIZE_INT;
        OFF_BUMP_X = Renderer.OFF_ERROR + OffHeapArray.SIZE_INT;
        OFF_BUMP_ERR = Renderer.OFF_BUMP_X + OffHeapArray.SIZE_INT;
        OFF_NEXT = Renderer.OFF_BUMP_ERR + OffHeapArray.SIZE_INT;
        OFF_YMAX = Renderer.OFF_NEXT + OffHeapArray.SIZE_INT;
        SIZEOF_EDGE_BYTES = (int)(Renderer.OFF_YMAX + OffHeapArray.SIZE_INT);
        CUB_DEC_ERR_SUBPIX = MarlinProperties.getCubicDecD2() * (Renderer.SUBPIXEL_POSITIONS_X / 8.0f);
        CUB_INC_ERR_SUBPIX = MarlinProperties.getCubicIncD1() * (Renderer.SUBPIXEL_POSITIONS_X / 8.0f);
        SCALE_DY = Renderer.SUBPIXEL_POSITIONS_X / (float)Renderer.SUBPIXEL_POSITIONS_Y;
        CUB_DEC_BND = 8.0f * Renderer.CUB_DEC_ERR_SUBPIX;
        CUB_INC_BND = 8.0f * Renderer.CUB_INC_ERR_SUBPIX;
        QUAD_DEC_ERR_SUBPIX = MarlinProperties.getQuadDecD2() * (Renderer.SUBPIXEL_POSITIONS_X / 8.0f);
        QUAD_DEC_BND = 8.0f * Renderer.QUAD_DEC_ERR_SUBPIX;
    }
}
