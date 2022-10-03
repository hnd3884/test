package sun.java2d.opengl;

import sun.java2d.loops.GraphicsPrimitive;
import sun.font.GlyphList;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import java.awt.Composite;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.BufferedTextPipe;

class OGLTextRenderer extends BufferedTextPipe
{
    OGLTextRenderer(final RenderQueue renderQueue) {
        super(renderQueue);
    }
    
    @Override
    protected native void drawGlyphList(final int p0, final boolean p1, final boolean p2, final boolean p3, final int p4, final float p5, final float p6, final long[] p7, final float[] p8);
    
    @Override
    protected void validateContext(final SunGraphics2D sunGraphics2D, final Composite composite) {
        final OGLSurfaceData oglSurfaceData = (OGLSurfaceData)sunGraphics2D.surfaceData;
        BufferedContext.validateContext(oglSurfaceData, oglSurfaceData, sunGraphics2D.getCompClip(), composite, null, sunGraphics2D.paint, sunGraphics2D, 0);
    }
    
    OGLTextRenderer traceWrap() {
        return new Tracer(this);
    }
    
    private static class Tracer extends OGLTextRenderer
    {
        Tracer(final OGLTextRenderer oglTextRenderer) {
            super(oglTextRenderer.rq);
        }
        
        @Override
        protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list) {
            GraphicsPrimitive.tracePrimitive("OGLDrawGlyphs");
            super.drawGlyphList(sunGraphics2D, list);
        }
    }
}
