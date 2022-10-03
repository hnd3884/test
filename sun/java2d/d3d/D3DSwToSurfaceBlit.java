package sun.java2d.d3d;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.Blit;

class D3DSwToSurfaceBlit extends Blit
{
    private int typeval;
    
    D3DSwToSurfaceBlit(final SurfaceType surfaceType, final int typeval) {
        super(surfaceType, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
        this.typeval = typeval;
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        D3DBlitLoops.Blit(surfaceData, surfaceData2, composite, region, null, 1, n, n2, n + n5, n2 + n6, n3, n4, n3 + n5, n4 + n6, this.typeval, false);
    }
}
