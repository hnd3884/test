package sun.java2d.pipe;

import sun.java2d.SunGraphics2D;

public interface PixelDrawPipe
{
    void drawLine(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4);
    
    void drawRect(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4);
    
    void drawRoundRect(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    void drawOval(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4);
    
    void drawArc(final SunGraphics2D p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    void drawPolyline(final SunGraphics2D p0, final int[] p1, final int[] p2, final int p3);
    
    void drawPolygon(final SunGraphics2D p0, final int[] p1, final int[] p2, final int p3);
}
