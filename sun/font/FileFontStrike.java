package sun.font;

import java.awt.Rectangle;
import java.util.Locale;
import java.awt.GraphicsEnvironment;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.GeneralPath;
import java.lang.ref.WeakReference;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.ref.SoftReference;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.ConcurrentHashMap;

public class FileFontStrike extends PhysicalStrike
{
    static final int INVISIBLE_GLYPHS = 65534;
    private FileFont fileFont;
    private static final int UNINITIALISED = 0;
    private static final int INTARRAY = 1;
    private static final int LONGARRAY = 2;
    private static final int SEGINTARRAY = 3;
    private static final int SEGLONGARRAY = 4;
    private volatile int glyphCacheFormat;
    private static final int SEGSHIFT = 5;
    private static final int SEGSIZE = 32;
    private boolean segmentedCache;
    private int[][] segIntGlyphImages;
    private long[][] segLongGlyphImages;
    private float[] horizontalAdvances;
    private float[][] segHorizontalAdvances;
    ConcurrentHashMap<Integer, Rectangle2D.Float> boundsMap;
    SoftReference<ConcurrentHashMap<Integer, Point2D.Float>> glyphMetricsMapRef;
    AffineTransform invertDevTx;
    boolean useNatives;
    NativeStrike[] nativeStrikes;
    private int intPtSize;
    private static boolean isXPorLater;
    private WeakReference<ConcurrentHashMap<Integer, GeneralPath>> outlineMapRef;
    
    private static native boolean initNative();
    
    FileFontStrike(final FileFont fileFont, final FontStrikeDesc fontStrikeDesc) {
        super(fileFont, fontStrikeDesc);
        this.glyphCacheFormat = 0;
        this.fileFont = fileFont;
        if (fontStrikeDesc.style != fileFont.style) {
            if ((fontStrikeDesc.style & 0x2) == 0x2 && (fileFont.style & 0x2) == 0x0) {
                this.algoStyle = true;
                this.italic = 0.7f;
            }
            if ((fontStrikeDesc.style & 0x1) == 0x1 && (fileFont.style & 0x1) == 0x0) {
                this.algoStyle = true;
                this.boldness = 1.33f;
            }
        }
        final double[] array = new double[4];
        final AffineTransform glyphTx = fontStrikeDesc.glyphTx;
        glyphTx.getMatrix(array);
        if (!fontStrikeDesc.devTx.isIdentity() && fontStrikeDesc.devTx.getType() != 1) {
            try {
                this.invertDevTx = fontStrikeDesc.devTx.createInverse();
            }
            catch (final NoninvertibleTransformException ex) {}
        }
        final boolean b = fontStrikeDesc.aaHint != 1 && fileFont.familyName.startsWith("Amble");
        if (Double.isNaN(array[0]) || Double.isNaN(array[1]) || Double.isNaN(array[2]) || Double.isNaN(array[3]) || fileFont.getScaler() == null) {
            this.pScalerContext = NullFontScaler.getNullScalerContext();
        }
        else {
            this.pScalerContext = fileFont.getScaler().createScalerContext(array, fontStrikeDesc.aaHint, fontStrikeDesc.fmHint, this.boldness, this.italic, b);
        }
        this.mapper = fileFont.getMapper();
        final int numGlyphs = this.mapper.getNumGlyphs();
        final float n = (float)array[3];
        final int intPtSize = (int)n;
        this.intPtSize = intPtSize;
        final int n2 = intPtSize;
        final boolean b2 = (glyphTx.getType() & 0x7C) == 0x0;
        this.segmentedCache = (numGlyphs > 256 || (numGlyphs > 64 && (!b2 || n != n2 || n2 < 6 || n2 > 36)));
        if (this.pScalerContext == 0L) {
            this.disposer = new FontStrikeDisposer(fileFont, fontStrikeDesc);
            this.initGlyphCache();
            this.pScalerContext = NullFontScaler.getNullScalerContext();
            SunFontManager.getInstance().deRegisterBadFont(fileFont);
            return;
        }
        if (FontUtilities.isWindows && FileFontStrike.isXPorLater && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless() && !fileFont.useJavaRasterizer && (fontStrikeDesc.aaHint == 4 || fontStrikeDesc.aaHint == 5) && array[1] == 0.0 && array[2] == 0.0 && array[0] == array[3] && array[0] >= 3.0 && array[0] <= 100.0 && !((TrueTypeFont)fileFont).useEmbeddedBitmapsForSize(this.intPtSize)) {
            this.useNatives = true;
        }
        else if (fileFont.checkUseNatives() && fontStrikeDesc.aaHint == 0 && !this.algoStyle && array[1] == 0.0 && array[2] == 0.0 && array[0] >= 6.0 && array[0] <= 36.0 && array[0] == array[3]) {
            this.useNatives = true;
            final int length = fileFont.nativeFonts.length;
            this.nativeStrikes = new NativeStrike[length];
            for (int i = 0; i < length; ++i) {
                this.nativeStrikes[i] = new NativeStrike(fileFont.nativeFonts[i], fontStrikeDesc, false);
            }
        }
        if (FontUtilities.isLogging() && FontUtilities.isWindows) {
            FontUtilities.getLogger().info("Strike for " + fileFont + " at size = " + this.intPtSize + " use natives = " + this.useNatives + " useJavaRasteriser = " + fileFont.useJavaRasterizer + " AAHint = " + fontStrikeDesc.aaHint + " Has Embedded bitmaps = " + ((TrueTypeFont)fileFont).useEmbeddedBitmapsForSize(this.intPtSize));
        }
        this.disposer = new FontStrikeDisposer(fileFont, fontStrikeDesc, this.pScalerContext);
        final double n3 = 48.0;
        if (!(this.getImageWithAdvance = (Math.abs(glyphTx.getScaleX()) <= n3 && Math.abs(glyphTx.getScaleY()) <= n3 && Math.abs(glyphTx.getShearX()) <= n3 && Math.abs(glyphTx.getShearY()) <= n3))) {
            if (!this.segmentedCache) {
                this.horizontalAdvances = new float[numGlyphs];
                for (int j = 0; j < numGlyphs; ++j) {
                    this.horizontalAdvances[j] = Float.MAX_VALUE;
                }
            }
            else {
                this.segHorizontalAdvances = new float[(numGlyphs + 32 - 1) / 32][];
            }
        }
    }
    
