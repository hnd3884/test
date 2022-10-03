package sun.java2d.marlin;

import java.util.Arrays;
import sun.misc.Unsafe;
import sun.java2d.pipe.AATileGenerator;

final class MarlinTileGenerator implements AATileGenerator, MarlinConst
{
    private static final boolean DISABLE_BLEND = false;
    private static final int MAX_TILE_ALPHA_SUM;
    private static final int TH_AA_ALPHA_FILL_EMPTY;
    private static final int TH_AA_ALPHA_FILL_FULL;
    private static final int FILL_TILE_W;
    private final Renderer rdrF;
    private final DRenderer rdrD;
    private final MarlinCache cache;
    private int x;
    private int y;
    final RendererStats rdrStats;
    
    MarlinTileGenerator(final RendererStats rdrStats, final MarlinRenderer marlinRenderer, final MarlinCache cache) {
        this.rdrStats = rdrStats;
        if (marlinRenderer instanceof Renderer) {
            this.rdrF = (Renderer)marlinRenderer;
            this.rdrD = null;
        }
        else {
            this.rdrF = null;
            this.rdrD = (DRenderer)marlinRenderer;
        }
        this.cache = cache;
    }
    
    MarlinTileGenerator init() {
        this.x = this.cache.bboxX0;
        this.y = this.cache.bboxY0;
        return this;
    }
    
    @Override
    public void dispose() {
        this.cache.dispose();
        if (this.rdrF != null) {
            this.rdrF.dispose();
        }
        else if (this.rdrD != null) {
            this.rdrD.dispose();
        }
    }
    
    void getBbox(final int[] array) {
        array[0] = this.cache.bboxX0;
        array[1] = this.cache.bboxY0;
        array[2] = this.cache.bboxX1;
        array[3] = this.cache.bboxY1;
    }
    
    @Override
    public int getTileWidth() {
        return MarlinTileGenerator.TILE_W;
    }
    
    @Override
    public int getTileHeight() {
        return MarlinTileGenerator.TILE_H;
    }
    
    @Override
    public int getTypicalAlpha() {
        final int alphaSumInTile = this.cache.alphaSumInTile(this.x);
        final int n = (alphaSumInTile == 0) ? 0 : ((alphaSumInTile == MarlinTileGenerator.MAX_TILE_ALPHA_SUM) ? 255 : 128);
        if (MarlinTileGenerator.DO_STATS) {
            this.rdrStats.hist_tile_generator_alpha.add(n);
        }
        return n;
    }
    
    @Override
    public void nextTile() {
        final int x = this.x + MarlinTileGenerator.TILE_W;
        this.x = x;
        if (x >= this.cache.bboxX1) {
            this.x = this.cache.bboxX0;
            this.y += MarlinTileGenerator.TILE_H;
            if (this.y < this.cache.bboxY1) {
                if (this.rdrF != null) {
                    this.rdrF.endRendering(this.y);
                }
                else if (this.rdrD != null) {
                    this.rdrD.endRendering(this.y);
                }
            }
        }
    }
    
    @Override
    public void getAlpha(final byte[] array, final int n, final int n2) {
        if (this.cache.useRLE) {
            this.getAlphaRLE(array, n, n2);
        }
        else {
            this.getAlphaNoRLE(array, n, n2);
        }
    }
    
