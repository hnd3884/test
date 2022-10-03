package sun.java2d.loops;

import sun.java2d.pipe.SpanIterator;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import sun.awt.image.IntegerComponentRaster;
import sun.java2d.pipe.Region;
import java.awt.Composite;
import sun.java2d.SurfaceData;

class XorCopyArgbToAny extends Blit
{
    XorCopyArgbToAny() {
        super(SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any);
    }
    
    @Override
    public void Blit(final SurfaceData surfaceData, final SurfaceData surfaceData2, final Composite composite, final Region region, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)surfaceData.getRaster(n, n2, n5, n6);
        final int[] dataStorage = integerComponentRaster.getDataStorage();
        final WritableRaster writableRaster = (WritableRaster)surfaceData2.getRaster(n3, n4, n5, n6);
        final ColorModel colorModel = surfaceData2.getColorModel();
        final SpanIterator spanIterator = CustomComponent.getRegionOfInterest(surfaceData, surfaceData2, region, n, n2, n3, n4, n5, n6).getSpanIterator();
        final Object dataElements = colorModel.getDataElements(((XORComposite)composite).getXorColor().getRGB(), null);
        Object dataElements2 = null;
        Object dataElements3 = null;
        final int scanlineStride = integerComponentRaster.getScanlineStride();
        n -= n3;
        n2 -= n4;
        final int[] array = new int[4];
        while (spanIterator.nextSpan(array)) {
            int n7 = integerComponentRaster.getDataOffset(0) + (n2 + array[1]) * scanlineStride + (n + array[0]);
            for (int i = array[1]; i < array[3]; ++i) {
                int n8 = n7;
                for (int j = array[0]; j < array[2]; ++j) {
                    dataElements2 = colorModel.getDataElements(dataStorage[n8++], dataElements2);
                    dataElements3 = writableRaster.getDataElements(j, i, dataElements3);
                    switch (colorModel.getTransferType()) {
                        case 0: {
                            final byte[] array2 = (byte[])dataElements2;
                            final byte[] array3 = (byte[])dataElements3;
                            final byte[] array4 = (byte[])dataElements;
                            for (int k = 0; k < array3.length; ++k) {
                                final byte[] array5 = array3;
                                final int n9 = k;
                                array5[n9] ^= (byte)(array2[k] ^ array4[k]);
                            }
                            break;
                        }
                        case 1:
                        case 2: {
                            final short[] array6 = (short[])dataElements2;
                            final short[] array7 = (short[])dataElements3;
                            final short[] array8 = (short[])dataElements;
                            for (int l = 0; l < array7.length; ++l) {
                                final short[] array9 = array7;
                                final int n10 = l;
                                array9[n10] ^= (short)(array6[l] ^ array8[l]);
                            }
                            break;
                        }
                        case 3: {
                            final int[] array10 = (int[])dataElements2;
                            final int[] array11 = (int[])dataElements3;
                            final int[] array12 = (int[])dataElements;
                            for (int n11 = 0; n11 < array11.length; ++n11) {
                                final int[] array13 = array11;
                                final int n12 = n11;
                                array13[n12] ^= (array10[n11] ^ array12[n11]);
                            }
                            break;
                        }
                        case 4: {
                            final float[] array14 = (float[])dataElements2;
                            final float[] array15 = (float[])dataElements3;
                            final float[] array16 = (float[])dataElements;
                            for (int n13 = 0; n13 < array15.length; ++n13) {
                                array15[n13] = Float.intBitsToFloat(Float.floatToIntBits(array15[n13]) ^ Float.floatToIntBits(array14[n13]) ^ Float.floatToIntBits(array16[n13]));
                            }
                            break;
                        }
                        case 5: {
                            final double[] array17 = (double[])dataElements2;
                            final double[] array18 = (double[])dataElements3;
                            final double[] array19 = (double[])dataElements;
                            for (int n14 = 0; n14 < array18.length; ++n14) {
                                array18[n14] = Double.longBitsToDouble(Double.doubleToLongBits(array18[n14]) ^ Double.doubleToLongBits(array17[n14]) ^ Double.doubleToLongBits(array19[n14]));
                            }
                            break;
                        }
                        default: {
                            throw new InternalError("Unsupported XOR pixel type");
                        }
                    }
                    writableRaster.setDataElements(j, i, dataElements3);
                }
                n7 += scanlineStride;
            }
        }
    }
}
