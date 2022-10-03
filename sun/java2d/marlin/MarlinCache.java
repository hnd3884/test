package sun.java2d.marlin;

import sun.misc.Unsafe;

public final class MarlinCache implements MarlinConst
{
    static final boolean FORCE_RLE;
    static final boolean FORCE_NO_RLE;
    static final int RLE_MIN_WIDTH;
    static final int RLE_MAX_WIDTH = 8388608;
    static final long INITIAL_CHUNK_ARRAY;
    static final byte[] ALPHA_MAP;
    static final OffHeapArray ALPHA_MAP_UNSAFE;
    int bboxX0;
    int bboxY0;
    int bboxX1;
    int bboxY1;
    final long[] rowAAChunkIndex;
    final int[] rowAAx0;
    final int[] rowAAx1;
    final int[] rowAAEnc;
    final long[] rowAALen;
    final long[] rowAAPos;
    final OffHeapArray rowAAChunk;
    long rowAAChunkPos;
    int[] touchedTile;
    final RendererStats rdrStats;
    private final IntArrayCache.Reference touchedTile_ref;
    int tileMin;
    int tileMax;
    boolean useRLE;
    
    MarlinCache(final IRendererContext rendererContext) {
        this.rowAAChunkIndex = new long[MarlinCache.TILE_H];
        this.rowAAx0 = new int[MarlinCache.TILE_H];
        this.rowAAx1 = new int[MarlinCache.TILE_H];
        this.rowAAEnc = new int[MarlinCache.TILE_H];
        this.rowAALen = new long[MarlinCache.TILE_H];
        this.rowAAPos = new long[MarlinCache.TILE_H];
        this.useRLE = false;
        this.rdrStats = rendererContext.stats();
        this.rowAAChunk = rendererContext.newOffHeapArray(MarlinCache.INITIAL_CHUNK_ARRAY);
        this.touchedTile_ref = rendererContext.newCleanIntArrayRef(256);
        this.touchedTile = this.touchedTile_ref.initial;
        this.tileMin = Integer.MAX_VALUE;
        this.tileMax = Integer.MIN_VALUE;
    }
    
    void init(final int bboxX0, final int bboxY0, final int bboxX2, final int bboxY2) {
        this.bboxX0 = bboxX0;
        this.bboxY0 = bboxY0;
        this.bboxX1 = bboxX2;
        this.bboxY1 = bboxY2;
        final int n = bboxX2 - bboxX0;
        if (MarlinCache.FORCE_NO_RLE) {
            this.useRLE = false;
        }
        else if (MarlinCache.FORCE_RLE) {
            this.useRLE = true;
        }
        else {
            this.useRLE = (n > MarlinCache.RLE_MIN_WIDTH && n < 8388608);
        }
        final int n2 = n + MarlinCache.TILE_W >> MarlinCache.TILE_W_LG;
        if (n2 > 256) {
            if (MarlinCache.DO_STATS) {
                this.rdrStats.stat_array_marlincache_touchedTile.add(n2);
            }
            this.touchedTile = this.touchedTile_ref.getArray(n2);
        }
    }
    
    void dispose() {
        this.resetTileLine(0);
        if (MarlinCache.DO_STATS) {
            final RendererStats rdrStats = this.rdrStats;
            rdrStats.totalOffHeap += this.rowAAChunk.length;
        }
        this.touchedTile = this.touchedTile_ref.putArray(this.touchedTile, 0, 0);
        if (this.rowAAChunk.length != MarlinCache.INITIAL_CHUNK_ARRAY) {
            this.rowAAChunk.resize(MarlinCache.INITIAL_CHUNK_ARRAY);
        }
    }
    
    void resetTileLine(final int bboxY0) {
        this.bboxY0 = bboxY0;
        if (MarlinCache.DO_STATS) {
            this.rdrStats.stat_cache_rowAAChunk.add(this.rowAAChunkPos);
        }
        this.rowAAChunkPos = 0L;
        if (this.tileMin != Integer.MAX_VALUE) {
            if (MarlinCache.DO_STATS) {
                this.rdrStats.stat_cache_tiles.add(this.tileMax - this.tileMin);
            }
            if (this.tileMax == 1) {
                this.touchedTile[0] = 0;
            }
            else {
                IntArrayCache.fill(this.touchedTile, this.tileMin, this.tileMax, 0);
            }
            this.tileMin = Integer.MAX_VALUE;
            this.tileMax = Integer.MIN_VALUE;
        }
    }
    
    void clearAARow(final int n) {
        final int n2 = n - this.bboxY0;
        this.rowAAx0[n2] = 0;
        this.rowAAx1[n2] = 0;
        this.rowAAEnc[n2] = 0;
    }
    
