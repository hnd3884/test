package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawLine extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawLine locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawLine)GraphicsPrimitiveMgr.locate(DrawLine.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawLine(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawLine.methodSignature, DrawLine.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawLine(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawLine.methodSignature, DrawLine.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawLine(final SunGraphics2D p0, final SurfaceData p1, final int p2, final int p3, final int p4, final int p5);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("DrawLine not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawLine(this);
    }
    
    static {
        methodSignature = "DrawLine(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceDrawLine extends DrawLine
    {
        DrawLine target;
        
        public TraceDrawLine(final DrawLine target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawLine(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawLine(sunGraphics2D, surfaceData, n, n2, n3, n4);
        }
    }
}
