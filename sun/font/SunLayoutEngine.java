package sun.font;

import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;
import java.util.WeakHashMap;
import java.lang.ref.SoftReference;

public final class SunLayoutEngine implements GlyphLayout.LayoutEngine, GlyphLayout.LayoutEngineFactory
{
    private GlyphLayout.LayoutEngineKey key;
    private static GlyphLayout.LayoutEngineFactory instance;
    private SoftReference cacheref;
    static WeakHashMap<Font2D, Boolean> aatInfo;
    
    private static native void initGVIDs();
    
    public static GlyphLayout.LayoutEngineFactory instance() {
        if (SunLayoutEngine.instance == null) {
            SunLayoutEngine.instance = new SunLayoutEngine();
        }
        return SunLayoutEngine.instance;
    }
    
    private SunLayoutEngine() {
        this.cacheref = new SoftReference(null);
    }
    
    @Override
    public GlyphLayout.LayoutEngine getEngine(final Font2D font2D, final int n, final int n2) {
        return this.getEngine(new GlyphLayout.LayoutEngineKey(font2D, n, n2));
    }
    
    @Override
    public GlyphLayout.LayoutEngine getEngine(final GlyphLayout.LayoutEngineKey layoutEngineKey) {
        ConcurrentHashMap concurrentHashMap = this.cacheref.get();
        if (concurrentHashMap == null) {
            concurrentHashMap = new ConcurrentHashMap();
            this.cacheref = new SoftReference(concurrentHashMap);
        }
        GlyphLayout.LayoutEngine layoutEngine = (GlyphLayout.LayoutEngine)concurrentHashMap.get(layoutEngineKey);
        if (layoutEngine == null) {
            final GlyphLayout.LayoutEngineKey copy = layoutEngineKey.copy();
            layoutEngine = new SunLayoutEngine(copy);
            concurrentHashMap.put(copy, layoutEngine);
        }
        return layoutEngine;
    }
    
    private SunLayoutEngine(final GlyphLayout.LayoutEngineKey key) {
        this.cacheref = new SoftReference(null);
        this.key = key;
    }
    
    private boolean isAAT(final Font2D font2D) {
        final Boolean b;
        synchronized (SunLayoutEngine.aatInfo) {
            b = SunLayoutEngine.aatInfo.get(font2D);
        }
        if (b != null) {
            return b;
        }
        boolean b2 = false;
        if (font2D instanceof TrueTypeFont) {
            final TrueTypeFont trueTypeFont = (TrueTypeFont)font2D;
            b2 = (trueTypeFont.getDirectoryEntry(1836020344) != null || trueTypeFont.getDirectoryEntry(1836020340) != null);
        }
        else if (font2D instanceof PhysicalFont) {
            final PhysicalFont physicalFont = (PhysicalFont)font2D;
            b2 = (physicalFont.getTableBytes(1836020344) != null || physicalFont.getTableBytes(1836020340) != null);
        }
        synchronized (SunLayoutEngine.aatInfo) {
            SunLayoutEngine.aatInfo.put(font2D, b2);
        }
        return b2;
    }
    
    @Override
    public void layout(final FontStrikeDesc fontStrikeDesc, final float[] array, final int n, final int n2, final TextRecord textRecord, final int n3, final Point2D.Float float1, final GlyphLayout.GVData gvData) {
        final Font2D font = this.key.font();
        nativeLayout(font, font.getStrike(fontStrikeDesc), array, n, n2, textRecord.text, textRecord.start, textRecord.limit, textRecord.min, textRecord.max, this.key.script(), this.key.lang(), n3, float1, gvData, font.getUnitsPerEm(), ((n3 & Integer.MIN_VALUE) != 0x0 && this.isAAT(font)) ? 0L : font.getLayoutTableCache());
    }
    
    private static native void nativeLayout(final Font2D p0, final FontStrike p1, final float[] p2, final int p3, final int p4, final char[] p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final int p12, final Point2D.Float p13, final GlyphLayout.GVData p14, final long p15, final long p16);
    
    static {
        FontManagerNativeLibrary.load();
        initGVIDs();
        SunLayoutEngine.aatInfo = new WeakHashMap<Font2D, Boolean>();
    }
}
