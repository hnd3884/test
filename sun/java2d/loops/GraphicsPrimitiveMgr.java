package sun.java2d.loops;

import sun.awt.SunHints;
import java.awt.geom.Path2D;
import java.awt.AlphaComposite;
import java.awt.geom.AffineTransform;
import java.awt.Color;
import sun.java2d.SunGraphics2D;
import java.util.Arrays;
import java.util.Comparator;

public final class GraphicsPrimitiveMgr
{
    private static final boolean debugTrace = false;
    private static GraphicsPrimitive[] primitives;
    private static GraphicsPrimitive[] generalPrimitives;
    private static boolean needssort;
    private static Comparator primSorter;
    private static Comparator primFinder;
    
    private static native void initIDs(final Class p0, final Class p1, final Class p2, final Class p3, final Class p4, final Class p5, final Class p6, final Class p7, final Class p8, final Class p9, final Class p10);
    
    private static native void registerNativeLoops();
    
    private GraphicsPrimitiveMgr() {
    }
    
    public static synchronized void register(final GraphicsPrimitive[] array) {
        final GraphicsPrimitive[] primitives = GraphicsPrimitiveMgr.primitives;
        int length = 0;
        final int length2 = array.length;
        if (primitives != null) {
            length = primitives.length;
        }
        final GraphicsPrimitive[] primitives2 = new GraphicsPrimitive[length + length2];
        if (primitives != null) {
            System.arraycopy(primitives, 0, primitives2, 0, length);
        }
        System.arraycopy(array, 0, primitives2, length, length2);
        GraphicsPrimitiveMgr.needssort = true;
        GraphicsPrimitiveMgr.primitives = primitives2;
    }
    
    public static synchronized void registerGeneral(final GraphicsPrimitive graphicsPrimitive) {
        if (GraphicsPrimitiveMgr.generalPrimitives == null) {
            GraphicsPrimitiveMgr.generalPrimitives = new GraphicsPrimitive[] { graphicsPrimitive };
            return;
        }
        final int length = GraphicsPrimitiveMgr.generalPrimitives.length;
        final GraphicsPrimitive[] generalPrimitives = new GraphicsPrimitive[length + 1];
        System.arraycopy(GraphicsPrimitiveMgr.generalPrimitives, 0, generalPrimitives, 0, length);
        generalPrimitives[length] = graphicsPrimitive;
        GraphicsPrimitiveMgr.generalPrimitives = generalPrimitives;
    }
    
    public static synchronized GraphicsPrimitive locate(final int n, final SurfaceType surfaceType) {
        return locate(n, SurfaceType.OpaqueColor, CompositeType.Src, surfaceType);
    }
    
    public static synchronized GraphicsPrimitive locate(final int n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        GraphicsPrimitive graphicsPrimitive = locatePrim(n, surfaceType, compositeType, surfaceType2);
        if (graphicsPrimitive == null) {
            graphicsPrimitive = locateGeneral(n);
            if (graphicsPrimitive != null) {
                graphicsPrimitive = graphicsPrimitive.makePrimitive(surfaceType, compositeType, surfaceType2);
                if (graphicsPrimitive != null && GraphicsPrimitive.traceflags != 0) {
                    graphicsPrimitive = graphicsPrimitive.traceWrap();
                }
            }
        }
        return graphicsPrimitive;
    }
    
    public static synchronized GraphicsPrimitive locatePrim(final int n, final SurfaceType surfaceType, final CompositeType compositeType, final SurfaceType surfaceType2) {
        final PrimitiveSpec primitiveSpec = new PrimitiveSpec();
        for (SurfaceType superType = surfaceType2; superType != null; superType = superType.getSuperType()) {
            for (SurfaceType superType2 = surfaceType; superType2 != null; superType2 = superType2.getSuperType()) {
                for (CompositeType superType3 = compositeType; superType3 != null; superType3 = superType3.getSuperType()) {
                    primitiveSpec.uniqueID = GraphicsPrimitive.makeUniqueID(n, superType2, superType3, superType);
                    final GraphicsPrimitive locate = locate(primitiveSpec);
                    if (locate != null) {
                        return locate;
                    }
                }
            }
        }
        return null;
    }
    
    private static GraphicsPrimitive locateGeneral(final int n) {
        if (GraphicsPrimitiveMgr.generalPrimitives == null) {
            return null;
        }
        for (int i = 0; i < GraphicsPrimitiveMgr.generalPrimitives.length; ++i) {
            final GraphicsPrimitive graphicsPrimitive = GraphicsPrimitiveMgr.generalPrimitives[i];
            if (graphicsPrimitive.getPrimTypeID() == n) {
                return graphicsPrimitive;
            }
        }
        return null;
    }
    
