package sun.java2d.loops;

import java.awt.Rectangle;
import sun.java2d.pipe.Region;
import sun.java2d.SurfaceData;

public final class CustomComponent
{
    public static void register() {
        final Class<CustomComponent> clazz = CustomComponent.class;
        GraphicsPrimitiveMgr.register(new GraphicsPrimitive[] { new GraphicsPrimitiveProxy(clazz, "OpaqueCopyAnyToArgb", Blit.methodSignature, Blit.primTypeID, SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb), new GraphicsPrimitiveProxy(clazz, "OpaqueCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.SrcNoEa, SurfaceType.Any), new GraphicsPrimitiveProxy(clazz, "XorCopyArgbToAny", Blit.methodSignature, Blit.primTypeID, SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any) });
    }
    
    public static Region getRegionOfInterest(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Region region, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final Region intersection = Region.getInstanceXYWH(n3, n4, n5, n6).getIntersection(surfaceData2.getBounds());
        final Rectangle bounds = surfaceData.getBounds();
        bounds.translate(n3 - n, n4 - n2);
        Region region2 = intersection.getIntersection(bounds);
        if (region != null) {
            region2 = region2.getIntersection(region);
        }
        return region2;
    }
}
