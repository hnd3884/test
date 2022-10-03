package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetDrawRectANY extends DrawRect
{
    SetDrawRectANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void DrawRect(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
        GeneralRenderer.doDrawRect(GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData), sunGraphics2D, surfaceData, n, n2, n3, n4);
    }
}
