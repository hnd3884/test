package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawRect extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawRect locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawRect)GraphicsPrimitiveMgr.locate(DrawRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawRect(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawRect.methodSignature, DrawRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawRect(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawRect.methodSignature, DrawRect.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawRect(final SunGraphics2D p0, final SurfaceData p1, final int p2, final int p3, final int p4, final int p5);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("DrawRect not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawRect(this);
    }
    
    static {
        methodSignature = "DrawRect(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceDrawRect extends DrawRect
    {
        DrawRect target;
        
        public TraceDrawRect(final DrawRect target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawRect(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final int n3, final int n4) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawRect(sunGraphics2D, surfaceData, n, n2, n3, n4);
        }
    }
}
