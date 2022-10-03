package sun.java2d.d3d;

import sun.java2d.SunGraphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedMaskBlit;

class D3DMaskBlit extends BufferedMaskBlit
{
    static void register() {
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new D3DMaskBlit(SurfaceType.IntArgb, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntArgbPre, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntRgb, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntRgb, CompositeType.SrcNoEa), new D3DMaskBlit(SurfaceType.IntBgr, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntBgr, CompositeType.SrcNoEa) });
    }
    
    private D3DMaskBlit(final SurfaceType surfaceType, final CompositeType compositeType) {
        super(D3DRenderQueue.getInstance(), surfaceType, compositeType, D3DSurfaceData.D3DSurface);
    }
    
    @Override
    protected void validateContext(final SurfaceData surfaceData, final Composite composite, final Region region) {
        final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)surfaceData;
        BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, region, composite, null, null, null, 0);
    }
}