    @Override
    public int getNumGlyphs() {
        return this.fileFont.getNumGlyphs();
    }
    
    long getGlyphImageFromNative(final int n) {
        if (FontUtilities.isWindows) {
            return this.getGlyphImageFromWindows(n);
        }
        return this.getGlyphImageFromX11(n);
    }
    
    private native long _getGlyphImageFromWindows(final String p0, final int p1, final int p2, final int p3, final boolean p4, final int p5);
    
    long getGlyphImageFromWindows(final int n) {
        final String familyName = this.fileFont.getFamilyName(null);
        final int n2 = (this.desc.style & 0x1) | (this.desc.style & 0x2) | this.fileFont.getStyle();
        final int intPtSize = this.intPtSize;
        final long getGlyphImageFromWindows = this._getGlyphImageFromWindows(familyName, n2, intPtSize, n, this.desc.fmHint == 2, ((TrueTypeFont)this.fileFont).fontDataSize);
        if (getGlyphImageFromWindows != 0L) {
            StrikeCache.unsafe.putFloat(getGlyphImageFromWindows + StrikeCache.xAdvanceOffset, this.getGlyphAdvance(n, false));
            return getGlyphImageFromWindows;
        }
        if (FontUtilities.isLogging()) {
            FontUtilities.getLogger().warning("Failed to render glyph using GDI: code=" + n + ", fontFamily=" + familyName + ", style=" + n2 + ", size=" + intPtSize);
        }
        return this.fileFont.getGlyphImage(this.pScalerContext, n);
    }
    
    long getGlyphImageFromX11(final int n) {
        final char c = this.fileFont.glyphToCharMap[n];
        for (int i = 0; i < this.nativeStrikes.length; ++i) {
            final CharToGlyphMapper mapper = this.fileFont.nativeFonts[i].getMapper();
            final int n2 = mapper.charToGlyph(c) & 0xFFFF;
            if (n2 != mapper.getMissingGlyphCode()) {
                final long glyphImagePtrNoCache = this.nativeStrikes[i].getGlyphImagePtrNoCache(n2);
                if (glyphImagePtrNoCache != 0L) {
                    return glyphImagePtrNoCache;
                }
            }
        }
        return this.fileFont.getGlyphImage(this.pScalerContext, n);
    }
    
