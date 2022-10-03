package sun.java2d.loops;

import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class DrawPolygons extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static DrawPolygons locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (DrawPolygons)GraphicsPrimitiveMgr.locate(DrawPolygons.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected DrawPolygons(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(DrawPolygons.methodSignature, DrawPolygons.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public DrawPolygons(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, DrawPolygons.methodSignature, DrawPolygons.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void DrawPolygons(final SunGraphics2D p0, final SurfaceData p1, final int[] p2, final int[] p3, final int[] p4, final int p5, final int p6, final int p7, final boolean p8);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("DrawPolygons not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceDrawPolygons(this);
    }
    
    static {
        methodSignature = "DrawPolygons(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceDrawPolygons extends DrawPolygons
    {
        DrawPolygons target;
        
        public TraceDrawPolygons(final DrawPolygons target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void DrawPolygons(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int[] array, final int[] array2, final int[] array3, final int n, final int n2, final int n3, final boolean b) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.DrawPolygons(sunGraphics2D, surfaceData, array, array2, array3, n, n2, n3, b);
        }
    }
}
