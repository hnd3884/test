package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.lang.ref.WeakReference;

class FreetypeFontScaler extends FontScaler
{
    private static final int TRUETYPE_FONT = 1;
    private static final int TYPE1_FONT = 2;
    
    private static native void initIDs(final Class p0);
    
    private void invalidateScaler() throws FontScalerException {
        this.nativeScaler = 0L;
        this.font = null;
        throw new FontScalerException();
    }
    
    public FreetypeFontScaler(final Font2D font2D, final int n, final boolean b, final int n2) {
        int n3 = 1;
        if (font2D instanceof Type1Font) {
            n3 = 2;
        }
        this.nativeScaler = this.initNativeScaler(font2D, n3, n, b, n2);
        this.font = new WeakReference<Font2D>(font2D);
    }
    
    @Override
    synchronized StrikeMetrics getFontMetrics(final long n) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getFontMetricsNative(this.font.get(), n, this.nativeScaler);
        }
        return FontScaler.getNullScaler().getFontMetrics(0L);
    }
    
    @Override
    synchronized float getGlyphAdvance(final long n, final int n2) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphAdvanceNative(this.font.get(), n, this.nativeScaler, n2);
        }
        return FontScaler.getNullScaler().getGlyphAdvance(0L, n2);
    }
    
    @Override
    synchronized void getGlyphMetrics(final long n, final int n2, final Point2D.Float float1) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            this.getGlyphMetricsNative(this.font.get(), n, this.nativeScaler, n2, float1);
            return;
        }
        FontScaler.getNullScaler().getGlyphMetrics(0L, n2, float1);
    }
    
    @Override
    synchronized long getGlyphImage(final long n, final int n2) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphImageNative(this.font.get(), n, this.nativeScaler, n2);
        }
        return FontScaler.getNullScaler().getGlyphImage(0L, n2);
    }
    
    @Override
    synchronized Rectangle2D.Float getGlyphOutlineBounds(final long n, final int n2) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphOutlineBoundsNative(this.font.get(), n, this.nativeScaler, n2);
        }
        return FontScaler.getNullScaler().getGlyphOutlineBounds(0L, n2);
    }
    
    @Override
    synchronized GeneralPath getGlyphOutline(final long n, final int n2, final float n3, final float n4) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphOutlineNative(this.font.get(), n, this.nativeScaler, n2, n3, n4);
        }
        return FontScaler.getNullScaler().getGlyphOutline(0L, n2, n3, n4);
    }
    
    @Override
    synchronized GeneralPath getGlyphVectorOutline(final long n, final int[] array, final int n2, final float n3, final float n4) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphVectorOutlineNative(this.font.get(), n, this.nativeScaler, array, n2, n3, n4);
        }
        return FontScaler.getNullScaler().getGlyphVectorOutline(0L, array, n2, n3, n4);
    }
    
    @Override
    synchronized long getLayoutTableCache() throws FontScalerException {
        return this.getLayoutTableCacheNative(this.nativeScaler);
    }
    
    @Override
    public synchronized void dispose() {
        if (this.nativeScaler != 0L) {
            this.disposeNativeScaler(this.font.get(), this.nativeScaler);
            this.nativeScaler = 0L;
        }
    }
    
    @Override
    public synchronized void disposeScaler() {
        if (this.nativeScaler != 0L) {
            new Thread(null, () -> this.dispose(), "free scaler", 0L).start();
        }
    }
    
    @Override
    synchronized int getNumGlyphs() throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getNumGlyphsNative(this.nativeScaler);
        }
        return FontScaler.getNullScaler().getNumGlyphs();
    }
    
    @Override
    synchronized int getMissingGlyphCode() throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getMissingGlyphCodeNative(this.nativeScaler);
        }
        return FontScaler.getNullScaler().getMissingGlyphCode();
    }
    
    @Override
    synchronized int getGlyphCode(final char c) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphCodeNative(this.font.get(), this.nativeScaler, c);
        }
        return FontScaler.getNullScaler().getGlyphCode(c);
    }
    
    @Override
    synchronized Point2D.Float getGlyphPoint(final long n, final int n2, final int n3) throws FontScalerException {
        if (this.nativeScaler != 0L) {
            return this.getGlyphPointNative(this.font.get(), n, this.nativeScaler, n2, n3);
        }
        return FontScaler.getNullScaler().getGlyphPoint(n, n2, n3);
    }
    
    @Override
    synchronized long getUnitsPerEm() {
        return this.getUnitsPerEMNative(this.nativeScaler);
    }
    
    @Override
    synchronized long createScalerContext(final double[] array, final int n, final int n2, final float n3, final float n4, final boolean b) {
        if (this.nativeScaler != 0L) {
            return this.createScalerContextNative(this.nativeScaler, array, n, n2, n3, n4);
        }
        return NullFontScaler.getNullScalerContext();
    }
    
    private native long initNativeScaler(final Font2D p0, final int p1, final int p2, final boolean p3, final int p4);
    
    private native StrikeMetrics getFontMetricsNative(final Font2D p0, final long p1, final long p2);
    
    private native float getGlyphAdvanceNative(final Font2D p0, final long p1, final long p2, final int p3);
    
    private native void getGlyphMetricsNative(final Font2D p0, final long p1, final long p2, final int p3, final Point2D.Float p4);
    
    private native long getGlyphImageNative(final Font2D p0, final long p1, final long p2, final int p3);
    
    private native Rectangle2D.Float getGlyphOutlineBoundsNative(final Font2D p0, final long p1, final long p2, final int p3);
    
    private native GeneralPath getGlyphOutlineNative(final Font2D p0, final long p1, final long p2, final int p3, final float p4, final float p5);
    
    private native GeneralPath getGlyphVectorOutlineNative(final Font2D p0, final long p1, final long p2, final int[] p3, final int p4, final float p5, final float p6);
    
    private native Point2D.Float getGlyphPointNative(final Font2D p0, final long p1, final long p2, final int p3, final int p4);
    
    private native long getLayoutTableCacheNative(final long p0);
    
    private native void disposeNativeScaler(final Font2D p0, final long p1);
    
    private native int getGlyphCodeNative(final Font2D p0, final long p1, final char p2);
    
    private native int getNumGlyphsNative(final long p0);
    
    private native int getMissingGlyphCodeNative(final long p0);
    
    private native long getUnitsPerEMNative(final long p0);
    
    private native long createScalerContextNative(final long p0, final double[] p1, final int p2, final int p3, final float p4, final float p5);
    
    @Override
    void invalidateScalerContext(final long n) {
    }
    
    static {
        FontManagerNativeLibrary.load();
        initIDs(FreetypeFontScaler.class);
    }
}
