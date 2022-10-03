package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class XorDrawGlyphListAAANY extends DrawGlyphListAA
{
    XorDrawGlyphListAAANY() {
        super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
    }
    
    @Override
    public void DrawGlyphListAA(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final GlyphList list) {
        GeneralRenderer.doDrawGlyphList(surfaceData, GeneralRenderer.createXorPixelWriter(sunGraphics2D, surfaceData), list, sunGraphics2D.getCompClip());
    }
}
