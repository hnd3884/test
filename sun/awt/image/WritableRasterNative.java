package sun.awt.image;

import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import sun.java2d.SurfaceData;
import java.awt.image.ColorModel;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class WritableRasterNative extends WritableRaster
{
    public static WritableRasterNative createNativeRaster(final SampleModel sampleModel, final DataBuffer dataBuffer) {
        return new WritableRasterNative(sampleModel, dataBuffer);
    }
    
    protected WritableRasterNative(final SampleModel sampleModel, final DataBuffer dataBuffer) {
        super(sampleModel, dataBuffer, new Point(0, 0));
    }
    
    public static WritableRasterNative createNativeRaster(final ColorModel colorModel, final SurfaceData surfaceData, final int n, final int n2) {
        int n3 = 0;
        SampleModel sampleModel = null;
        switch (colorModel.getPixelSize()) {
            case 8:
            case 12: {
                if (colorModel.getPixelSize() == 8) {
                    n3 = 0;
                }
                else {
                    n3 = 1;
                }
                sampleModel = new PixelInterleavedSampleModel(n3, n, n2, 1, n, new int[] { 0 });
                break;
            }
            case 15:
            case 16: {
                n3 = 1;
                final int[] array = new int[3];
                final DirectColorModel directColorModel = (DirectColorModel)colorModel;
                array[0] = directColorModel.getRedMask();
                array[1] = directColorModel.getGreenMask();
                array[2] = directColorModel.getBlueMask();
                sampleModel = new SinglePixelPackedSampleModel(n3, n, n2, n, array);
                break;
            }
            case 24:
            case 32: {
                n3 = 3;
                final int[] array2 = new int[3];
                final DirectColorModel directColorModel2 = (DirectColorModel)colorModel;
                array2[0] = directColorModel2.getRedMask();
                array2[1] = directColorModel2.getGreenMask();
                array2[2] = directColorModel2.getBlueMask();
                sampleModel = new SinglePixelPackedSampleModel(n3, n, n2, n, array2);
                break;
            }
            default: {
                throw new InternalError("Unsupported depth " + colorModel.getPixelSize());
            }
        }
        return new WritableRasterNative(sampleModel, new DataBufferNative(surfaceData, n3, n, n2));
    }
}
