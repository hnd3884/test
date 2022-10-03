package sun.java2d.loops;

import java.lang.ref.WeakReference;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class MaskBlit extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache blitcache;
    
    public static MaskBlit locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (MaskBlit)GraphicsPrimitiveMgr.locate(MaskBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static MaskBlit getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = MaskBlit.blitcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (MaskBlit)value;
        }
        final MaskBlit locate = locate(surfaceType, compositeType, surfaceType2);
        if (locate == null) {
            System.out.println("mask blit loop not found for:");
            System.out.println("src:  " + surfaceType);
            System.out.println("comp: " + compositeType);
            System.out.println("dst:  " + surfaceType2);
        }
        else {
            MaskBlit.blitcache.put(surfaceType, compositeType, surfaceType2, locate);
        }
        return locate;
    }
    
    protected MaskBlit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(MaskBlit.methodSignature, MaskBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public MaskBlit(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, MaskBlit.methodSignature, MaskBlit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void MaskBlit(final SurfaceData p0, final SurfaceData p1, final Composite p2, final Region p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final byte[] p10, final int p11, final int p12);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        if (CompositeType.Xor.equals(compositeType)) {
            throw new InternalError("Cannot construct MaskBlit for XOR mode");
        }
        final General general = new General(surfaceType, compositeType, surfaceType2);
        this.setupGeneralBinaryOp(general);
        return general;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceMaskBlit(this);
    }
    
    static {
        methodSignature = "MaskBlit(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        MaskBlit.blitcache = new RenderCache(20);
        GraphicsPrimitiveMgr.registerGeneral(new MaskBlit(null, null, null));
    }
    
    private static class General extends MaskBlit implements GeneralBinaryOp
    {
        Blit convertsrc;
        Blit convertdst;
        MaskBlit performop;
        Blit convertresult;
        WeakReference srcTmp;
        WeakReference dstTmp;
        
        public General(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
        }
        
        @Override
        public void setPrimitives(final Blit convertsrc, final Blit convertdst, final GraphicsPrimitive graphicsPrimitive, final Blit convertresult) {
            this.convertsrc = convertsrc;
            this.convertdst = convertdst;
            this.performop = (MaskBlit)graphicsPrimitive;
            this.convertresult = convertresult;
        }
        
        @Override
        public synchronized void MaskBlit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final byte[] array, final int n7, final int n8) {
            SurfaceData convert;
            int n9;
            int n10;
            if (this.convertsrc == null) {
                convert = surfaceData;
                n9 = n;
                n10 = n2;
            }
            else {
                SurfaceData surfaceData3 = null;
                if (this.srcTmp != null) {
                    surfaceData3 = (SurfaceData)this.srcTmp.get();
                }
                convert = GraphicsPrimitive.convertFrom(this.convertsrc, surfaceData, n, n2, n5, n6, surfaceData3);
                n9 = 0;
                n10 = 0;
                if (convert != surfaceData3) {
                    this.srcTmp = new WeakReference(convert);
                }
            }
            SurfaceData convert2;
            int n11;
            int n12;
            Region region2;
            if (this.convertdst == null) {
                convert2 = surfaceData2;
                n11 = n3;
                n12 = n4;
                region2 = region;
            }
            else {
                SurfaceData surfaceData4 = null;
                if (this.dstTmp != null) {
                    surfaceData4 = (SurfaceData)this.dstTmp.get();
                }
                convert2 = GraphicsPrimitive.convertFrom(this.convertdst, surfaceData2, n3, n4, n5, n6, surfaceData4);
                n11 = 0;
                n12 = 0;
                region2 = null;
                if (convert2 != surfaceData4) {
                    this.dstTmp = new WeakReference(convert2);
                }
            }
            this.performop.MaskBlit(convert, convert2, composite, region2, n9, n10, n11, n12, n5, n6, array, n7, n8);
            if (this.convertresult != null) {
                GraphicsPrimitive.convertTo(this.convertresult, convert2, surfaceData2, region, n3, n4, n5, n6);
            }
        }
    }
    
    private static class TraceMaskBlit extends MaskBlit
    {
        MaskBlit target;
        
        public TraceMaskBlit(final MaskBlit target) {
            super(target.getNativePrim(), target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void MaskBlit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final byte[] array, final int n7, final int n8) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.MaskBlit(surfaceData, surfaceData2, composite, region, n, n2, n3, n4, n5, n6, array, n7, n8);
        }
    }
}
