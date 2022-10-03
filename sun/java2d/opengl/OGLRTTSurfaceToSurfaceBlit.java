package sun.java2d.opengl;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.Blit;

class OGLRTTSurfaceToSurfaceBlit extends Blit
{
    OGLRTTSurfaceToSurfaceBlit() {
        super(OGLSurfaceData.OpenGLSurfaceRTT, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        OGLBlitLoops.IsoBlit(surfaceData, surfaceData2, null, null, composite, region, null, 1, n, n2, n + n5, n2 + n6, n3, n4, n3 + n5, n4 + n6, true);
    }
}
