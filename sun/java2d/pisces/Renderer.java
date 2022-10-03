package sun.java2d.pisces;

import java.util.Arrays;
import sun.awt.geom.PathConsumer2D;

final class Renderer implements PathConsumer2D
{
    private static final int YMAX = 0;
    private static final int CURX = 1;
    private static final int OR = 2;
    private static final int SLOPE = 3;
    private static final int NEXT = 4;
    private float edgeMinY;
    private float edgeMaxY;
    private float edgeMinX;
    private float edgeMaxX;
    private static final int SIZEOF_EDGE = 5;
    private static final int NULL = -5;
    private float[] edges;
    private static final int INIT_NUM_EDGES = 8;
    private int[] edgeBuckets;
    private int[] edgeBucketCounts;
    private int numEdges;
    private static final float DEC_BND = 20.0f;
    private static final float INC_BND = 8.0f;
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;
    private final int SUBPIXEL_LG_POSITIONS_X;
    private final int SUBPIXEL_LG_POSITIONS_Y;
    private final int SUBPIXEL_POSITIONS_X;
    private final int SUBPIXEL_POSITIONS_Y;
    private final int SUBPIXEL_MASK_X;
    private final int SUBPIXEL_MASK_Y;
    final int MAX_AA_ALPHA;
    PiscesCache cache;
    private final int boundsMinX;
    private final int boundsMinY;
    private final int boundsMaxX;
    private final int boundsMaxY;
    private final int windingRule;
    private float x0;
    private float y0;
    private float pix_sx0;
    private float pix_sy0;
    private Curve c;
    
    private void addEdgeToBucket(final int n, final int n2) {
        this.edges[n + 4] = (float)this.edgeBuckets[n2];
        this.edgeBuckets[n2] = n;
        final int[] edgeBucketCounts = this.edgeBucketCounts;
        edgeBucketCounts[n2] += 2;
    }
    
    private void quadBreakIntoLinesAndAdd(float n, float n2, final Curve curve, final float n3, final float n4) {
        int n5 = 16;
        final int n6 = n5 * n5;
        for (float max = Math.max(curve.dbx / n6, curve.dby / n6); max > 32.0f; max /= 4.0f, n5 <<= 1) {}
        final int n7 = n5 * n5;
        final float n8 = curve.dbx / n7;
        final float n9 = curve.dby / n7;
        float n10 = curve.bx / n7 + curve.cx / n5;
        float n11 = curve.by / n7 + curve.cy / n5;
        while (n5-- > 1) {
            final float n12 = n + n10;
            n10 += n8;
            final float n13 = n2 + n11;
            n11 += n9;
            this.addLine(n, n2, n12, n13);
            n = n12;
            n2 = n13;
        }
        this.addLine(n, n2, n3, n4);
    }
    
    private void curveBreakIntoLinesAndAdd(float n, float n2, final Curve curve, final float n3, final float n4) {
        int i = 8;
        float n5 = 2.0f * curve.dax / 512.0f;
        float n6 = 2.0f * curve.day / 512.0f;
        float n7 = n5 + curve.dbx / 64.0f;
        float n8 = n6 + curve.dby / 64.0f;
        float n9 = curve.ax / 512.0f + curve.bx / 64.0f + curve.cx / 8.0f;
        float n10 = curve.ay / 512.0f + curve.by / 64.0f + curve.cy / 8.0f;
        float n11 = n;
        float n12 = n2;
        while (i > 0) {
            while (Math.abs(n7) > 20.0f || Math.abs(n8) > 20.0f) {
                n5 /= 8.0f;
                n6 /= 8.0f;
                n7 = n7 / 4.0f - n5;
                n8 = n8 / 4.0f - n6;
                n9 = (n9 - n7) / 2.0f;
                n10 = (n10 - n8) / 2.0f;
                i <<= 1;
            }
            while (i % 2 == 0 && Math.abs(n9) <= 8.0f && Math.abs(n10) <= 8.0f) {
                n9 = 2.0f * n9 + n7;
                n10 = 2.0f * n10 + n8;
                n7 = 4.0f * (n7 + n5);
                n8 = 4.0f * (n8 + n6);
                n5 *= 8.0f;
                n6 *= 8.0f;
                i >>= 1;
            }
            if (--i > 0) {
                n11 += n9;
                n9 += n7;
                n7 += n5;
                n12 += n10;
                n10 += n8;
                n8 += n6;
            }
            else {
                n11 = n3;
                n12 = n4;
            }
            this.addLine(n, n2, n11, n12);
            n = n11;
            n2 = n12;
        }
    }
    
