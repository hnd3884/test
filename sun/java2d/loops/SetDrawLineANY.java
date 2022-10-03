package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetDrawLineANY extends DrawLine
{
    SetDrawLineANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void DrawLine(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
        final PixelWriter solidPixelWriter = GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData);
        if (n2 >= n4) {
            GeneralRenderer.doDrawLine(surfaceData, solidPixelWriter, null, sunGraphics2D.getCompClip(), n3, n4, n, n2);
        }
        else {
            GeneralRenderer.doDrawLine(surfaceData, solidPixelWriter, null, sunGraphics2D.getCompClip(), n, n2, n3, n4);
        }
    }
}
