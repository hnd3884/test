package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawParallelogram extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawParallelogram locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawParallelogram)GraphicsPrimitiveMgr.locate(DrawParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawParallelogram(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawParallelogram.methodSignature, DrawParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawParallelogram(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawParallelogram.methodSignature, DrawParallelogram.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawParallelogram(final SunGraphics2D p0, final SurfaceData p1, final double p2, final double p3, final double p4, final double p5, final double p6, final double p7, final double p8, final double p9);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("DrawParallelogram not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawParallelogram(this);
    }
    
    static {
        methodSignature = "DrawParallelogram(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceDrawParallelogram extends DrawParallelogram
    {
        DrawParallelogram target;
        
        public TraceDrawParallelogram(final DrawParallelogram target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawParallelogram(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final double n, final double n2, final double n3, final double n4, final double n5, final double n6, final double n7, final double n8) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawParallelogram(sunGraphics2D, surfaceData, n, n2, n3, n4, n5, n6, n7, n8);
        }
    }
}
