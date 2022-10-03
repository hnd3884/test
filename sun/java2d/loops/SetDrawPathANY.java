package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class SetDrawPathANY extends DrawPath
{
    SetDrawPathANY() {
        super(SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void DrawPath(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final Path2D.Float float1) {
        ProcessPath.drawPath(new PixelWriterDrawHandler(surfaceData, GeneralRenderer.createSolidPixelWriter(sunGraphics2D, surfaceData), sunGraphics2D.getCompClip(), sunGraphics2D.strokeHint), float1, n, n2);
    }
}
