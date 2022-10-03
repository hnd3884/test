package sun.java2d.d3d;

import sun.java2d.loops.GraphicsPrimitive;
import sun.font.GlyphList;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.BufferedTextPipe;

class D3DTextRenderer extends BufferedTextPipe
{
    D3DTextRenderer(final RenderQueue renderQueue) {
        super(renderQueue);
    }
    
    @Override
    protected native void drawGlyphList(final int p0, final boolean p1, final boolean p2, final boolean p3, final int p4, final float p5, final float p6, final long[] p7, final float[] p8);
    
    @Override
    protected void validateContext(final SunGraphics2D sunGraphics2D, final Composite composite) {
        final D3DSurfaceData d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
        BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, sunGraphics2D.getCompClip(), composite, null, sunGraphics2D.paint, sunGraphics2D, 0);
    }
    
    D3DTextRenderer traceWrap() {
        return new Tracer(this);
    }
    
    private static class Tracer extends D3DTextRenderer
    {
        Tracer(final D3DTextRenderer d3DTextRenderer) {
            super(d3DTextRenderer.rq);
        }
        
        @Override
        protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
            GraphicsPrimitive.tracePrimitive("D3DDrawGlyphs");
            super.drawGlyphList(sunGraphics2D, list);
        }
    }
}
