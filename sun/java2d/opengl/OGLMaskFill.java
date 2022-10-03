package sun.java2d.opengl;

import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.InvalidPipeException;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.loops.GraphicsPrimitiveMgr;
import sun.java2d.loops.CompositeType;
import sun.java2d.loops.SurfaceType;
import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.BufferedMaskFill;

class OGLMaskFill extends BufferedMaskFill
{
    static void register() {
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new OGLMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new OGLMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new OGLMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa) });
    }
    
    protected OGLMaskFill(final SurfaceType surfaceType, final CompositeType compositeType) {
        super(OGLRenderQueue.getInstance(), surfaceType, compositeType, OGLSurfaceData.OpenGLSurface);
    }
    
    @Override
    protected native void maskFill(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final byte[] p7);
    
    @Override
    protected void validateContext(final SunGraphics2D sunGraphics2D, final Composite composite, final int n) {
        OGLSurfaceData oglSurfaceData;
        try {
            oglSurfaceData = (OGLSurfaceData)sunGraphics2D.surfaceData;
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
        BufferedContext.validateContext(oglSurfaceData, oglSurfaceData, sunGraphics2D.getCompClip(), composite, null, sunGraphics2D.paint, sunGraphics2D, n);
    }
}