    private void getAlphaNoRLE(final byte[] array, final int n, final int n2) {
        final MarlinCache cache = this.cache;
        final long[] rowAAChunkIndex = cache.rowAAChunkIndex;
        final int[] rowAAx0 = cache.rowAAx0;
        final int[] rowAAx2 = cache.rowAAx1;
        final int x = this.x;
        final int min = FloatMath.min(x + MarlinTileGenerator.TILE_W, cache.bboxX1);
        final int n3 = FloatMath.min(this.y + MarlinTileGenerator.TILE_H, cache.bboxY1) - this.y;
        if (MarlinTileGenerator.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("getAlpha = [" + x + " ... " + min + "[ [" + 0 + " ... " + n3 + "[");
        }
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = cache.rowAAChunk.address;
        final int n4 = n2 - (min - x);
        int n5 = n;
        for (int i = 0; i < n3; ++i) {
            int j = x;
            final int n6 = rowAAx2[i];
            if (n6 > x) {
                final int n7 = rowAAx0[i];
                if (n7 < min) {
                    j = n7;
                    if (j <= x) {
                        j = x;
                    }
                    else {
                        for (int k = x; k < j; ++k) {
                            array[n5++] = 0;
                        }
                    }
                    long n8 = address + rowAAChunkIndex[i] + (j - n7);
                    while (j < ((n6 <= min) ? n6 : min)) {
                        array[n5++] = unsafe.getByte(n8);
                        ++n8;
                        ++j;
                    }
                }
            }
            while (j < min) {
                array[n5++] = 0;
                ++j;
            }
            if (MarlinTileGenerator.DO_TRACE) {
                for (int l = n5 - (min - x); l < n5; ++l) {
                    System.out.print(hex(array[l], 2));
                }
                System.out.println();
            }
            n5 += n4;
        }
        this.nextTile();
    }
    
