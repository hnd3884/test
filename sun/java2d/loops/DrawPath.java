package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawPath extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawPath locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawPath)GraphicsPrimitiveMgr.locate(DrawPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawPath(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawPath.methodSignature, DrawPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawPath(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawPath.methodSignature, DrawPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawPath(final SunGraphics2D p0, final SurfaceData p1, final int p2, final int p3, final Path2D.Float p4);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("DrawPath not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawPath(this);
    }
    
    static {
        methodSignature = "DrawPath(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceDrawPath extends DrawPath
    {
        DrawPath target;
        
        public TraceDrawPath(final DrawPath target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawPath(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final Path2D.Float float1) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawPath(sunGraphics2D, surfaceData, n, n2, float1);
        }
    }
}
