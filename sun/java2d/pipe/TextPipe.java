package sun.java2d.pipe;

import java.awt.font.GlyphVector;
import sun.java2d.SunGraphics2D;

public interface TextPipe
{
    void drawString(final SunGraphics2D p0, final String p1, final double p2, final double p3);
    
    void drawGlyphVector(final SunGraphics2D p0, final GlyphVector p1, final float p2, final float p3);
    
    void drawChars(final SunGraphics2D p0, final char[] p1, final int p2, final int p3, final int p4, final int p5);
}