    private void getAlphaRLE(final byte[] array, final int n, final int n2) {
        final MarlinCache cache = this.cache;
        final long[] rowAAChunkIndex = cache.rowAAChunkIndex;
        final int[] rowAAx0 = cache.rowAAx0;
        final int[] rowAAx2 = cache.rowAAx1;
        final int[] rowAAEnc = cache.rowAAEnc;
        final long[] rowAALen = cache.rowAALen;
        final long[] rowAAPos = cache.rowAAPos;
        final int x = this.x;
        final int min = FloatMath.min(x + MarlinTileGenerator.TILE_W, cache.bboxX1);
        final int n3 = min - x;
        final int n4 = FloatMath.min(this.y + MarlinTileGenerator.TILE_H, cache.bboxY1) - this.y;
        if (MarlinTileGenerator.DO_LOG_BOUNDS) {
            MarlinUtils.logInfo("getAlpha = [" + x + " ... " + min + "[ [" + 0 + " ... " + n4 + "[");
        }
        final int n5;
        int n6;
        byte b;
        if (n3 >= MarlinTileGenerator.FILL_TILE_W && (n5 = n3 * n4) > 64) {
            final int alphaSumInTile = this.cache.alphaSumInTile(x);
            if (alphaSumInTile < n5 * MarlinTileGenerator.TH_AA_ALPHA_FILL_EMPTY) {
                n6 = 1;
                b = 0;
            }
            else if (alphaSumInTile > n5 * MarlinTileGenerator.TH_AA_ALPHA_FILL_FULL) {
                n6 = 2;
                b = -1;
            }
            else {
                n6 = 0;
                b = 0;
            }
        }
        else {
            n6 = 0;
            b = 0;
        }
        final Unsafe unsafe = OffHeapArray.UNSAFE;
        final long address = cache.rowAAChunk.address;
        final int n7 = n2 - n3;
        int n8 = n;
        switch (n6) {
            case 1: {
                Arrays.fill(array, n, n + n4 * n2, b);
                for (int i = 0; i < n4; ++i) {
                    int j = x;
                    if (rowAAEnc[i] == 0) {
                        final int n9 = rowAAx2[i];
                        if (n9 > x) {
                            final int n10 = rowAAx0[i];
                            if (n10 < min) {
                                j = n10;
                                if (j <= x) {
                                    j = x;
                                }
                                else {
                                    n8 += j - x;
                                }
                                long n11 = address + rowAAChunkIndex[i] + (j - n10);
                                while (j < ((n9 <= min) ? n9 : min)) {
                                    array[n8++] = unsafe.getByte(n11);
                                    ++n11;
                                    ++j;
                                }
                            }
                        }
                    }
                    else if (rowAAx2[i] > x) {
                        j = rowAAx0[i];
                        if (j > min) {
                            j = min;
                        }
                        if (j > x) {
                            n8 += j - x;
                        }
                        final long n12 = address + rowAAChunkIndex[i];
                        final long n13 = n12 + rowAALen[i];
                        long n14 = n12 + rowAAPos[i];
                        long n15 = 0L;
                        while (j < min && n14 < n13) {
                            n15 = n14;
                            final int int1 = unsafe.getInt(n14);
                            final int n16 = int1 >> 8;
                            n14 += 4L;
                            int n17 = j;
                            if (n17 < x) {
                                n17 = x;
                            }
                            int n18;
                            j = (n18 = n16);
                            if (n18 > min) {
                                n18 = min;
                                j = min;
                            }
                            int n19 = n18 - n17;
                            if (n19 > 0) {
                                final int n20 = int1 & 0xFF;
                                if (n20 == 0) {
                                    n8 += n19;
                                }
                                else {
                                    final byte b2 = (byte)n20;
                                    do {
                                        array[n8++] = b2;
                                    } while (--n19 > 0);
                                }
                            }
                        }
                        if (n15 != 0L) {
                            rowAAx0[i] = j;
                            rowAAPos[i] = n15 - n12;
                        }
                    }
                    if (j < min) {
                        n8 += min - j;
                    }
                    if (MarlinTileGenerator.DO_TRACE) {
                        for (int k = n8 - (min - x); k < n8; ++k) {
                            System.out.print(hex(array[k], 2));
                        }
                        System.out.println();
                    }
                    n8 += n7;
                }
                break;
            }
            default: {
                for (int l = 0; l < n4; ++l) {
                    int n21 = x;
                    if (rowAAEnc[l] == 0) {
                        final int n22 = rowAAx2[l];
                        if (n22 > x) {
                            final int n23 = rowAAx0[l];
                            if (n23 < min) {
                                n21 = n23;
                                if (n21 <= x) {
                                    n21 = x;
                                }
                                else {
                                    for (int n24 = x; n24 < n21; ++n24) {
                                        array[n8++] = 0;
                                    }
                                }
                                long n25 = address + rowAAChunkIndex[l] + (n21 - n23);
                                while (n21 < ((n22 <= min) ? n22 : min)) {
                                    array[n8++] = unsafe.getByte(n25);
                                    ++n25;
                                    ++n21;
                                }
                            }
                        }
                    }
                    else if (rowAAx2[l] > x) {
                        n21 = rowAAx0[l];
                        if (n21 > min) {
                            n21 = min;
                        }
                        for (int n26 = x; n26 < n21; ++n26) {
                            array[n8++] = 0;
                        }
                        final long n27 = address + rowAAChunkIndex[l];
                        final long n28 = n27 + rowAALen[l];
                        long n29 = n27 + rowAAPos[l];
                        long n30 = 0L;
                        while (n21 < min && n29 < n28) {
                            n30 = n29;
                            final int int2 = unsafe.getInt(n29);
                            final int n31 = int2 >> 8;
                            n29 += 4L;
                            int n32 = n21;
                            if (n32 < x) {
                                n32 = x;
                            }
                            int n33;
                            n21 = (n33 = n31);
                            if (n33 > min) {
                                n33 = min;
                                n21 = min;
                            }
                            int n34 = n33 - n32;
                            if (n34 > 0) {
                                final byte b3 = (byte)(int2 & 0xFF);
                                do {
                                    array[n8++] = b3;
                                } while (--n34 > 0);
                            }
                        }
                        if (n30 != 0L) {
                            rowAAx0[l] = n21;
                            rowAAPos[l] = n30 - n27;
                        }
                    }
                    while (n21 < min) {
                        array[n8++] = 0;
                        ++n21;
                    }
                    if (MarlinTileGenerator.DO_TRACE) {
                        for (int n35 = n8 - (min - x); n35 < n8; ++n35) {
                            System.out.print(hex(array[n35], 2));
                        }
                        System.out.println();
                    }
                    n8 += n7;
                }
                break;
            }
            case 2: {
                Arrays.fill(array, n, n + n4 * n2, b);
                for (int n36 = 0; n36 < n4; ++n36) {
                    int n37 = x;
                    if (rowAAEnc[n36] == 0) {
                        final int n38 = rowAAx2[n36];
                        if (n38 > x) {
                            final int n39 = rowAAx0[n36];
                            if (n39 < min) {
                                n37 = n39;
                                if (n37 <= x) {
                                    n37 = x;
                                }
                                else {
                                    for (int n40 = x; n40 < n37; ++n40) {
                                        array[n8++] = 0;
                                    }
                                }
                                long n41 = address + rowAAChunkIndex[n36] + (n37 - n39);
                                while (n37 < ((n38 <= min) ? n38 : min)) {
                                    array[n8++] = unsafe.getByte(n41);
                                    ++n41;
                                    ++n37;
                                }
                            }
                        }
                    }
                    else if (rowAAx2[n36] > x) {
                        n37 = rowAAx0[n36];
                        if (n37 > min) {
                            n37 = min;
                        }
                        for (int n42 = x; n42 < n37; ++n42) {
                            array[n8++] = 0;
                        }
                        final long n43 = address + rowAAChunkIndex[n36];
                        final long n44 = n43 + rowAALen[n36];
                        long n45 = n43 + rowAAPos[n36];
                        long n46 = 0L;
                        while (n37 < min && n45 < n44) {
                            n46 = n45;
                            final int int3 = unsafe.getInt(n45);
                            final int n47 = int3 >> 8;
                            n45 += 4L;
                            int n48 = n37;
                            if (n48 < x) {
                                n48 = x;
                            }
                            int n49;
                            n37 = (n49 = n47);
                            if (n49 > min) {
                                n49 = min;
                                n37 = min;
                            }
                            int n50 = n49 - n48;
                            if (n50 > 0) {
                                final int n51 = int3 & 0xFF;
                                if (n51 == 255) {
                                    n8 += n50;
                                }
                                else {
                                    final byte b4 = (byte)n51;
                                    do {
                                        array[n8++] = b4;
                                    } while (--n50 > 0);
                                }
                            }
                        }
                        if (n46 != 0L) {
                            rowAAx0[n36] = n37;
                            rowAAPos[n36] = n46 - n43;
                        }
                    }
                    while (n37 < min) {
                        array[n8++] = 0;
                        ++n37;
                    }
                    if (MarlinTileGenerator.DO_TRACE) {
                        for (int n52 = n8 - (min - x); n52 < n8; ++n52) {
                            System.out.print(hex(array[n52], 2));
                        }
                        System.out.println();
                    }
                    n8 += n7;
                }
                break;
            }
        }
        this.nextTile();
    }
    
