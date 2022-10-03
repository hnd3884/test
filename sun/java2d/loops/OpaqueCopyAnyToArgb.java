package sun.java2d.loops;

import sun.java2d.pipe.SpanIterator;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

class OpaqueCopyAnyToArgb extends Blit
{
    OpaqueCopyAnyToArgb() {
        super(SurfaceType.Any, CompositeType.SrcNoEa, SurfaceType.IntArgb);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        final Raster raster = surfaceData.getRaster(n, n2, n5, n6);
        final ColorModel colorModel = surfaceData.getColorModel();
        final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)surfaceData2.getRaster(n3, n4, n5, n6);
        final int[] dataStorage = integerComponentRaster.getDataStorage();
        final SpanIterator spanIterator = CustomComponent.getRegionOfInterest(surfaceData, surfaceData2, region, n, n2, n3, n4, n5, n6).getSpanIterator();
        Object dataElements = null;
        final int scanlineStride = integerComponentRaster.getScanlineStride();
        n -= n3;
        n2 -= n4;
        final int[] array = new int[4];
        while (spanIterator.nextSpan(array)) {
            int n7 = integerComponentRaster.getDataOffset(0) + array[1] * scanlineStride + array[0];
            for (int i = array[1]; i < array[3]; ++i) {
                int n8 = n7;
                for (int j = array[0]; j < array[2]; ++j) {
                    dataElements = raster.getDataElements(j + n, i + n2, dataElements);
                    dataStorage[n8++] = colorModel.getRGB(dataElements);
                }
                n7 += scanlineStride;
            }
        }
        integerComponentRaster.markDirty();
    }
}
