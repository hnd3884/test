package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.FontFormatException;

public abstract class PhysicalFont extends Font2D
{
    protected String platName;
    protected Object nativeNames;
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o.getClass() == this.getClass() && ((Font2D)o).fullName.equals(this.fullName);
    }
    
    @Override
    public int hashCode() {
        return this.fullName.hashCode();
    }
    
    PhysicalFont(final String platName, final Object nativeNames) throws FontFormatException {
        this.handle = new Font2DHandle(this);
        this.platName = platName;
        this.nativeNames = nativeNames;
    }
    
    protected PhysicalFont() {
        this.handle = new Font2DHandle(this);
    }
    
    Point2D.Float getGlyphPoint(final long n, final int n2, final int n3) {
        return new Point2D.Float();
    }
    
    abstract StrikeMetrics getFontMetrics(final long p0);
    
    abstract float getGlyphAdvance(final long p0, final int p1);
    
    abstract void getGlyphMetrics(final long p0, final int p1, final Point2D.Float p2);
    
    abstract long getGlyphImage(final long p0, final int p1);
    
    abstract Rectangle2D.Float getGlyphOutlineBounds(final long p0, final int p1);
    
    abstract GeneralPath getGlyphOutline(final long p0, final int p1, final float p2, final float p3);
    
    abstract GeneralPath getGlyphVectorOutline(final long p0, final int[] p1, final int p2, final float p3, final float p4);
}
