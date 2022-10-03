package sun.java2d.pipe;

import sun.java2d.SunGraphics2D;

public interface PixelFillPipe
{
    void fillRect(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4);
    
    void fillRoundRect(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    void fillOval(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4);
    
    void fillArc(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    void fillPolygon(final SunGraphics2D p0, final int[] p1, final int[] p2, final int p3);
}