    private static GraphicsPrimitive locate(final PrimitiveSpec primitiveSpec) {
        if (GraphicsPrimitiveMgr.needssort) {
            if (GraphicsPrimitive.traceflags != 0) {
                for (int i = 0; i < GraphicsPrimitiveMgr.primitives.length; ++i) {
                    GraphicsPrimitiveMgr.primitives[i] = GraphicsPrimitiveMgr.primitives[i].traceWrap();
                }
            }
            Arrays.sort(GraphicsPrimitiveMgr.primitives, GraphicsPrimitiveMgr.primSorter);
            GraphicsPrimitiveMgr.needssort = false;
        }
        final GraphicsPrimitive[] primitives = GraphicsPrimitiveMgr.primitives;
        if (primitives == null) {
            return null;
        }
        final int binarySearch = Arrays.binarySearch(primitives, primitiveSpec, GraphicsPrimitiveMgr.primFinder);
        if (binarySearch >= 0) {
            GraphicsPrimitive instantiate = primitives[binarySearch];
            if (instantiate instanceof GraphicsPrimitiveProxy) {
                instantiate = ((GraphicsPrimitiveProxy)instantiate).instantiate();
                primitives[binarySearch] = instantiate;
            }
            return instantiate;
        }
        return null;
    }
    
    private static void writeLog(final String s) {
    }
    
    public static void testPrimitiveInstantiation() {
        testPrimitiveInstantiation(false);
    }
    
    public static void testPrimitiveInstantiation(final boolean b) {
        int n = 0;
        int n2 = 0;
        final GraphicsPrimitive[] primitives = GraphicsPrimitiveMgr.primitives;
        for (int i = 0; i < primitives.length; ++i) {
            final GraphicsPrimitive graphicsPrimitive = primitives[i];
            if (graphicsPrimitive instanceof GraphicsPrimitiveProxy) {
                final GraphicsPrimitive instantiate = ((GraphicsPrimitiveProxy)graphicsPrimitive).instantiate();
                if (!instantiate.getSignature().equals(graphicsPrimitive.getSignature()) || instantiate.getUniqueID() != graphicsPrimitive.getUniqueID()) {
                    System.out.println("r.getSignature == " + instantiate.getSignature());
                    System.out.println("r.getUniqueID == " + instantiate.getUniqueID());
                    System.out.println("p.getSignature == " + graphicsPrimitive.getSignature());
                    System.out.println("p.getUniqueID == " + graphicsPrimitive.getUniqueID());
                    throw new RuntimeException("Primitive " + graphicsPrimitive + " returns wrong signature for " + instantiate.getClass());
                }
                ++n2;
                final GraphicsPrimitive graphicsPrimitive2 = instantiate;
                if (b) {
                    System.out.println(graphicsPrimitive2);
                }
            }
            else {
                if (b) {
                    System.out.println(graphicsPrimitive + " (not proxied).");
                }
                ++n;
            }
        }
        System.out.println(n + " graphics primitives were not proxied.");
        System.out.println(n2 + " proxied graphics primitives resolved correctly.");
        System.out.println(n + n2 + " total graphics primitives");
    }
    
    public static void main(final String[] array) {
        if (GraphicsPrimitiveMgr.needssort) {
            Arrays.sort(GraphicsPrimitiveMgr.primitives, GraphicsPrimitiveMgr.primSorter);
            GraphicsPrimitiveMgr.needssort = false;
        }
        testPrimitiveInstantiation(array.length > 0);
    }
    
    static {
        GraphicsPrimitiveMgr.needssort = true;
        initIDs(GraphicsPrimitive.class, SurfaceType.class, CompositeType.class, SunGraphics2D.class, Color.class, AffineTransform.class, XORComposite.class, AlphaComposite.class, Path2D.class, Path2D.Float.class, SunHints.class);
        CustomComponent.register();
        GeneralRenderer.register();
        registerNativeLoops();
        GraphicsPrimitiveMgr.primSorter = new Comparator() {
            @Override
            public int compare(final Object o, final Object o2) {
                final int uniqueID = ((GraphicsPrimitive)o).getUniqueID();
                final int uniqueID2 = ((GraphicsPrimitive)o2).getUniqueID();
                return (uniqueID == uniqueID2) ? 0 : ((uniqueID < uniqueID2) ? -1 : 1);
            }
        };
        GraphicsPrimitiveMgr.primFinder = new Comparator() {
            @Override
            public int compare(final Object o, final Object o2) {
                final int uniqueID = ((GraphicsPrimitive)o).getUniqueID();
                final int uniqueID2 = ((PrimitiveSpec)o2).uniqueID;
                return (uniqueID == uniqueID2) ? 0 : ((uniqueID < uniqueID2) ? -1 : 1);
            }
        };
    }
    
    private static class PrimitiveSpec
    {
        public int uniqueID;
    }
}
