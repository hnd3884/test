package sun.java2d.opengl;

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

class OGLMaskBlit extends BufferedMaskBlit
{
    static void register() {
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new OGLMaskBlit(SurfaceType.IntArgb, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntArgbPre, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntRgb, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntRgb, CompositeType.SrcNoEa), new OGLMaskBlit(SurfaceType.IntBgr, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntBgr, CompositeType.SrcNoEa) });
    }
    
    private OGLMaskBlit(final SurfaceType surfaceType, final CompositeType compositeType) {
        super(OGLRenderQueue.getInstance(), surfaceType, compositeType, OGLSurfaceData.OpenGLSurface);
    }
    
    @Override
    protected void validateContext(final SurfaceData surfaceData, final Composite composite, final Region region) {
        final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)surfaceData;
        BufferedContext.validateContext(oglSurfaceData, oglSurfaceData, region, composite, null, null, null, 0);
    }
}
