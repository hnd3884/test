package sun.java2d.loops;

import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetDrawPolygonsANY extends DrawPolygons
{
    SetDrawPolygonsANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void DrawPolygons(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int[] array, final int[] array2, final int[] array3, final int n, final int n2, final int n3, final boolean b) {
        final PixelWriter solidPixelWriter = GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData);
        int n4 = 0;
        final Region compClip = sunGraphics2D.getCompClip();
        for (final int n5 : array3) {
            GeneralRenderer.doDrawPoly(surfaceData, solidPixelWriter, array, array2, n4, n5, compClip, n2, n3, b);
            n4 += n5;
        }
    }
}
