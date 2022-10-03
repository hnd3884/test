package sun.java2d.loops;

import java.lang.ref.WeakReference;
import sun.java2d.pipe.SpanIterator;
import java.awt.CompositeContext;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Map;
import java.awt.RenderingHints;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

public class Blit extends GraphicsPrimitive
{
    public static final String methodSignature;
    public static final int primTypeID;
    private static RenderCache blitcache;
    
    public static Blit locate(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        return (Blit)GraphicsPrimitiveMgr.locate(Blit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public static Blit getFromCache(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final Object value = Blit.blitcache.get(surfaceType, compositeType, surfaceType2);
        if (value != null) {
            return (Blit)value;
        }
        final Blit locate = locate(surfaceType, compositeType, surfaceType2);
        if (locate == null) {
            System.out.println("blit loop not found for:");
            System.out.println("src:  " + surfaceType);
            System.out.println("comp: " + compositeType);
            System.out.println("dst:  " + surfaceType2);
        }
        else {
            Blit.blitcache.put(surfaceType, compositeType, surfaceType2, locate);
        }
        return locate;
    }
    
    protected Blit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(Blit.methodSignature, Blit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public Blit(final long n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        super(n, Blit.methodSignature, Blit.primTypeID, surfaceType, compositeType, surfaceType2);
    }
    
    public native void Blit(final SurfaceData p0, final SurfaceData p1, final Composite p2, final Region p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9);
    
    @Override
    public GraphicsPrimitive makePrimitive(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        if (compositeType.isDerivedFrom(CompositeType.Xor)) {
            final GeneralXorBlit generalXorBlit = new GeneralXorBlit(surfaceType, compositeType, surfaceType2);
            this.setupGeneralBinaryOp(generalXorBlit);
            return generalXorBlit;
        }
        if (compositeType.isDerivedFrom(CompositeType.AnyAlpha)) {
            return new GeneralMaskBlit(surfaceType, compositeType, surfaceType2);
        }
        return AnyBlit.instance;
    }
    
    @Override
    public GraphicsPrimitive traceWrap() {
        return new TraceBlit(this);
    }
    
    static {
        methodSignature = "Blit(...)".toString();
        primTypeID = GraphicsPrimitive.makePrimTypeID();
        Blit.blitcache = new RenderCache(20);
        GraphicsPrimitiveMgr.registerGeneral(new Blit(null, null, null));
    }
    
    private static class AnyBlit extends Blit
    {
        public static AnyBlit instance;
        
        public AnyBlit() {
            super(SurfaceType.Any, CompositeType.Any, SurfaceType.Any);
        }
        
        @Override
        public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, Region instanceXYWH, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
            final CompositeContext context = composite.createContext(surfaceData.getColorModel(), surfaceData2.getColorModel(), new RenderingHints(null));
            final Raster raster = surfaceData.getRaster(n, n2, n5, n6);
            final WritableRaster writableRaster = (WritableRaster)surfaceData2.getRaster(n3, n4, n5, n6);
            if (instanceXYWH == null) {
                instanceXYWH = Region.getInstanceXYWH(n3, n4, n5, n6);
            }
            final int[] array = { n3, n4, n3 + n5, n4 + n6 };
            final SpanIterator spanIterator = instanceXYWH.getSpanIterator(array);
            n -= n3;
            n2 -= n4;
            while (spanIterator.nextSpan(array)) {
                final int n7 = array[2] - array[0];
                final int n8 = array[3] - array[1];
                final Raster child = raster.createChild(n + array[0], n2 + array[1], n7, n8, 0, 0, null);
                final WritableRaster writableChild = writableRaster.createWritableChild(array[0], array[1], n7, n8, 0, 0, null);
                context.compose(child, writableChild, writableChild);
            }
            context.dispose();
        }
        
        static {
            AnyBlit.instance = new AnyBlit();
        }
    }
    
    private static class GeneralMaskBlit extends Blit
    {
        MaskBlit performop;
        
        public GeneralMaskBlit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
            this.performop = MaskBlit.locate(surfaceType, compositeType, surfaceType2);
        }
        
        @Override
        public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            this.performop.MaskBlit(surfaceData, surfaceData2, composite, region, n, n2, n3, n4, n5, n6, null, 0, 0);
        }
    }
    
    private static class GeneralXorBlit extends Blit implements GeneralBinaryOp
    {
        Blit convertsrc;
        Blit convertdst;
        Blit performop;
        Blit convertresult;
        WeakReference srcTmp;
        WeakReference dstTmp;
        
        public GeneralXorBlit(final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
            super(surfaceType, compositeType, surfaceType2);
        }
        
        @Override
        public void setPrimitives(final Blit convertsrc, final Blit convertdst, final GraphicsPrimitive graphicsPrimitive, final Blit convertresult) {
            this.convertsrc = convertsrc;
            this.convertdst = convertdst;
            this.performop = (Blit)graphicsPrimitive;
            this.convertresult = convertresult;
        }
        
        @Override
        public synchronized void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            SurfaceData convert;
            int n7;
            int n8;
            if (this.convertsrc == null) {
                convert = surfaceData;
                n7 = n;
                n8 = n2;
            }
            else {
                SurfaceData surfaceData3 = null;
                if (this.srcTmp != null) {
                    surfaceData3 = (SurfaceData)this.srcTmp.get();
                }
                convert = GraphicsPrimitive.convertFrom(this.convertsrc, surfaceData, n, n2, n5, n6, surfaceData3);
                n7 = 0;
                n8 = 0;
                if (convert != surfaceData3) {
                    this.srcTmp = new WeakReference(convert);
                }
            }
            SurfaceData convert2;
            int n9;
            int n10;
            Region region2;
            if (this.convertdst == null) {
                convert2 = surfaceData2;
                n9 = n3;
                n10 = n4;
                region2 = region;
            }
            else {
                SurfaceData surfaceData4 = null;
                if (this.dstTmp != null) {
                    surfaceData4 = (SurfaceData)this.dstTmp.get();
                }
                convert2 = GraphicsPrimitive.convertFrom(this.convertdst, surfaceData2, n3, n4, n5, n6, surfaceData4);
                n9 = 0;
                n10 = 0;
                region2 = null;
                if (convert2 != surfaceData4) {
                    this.dstTmp = new WeakReference(convert2);
                }
            }
            this.performop.Blit(convert, convert2, composite, region2, n7, n8, n9, n10, n5, n6);
            if (this.convertresult != null) {
                GraphicsPrimitive.convertTo(this.convertresult, convert2, surfaceData2, region, n3, n4, n5, n6);
            }
        }
    }
    
    private static class TraceBlit extends Blit
    {
        Blit target;
        
        public TraceBlit(final Blit target) {
            super(target.getSourceType(), target.getCompositeType(), target.getDestType());
            this.target = target;
        }
        
        @Override
        public GraphicsPrimitive traceWrap() {
            return this;
        }
        
        @Override
        public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
            GraphicsPrimitive.tracePrimitive(this.target);
            this.target.Blit(surfaceData, surfaceData2, composite, region, n, n2, n3, n4, n5, n6);
        }
    }
}
