package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public class LCDTextRenderer extends GlyphListLoopPipe
{
    @Override
    protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
        sunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(sunGraphics2D, sunGraphics2D.surfaceData, list);
    }
}
