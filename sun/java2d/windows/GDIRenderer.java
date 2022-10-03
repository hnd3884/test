package sun.java2d.windows;

import sun.java2d.loops.GraphicsPrimitive;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.LoopPipe;
import sun.java2d.pipe.SpanIterator;
import java.awt.Shape;
import java.awt.geom.Path2D;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import java.awt.Composite;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.ShapeDrawPipe;
import sun.java2d.pipe.PixelFillPipe;
import sun.java2d.pipe.PixelDrawPipe;

public class GDIRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe
{
    native void doDrawLine(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    @Override
    public void drawLine(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        final int transX = sunGraphics2D.transX;
        final int transY = sunGraphics2D.transY;
        try {
            this.doDrawLine((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + transX, n2 + transY, n3 + transX, n4 + transY);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doDrawRect(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    @Override
    public void drawRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        try {
            this.doDrawRect((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doDrawRoundRect(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    @Override
    public void drawRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.doDrawRoundRect((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4, n5, n6);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doDrawOval(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    @Override
    public void drawOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        try {
            this.doDrawOval((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doDrawArc(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    @Override
    public void drawArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.doDrawArc((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4, n5, n6);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doDrawPoly(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int[] p6, final int[] p7, final int p8, final boolean p9);
    
    @Override
    public void drawPolyline(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        try {
            this.doDrawPoly((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, sunGraphics2D.transX, sunGraphics2D.transY, array, array2, n, false);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    @Override
    public void drawPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        try {
            this.doDrawPoly((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, sunGraphics2D.transX, sunGraphics2D.transY, array, array2, n, true);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doFillRect(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    @Override
    public void fillRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        try {
            this.doFillRect((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doFillRoundRect(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    @Override
    public void fillRoundRect(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.doFillRoundRect((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4, n5, n6);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doFillOval(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7);
    
    @Override
    public void fillOval(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4) {
        try {
            this.doFillOval((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doFillArc(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    @Override
    public void fillArc(final SunGraphics2D sunGraphics2D, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        try {
            this.doFillArc((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, n + sunGraphics2D.transX, n2 + sunGraphics2D.transY, n3, n4, n5, n6);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doFillPoly(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final int[] p6, final int[] p7, final int p8);
    
    @Override
    public void fillPolygon(final SunGraphics2D sunGraphics2D, final int[] array, final int[] array2, final int n) {
        try {
            this.doFillPoly((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, sunGraphics2D.transX, sunGraphics2D.transY, array, array2, n);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    native void doShape(final GDIWindowSurfaceData p0, final Region p1, final Composite p2, final int p3, final int p4, final int p5, final Path2D.Float p6, final boolean p7);
    
    void doShape(final SunGraphics2D sunGraphics2D, final Shape shape, final boolean b) {
        Path2D.Float float1;
        int transX;
        int transY;
        if (sunGraphics2D.transformState <= 1) {
            if (shape instanceof Path2D.Float) {
                float1 = (Path2D.Float)shape;
            }
            else {
                float1 = new Path2D.Float(shape);
            }
            transX = sunGraphics2D.transX;
            transY = sunGraphics2D.transY;
        }
        else {
            float1 = new Path2D.Float(shape, sunGraphics2D.transform);
            transX = 0;
            transY = 0;
        }
        try {
            this.doShape((GDIWindowSurfaceData)sunGraphics2D.surfaceData, sunGraphics2D.getCompClip(), sunGraphics2D.composite, sunGraphics2D.eargb, transX, transY, float1, b);
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
    }
    
    public void doFillSpans(final SunGraphics2D sunGraphics2D, final SpanIterator spanIterator) {
        final int[] array = new int[4];
        GDIWindowSurfaceData gdiWindowSurfaceData;
        try {
            gdiWindowSurfaceData = (GDIWindowSurfaceData)sunGraphics2D.surfaceData;
        }
        catch (final ClassCastException ex) {
            throw new InvalidPipeException("wrong surface data type: " + sunGraphics2D.surfaceData);
        }
        final Region compClip = sunGraphics2D.getCompClip();
        final Composite composite = sunGraphics2D.composite;
        final int eargb = sunGraphics2D.eargb;
        while (spanIterator.nextSpan(array)) {
            this.doFillRect(gdiWindowSurfaceData, compClip, composite, eargb, array[0], array[1], array[2] - array[0], array[3] - array[1]);
        }
    }
    
    @Override
    public void draw(final SunGraphics2D sunGraphics2D, final Shape shape) {
        if (sunGraphics2D.strokeState == 0) {
            this.doShape(sunGraphics2D, shape, false);
        }
        else if (sunGraphics2D.strokeState < 3) {
            final ShapeSpanIterator strokeSpans = LoopPipe.getStrokeSpans(sunGraphics2D, shape);
            try {
                this.doFillSpans(sunGraphics2D, strokeSpans);
            }
            finally {
                strokeSpans.dispose();
            }
        }
        else {
            this.doShape(sunGraphics2D, sunGraphics2D.stroke.createStrokedShape(shape), true);
        }
    }
    
    @Override
    public void fill(final SunGraphics2D sunGraphics2D, final Shape shape) {
        this.doShape(sunGraphics2D, shape, true);
    }
    
    public native void devCopyArea(final GDIWindowSurfaceData p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    public GDIRenderer traceWrap() {
        return new Tracer();
    }
    
    public static class Tracer extends GDIRenderer
    {
        @Override
        void doDrawLine(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5) {
            GraphicsPrimitive.tracePrimitive("GDIDrawLine");
            super.doDrawLine(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5);
        }
        
        @Override
        void doDrawRect(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5) {
            GraphicsPrimitive.tracePrimitive("GDIDrawRect");
            super.doDrawRect(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5);
        }
        
        @Override
        void doDrawRoundRect(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive("GDIDrawRoundRect");
            super.doDrawRoundRect(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5, n6, n7);
        }
        
        @Override
        void doDrawOval(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5) {
            GraphicsPrimitive.tracePrimitive("GDIDrawOval");
            super.doDrawOval(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5);
        }
        
        @Override
        void doDrawArc(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive("GDIDrawArc");
            super.doDrawArc(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5, n6, n7);
        }
        
        @Override
        void doDrawPoly(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int[] array, final int[] array2, final int n4, final boolean b) {
            GraphicsPrimitive.tracePrimitive("GDIDrawPoly");
            super.doDrawPoly(gdiWindowSurfaceData, region, composite, n, n2, n3, array, array2, n4, b);
        }
        
        @Override
        void doFillRect(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5) {
            GraphicsPrimitive.tracePrimitive("GDIFillRect");
            super.doFillRect(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5);
        }
        
        @Override
        void doFillRoundRect(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive("GDIFillRoundRect");
            super.doFillRoundRect(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5, n6, n7);
        }
        
        @Override
        void doFillOval(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5) {
            GraphicsPrimitive.tracePrimitive("GDIFillOval");
            super.doFillOval(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5);
        }
        
        @Override
        void doFillArc(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) {
            GraphicsPrimitive.tracePrimitive("GDIFillArc");
            super.doFillArc(gdiWindowSurfaceData, region, composite, n, n2, n3, n4, n5, n6, n7);
        }
        
        @Override
        void doFillPoly(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final int[] array, final int[] array2, final int n4) {
            GraphicsPrimitive.tracePrimitive("GDIFillPoly");
            super.doFillPoly(gdiWindowSurfaceData, region, composite, n, n2, n3, array, array2, n4);
        }
        
        @Override
        void doShape(final GDIWindowSurfaceData gdiWindowSurfaceData, final Region region, final Composite composite, final int n, final int n2, final int n3, final Path2D.Float float1, final boolean b) {
            GraphicsPrimitive.tracePrimitive(b ? "GDIFillShape" : "GDIDrawShape");
            super.doShape(gdiWindowSurfaceData, region, composite, n, n2, n3, float1, b);
        }
        
        @Override
        public void devCopyArea(final GDIWindowSurfaceData gdiWindowSurfaceData, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            GraphicsPrimitive.tracePrimitive("GDICopyArea");
            super.devCopyArea(gdiWindowSurfaceData, n, n2, n3, n4, n5, n6);
        }
    }
}
