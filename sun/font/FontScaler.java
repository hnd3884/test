package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import sun.java2d.Disposer;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import sun.java2d.DisposerRecord;

public abstract class FontScaler implements DisposerRecord
{
    private static FontScaler nullScaler;
    private static Constructor<FontScaler> scalerConstructor;
    protected WeakReference<Font2D> font;
    protected long nativeScaler;
    protected boolean disposed;
    
    public FontScaler() {
        this.font = null;
        this.nativeScaler = 0L;
        this.disposed = false;
    }
    
    public static FontScaler getScaler(final Font2D font2D, final int n, final boolean b, final int n2) {
        FontScaler nullScaler;
        try {
            nullScaler = FontScaler.scalerConstructor.newInstance(font2D, n, b, n2);
            Disposer.addObjectRecord(font2D, nullScaler);
        }
        catch (final Throwable t) {
            nullScaler = FontScaler.nullScaler;
            FontManagerFactory.getInstance().deRegisterBadFont(font2D);
        }
        return nullScaler;
    }
    
    public static synchronized FontScaler getNullScaler() {
        if (FontScaler.nullScaler == null) {
            FontScaler.nullScaler = new NullFontScaler();
        }
        return FontScaler.nullScaler;
    }
    
    abstract StrikeMetrics getFontMetrics(final long p0) throws FontScalerException;
    
    abstract float getGlyphAdvance(final long p0, final int p1) throws FontScalerException;
    
    abstract void getGlyphMetrics(final long p0, final int p1, final Point2D.Float p2) throws FontScalerException;
    
    abstract long getGlyphImage(final long p0, final int p1) throws FontScalerException;
    
    abstract Rectangle2D.Float getGlyphOutlineBounds(final long p0, final int p1) throws FontScalerException;
    
    abstract GeneralPath getGlyphOutline(final long p0, final int p1, final float p2, final float p3) throws FontScalerException;
    
    abstract GeneralPath getGlyphVectorOutline(final long p0, final int[] p1, final int p2, final float p3, final float p4) throws FontScalerException;
    
    @Override
    public void dispose() {
    }
    
    public void disposeScaler() {
    }
    
    abstract int getNumGlyphs() throws FontScalerException;
    
    abstract int getMissingGlyphCode() throws FontScalerException;
    
    abstract int getGlyphCode(final char p0) throws FontScalerException;
    
    abstract long getLayoutTableCache() throws FontScalerException;
    
    abstract Point2D.Float getGlyphPoint(final long p0, final int p1, final int p2) throws FontScalerException;
    
    abstract long getUnitsPerEm();
    
    abstract long createScalerContext(final double[] p0, final int p1, final int p2, final float p3, final float p4, final boolean p5);
    
    abstract void invalidateScalerContext(final long p0);
    
    static {
        FontScaler.nullScaler = null;
        FontScaler.scalerConstructor = null;
        final Class[] array = { Font2D.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE };
        Class<?> forName;
        try {
            forName = Class.forName("sun.font.FreetypeFontScaler");
        }
        catch (final ClassNotFoundException ex) {
            forName = NullFontScaler.class;
        }
        try {
            FontScaler.scalerConstructor = (Constructor<FontScaler>)forName.getConstructor((Class<?>[])array);
        }
        catch (final NoSuchMethodException ex2) {}
    }
}
