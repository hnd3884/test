package sun.java2d.pipe;

import sun.font.GlyphList;
import sun.java2d.SunGraphics2D;

public abstract class GlyphListLoopPipe extends GlyphListPipe implements LoopBasedPipe
{
    @Override
    protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list, final int n) {
        switch (n) {
            case 1: {
                sunGraphics2D.loops.drawGlyphListLoop.DrawGlyphList(sunGraphics2D, sunGraphics2D.surfaceData, list);
                return;
            }
            case 2: {
                sunGraphics2D.loops.drawGlyphListAALoop.DrawGlyphListAA(sunGraphics2D, sunGraphics2D.surfaceData, list);
                return;
            }
            case 4:
            case 6: {
                sunGraphics2D.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(sunGraphics2D, sunGraphics2D.surfaceData, list);
            }
            default: {}
        }
    }
}
