package sun.java2d.d3d;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.TransformBlit;

class D3DSurfaceToGDIWindowSurfaceTransform extends TransformBlit
{
    D3DSurfaceToGDIWindowSurfaceTransform() {
        super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi);
    }
    
    @Override
    public void Transform(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final AffineTransform affineTransform, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
        D3DVolatileSurfaceManager.handleVItoScreenOp(surfaceData, surfaceData2);
    }
}
