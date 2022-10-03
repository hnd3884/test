package sun.java2d.loops;

import sun.java2d.pipe.SpanIterator;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

class OpaqueCopyArgbToAny extends Blit
{
    OpaqueCopyArgbToAny() {
        super(SurfaceType.IntArgb, CompositeType.SrcNoEa, SurfaceType.Any);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)surfaceData.getRaster(n, n2, n5, n6);
        final int[] dataStorage = integerComponentRaster.getDataStorage();
        final WritableRaster writableRaster = (WritableRaster)surfaceData2.getRaster(n3, n4, n5, n6);
        final ColorModel colorModel = surfaceData2.getColorModel();
        final SpanIterator spanIterator = CustomComponent.getRegionOfInterest(surfaceData, surfaceData2, region, n, n2, n3, n4, n5, n6).getSpanIterator();
        Object dataElements = null;
        final int scanlineStride = integerComponentRaster.getScanlineStride();
        n -= n3;
        n2 -= n4;
        final int[] array = new int[4];
        while (spanIterator.nextSpan(array)) {
            int n7 = integerComponentRaster.getDataOffset(0) + (n2 + array[1]) * scanlineStride + (n + array[0]);
            for (int i = array[1]; i < array[3]; ++i) {
                int n8 = n7;
                for (int j = array[0]; j < array[2]; ++j) {
                    dataElements = colorModel.getDataElements(dataStorage[n8++], dataElements);
                    writableRaster.setDataElements(j, i, dataElements);
                }
                n7 += scanlineStride;
            }
        }
    }
}