    void copyAARowNoRLE(final int[] array, final int n, final int n2, final int n3) {
        final int min = FloatMath.min(n3, this.bboxX1);
        if (MarlinCache.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("row = [" + n2 + " ... " + min + " (" + n3 + ") [ for y=" + n);
        }
        final int n4 = n - this.bboxY0;
        this.rowAAx0[n4] = n2;
        this.rowAAx1[n4] = min;
        this.rowAAEnc[n4] = 0;
        final long rowAAChunkPos = this.rowAAChunkPos;
        this.rowAAChunkIndex[n4] = rowAAChunkPos;
        final long rowAAChunkPos2 = rowAAChunkPos + (min - n2 + 3 & 0xFFFFFFFC);
        this.rowAAChunkPos = rowAAChunkPos2;
        final OffHeapArray rowAAChunk = this.rowAAChunk;
        if (rowAAChunk.length < rowAAChunkPos2) {
            this.expandRowAAChunk(rowAAChunkPos2);
        }
        if (MarlinCache.DO_STATS) {
            this.rdrStats.stat_cache_rowAA.add(min - n2);
        }
        final int[] touchedTile = this.touchedTile;
        final int tile_W_LG = MarlinCache.TILE_W_LG;
        final int n5 = n2 - this.bboxX0;
        final int n6 = min - this.bboxX0;
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = MarlinCache.ALPHA_MAP_UNSAFE.address;
        long n7 = rowAAChunk.address + rowAAChunkPos;
        int i = n5;
        int n8 = 0;
        while (i < n6) {
            n8 += array[i];
            if (n8 == 0) {
                unsafe.putByte(n7, (byte)0);
            }
            else {
                unsafe.putByte(n7, unsafe.getByte(address + n8));
                final int[] array2 = touchedTile;
                final int n9 = i >> tile_W_LG;
                array2[n9] += n8;
            }
            ++n7;
            ++i;
        }
        final int tileMin = n5 >> tile_W_LG;
        if (tileMin < this.tileMin) {
            this.tileMin = tileMin;
        }
        final int tileMax = (n6 - 1 >> tile_W_LG) + 1;
        if (tileMax > this.tileMax) {
            this.tileMax = tileMax;
        }
        if (MarlinCache.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("clear = [" + n5 + " ... " + n6 + "[");
        }
        IntArrayCache.fill(array, n5, n3 + 1 - this.bboxX0, 0);
    }
    
    void copyAARowRLE_WithBlockFlags(final int[] array, final int[] array2, final int n, final int n2, final int n3) {
        final int bboxX0 = this.bboxX0;
        final int n4 = n - this.bboxY0;
        final int n5 = n2 - bboxX0;
        final int min = FloatMath.min(n3, this.bboxX1);
        final int n6 = min - bboxX0;
        if (MarlinCache.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("row = [" + n2 + " ... " + min + " (" + n3 + ") [ for y=" + n);
        }
        final long startRLERow = this.startRLERow(n4, n2, min);
        final long n7 = startRLERow + (n6 - n5 << 2);
        final OffHeapArray rowAAChunk = this.rowAAChunk;
        if (rowAAChunk.length < n7) {
            this.expandRowAAChunk(n7);
        }
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = MarlinCache.ALPHA_MAP_UNSAFE.address;
        long n8 = rowAAChunk.address + startRLERow;
        final int[] touchedTile = this.touchedTile;
        final int tile_W_LG = MarlinCache.TILE_W_LG;
        final int block_SIZE_LG = MarlinCache.BLOCK_SIZE_LG;
        final int n9 = n5 >> block_SIZE_LG;
        final int n10 = (n6 >> block_SIZE_LG) + 1;
        array[n10] = 0;
        int n11 = 0;
        int n12 = n5;
        int n13 = Integer.MAX_VALUE;
        int n14 = 0;
        for (int i = n9; i <= n10; ++i) {
            if (array[i] != 0) {
                array[i] = 0;
                if (n13 == Integer.MAX_VALUE) {
                    n13 = i;
                }
            }
            else if (n13 != Integer.MAX_VALUE) {
                final int max = FloatMath.max(n13 << block_SIZE_LG, n5);
                n13 = Integer.MAX_VALUE;
                for (int min2 = FloatMath.min((i << block_SIZE_LG) + 1, n6), j = max; j < min2; ++j) {
                    final int n15;
                    if ((n15 = array2[j]) != 0) {
                        array2[j] = 0;
                        if (j != n12) {
                            final int n16 = j - n12;
                            if (n11 == 0) {
                                unsafe.putInt(n8, bboxX0 + j << 8);
                            }
                            else {
                                unsafe.putInt(n8, bboxX0 + j << 8 | (unsafe.getByte(address + n11) & 0xFF));
                                if (n16 == 1) {
                                    final int[] array3 = touchedTile;
                                    final int n17 = n12 >> tile_W_LG;
                                    array3[n17] += n11;
                                }
                                else {
                                    this.touchTile(n12, n11, j, n16, touchedTile);
                                }
                            }
                            n8 += 4L;
                            if (MarlinCache.DO_STATS) {
                                this.rdrStats.hist_tile_generator_encoding_runLen.add(n16);
                            }
                            n12 = j;
                        }
                        n11 += n15;
                    }
                }
            }
            else if (MarlinCache.DO_STATS) {
                ++n14;
            }
        }
        final int n18 = n6 - n12;
        if (n11 == 0) {
            unsafe.putInt(n8, bboxX0 + n6 << 8);
        }
        else {
            unsafe.putInt(n8, bboxX0 + n6 << 8 | (unsafe.getByte(address + n11) & 0xFF));
            if (n18 == 1) {
                final int[] array4 = touchedTile;
                final int n19 = n12 >> tile_W_LG;
                array4[n19] += n11;
            }
            else {
                this.touchTile(n12, n11, n6, n18, touchedTile);
            }
        }
        final long n20 = n8 + 4L;
        if (MarlinCache.DO_STATS) {
            this.rdrStats.hist_tile_generator_encoding_runLen.add(n18);
        }
        final long rowAAChunkPos = n20 - rowAAChunk.address;
        this.rowAALen[n4] = rowAAChunkPos - startRLERow;
        this.rowAAChunkPos = rowAAChunkPos;
        if (MarlinCache.DO_STATS) {
            this.rdrStats.stat_cache_rowAA.add(this.rowAALen[n4]);
            this.rdrStats.hist_tile_generator_encoding_ratio.add(100 * n14 / (n10 - n9));
        }
        final int tileMin = n5 >> tile_W_LG;
        if (tileMin < this.tileMin) {
            this.tileMin = tileMin;
        }
        final int tileMax = (n6 - 1 >> tile_W_LG) + 1;
        if (tileMax > this.tileMax) {
            this.tileMax = tileMax;
        }
        array2[n6] = 0;
        if (MarlinCache.DO_CHECKS) {
            IntArrayCache.check(array, n9, n10, 0);
            IntArrayCache.check(array2, n5, n3 + 1 - this.bboxX0, 0);
        }
    }
    