    static String hex(final int n, final int n2) {
        String s;
        for (s = Integer.toHexString(n); s.length() < n2; s = "0" + s) {}
        return s.substring(0, n2);
    }
    
    static {
        MAX_TILE_ALPHA_SUM = MarlinTileGenerator.TILE_W * MarlinTileGenerator.TILE_H * MarlinTileGenerator.MAX_AA_ALPHA;
        TH_AA_ALPHA_FILL_EMPTY = (MarlinTileGenerator.MAX_AA_ALPHA + 1) / 3;
        TH_AA_ALPHA_FILL_FULL = (MarlinTileGenerator.MAX_AA_ALPHA + 1) * 2 / 3;
        FILL_TILE_W = MarlinTileGenerator.TILE_W >> 1;
        if (MarlinTileGenerator.MAX_TILE_ALPHA_SUM <= 0) {
            throw new IllegalStateException("Invalid MAX_TILE_ALPHA_SUM: " + MarlinTileGenerator.MAX_TILE_ALPHA_SUM);
        }
        if (MarlinTileGenerator.DO_TRACE) {
            MarlinUtils.logInfo("MAX_AA_ALPHA           : " + MarlinTileGenerator.MAX_AA_ALPHA);
            MarlinUtils.logInfo("TH_AA_ALPHA_FILL_EMPTY : " + MarlinTileGenerator.TH_AA_ALPHA_FILL_EMPTY);
            MarlinUtils.logInfo("TH_AA_ALPHA_FILL_FULL  : " + MarlinTileGenerator.TH_AA_ALPHA_FILL_FULL);
            MarlinUtils.logInfo("FILL_TILE_W            : " + MarlinTileGenerator.FILL_TILE_W);
        }
    }
}
