package sun.java2d.loops;

import java.awt.geom.Path2D;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public class FillPath extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    
    public static FillPath locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (FillPath)GraphicsPrimitiveMgr.locate(FillPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    protected FillPath(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(FillPath.methodSignature, FillPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public FillPath(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, FillPath.methodSignature, FillPath.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void FillPath(final SunGraphics2D p0, final SurfaceData p1, final int p2, final int p3, final Path2D.Float p4);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        throw new InternalError("FillPath not implemented for " + surfaceType + " with " + compositeType);
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceFillPath(this);
    }
    
    static {
        methodSignature = "FillPath(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
    }
    
    private static class TraceFillPath extends FillPath
    {
        FillPath target;
        
        public TraceFillPath(final FillPath target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void FillPath(final SunGraphics2D sunGraphics2D, final SurfaceData surfaceData, final int n, final int n2, final Path2D.Float float1) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.FillPath(sunGraphics2D, surfaceData, n, n2, float1);
        }
    }
}
