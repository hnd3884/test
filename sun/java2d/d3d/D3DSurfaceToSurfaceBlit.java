package sun.java2d.d3d;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImageOp;
import java.awt.image.BufferedImage;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.Blit;

class D3DSurfaceToSurfaceBlit extends Blit
{
    D3DSurfaceToSurfaceBlit() {
        super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        D3DBlitLoops.IsoBlit(surfaceData, surfaceData2, null, null, composite, region, null, 1, n, n2, n + n5, n2 + n6, n3, n4, n3 + n5, n4 + n6, false);
    }
}
