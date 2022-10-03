package sun.java2d.loops;

import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetFillRectANY extends FillRect
{
    SetFillRectANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void FillRect(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
        final PixelWriter solidPixelWriter = GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData);
        final Region boundsIntersectionXYWH = sunGraphics2D.getCompClip().getBoundsIntersectionXYWH(n, n2, n3, n4);
        GeneralRenderer.doSetRect(surfaceData, solidPixelWriter, boundsIntersectionXYWH.getLoX(), boundsIntersectionXYWH.getLoY(), boundsIntersectionXYWH.getHiX(), boundsIntersectionXYWH.getHiY());
    }
}
