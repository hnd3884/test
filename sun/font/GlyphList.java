package sun.font;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;

public final class GlyphList
{
    private static final int MINGRAYLENGTH = 1024;
    private static final int MAXGRAYLENGTH = 8192;
    private static final int DEFAULT_LENGTH = 32;
    int glyphindex;
    int[] metrics;
    byte[] graybits;
    Object strikelist;
    int len;
    int maxLen;
    int maxPosLen;
    int[] glyphData;
    char[] chData;
    long[] images;
    float[] positions;
    float x;
    float y;
    float gposx;
    float gposy;
    boolean usePositions;
    boolean lcdRGBOrder;
    boolean lcdSubPixPos;
    private static GlyphList reusableGL;
    private static boolean inUse;
    
    void ensureCapacity(int n) {
        if (n < 0) {
            n = 0;
        }
        if (this.usePositions && n > this.maxPosLen) {
            this.positions = new float[n * 2 + 2];
            this.maxPosLen = n;
        }
        if (this.maxLen == 0 || n > this.maxLen) {
            this.glyphData = new int[n];
            this.chData = new char[n];
            this.images = new long[n];
            this.maxLen = n;
        }
    }
    
    private GlyphList() {
        this.len = 0;
        this.maxLen = 0;
        this.maxPosLen = 0;
    }
    
    public static GlyphList getInstance() {
        if (GlyphList.inUse) {
            return new GlyphList();
        }
        synchronized (GlyphList.class) {
            if (GlyphList.inUse) {
                return new GlyphList();
            }
            GlyphList.inUse = true;
            return GlyphList.reusableGL;
        }
    }
    
    public boolean setFromString(final FontInfo fontInfo, final String s, final float x, final float y) {
        this.x = x;
        this.y = y;
        this.strikelist = fontInfo.fontStrike;
        this.lcdRGBOrder = fontInfo.lcdRGBOrder;
        this.lcdSubPixPos = fontInfo.lcdSubPixPos;
        this.ensureCapacity(this.len = s.length());
        s.getChars(0, this.len, this.chData, 0);
        return this.mapChars(fontInfo, this.len);
    }
    
    public boolean setFromChars(final FontInfo fontInfo, final char[] array, final int n, final int n2, final float x, final float y) {
        this.x = x;
        this.y = y;
        this.strikelist = fontInfo.fontStrike;
        this.lcdRGBOrder = fontInfo.lcdRGBOrder;
        this.lcdSubPixPos = fontInfo.lcdSubPixPos;
        this.len = n2;
        if (n2 < 0) {
            this.len = 0;
        }
        else {
            this.len = n2;
        }
        this.ensureCapacity(this.len);
        System.arraycopy(array, n, this.chData, 0, this.len);
        return this.mapChars(fontInfo, this.len);
    }
    
    private final boolean mapChars(final FontInfo fontInfo, final int n) {
        if (fontInfo.font2D.getMapper().charsToGlyphsNS(n, this.chData, this.glyphData)) {
            return false;
        }
        fontInfo.fontStrike.getGlyphImagePtrs(this.glyphData, this.images, n);
        this.glyphindex = -1;
        return true;
    }
    
    public void setFromGlyphVector(final FontInfo fontInfo, final GlyphVector glyphVector, final float x, final float y) {
        this.x = x;
        this.y = y;
        this.lcdRGBOrder = fontInfo.lcdRGBOrder;
        this.lcdSubPixPos = fontInfo.lcdSubPixPos;
        final StandardGlyphVector standardGV = StandardGlyphVector.getStandardGV(glyphVector, fontInfo);
        this.usePositions = standardGV.needsPositions(fontInfo.devTx);
        this.ensureCapacity(this.len = standardGV.getNumGlyphs());
        this.strikelist = standardGV.setupGlyphImages(this.images, (float[])(this.usePositions ? this.positions : null), fontInfo.devTx);
        this.glyphindex = -1;
    }
    
    public int[] getBounds() {
        if (this.glyphindex >= 0) {
            throw new InternalError("calling getBounds after setGlyphIndex");
        }
        if (this.metrics == null) {
            this.metrics = new int[5];
        }
        this.gposx = this.x + 0.5f;
        this.gposy = this.y + 0.5f;
        this.fillBounds(this.metrics);
        return this.metrics;
    }
    
