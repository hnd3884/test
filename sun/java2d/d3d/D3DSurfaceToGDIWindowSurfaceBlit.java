package sun.java2d.d3d;

import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.Blit;

class D3DSurfaceToGDIWindowSurfaceBlit extends Blit
{
    D3DSurfaceToGDIWindowSurfaceBlit() {
        super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        D3DVolatileSurfaceManager.handleVItoScreenOp(surfaceData, surfaceData2);
    }
}
