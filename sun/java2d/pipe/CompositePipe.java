package sun.java2d.pipe;

import java.awt.Rectangle;
import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public interface CompositePipe
{
    Object startSequence(final SunGraphics2D p0, final Shape p1, final Rectangle p2, final int[] p3);
    
    boolean needTile(final Object p0, final int p1, final int p2, final int p3, final int p4);
    
    void renderPathTile(final Object p0, final byte[] p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    void skipTile(final Object p0, final int p1, final int p2);
    
    void endSequence(final Object p0);
}