    @Override
    long getGlyphImagePtr(final int n) {
        if (n >= 65534) {
            return StrikeCache.invisibleGlyphPtr;
        }
        long n2;
        if ((n2 = this.getCachedGlyphPtr(n)) != 0L) {
            return n2;
        }
        if (this.useNatives) {
            n2 = this.getGlyphImageFromNative(n);
            if (n2 == 0L && FontUtilities.isLogging()) {
                FontUtilities.getLogger().info("Strike for " + this.fileFont + " at size = " + this.intPtSize + " couldn't get native glyph for code = " + n);
            }
        }
        if (n2 == 0L) {
            n2 = this.fileFont.getGlyphImage(this.pScalerContext, n);
        }
        return this.setCachedGlyphPtr(n, n2);
    }
    
    @Override
    void getGlyphImagePtrs(final int[] array, final long[] array2, final int n) {
        for (int i = 0; i < n; ++i) {
            final int n2 = array[i];
            if (n2 >= 65534) {
                array2[i] = StrikeCache.invisibleGlyphPtr;
            }
            else {
                final int n3 = i;
                final long cachedGlyphPtr = this.getCachedGlyphPtr(n2);
                array2[n3] = cachedGlyphPtr;
                if (cachedGlyphPtr == 0L) {
                    long n4 = 0L;
                    if (this.useNatives) {
                        n4 = this.getGlyphImageFromNative(n2);
                    }
                    if (n4 == 0L) {
                        n4 = this.fileFont.getGlyphImage(this.pScalerContext, n2);
                    }
                    array2[i] = this.setCachedGlyphPtr(n2, n4);
                }
            }
        }
    }
    
