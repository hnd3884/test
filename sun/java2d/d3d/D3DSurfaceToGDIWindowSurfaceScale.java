package sun.java2d.d3d;

import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.windows.GDIWindowSurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.ScaledBlit;

class D3DSurfaceToGDIWindowSurfaceScale extends ScaledBlit
{
    D3DSurfaceToGDIWindowSurfaceScale() {
        super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi);
    }
    
    @Override
    public void Scale(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final double n5, final double n6, final double n7, final double n8) {
        D3DVolatileSurfaceManager.handleVItoScreenOp(surfaceData, surfaceData2);
    }
}
