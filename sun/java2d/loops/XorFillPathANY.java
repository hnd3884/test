package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

class XorFillPathANY extends FillPath
{
    XorFillPathANY() {
        super(SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
    }
    
    @Override
    public void FillPath(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final Path2D.Float float1) {
        ProcessPath.fillPath(new PixelWriterDrawHandler(surfaceData, GeneralRenderer.createXorPixelWriter(sunGraphics2D, surfaceData), sunGraphics2D.getCompClip(), sunGraphics2D.strokeHint), float1, n, n2);
    }
}
