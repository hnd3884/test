package sun.java2d.d3d;

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

class D3DMaskFill extends BufferedMaskFill
{
    static void register() {
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new D3DMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa) });
    }
    
    protected D3DMaskFill(final SurfaceType surfaceType, final CompositeType compositeType) {
        super(D3DRenderQueue.getInstance(), surfaceType, compositeType, D3DSurfaceData.D3DSurface);
    }
    
    @Override
    protected native void maskFill(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final byte[] p7);
    
    @Override
    protected void validateContext(final SunGraphics2D sunGraphics2D, final Composite composite, final int n) {
        D3DSurfaceData d3DSurfaceData;
        try {
            d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
        BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, sunGraphics2D.getCompClip(), composite, null, sunGraphics2D.paint, sunGraphics2D, n);
    }
}