    public void setGlyphIndex(final int glyphindex) {
        this.glyphindex = glyphindex;
        if (this.images[glyphindex] == 0L) {
            this.metrics[0] = (int)this.gposx;
            this.metrics[1] = (int)this.gposy;
            this.metrics[2] = 0;
            this.metrics[3] = 0;
            this.metrics[4] = 0;
            return;
        }
        final float float1 = StrikeCache.unsafe.getFloat(this.images[glyphindex] + StrikeCache.topLeftXOffset);
        final float float2 = StrikeCache.unsafe.getFloat(this.images[glyphindex] + StrikeCache.topLeftYOffset);
        if (this.usePositions) {
            this.metrics[0] = (int)Math.floor(this.positions[glyphindex << 1] + this.gposx + float1);
            this.metrics[1] = (int)Math.floor(this.positions[(glyphindex << 1) + 1] + this.gposy + float2);
        }
        else {
            this.metrics[0] = (int)Math.floor(this.gposx + float1);
            this.metrics[1] = (int)Math.floor(this.gposy + float2);
            this.gposx += StrikeCache.unsafe.getFloat(this.images[glyphindex] + StrikeCache.xAdvanceOffset);
            this.gposy += StrikeCache.unsafe.getFloat(this.images[glyphindex] + StrikeCache.yAdvanceOffset);
        }
        this.metrics[2] = StrikeCache.unsafe.getChar(this.images[glyphindex] + StrikeCache.widthOffset);
        this.metrics[3] = StrikeCache.unsafe.getChar(this.images[glyphindex] + StrikeCache.heightOffset);
        this.metrics[4] = StrikeCache.unsafe.getChar(this.images[glyphindex] + StrikeCache.rowBytesOffset);
    }
    
    public int[] getMetrics() {
        return this.metrics;
    }
    
    public byte[] getGrayBits() {
        final int n = this.metrics[4] * this.metrics[3];
        if (this.graybits == null) {
            this.graybits = new byte[Math.max(n, 1024)];
        }
        else if (n > this.graybits.length) {
            this.graybits = new byte[n];
        }
        if (this.images[this.glyphindex] == 0L) {
            return this.graybits;
        }
        final long address = StrikeCache.unsafe.getAddress(this.images[this.glyphindex] + StrikeCache.pixelDataOffset);
        if (address == 0L) {
            return this.graybits;
        }
        for (int i = 0; i < n; ++i) {
            this.graybits[i] = StrikeCache.unsafe.getByte(address + i);
        }
        return this.graybits;
    }
    
    public long[] getImages() {
        return this.images;
    }
    
    public boolean usePositions() {
        return this.usePositions;
    }
    
    public float[] getPositions() {
        return this.positions;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public Object getStrike() {
        return this.strikelist;
    }
    
    public boolean isSubPixPos() {
        return this.lcdSubPixPos;
    }
    
    public boolean isRGBOrder() {
        return this.lcdRGBOrder;
    }
    
    public void dispose() {
        if (this == GlyphList.reusableGL) {
            if (this.graybits != null && this.graybits.length > 8192) {
                this.graybits = null;
            }
            this.usePositions = false;
            this.strikelist = null;
            GlyphList.inUse = false;
        }
    }
    
    public int getNumGlyphs() {
        return this.len;
    }
    
    private void fillBounds(final int[] array) {
        final int topLeftXOffset = StrikeCache.topLeftXOffset;
        final int topLeftYOffset = StrikeCache.topLeftYOffset;
        final int widthOffset = StrikeCache.widthOffset;
        final int heightOffset = StrikeCache.heightOffset;
        final int xAdvanceOffset = StrikeCache.xAdvanceOffset;
        final int yAdvanceOffset = StrikeCache.yAdvanceOffset;
        if (this.len == 0) {
            final int n = 0;
            final int n2 = 1;
            final int n3 = 2;
            final int n4 = 3;
            final int n5 = 0;
            array[n3] = (array[n4] = n5);
            array[n] = (array[n2] = n5);
            return;
        }
        float n7;
        float n6 = n7 = Float.POSITIVE_INFINITY;
        float n9;
        float n8 = n9 = Float.NEGATIVE_INFINITY;
        int n10 = 0;
        float n11 = this.x + 0.5f;
        float n12 = this.y + 0.5f;
        for (int i = 0; i < this.len; ++i) {
            if (this.images[i] != 0L) {
                final float float1 = StrikeCache.unsafe.getFloat(this.images[i] + topLeftXOffset);
                final float float2 = StrikeCache.unsafe.getFloat(this.images[i] + topLeftYOffset);
                final char char1 = StrikeCache.unsafe.getChar(this.images[i] + widthOffset);
                final char char2 = StrikeCache.unsafe.getChar(this.images[i] + heightOffset);
                float n13;
                float n14;
                if (this.usePositions) {
                    n13 = this.positions[n10++] + float1 + n11;
                    n14 = this.positions[n10++] + float2 + n12;
                }
                else {
                    n13 = n11 + float1;
                    n14 = n12 + float2;
                    n11 += StrikeCache.unsafe.getFloat(this.images[i] + xAdvanceOffset);
                    n12 += StrikeCache.unsafe.getFloat(this.images[i] + yAdvanceOffset);
                }
                final float n15 = n13 + char1;
                final float n16 = n14 + char2;
                if (n7 > n13) {
                    n7 = n13;
                }
                if (n6 > n14) {
                    n6 = n14;
                }
                if (n9 < n15) {
                    n9 = n15;
                }
                if (n8 < n16) {
                    n8 = n16;
                }
            }
        }
        array[0] = (int)Math.floor(n7);
        array[1] = (int)Math.floor(n6);
        array[2] = (int)Math.floor(n9);
        array[3] = (int)Math.floor(n8);
    }
    
    static {
        GlyphList.reusableGL = new GlyphList();
    }
}
