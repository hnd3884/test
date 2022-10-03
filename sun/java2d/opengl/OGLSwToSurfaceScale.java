package sun.java2d.opengl;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.ScaledBlit;

class OGLSwToSurfaceScale extends ScaledBlit
{
    private int typeval;
    
    OGLSwToSurfaceScale(final SurfaceType surfaceType, final int typeval) {
        super(surfaceType, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
        this.typeval = typeval;
    }
    
    @Override
    public void Scale(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
        OGLBlitLoops.Blit(surfaceData, surfaceData2, composite, region, null, 1, n, n2, n3, n4, n5, n6, n7, n8, this.typeval, false);
    }
}
