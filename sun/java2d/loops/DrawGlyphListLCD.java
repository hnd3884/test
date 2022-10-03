package sun.java2d.loops;

import sun.font.GlyphList;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawGlyphListLCD extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawGlyphListLCD locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawGlyphListLCD)GraphicsPrimitiveMgr.locate(DrawGlyphListLCD.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawGlyphListLCD(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawGlyphListLCD.methodSignature, DrawGlyphListLCD.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawGlyphListLCD(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawGlyphListLCD.methodSignature, DrawGlyphListLCD.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawGlyphListLCD(final SunGraphics2D p0, final SurfaceData p1, final GlyphList p2);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return null;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawGlyphListLCD(this);
    }
    
    static {
        methodSignature = "DrawGlyphListLCD(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListLCD(null, null, null));
    }
    
    private static class TraceDrawGlyphListLCD extends DrawGlyphListLCD
    {
        DrawGlyphListLCD target;
        
        public TraceDrawGlyphListLCD(final DrawGlyphListLCD target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawGlyphListLCD(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final GlyphList list) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawGlyphListLCD(sunGraphics2D, surfaceData, list);
        }
    }
}