    private void addLine(float n, float edgeMinY, float n2, float edgeMaxY) {
        float n3 = 1.0f;
        if (edgeMaxY < edgeMinY) {
            final float n4 = edgeMaxY;
            edgeMaxY = edgeMinY;
            edgeMinY = n4;
            final float n5 = n2;
            n2 = n;
            n = n5;
            n3 = 0.0f;
        }
        final int max = Math.max((int)Math.ceil(edgeMinY), this.boundsMinY);
        final int min = Math.min((int)Math.ceil(edgeMaxY), this.boundsMaxY);
        if (max >= min) {
            return;
        }
        if (edgeMinY < this.edgeMinY) {
            this.edgeMinY = edgeMinY;
        }
        if (edgeMaxY > this.edgeMaxY) {
            this.edgeMaxY = edgeMaxY;
        }
        final float n6 = (n2 - n) / (edgeMaxY - edgeMinY);
        if (n6 > 0.0f) {
            if (n < this.edgeMinX) {
                this.edgeMinX = n;
            }
            if (n2 > this.edgeMaxX) {
                this.edgeMaxX = n2;
            }
        }
        else {
            if (n2 < this.edgeMinX) {
                this.edgeMinX = n2;
            }
            if (n > this.edgeMaxX) {
                this.edgeMaxX = n;
            }
        }
        final int n7 = this.numEdges * 5;
        this.edges = Helpers.widenArray(this.edges, n7, 5);
        ++this.numEdges;
        this.edges[n7 + 2] = n3;
        this.edges[n7 + 1] = n + (max - edgeMinY) * n6;
        this.edges[n7 + 3] = n6;
        this.edges[n7 + 0] = (float)min;
        this.addEdgeToBucket(n7, max - this.boundsMinY);
        final int[] edgeBucketCounts = this.edgeBucketCounts;
        final int n8 = min - this.boundsMinY;
        edgeBucketCounts[n8] |= 0x1;
    }
    
    public Renderer(final int subpixel_LG_POSITIONS_X, final int subpixel_LG_POSITIONS_Y, final int n, final int n2, final int n3, final int n4, final int windingRule) {
        this.edgeMinY = Float.POSITIVE_INFINITY;
        this.edgeMaxY = Float.NEGATIVE_INFINITY;
        this.edgeMinX = Float.POSITIVE_INFINITY;
        this.edgeMaxX = Float.NEGATIVE_INFINITY;
        this.edges = null;
        this.edgeBuckets = null;
        this.edgeBucketCounts = null;
        this.c = new Curve();
        this.SUBPIXEL_LG_POSITIONS_X = subpixel_LG_POSITIONS_X;
        this.SUBPIXEL_LG_POSITIONS_Y = subpixel_LG_POSITIONS_Y;
        this.SUBPIXEL_MASK_X = (1 << this.SUBPIXEL_LG_POSITIONS_X) - 1;
        this.SUBPIXEL_MASK_Y = (1 << this.SUBPIXEL_LG_POSITIONS_Y) - 1;
        this.SUBPIXEL_POSITIONS_X = 1 << this.SUBPIXEL_LG_POSITIONS_X;
        this.SUBPIXEL_POSITIONS_Y = 1 << this.SUBPIXEL_LG_POSITIONS_Y;
        this.MAX_AA_ALPHA = this.SUBPIXEL_POSITIONS_X * this.SUBPIXEL_POSITIONS_Y;
        this.windingRule = windingRule;
        this.boundsMinX = n * this.SUBPIXEL_POSITIONS_X;
        this.boundsMinY = n2 * this.SUBPIXEL_POSITIONS_Y;
        this.boundsMaxX = (n + n3) * this.SUBPIXEL_POSITIONS_X;
        this.boundsMaxY = (n2 + n4) * this.SUBPIXEL_POSITIONS_Y;
        this.edges = new float[40];
        this.numEdges = 0;
        Arrays.fill(this.edgeBuckets = new int[this.boundsMaxY - this.boundsMinY], -5);
        this.edgeBucketCounts = new int[this.edgeBuckets.length + 1];
    }
    
    private float tosubpixx(final float n) {
        return n * this.SUBPIXEL_POSITIONS_X;
    }
    
    private float tosubpixy(final float n) {
        return n * this.SUBPIXEL_POSITIONS_Y;
    }
    
    @Override
    public void moveTo(final float pix_sx0, final float pix_sy0) {
        this.closePath();
        this.pix_sx0 = pix_sx0;
        this.pix_sy0 = pix_sy0;
        this.y0 = this.tosubpixy(pix_sy0);
        this.x0 = this.tosubpixx(pix_sx0);
    }
    
