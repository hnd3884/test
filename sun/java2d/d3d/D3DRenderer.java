package sun.java2d.d3d;

import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.ParallelogramPipe;
import sun.java2d.pipe.SpanIterator;
import java.awt.geom.Path2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.pipe.BufferedContext;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.pipe.RenderQueue;
import sun.java2d.pipe.BufferedRenderPipe;

class D3DRenderer extends BufferedRenderPipe
{
    D3DRenderer(final RenderQueue renderQueue) {
        super(renderQueue);
    }
    
    @Override
    protected void validateContext(final SunGraphics2D sunGraphics2D) {
        final int n = (sunGraphics2D.paint.getTransparency() == 1) ? 1 : 0;
        D3DSurfaceData d3DSurfaceData;
        try {
            d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
        BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, null, sunGraphics2D.paint, sunGraphics2D, n);
    }
    
    @Override
    protected void validateContextAA(final SunGraphics2D sunGraphics2D) {
        final int n = 0;
        D3DSurfaceData d3DSurfaceData;
        try {
            d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
        BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, null, sunGraphics2D.paint, sunGraphics2D, n);
    }
    
    void copyArea(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.rq.lock();
        try {
            final int n7 = (sunGraphics2D.surfaceData.getTransparency() == 1) ? 1 : 0;
            D3DSurfaceData d3DSurfaceData;
            try {
                d3DSurfaceData = (D3DSurfaceData)sunGraphics2D.surfaceData;
            }
            catch (final ClassCastException ex) {
                throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
            }
            BufferedContext.validateContext(d3DSurfaceData, d3DSurfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, null, null, null, n7);
            this.rq.ensureCapacity(28);
            this.buf.putInt(30);
            this.buf.putInt(n).putInt(n2).putInt(n3).putInt(n4);
            this.buf.putInt(n5).putInt(n6);
        }
        finally {
            this.rq.unlock();
        }
    }
    
    @Override
    protected native void drawPoly(final int[] p0, final int[] p1, final int p2, final boolean p3, final int p4, final int p5);
    
    D3DRenderer traceWrap() {
        return new Tracer(this);
    }
    
    private class Tracer extends D3DRenderer
    {
        private D3DRenderer d3dr;
        
        Tracer(final D3DRenderer d3dr) {
            super(d3dr.rq);
            this.d3dr = d3dr;
        }
        
        @Override
        public ParallelogramPipe getAAParallelogramPipe() {
            return new ParallelogramPipe() {
                final /* synthetic */ ParallelogramPipe val$realpipe = Tracer.this.d3dr.getAAParallelogramPipe();
                
                @Override
                public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
                    GraphicsPrimitive.tracePrimitive("D3DFillAAParallelogram");
                    this.val$realpipe.fillParallelogram(sunGraphics2D, n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
                }
                
                @Override
                public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
                    GraphicsPrimitive.tracePrimitive("D3DDrawAAParallelogram");
                    this.val$realpipe.drawParallelogram(sunGraphics2D, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
                }
            };
        }
        
        @Override
        protected void validateContext(final SunGraphics2D sunGraphics2D) {
            this.d3dr.validateContext(sunGraphics2D);
        }
        
        @Override
        public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive("D3DDrawLine");
            this.d3dr.drawLine(sunGraphics2D, n, n2, n3, n4);
        }
        
        @Override
        public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive("D3DDrawRect");
            this.d3dr.drawRect(sunGraphics2D, n, n2, n3, n4);
        }
        
        @Override
        protected void drawPoly(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n, final boolean b) {
            GraphicsPrimitive.tracePrimitive("D3DDrawPoly");
            this.d3dr.drawPoly(sunGraphics2D, array, array2, n, b);
        }
        
        @Override
        public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive("D3DFillRect");
            this.d3dr.fillRect(sunGraphics2D, n, n2, n3, n4);
        }
        
        @Override
        protected void drawPath(final SunGraphics2D sunGraphics2D, final Path2D.Float float1, final int n, final int n2) {
            GraphicsPrimitive.tracePrimitive("D3DDrawPath");
            this.d3dr.drawPath(sunGraphics2D, float1, n, n2);
        }
        
        @Override
        protected void fillPath(final SunGraphics2D sunGraphics2D, final Path2D.Float float1, final int n, final int n2) {
            GraphicsPrimitive.tracePrimitive("D3DFillPath");
            this.d3dr.fillPath(sunGraphics2D, float1, n, n2);
        }
        
        @Override
        protected void fillSpans(final SunGraphics2D sunGraphics2D, final SpanIterator spanIterator, final int n, final int n2) {
            GraphicsPrimitive.tracePrimitive("D3DFillSpans");
            this.d3dr.fillSpans(sunGraphics2D, spanIterator, n, n2);
        }
        
        @Override
        public void fillParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10) {
            GraphicsPrimitive.tracePrimitive("D3DFillParallelogram");
            this.d3dr.fillParallelogram(sunGraphics2D, n, n2, n3, n4, n5, n6, n7, n8, n9, n10);
        }
        
        @Override
        public void drawParallelogram(final SunGraphics2D sunGraphics2D, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8, final double n9, final double n10, final double n11, final double n12) {
            GraphicsPrimitive.tracePrimitive("D3DDrawParallelogram");
            this.d3dr.drawParallelogram(sunGraphics2D, n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12);
        }
        
        public void copyArea(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            GraphicsPrimitive.tracePrimitive("D3DCopyArea");
            this.d3dr.copyArea(sunGraphics2D, n, n2, n3, n4, n5, n6);
        }
    }
}