    @Override
    int getSlot0GlyphImagePtrs(final int[] array, final long[] array2, final int n) {
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            final int n3 = array[i];
            if (n3 >>> 24 != 0) {
                return n2;
            }
            ++n2;
            if (n3 >= 65534) {
                array2[i] = StrikeCache.invisibleGlyphPtr;
            }
            else {
                final int n4 = i;
                final long cachedGlyphPtr = this.getCachedGlyphPtr(n3);
                array2[n4] = cachedGlyphPtr;
                if (cachedGlyphPtr == 0L) {
                    long n5 = 0L;
                    if (this.useNatives) {
                        n5 = this.getGlyphImageFromNative(n3);
                    }
                    if (n5 == 0L) {
                        n5 = this.fileFont.getGlyphImage(this.pScalerContext, n3);
                    }
                    array2[i] = this.setCachedGlyphPtr(n3, n5);
                }
            }
        }
        return n2;
    }
    
    long getCachedGlyphPtr(final int n) {
        try {
            return this.getCachedGlyphPtrInternal(n);
        }
        catch (final Exception ex) {
            return ((NullFontScaler)FontScaler.getNullScaler()).getGlyphImage(NullFontScaler.getNullScalerContext(), n);
        }
    }
    
    private long getCachedGlyphPtrInternal(final int n) {
        switch (this.glyphCacheFormat) {
            case 1: {
                return (long)this.intGlyphImages[n] & 0xFFFFFFFFL;
            }
            case 3: {
                final int n2 = n >> 5;
                if (this.segIntGlyphImages[n2] != null) {
                    return (long)this.segIntGlyphImages[n2][n % 32] & 0xFFFFFFFFL;
                }
                return 0L;
            }
            case 2: {
                return this.longGlyphImages[n];
            }
            case 4: {
                final int n3 = n >> 5;
                if (this.segLongGlyphImages[n3] != null) {
                    return this.segLongGlyphImages[n3][n % 32];
                }
                return 0L;
            }
            default: {
                return 0L;
            }
        }
    }
    
    private synchronized long setCachedGlyphPtr(final int n, final long n2) {
        try {
            return this.setCachedGlyphPtrInternal(n, n2);
        }
        catch (final Exception ex) {
            switch (this.glyphCacheFormat) {
                case 1:
                case 3: {
                    StrikeCache.freeIntPointer((int)n2);
                    break;
                }
                case 2:
                case 4: {
                    StrikeCache.freeLongPointer(n2);
                    break;
                }
            }
            return ((NullFontScaler)FontScaler.getNullScaler()).getGlyphImage(NullFontScaler.getNullScalerContext(), n);
        }
    }
    
    private long setCachedGlyphPtrInternal(final int n, final long n2) {
        switch (this.glyphCacheFormat) {
            case 1: {
                if (this.intGlyphImages[n] == 0) {
                    this.intGlyphImages[n] = (int)n2;
                    return n2;
                }
                StrikeCache.freeIntPointer((int)n2);
                return (long)this.intGlyphImages[n] & 0xFFFFFFFFL;
            }
            case 3: {
                final int n3 = n >> 5;
                final int n4 = n % 32;
                if (this.segIntGlyphImages[n3] == null) {
                    this.segIntGlyphImages[n3] = new int[32];
                }
                if (this.segIntGlyphImages[n3][n4] == 0) {
                    this.segIntGlyphImages[n3][n4] = (int)n2;
                    return n2;
                }
                StrikeCache.freeIntPointer((int)n2);
                return (long)this.segIntGlyphImages[n3][n4] & 0xFFFFFFFFL;
            }
            case 2: {
                if (this.longGlyphImages[n] == 0L) {
                    return this.longGlyphImages[n] = n2;
                }
                StrikeCache.freeLongPointer(n2);
                return this.longGlyphImages[n];
            }
            case 4: {
                final int n5 = n >> 5;
                final int n6 = n % 32;
                if (this.segLongGlyphImages[n5] == null) {
                    this.segLongGlyphImages[n5] = new long[32];
                }
                if (this.segLongGlyphImages[n5][n6] == 0L) {
                    return this.segLongGlyphImages[n5][n6] = n2;
                }
                StrikeCache.freeLongPointer(n2);
                return this.segLongGlyphImages[n5][n6];
            }
            default: {
                this.initGlyphCache();
                return this.setCachedGlyphPtr(n, n2);
            }
        }
    }
    
    private synchronized void initGlyphCache() {
        final int numGlyphs = this.mapper.getNumGlyphs();
        int glyphCacheFormat;
        if (this.segmentedCache) {
            final int n = (numGlyphs + 32 - 1) / 32;
            if (FileFontStrike.longAddresses) {
                glyphCacheFormat = 4;
                this.segLongGlyphImages = new long[n][];
                this.disposer.segLongGlyphImages = this.segLongGlyphImages;
            }
            else {
                glyphCacheFormat = 3;
                this.segIntGlyphImages = new int[n][];
                this.disposer.segIntGlyphImages = this.segIntGlyphImages;
            }
        }
        else if (FileFontStrike.longAddresses) {
            glyphCacheFormat = 2;
            this.longGlyphImages = new long[numGlyphs];
            this.disposer.longGlyphImages = this.longGlyphImages;
        }
        else {
            glyphCacheFormat = 1;
            this.intGlyphImages = new int[numGlyphs];
            this.disposer.intGlyphImages = this.intGlyphImages;
        }
        this.glyphCacheFormat = glyphCacheFormat;
    }
    
    @Override
    float getGlyphAdvance(final int n) {
        return this.getGlyphAdvance(n, true);
    }
    
    private float getGlyphAdvance(final int n, final boolean b) {
        if (n >= 65534) {
            return 0.0f;
        }
        if (this.horizontalAdvances != null) {
            final float n2 = this.horizontalAdvances[n];
            if (n2 != Float.MAX_VALUE) {
                if (!b && this.invertDevTx != null) {
                    final Point2D.Float float1 = new Point2D.Float(n2, 0.0f);
                    this.desc.devTx.deltaTransform(float1, float1);
                    return float1.x;
                }
                return n2;
            }
        }
        else if (this.segmentedCache && this.segHorizontalAdvances != null) {
            final float[] array = this.segHorizontalAdvances[n >> 5];
            if (array != null) {
                final float n3 = array[n % 32];
                if (n3 != Float.MAX_VALUE) {
                    if (!b && this.invertDevTx != null) {
                        final Point2D.Float float2 = new Point2D.Float(n3, 0.0f);
                        this.desc.devTx.deltaTransform(float2, float2);
                        return float2.x;
                    }
                    return n3;
                }
            }
        }
        if (!b && this.invertDevTx != null) {
            final Point2D.Float float3 = new Point2D.Float();
            this.fileFont.getGlyphMetrics(this.pScalerContext, n, float3);
            return float3.x;
        }
        float n4;
        if (this.invertDevTx != null || !b) {
            n4 = this.getGlyphMetrics(n, b).x;
        }
        else {
            long n5;
            if (this.getImageWithAdvance) {
                n5 = this.getGlyphImagePtr(n);
            }
            else {
                n5 = this.getCachedGlyphPtr(n);
            }
            if (n5 != 0L) {
                n4 = StrikeCache.unsafe.getFloat(n5 + StrikeCache.xAdvanceOffset);
            }
            else {
                n4 = this.fileFont.getGlyphAdvance(this.pScalerContext, n);
            }
        }
        if (this.horizontalAdvances != null) {
            this.horizontalAdvances[n] = n4;
        }
        else if (this.segmentedCache && this.segHorizontalAdvances != null) {
            final int n6 = n >> 5;
            final int n7 = n % 32;
            if (this.segHorizontalAdvances[n6] == null) {
                this.segHorizontalAdvances[n6] = new float[32];
                for (int i = 0; i < 32; ++i) {
                    this.segHorizontalAdvances[n6][i] = Float.MAX_VALUE;
                }
            }
            this.segHorizontalAdvances[n6][n7] = n4;
        }
        return n4;
    }
    
    @Override
    float getCodePointAdvance(final int n) {
        return this.getGlyphAdvance(this.mapper.charToGlyph(n));
    }
    
    @Override
    void getGlyphImageBounds(final int n, final Point2D.Float float1, final Rectangle rectangle) {
        final long glyphImagePtr = this.getGlyphImagePtr(n);
        if (glyphImagePtr == 0L) {
            rectangle.x = (int)Math.floor(float1.x);
            rectangle.y = (int)Math.floor(float1.y);
            final int n2 = 0;
            rectangle.height = n2;
            rectangle.width = n2;
            return;
        }
        final float float2 = StrikeCache.unsafe.getFloat(glyphImagePtr + StrikeCache.topLeftXOffset);
        final float float3 = StrikeCache.unsafe.getFloat(glyphImagePtr + StrikeCache.topLeftYOffset);
        rectangle.x = (int)Math.floor(float1.x + float2);
        rectangle.y = (int)Math.floor(float1.y + float3);
        rectangle.width = (StrikeCache.unsafe.getShort(glyphImagePtr + StrikeCache.widthOffset) & 0xFFFF);
        rectangle.height = (StrikeCache.unsafe.getShort(glyphImagePtr + StrikeCache.heightOffset) & 0xFFFF);
        if ((this.desc.aaHint == 4 || this.desc.aaHint == 5) && float2 <= -2.0f && this.getGlyphImageMinX(glyphImagePtr, rectangle.x) > rectangle.x) {
            ++rectangle.x;
            --rectangle.width;
        }
    }
    
    private int getGlyphImageMinX(final long n, final int n2) {
        final char char1 = StrikeCache.unsafe.getChar(n + StrikeCache.widthOffset);
        final char char2 = StrikeCache.unsafe.getChar(n + StrikeCache.heightOffset);
        final char char3 = StrikeCache.unsafe.getChar(n + StrikeCache.rowBytesOffset);
        if (char3 == char1) {
            return n2;
        }
        final long address = StrikeCache.unsafe.getAddress(n + StrikeCache.pixelDataOffset);
        if (address == 0L) {
            return n2;
        }
        for (char c = '\0'; c < char2; ++c) {
            for (int i = 0; i < 3; ++i) {
                if (StrikeCache.unsafe.getByte(address + c * char3 + i) != 0) {
                    return n2;
                }
            }
        }
        return n2 + 1;
    }
    
    @Override
    StrikeMetrics getFontMetrics() {
        if (this.strikeMetrics == null) {
            this.strikeMetrics = this.fileFont.getFontMetrics(this.pScalerContext);
            if (this.invertDevTx != null) {
                this.strikeMetrics.convertToUserSpace(this.invertDevTx);
            }
        }
        return this.strikeMetrics;
    }
    
    @Override
    Point2D.Float getGlyphMetrics(final int n) {
        return this.getGlyphMetrics(n, true);
    }
    
    private Point2D.Float getGlyphMetrics(final int n, final boolean b) {
        Point2D.Float float1 = new Point2D.Float();
        if (n >= 65534) {
            return float1;
        }
        long n2;
        if (this.getImageWithAdvance && b) {
            n2 = this.getGlyphImagePtr(n);
        }
        else {
            n2 = this.getCachedGlyphPtr(n);
        }
        if (n2 != 0L) {
            float1 = new Point2D.Float();
            float1.x = StrikeCache.unsafe.getFloat(n2 + StrikeCache.xAdvanceOffset);
            float1.y = StrikeCache.unsafe.getFloat(n2 + StrikeCache.yAdvanceOffset);
            if (this.invertDevTx != null) {
                this.invertDevTx.deltaTransform(float1, float1);
            }
        }
        else {
            final Integer value = n;
            Point2D.Float float2 = null;
            ConcurrentHashMap<Integer, Point2D.Float> concurrentHashMap = null;
            if (this.glyphMetricsMapRef != null) {
                concurrentHashMap = this.glyphMetricsMapRef.get();
            }
            if (concurrentHashMap != null) {
                float2 = concurrentHashMap.get(value);
                if (float2 != null) {
                    float1.x = float2.x;
                    float1.y = float2.y;
                    return float1;
                }
            }
            if (float2 == null) {
                this.fileFont.getGlyphMetrics(this.pScalerContext, n, float1);
                if (this.invertDevTx != null) {
                    this.invertDevTx.deltaTransform(float1, float1);
                }
                final Point2D.Float float3 = new Point2D.Float(float1.x, float1.y);
                if (concurrentHashMap == null) {
                    concurrentHashMap = new ConcurrentHashMap<Integer, Point2D.Float>();
                    this.glyphMetricsMapRef = new SoftReference<ConcurrentHashMap<Integer, Point2D.Float>>(concurrentHashMap);
                }
                concurrentHashMap.put(value, float3);
            }
        }
        return float1;
    }
    
    @Override
    Point2D.Float getCharMetrics(final char c) {
        return this.getGlyphMetrics(this.mapper.charToGlyph(c));
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final int n) {
        if (this.boundsMap == null) {
            this.boundsMap = new ConcurrentHashMap<Integer, Rectangle2D.Float>();
        }
        final Integer value = n;
        Rectangle2D.Float glyphOutlineBounds = this.boundsMap.get(value);
        if (glyphOutlineBounds == null) {
            glyphOutlineBounds = this.fileFont.getGlyphOutlineBounds(this.pScalerContext, n);
            this.boundsMap.put(value, glyphOutlineBounds);
        }
        return glyphOutlineBounds;
    }
    
    public Rectangle2D getOutlineBounds(final int n) {
        return this.fileFont.getGlyphOutlineBounds(this.pScalerContext, n);
    }
    
    @Override
    GeneralPath getGlyphOutline(final int n, final float n2, final float n3) {
        GeneralPath glyphOutline = null;
        ConcurrentHashMap<?, ?> concurrentHashMap = null;
        if (this.outlineMapRef != null) {
            concurrentHashMap = this.outlineMapRef.get();
            if (concurrentHashMap != null) {
                glyphOutline = (GeneralPath)concurrentHashMap.get(n);
            }
        }
        if (glyphOutline == null) {
            glyphOutline = this.fileFont.getGlyphOutline(this.pScalerContext, n, 0.0f, 0.0f);
            if (concurrentHashMap == null) {
                concurrentHashMap = new ConcurrentHashMap<Object, Object>();
                this.outlineMapRef = new WeakReference<ConcurrentHashMap<Integer, GeneralPath>>((ConcurrentHashMap<Integer, GeneralPath>)concurrentHashMap);
            }
            concurrentHashMap.put((Object)n, glyphOutline);
        }
        final GeneralPath generalPath = (GeneralPath)glyphOutline.clone();
        if (n2 != 0.0f || n3 != 0.0f) {
            generalPath.transform(AffineTransform.getTranslateInstance(n2, n3));
        }
        return generalPath;
    }
    
    @Override
    GeneralPath getGlyphVectorOutline(final int[] array, final float n, final float n2) {
        return this.fileFont.getGlyphVectorOutline(this.pScalerContext, array, array.length, n, n2);
    }
    
    @Override
    protected void adjustPoint(final Point2D.Float float1) {
        if (this.invertDevTx != null) {
            this.invertDevTx.deltaTransform(float1, float1);
        }
    }
    
    static {
        FileFontStrike.isXPorLater = false;
        if (FontUtilities.isWindows && !FontUtilities.useT2K && !GraphicsEnvironment.isHeadless()) {
            FileFontStrike.isXPorLater = initNative();
        }
    }
}