    long startRLERow(final int n, final int n2, final int n3) {
        this.rowAAx0[n] = n2;
        this.rowAAx1[n] = n3;
        this.rowAAEnc[n] = 1;
        this.rowAAPos[n] = 0L;
        return this.rowAAChunkIndex[n] = this.rowAAChunkPos;
    }
    
    private void expandRowAAChunk(final long n) {
        if (MarlinCache.DO_STATS) {
            this.rdrStats.stat_array_marlincache_rowAAChunk.add(n);
        }
        this.rowAAChunk.resize(ArrayCacheConst.getNewLargeSize(this.rowAAChunk.length, n));
    }
    
    private void touchTile(final int n, final int n2, final int n3, final int n4, final int[] array) {
        final int tile_W_LG = MarlinCache.TILE_W_LG;
        int i = n >> tile_W_LG;
        if (i == n3 >> tile_W_LG) {
            final int n5 = i;
            array[n5] += n2 * n4;
            return;
        }
        final int n6 = n3 - 1 >> tile_W_LG;
        if (i <= n6) {
            final int n7 = i + 1 << tile_W_LG;
            final int n8 = i++;
            array[n8] += n2 * (n7 - n);
        }
        if (i < n6) {
            final int n9 = n2 << tile_W_LG;
            while (i < n6) {
                final int n10 = i;
                array[n10] += n9;
                ++i;
            }
        }
        if (i == n6) {
            final int n11 = i << tile_W_LG;
            final int n12 = i + 1 << tile_W_LG;
            final int n13 = (n12 <= n3) ? n12 : n3;
            final int n14 = i;
            array[n14] += n2 * (n13 - n11);
        }
    }
    
    int alphaSumInTile(final int n) {
        return this.touchedTile[n - this.bboxX0 >> MarlinCache.TILE_W_LG];
    }
    
    @Override
    public String toString() {
        return "bbox = [" + this.bboxX0 + ", " + this.bboxY0 + " => " + this.bboxX1 + ", " + this.bboxY1 + "]\n";
    }
    
    private static byte[] buildAlphaMap(final int n) {
        final byte[] array = new byte[n << 1];
        final int n2 = n >> 2;
        for (int i = 0; i <= n; ++i) {
            array[i] = (byte)((i * 255 + n2) / n);
        }
        return array;
    }
    
    static {
        FORCE_RLE = MarlinProperties.isForceRLE();
        FORCE_NO_RLE = MarlinProperties.isForceNoRLE();
        RLE_MIN_WIDTH = Math.max(MarlinCache.BLOCK_SIZE, MarlinProperties.getRLEMinWidth());
        INITIAL_CHUNK_ARRAY = MarlinCache.TILE_H * MarlinCache.INITIAL_PIXEL_WIDTH >> 2;
        final byte[] buildAlphaMap = buildAlphaMap(MarlinCache.MAX_AA_ALPHA);
        ALPHA_MAP_UNSAFE = new OffHeapArray(buildAlphaMap, buildAlphaMap.length);
        ALPHA_MAP = buildAlphaMap;
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = MarlinCache.ALPHA_MAP_UNSAFE.address;
        for (int i = 0; i < buildAlphaMap.length; ++i) {
            unsafe.putByte(address + i, buildAlphaMap[i]);
        }
    }
}