    @Override
    public void lineTo(final float n, final float n2) {
        final float tosubpixx = this.tosubpixx(n);
        final float tosubpixy = this.tosubpixy(n2);
        this.addLine(this.x0, this.y0, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void curveTo(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float tosubpixx = this.tosubpixx(n5);
        final float tosubpixy = this.tosubpixy(n6);
        this.c.set(this.x0, this.y0, this.tosubpixx(n), this.tosubpixy(n2), this.tosubpixx(n3), this.tosubpixy(n4), tosubpixx, tosubpixy);
        this.curveBreakIntoLinesAndAdd(this.x0, this.y0, this.c, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void quadTo(final float n, final float n2, final float n3, final float n4) {
        final float tosubpixx = this.tosubpixx(n3);
        final float tosubpixy = this.tosubpixy(n4);
        this.c.set(this.x0, this.y0, this.tosubpixx(n), this.tosubpixy(n2), tosubpixx, tosubpixy);
        this.quadBreakIntoLinesAndAdd(this.x0, this.y0, this.c, tosubpixx, tosubpixy);
        this.x0 = tosubpixx;
        this.y0 = tosubpixy;
    }
    
    @Override
    public void closePath() {
        this.lineTo(this.pix_sx0, this.pix_sy0);
    }
    
    @Override
    public void pathDone() {
        this.closePath();
    }
    
    @Override
    public long getNativeConsumer() {
        throw new InternalError("Renderer does not use a native consumer.");
    }
    
    private void _endRendering(final int n, final int n2, final int n3, final int n4) {
        final int n5 = (this.windingRule == 0) ? 1 : -1;
        final int[] array = new int[n2 - n + 2];
        final int n6 = n << this.SUBPIXEL_LG_POSITIONS_X;
        final int n7 = n2 << this.SUBPIXEL_LG_POSITIONS_X;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int n8 = this.boundsMinY;
        final ScanlineIterator scanlineIterator = new ScanlineIterator(n3, n4);
        while (scanlineIterator.hasNext()) {
            final int access$600 = scanlineIterator.next();
            final int[] access$601 = scanlineIterator.crossings;
            n8 = scanlineIterator.curY();
            if (access$600 > 0) {
                final int n9 = access$601[0] >> 1;
                final int n10 = access$601[access$600 - 1] >> 1;
                final int max2 = Math.max(n9, n6);
                final int min2 = Math.min(n10, n7);
                min = Math.min(min, max2 >> this.SUBPIXEL_LG_POSITIONS_X);
                max = Math.max(max, min2 >> this.SUBPIXEL_LG_POSITIONS_X);
            }
            int n11 = 0;
            int n12 = n6;
            for (final int n13 : access$601) {
                final int n14 = n13 >> 1;
                final int n15 = ((n13 & 0x1) << 1) - 1;
                if ((n11 & n5) != 0x0) {
                    final int max3 = Math.max(n12, n6);
                    final int min3 = Math.min(n14, n7);
                    if (max3 < min3) {
                        final int n16 = max3 - n6;
                        final int n17 = min3 - n6;
                        final int n18 = n16 >> this.SUBPIXEL_LG_POSITIONS_X;
                        if (n18 == n17 - 1 >> this.SUBPIXEL_LG_POSITIONS_X) {
                            final int[] array2 = array;
                            final int n19 = n18;
                            array2[n19] += n17 - n16;
                            final int[] array3 = array;
                            final int n20 = n18 + 1;
                            array3[n20] -= n17 - n16;
                        }
                        else {
                            final int n21 = n17 >> this.SUBPIXEL_LG_POSITIONS_X;
                            final int[] array4 = array;
                            final int n22 = n18;
                            array4[n22] += this.SUBPIXEL_POSITIONS_X - (n16 & this.SUBPIXEL_MASK_X);
                            final int[] array5 = array;
                            final int n23 = n18 + 1;
                            array5[n23] += (n16 & this.SUBPIXEL_MASK_X);
                            final int[] array6 = array;
                            final int n24 = n21;
                            array6[n24] -= this.SUBPIXEL_POSITIONS_X - (n17 & this.SUBPIXEL_MASK_X);
                            final int[] array7 = array;
                            final int n25 = n21 + 1;
                            array7[n25] -= (n17 & this.SUBPIXEL_MASK_X);
                        }
                    }
                }
                n11 += n15;
                n12 = n14;
            }
            if ((n8 & this.SUBPIXEL_MASK_Y) == this.SUBPIXEL_MASK_Y) {
                this.emitRow(array, n8 >> this.SUBPIXEL_LG_POSITIONS_Y, min, max);
                min = Integer.MAX_VALUE;
                max = Integer.MIN_VALUE;
            }
        }
        if (max >= min) {
            this.emitRow(array, n8 >> this.SUBPIXEL_LG_POSITIONS_Y, min, max);
        }
    }
    
    public void endRendering() {
        final int max = Math.max((int)Math.ceil(this.edgeMinX), this.boundsMinX);
        final int min = Math.min((int)Math.ceil(this.edgeMaxX), this.boundsMaxX);
        final int max2 = Math.max((int)Math.ceil(this.edgeMinY), this.boundsMinY);
        final int min2 = Math.min((int)Math.ceil(this.edgeMaxY), this.boundsMaxY);
        final int n = max >> this.SUBPIXEL_LG_POSITIONS_X;
        final int n2 = min + this.SUBPIXEL_MASK_X >> this.SUBPIXEL_LG_POSITIONS_X;
        final int n3 = max2 >> this.SUBPIXEL_LG_POSITIONS_Y;
        final int n4 = min2 + this.SUBPIXEL_MASK_Y >> this.SUBPIXEL_LG_POSITIONS_Y;
        if (n > n2 || n3 > n4) {
            this.cache = new PiscesCache(this.boundsMinX >> this.SUBPIXEL_LG_POSITIONS_X, this.boundsMinY >> this.SUBPIXEL_LG_POSITIONS_Y, this.boundsMaxX >> this.SUBPIXEL_LG_POSITIONS_X, this.boundsMaxY >> this.SUBPIXEL_LG_POSITIONS_Y);
            return;
        }
        this.cache = new PiscesCache(n, n3, n2, n4);
        this._endRendering(n, n2, max2, min2);
    }
    
    public PiscesCache getCache() {
        if (this.cache == null) {
            throw new InternalError("cache not yet initialized");
        }
        return this.cache;
    }
    
    private void emitRow(final int[] array, final int n, final int n2, final int n3) {
        if (this.cache != null && n3 >= n2) {
            this.cache.startRow(n, n2);
            final int n4 = n2 - this.cache.bboxX0;
            final int n5 = n3 - this.cache.bboxX0;
            int n6 = 1;
            int n7 = array[n4];
            for (int i = n4 + 1; i <= n5; ++i) {
                final int n8 = n7 + array[i];
                if (n8 == n7) {
                    ++n6;
                }
                else {
                    this.cache.addRLERun(n7, n6);
                    n6 = 1;
                    n7 = n8;
                }
            }
            this.cache.addRLERun(n7, n6);
        }
        Arrays.fill(array, 0);
    }
    
    private class ScanlineIterator
    {
        private int[] crossings;
        private final int maxY;
        private int nextY;
        private int edgeCount;
        private int[] edgePtrs;
        private static final int INIT_CROSSINGS_SIZE = 10;
        
        private ScanlineIterator(final int nextY, final int maxY) {
            this.crossings = new int[10];
            this.edgePtrs = new int[10];
            this.nextY = nextY;
            this.maxY = maxY;
            this.edgeCount = 0;
        }
        
        private int next() {
            final int n = this.nextY++;
            final int n2 = n - Renderer.this.boundsMinY;
            int edgeCount = this.edgeCount;
            final int[] edgePtrs = this.edgePtrs;
            final int n3 = Renderer.this.edgeBucketCounts[n2];
            if ((n3 & 0x1) != 0x0) {
                int n4 = 0;
                for (final int n5 : edgePtrs) {
                    if (Renderer.this.edges[n5 + 0] > n) {
                        edgePtrs[n4++] = n5;
                    }
                }
                edgeCount = n4;
            }
            final int[] widenArray = Helpers.widenArray(edgePtrs, edgeCount, n3 >> 1);
            for (int j = Renderer.this.edgeBuckets[n2]; j != -5; j = (int)Renderer.this.edges[j + 4]) {
                widenArray[edgeCount++] = j;
            }
            this.edgePtrs = widenArray;
            this.edgeCount = edgeCount;
            int[] crossings = this.crossings;
            if (crossings.length < edgeCount) {
                crossings = (this.crossings = new int[widenArray.length]);
            }
            for (int k = 0; k < edgeCount; ++k) {
                final int n6 = widenArray[k];
                final float n7 = Renderer.this.edges[n6 + 1];
                int n8 = (int)n7 << 1;
                Renderer.this.edges[n6 + 1] = n7 + Renderer.this.edges[n6 + 3];
                if (Renderer.this.edges[n6 + 2] > 0.0f) {
                    n8 |= 0x1;
                }
                int n9 = k;
                while (--n9 >= 0) {
                    final int n10 = crossings[n9];
                    if (n10 <= n8) {
                        break;
                    }
                    crossings[n9 + 1] = n10;
                    widenArray[n9 + 1] = widenArray[n9];
                }
                crossings[n9 + 1] = n8;
                widenArray[n9 + 1] = n6;
            }
            return edgeCount;
        }
        
        private boolean hasNext() {
            return this.nextY < this.maxY;
        }
        
        private int curY() {
            return this.nextY - 1;
        }
    }
}
