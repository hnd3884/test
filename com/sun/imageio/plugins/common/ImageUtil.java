package com.sun.imageio.plugins.common;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.IIOException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import java.awt.image.WritableRaster;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferByte;
import java.awt.Rectangle;
import java.awt.image.Raster;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.ComponentColorModel;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentSampleModel;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;

public class ImageUtil
{
    public static final ColorModel createColorModel(final SampleModel sampleModel) {
        if (sampleModel == null) {
            throw new IllegalArgumentException("sampleModel == null!");
        }
        final int dataType = sampleModel.getDataType();
        switch (dataType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5: {
                ColorModel colorModel = null;
                final int[] sampleSize = sampleModel.getSampleSize();
                if (sampleModel instanceof ComponentSampleModel) {
                    final int numBands = sampleModel.getNumBands();
                    ColorSpace colorSpace;
                    if (numBands <= 2) {
                        colorSpace = ColorSpace.getInstance(1003);
                    }
                    else if (numBands <= 4) {
                        colorSpace = ColorSpace.getInstance(1000);
                    }
                    else {
                        colorSpace = new BogusColorSpace(numBands);
                    }
                    final boolean b = numBands == 2 || numBands == 4;
                    colorModel = new ComponentColorModel(colorSpace, sampleSize, b, false, b ? 3 : 1, dataType);
                }
                else {
                    if (sampleModel.getNumBands() <= 4 && sampleModel instanceof SinglePixelPackedSampleModel) {
                        final int[] bitMasks = ((SinglePixelPackedSampleModel)sampleModel).getBitMasks();
                        int n = 0;
                        final int length = bitMasks.length;
                        int n4;
                        int n3;
                        int n2;
                        if (length <= 2) {
                            n2 = (n3 = (n4 = bitMasks[0]));
                            if (length == 2) {
                                n = bitMasks[1];
                            }
                        }
                        else {
                            n3 = bitMasks[0];
                            n2 = bitMasks[1];
                            n4 = bitMasks[2];
                            if (length == 4) {
                                n = bitMasks[3];
                            }
                        }
                        int n5 = 0;
                        for (int i = 0; i < sampleSize.length; ++i) {
                            n5 += sampleSize[i];
                        }
                        return new DirectColorModel(n5, n3, n2, n4, n);
                    }
                    if (sampleModel instanceof MultiPixelPackedSampleModel) {
                        final int n6 = sampleSize[0];
                        final int n7 = 1 << n6;
                        final byte[] array = new byte[n7];
                        for (int j = 0; j < n7; ++j) {
                            array[j] = (byte)(j * 255 / (n7 - 1));
                        }
                        colorModel = new IndexColorModel(n6, n7, array, array, array);
                    }
                }
                return colorModel;
            }
            default: {
                return null;
            }
        }
    }
    
    public static byte[] getPackedBinaryData(final Raster raster, final Rectangle rectangle) {
        final SampleModel sampleModel = raster.getSampleModel();
        if (!isBinary(sampleModel)) {
            throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
        }
        final int x = rectangle.x;
        final int y = rectangle.y;
        final int width = rectangle.width;
        final int height = rectangle.height;
        final DataBuffer dataBuffer = raster.getDataBuffer();
        final int n = x - raster.getSampleModelTranslateX();
        final int n2 = y - raster.getSampleModelTranslateY();
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
        final int scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
        int n3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, n2);
        final int bitOffset = multiPixelPackedSampleModel.getBitOffset(n);
        final int n4 = (width + 7) / 8;
        if (dataBuffer instanceof DataBufferByte && n3 == 0 && bitOffset == 0 && n4 == scanlineStride && ((DataBufferByte)dataBuffer).getData().length == n4 * height) {
            return ((DataBufferByte)dataBuffer).getData();
        }
        final byte[] array = new byte[n4 * height];
        int n5 = 0;
        if (bitOffset == 0) {
            if (dataBuffer instanceof DataBufferByte) {
                final byte[] data = ((DataBufferByte)dataBuffer).getData();
                final int n6 = n4;
                int n7 = 0;
                for (int i = 0; i < height; ++i) {
                    System.arraycopy(data, n3, array, n7, n6);
                    n7 += n6;
                    n3 += scanlineStride;
                }
            }
            else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
                final short[] array2 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                for (int j = 0; j < height; ++j) {
                    int k = width;
                    int n8 = n3;
                    while (k > 8) {
                        final short n9 = array2[n8++];
                        array[n5++] = (byte)(n9 >>> 8 & 0xFF);
                        array[n5++] = (byte)(n9 & 0xFF);
                        k -= 16;
                    }
                    if (k > 0) {
                        array[n5++] = (byte)(array2[n8] >>> 8 & 0xFF);
                    }
                    n3 += scanlineStride;
                }
            }
            else if (dataBuffer instanceof DataBufferInt) {
                final int[] data2 = ((DataBufferInt)dataBuffer).getData();
                for (int l = 0; l < height; ++l) {
                    int n10 = width;
                    int n11 = n3;
                    while (n10 > 24) {
                        final int n12 = data2[n11++];
                        array[n5++] = (byte)(n12 >>> 24 & 0xFF);
                        array[n5++] = (byte)(n12 >>> 16 & 0xFF);
                        array[n5++] = (byte)(n12 >>> 8 & 0xFF);
                        array[n5++] = (byte)(n12 & 0xFF);
                        n10 -= 32;
                    }
                    int n13 = 24;
                    while (n10 > 0) {
                        array[n5++] = (byte)(data2[n11] >>> n13 & 0xFF);
                        n13 -= 8;
                        n10 -= 8;
                    }
                    n3 += scanlineStride;
                }
            }
        }
        else if (dataBuffer instanceof DataBufferByte) {
            final byte[] data3 = ((DataBufferByte)dataBuffer).getData();
            if ((bitOffset & 0x7) == 0x0) {
                final int n14 = n4;
                int n15 = 0;
                for (int n16 = 0; n16 < height; ++n16) {
                    System.arraycopy(data3, n3, array, n15, n14);
                    n15 += n14;
                    n3 += scanlineStride;
                }
            }
            else {
                final int n17 = bitOffset & 0x7;
                final int n18 = 8 - n17;
                for (int n19 = 0; n19 < height; ++n19) {
                    int n20 = n3;
                    for (int n21 = width; n21 > 0; n21 -= 8) {
                        if (n21 > n18) {
                            array[n5++] = (byte)((data3[n20++] & 0xFF) << n17 | (data3[n20] & 0xFF) >>> n18);
                        }
                        else {
                            array[n5++] = (byte)((data3[n20] & 0xFF) << n17);
                        }
                    }
                    n3 += scanlineStride;
                }
            }
        }
        else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
            final short[] array3 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
            for (int n22 = 0; n22 < height; ++n22) {
                for (int n23 = bitOffset, n24 = 0; n24 < width; n24 += 8, n23 += 8) {
                    final int n25 = n3 + n23 / 16;
                    final int n26 = n23 % 16;
                    final int n27 = array3[n25] & 0xFFFF;
                    if (n26 <= 8) {
                        array[n5++] = (byte)(n27 >>> 8 - n26);
                    }
                    else {
                        final int n28 = n26 - 8;
                        array[n5++] = (byte)(n27 << n28 | (array3[n25 + 1] & 0xFFFF) >>> 16 - n28);
                    }
                }
                n3 += scanlineStride;
            }
        }
        else if (dataBuffer instanceof DataBufferInt) {
            final int[] data4 = ((DataBufferInt)dataBuffer).getData();
            for (int n29 = 0; n29 < height; ++n29) {
                for (int n30 = bitOffset, n31 = 0; n31 < width; n31 += 8, n30 += 8) {
                    final int n32 = n3 + n30 / 32;
                    final int n33 = n30 % 32;
                    final int n34 = data4[n32];
                    if (n33 <= 24) {
                        array[n5++] = (byte)(n34 >>> 24 - n33);
                    }
                    else {
                        final int n35 = n33 - 24;
                        array[n5++] = (byte)(n34 << n35 | data4[n32 + 1] >>> 32 - n35);
                    }
                }
                n3 += scanlineStride;
            }
        }
        return array;
    }
    
    public static byte[] getUnpackedBinaryData(final Raster raster, final Rectangle rectangle) {
        final SampleModel sampleModel = raster.getSampleModel();
        if (!isBinary(sampleModel)) {
            throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
        }
        final int x = rectangle.x;
        final int y = rectangle.y;
        final int width = rectangle.width;
        final int height = rectangle.height;
        final DataBuffer dataBuffer = raster.getDataBuffer();
        final int n = x - raster.getSampleModelTranslateX();
        final int n2 = y - raster.getSampleModelTranslateY();
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
        final int scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
        int n3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, n2);
        final int bitOffset = multiPixelPackedSampleModel.getBitOffset(n);
        final byte[] array = new byte[width * height];
        final int n4 = y + height;
        final int n5 = x + width;
        int n6 = 0;
        if (dataBuffer instanceof DataBufferByte) {
            final byte[] data = ((DataBufferByte)dataBuffer).getData();
            for (int i = y; i < n4; ++i) {
                int n7 = n3 * 8 + bitOffset;
                for (int j = x; j < n5; ++j) {
                    array[n6++] = (byte)(data[n7 / 8] >>> (7 - n7 & 0x7) & 0x1);
                    ++n7;
                }
                n3 += scanlineStride;
            }
        }
        else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
            final short[] array2 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
            for (int k = y; k < n4; ++k) {
                int n8 = n3 * 16 + bitOffset;
                for (int l = x; l < n5; ++l) {
                    array[n6++] = (byte)(array2[n8 / 16] >>> 15 - n8 % 16 & 0x1);
                    ++n8;
                }
                n3 += scanlineStride;
            }
        }
        else if (dataBuffer instanceof DataBufferInt) {
            final int[] data2 = ((DataBufferInt)dataBuffer).getData();
            for (int n9 = y; n9 < n4; ++n9) {
                int n10 = n3 * 32 + bitOffset;
                for (int n11 = x; n11 < n5; ++n11) {
                    array[n6++] = (byte)(data2[n10 / 32] >>> 31 - n10 % 32 & 0x1);
                    ++n10;
                }
                n3 += scanlineStride;
            }
        }
        return array;
    }
    
    public static void setPackedBinaryData(final byte[] array, final WritableRaster writableRaster, final Rectangle rectangle) {
        final SampleModel sampleModel = writableRaster.getSampleModel();
        if (!isBinary(sampleModel)) {
            throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
        }
        final int x = rectangle.x;
        final int y = rectangle.y;
        final int width = rectangle.width;
        final int height = rectangle.height;
        final DataBuffer dataBuffer = writableRaster.getDataBuffer();
        final int n = x - writableRaster.getSampleModelTranslateX();
        final int n2 = y - writableRaster.getSampleModelTranslateY();
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
        final int scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
        int n3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, n2);
        final int bitOffset = multiPixelPackedSampleModel.getBitOffset(n);
        int n4 = 0;
        if (bitOffset == 0) {
            if (dataBuffer instanceof DataBufferByte) {
                final byte[] data = ((DataBufferByte)dataBuffer).getData();
                if (data == array) {
                    return;
                }
                final int n5 = (width + 7) / 8;
                int n6 = 0;
                for (int i = 0; i < height; ++i) {
                    System.arraycopy(array, n6, data, n3, n5);
                    n6 += n5;
                    n3 += scanlineStride;
                }
            }
            else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
                final short[] array2 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                for (int j = 0; j < height; ++j) {
                    int k = width;
                    int n7 = n3;
                    while (k > 8) {
                        array2[n7++] = (short)((array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF));
                        k -= 16;
                    }
                    if (k > 0) {
                        array2[n7++] = (short)((array[n4++] & 0xFF) << 8);
                    }
                    n3 += scanlineStride;
                }
            }
            else if (dataBuffer instanceof DataBufferInt) {
                final int[] data2 = ((DataBufferInt)dataBuffer).getData();
                for (int l = 0; l < height; ++l) {
                    int n8 = width;
                    int n9 = n3;
                    while (n8 > 24) {
                        data2[n9++] = ((array[n4++] & 0xFF) << 24 | (array[n4++] & 0xFF) << 16 | (array[n4++] & 0xFF) << 8 | (array[n4++] & 0xFF));
                        n8 -= 32;
                    }
                    int n10 = 24;
                    while (n8 > 0) {
                        final int[] array3 = data2;
                        final int n11 = n9;
                        array3[n11] |= (array[n4++] & 0xFF) << n10;
                        n10 -= 8;
                        n8 -= 8;
                    }
                    n3 += scanlineStride;
                }
            }
        }
        else {
            final int n12 = (width + 7) / 8;
            int n13 = 0;
            if (dataBuffer instanceof DataBufferByte) {
                final byte[] data3 = ((DataBufferByte)dataBuffer).getData();
                if ((bitOffset & 0x7) == 0x0) {
                    for (int n14 = 0; n14 < height; ++n14) {
                        System.arraycopy(array, n13, data3, n3, n12);
                        n13 += n12;
                        n3 += scanlineStride;
                    }
                }
                else {
                    final int n15 = bitOffset & 0x7;
                    final int n16 = 8 - n15;
                    final int n17 = 8 + n16;
                    final byte b = (byte)(255 << n16);
                    final byte b2 = (byte)~b;
                    for (int n18 = 0; n18 < height; ++n18) {
                        int n19 = n3;
                        for (int n20 = width; n20 > 0; n20 -= 8) {
                            final byte b3 = array[n4++];
                            if (n20 > n17) {
                                data3[n19] = (byte)((data3[n19] & b) | (b3 & 0xFF) >>> n15);
                                data3[++n19] = (byte)((b3 & 0xFF) << n16);
                            }
                            else if (n20 > n16) {
                                data3[n19] = (byte)((data3[n19] & b) | (b3 & 0xFF) >>> n15);
                                ++n19;
                                data3[n19] = (byte)((data3[n19] & b2) | (b3 & 0xFF) << n16);
                            }
                            else {
                                final int n21 = (1 << n16 - n20) - 1;
                                data3[n19] = (byte)((data3[n19] & (b | n21)) | ((b3 & 0xFF) >>> n15 & ~n21));
                            }
                        }
                        n3 += scanlineStride;
                    }
                }
            }
            else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
                final short[] array4 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
                final int n22 = bitOffset & 0x7;
                final int n23 = 8 - n22;
                final int n24 = 16 + n23;
                final short n25 = (short)~(255 << n23);
                final short n26 = (short)(65535 << n23);
                final short n27 = (short)~n26;
                for (int n28 = 0; n28 < height; ++n28) {
                    for (int n29 = bitOffset, n30 = width, n31 = 0; n31 < width; n31 += 8, n29 += 8, n30 -= 8) {
                        int n32 = n3 + (n29 >> 4);
                        final int n33 = n29 & 0xF;
                        int n34 = array[n4++] & 0xFF;
                        if (n33 <= 8) {
                            if (n30 < 8) {
                                n34 &= 255 << 8 - n30;
                            }
                            array4[n32] = (short)((array4[n32] & n25) | n34 << n23);
                        }
                        else if (n30 > n24) {
                            array4[n32] = (short)((array4[n32] & n26) | (n34 >>> n22 & 0xFFFF));
                            array4[++n32] = (short)(n34 << n23 & 0xFFFF);
                        }
                        else if (n30 > n23) {
                            array4[n32] = (short)((array4[n32] & n26) | (n34 >>> n22 & 0xFFFF));
                            ++n32;
                            array4[n32] = (short)((array4[n32] & n27) | (n34 << n23 & 0xFFFF));
                        }
                        else {
                            final int n35 = (1 << n23 - n30) - 1;
                            array4[n32] = (short)((array4[n32] & (n26 | n35)) | (n34 >>> n22 & 0xFFFF & ~n35));
                        }
                    }
                    n3 += scanlineStride;
                }
            }
            else if (dataBuffer instanceof DataBufferInt) {
                final int[] data4 = ((DataBufferInt)dataBuffer).getData();
                final int n36 = bitOffset & 0x7;
                final int n37 = 8 - n36;
                final int n38 = 32 + n37;
                final int n39 = -1 << n37;
                final int n40 = ~n39;
                for (int n41 = 0; n41 < height; ++n41) {
                    for (int n42 = bitOffset, n43 = width, n44 = 0; n44 < width; n44 += 8, n42 += 8, n43 -= 8) {
                        int n45 = n3 + (n42 >> 5);
                        final int n46 = n42 & 0x1F;
                        int n47 = array[n4++] & 0xFF;
                        if (n46 <= 24) {
                            final int n48 = 24 - n46;
                            if (n43 < 8) {
                                n47 &= 255 << 8 - n43;
                            }
                            data4[n45] = ((data4[n45] & ~(255 << n48)) | n47 << n48);
                        }
                        else if (n43 > n38) {
                            data4[n45] = ((data4[n45] & n39) | n47 >>> n36);
                            data4[++n45] = n47 << n37;
                        }
                        else if (n43 > n37) {
                            data4[n45] = ((data4[n45] & n39) | n47 >>> n36);
                            ++n45;
                            data4[n45] = ((data4[n45] & n40) | n47 << n37);
                        }
                        else {
                            final int n49 = (1 << n37 - n43) - 1;
                            data4[n45] = ((data4[n45] & (n39 | n49)) | (n47 >>> n36 & ~n49));
                        }
                    }
                    n3 += scanlineStride;
                }
            }
        }
    }
    
    public static void setUnpackedBinaryData(final byte[] array, final WritableRaster writableRaster, final Rectangle rectangle) {
        final SampleModel sampleModel = writableRaster.getSampleModel();
        if (!isBinary(sampleModel)) {
            throw new IllegalArgumentException(I18N.getString("ImageUtil0"));
        }
        final int x = rectangle.x;
        final int y = rectangle.y;
        final int width = rectangle.width;
        final int height = rectangle.height;
        final DataBuffer dataBuffer = writableRaster.getDataBuffer();
        final int n = x - writableRaster.getSampleModelTranslateX();
        final int n2 = y - writableRaster.getSampleModelTranslateY();
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
        final int scanlineStride = multiPixelPackedSampleModel.getScanlineStride();
        int n3 = dataBuffer.getOffset() + multiPixelPackedSampleModel.getOffset(n, n2);
        final int bitOffset = multiPixelPackedSampleModel.getBitOffset(n);
        int n4 = 0;
        if (dataBuffer instanceof DataBufferByte) {
            final byte[] data = ((DataBufferByte)dataBuffer).getData();
            for (int i = 0; i < height; ++i) {
                int n5 = n3 * 8 + bitOffset;
                for (int j = 0; j < width; ++j) {
                    if (array[n4++] != 0) {
                        final byte[] array2 = data;
                        final int n6 = n5 / 8;
                        array2[n6] |= (byte)(1 << (7 - n5 & 0x7));
                    }
                    ++n5;
                }
                n3 += scanlineStride;
            }
        }
        else if (dataBuffer instanceof DataBufferShort || dataBuffer instanceof DataBufferUShort) {
            final short[] array3 = (dataBuffer instanceof DataBufferShort) ? ((DataBufferShort)dataBuffer).getData() : ((DataBufferUShort)dataBuffer).getData();
            for (int k = 0; k < height; ++k) {
                int n7 = n3 * 16 + bitOffset;
                for (int l = 0; l < width; ++l) {
                    if (array[n4++] != 0) {
                        final short[] array4 = array3;
                        final int n8 = n7 / 16;
                        array4[n8] |= (short)(1 << 15 - n7 % 16);
                    }
                    ++n7;
                }
                n3 += scanlineStride;
            }
        }
        else if (dataBuffer instanceof DataBufferInt) {
            final int[] data2 = ((DataBufferInt)dataBuffer).getData();
            for (int n9 = 0; n9 < height; ++n9) {
                int n10 = n3 * 32 + bitOffset;
                for (int n11 = 0; n11 < width; ++n11) {
                    if (array[n4++] != 0) {
                        final int[] array5 = data2;
                        final int n12 = n10 / 32;
                        array5[n12] |= 1 << 31 - n10 % 32;
                    }
                    ++n10;
                }
                n3 += scanlineStride;
            }
        }
    }
    
    public static boolean isBinary(final SampleModel sampleModel) {
        return sampleModel instanceof MultiPixelPackedSampleModel && ((MultiPixelPackedSampleModel)sampleModel).getPixelBitStride() == 1 && sampleModel.getNumBands() == 1;
    }
    
    public static ColorModel createColorModel(ColorSpace instance, final SampleModel sampleModel) {
        ColorModel colorModel = null;
        if (sampleModel == null) {
            throw new IllegalArgumentException(I18N.getString("ImageUtil1"));
        }
        final int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return null;
        }
        final int dataType = sampleModel.getDataType();
        if (sampleModel instanceof ComponentSampleModel) {
            if (dataType < 0 || dataType > 5) {
                return null;
            }
            if (instance == null) {
                instance = ((numBands <= 2) ? ColorSpace.getInstance(1003) : ColorSpace.getInstance(1000));
            }
            final boolean b = numBands == 2 || numBands == 4;
            final int n = b ? 3 : 1;
            final boolean b2 = false;
            final int dataTypeSize = DataBuffer.getDataTypeSize(dataType);
            final int[] array = new int[numBands];
            for (int i = 0; i < numBands; ++i) {
                array[i] = dataTypeSize;
            }
            colorModel = new ComponentColorModel(instance, array, b, b2, n, dataType);
        }
        else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            final int[] bitMasks = singlePixelPackedSampleModel.getBitMasks();
            int n2 = 0;
            final int length = bitMasks.length;
            int n5;
            int n4;
            int n3;
            if (length <= 2) {
                n3 = (n4 = (n5 = bitMasks[0]));
                if (length == 2) {
                    n2 = bitMasks[1];
                }
            }
            else {
                n4 = bitMasks[0];
                n3 = bitMasks[1];
                n5 = bitMasks[2];
                if (length == 4) {
                    n2 = bitMasks[3];
                }
            }
            final int[] sampleSize = singlePixelPackedSampleModel.getSampleSize();
            int n6 = 0;
            for (int j = 0; j < sampleSize.length; ++j) {
                n6 += sampleSize[j];
            }
            if (instance == null) {
                instance = ColorSpace.getInstance(1000);
            }
            colorModel = new DirectColorModel(instance, n6, n4, n3, n5, n2, false, sampleModel.getDataType());
        }
        else if (sampleModel instanceof MultiPixelPackedSampleModel) {
            final int pixelBitStride = ((MultiPixelPackedSampleModel)sampleModel).getPixelBitStride();
            final int n7 = 1 << pixelBitStride;
            final byte[] array2 = new byte[n7];
            for (int k = 0; k < n7; ++k) {
                array2[k] = (byte)(255 * k / (n7 - 1));
            }
            colorModel = new IndexColorModel(pixelBitStride, n7, array2, array2, array2);
        }
        return colorModel;
    }
    
    public static int getElementSize(final SampleModel sampleModel) {
        final int dataTypeSize = DataBuffer.getDataTypeSize(sampleModel.getDataType());
        if (sampleModel instanceof MultiPixelPackedSampleModel) {
            final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
            return multiPixelPackedSampleModel.getSampleSize(0) * multiPixelPackedSampleModel.getNumBands();
        }
        if (sampleModel instanceof ComponentSampleModel) {
            return sampleModel.getNumBands() * dataTypeSize;
        }
        if (sampleModel instanceof SinglePixelPackedSampleModel) {
            return dataTypeSize;
        }
        return dataTypeSize * sampleModel.getNumBands();
    }
    
    public static long getTileSize(final SampleModel sampleModel) {
        final int dataTypeSize = DataBuffer.getDataTypeSize(sampleModel.getDataType());
        if (sampleModel instanceof MultiPixelPackedSampleModel) {
            final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)sampleModel;
            return (multiPixelPackedSampleModel.getScanlineStride() * multiPixelPackedSampleModel.getHeight() + (multiPixelPackedSampleModel.getDataBitOffset() + dataTypeSize - 1) / dataTypeSize) * ((dataTypeSize + 7) / 8);
        }
        if (sampleModel instanceof ComponentSampleModel) {
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
            final int[] bandOffsets = componentSampleModel.getBandOffsets();
            int max = bandOffsets[0];
            for (int i = 1; i < bandOffsets.length; ++i) {
                max = Math.max(max, bandOffsets[i]);
            }
            long n = 0L;
            final int pixelStride = componentSampleModel.getPixelStride();
            final int scanlineStride = componentSampleModel.getScanlineStride();
            if (max >= 0) {
                n += max + 1;
            }
            if (pixelStride > 0) {
                n += pixelStride * (sampleModel.getWidth() - 1);
            }
            if (scanlineStride > 0) {
                n += scanlineStride * (sampleModel.getHeight() - 1);
            }
            final int[] bankIndices = componentSampleModel.getBankIndices();
            int max2 = bankIndices[0];
            for (int j = 1; j < bankIndices.length; ++j) {
                max2 = Math.max(max2, bankIndices[j]);
            }
            return n * (max2 + 1) * ((dataTypeSize + 7) / 8);
        }
        if (sampleModel instanceof SinglePixelPackedSampleModel) {
            final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
            return (singlePixelPackedSampleModel.getScanlineStride() * (singlePixelPackedSampleModel.getHeight() - 1) + singlePixelPackedSampleModel.getWidth()) * (long)((dataTypeSize + 7) / 8);
        }
        return 0L;
    }
    
    public static long getBandSize(final SampleModel sampleModel) {
        final int dataTypeSize = DataBuffer.getDataTypeSize(sampleModel.getDataType());
        if (sampleModel instanceof ComponentSampleModel) {
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
            final int pixelStride = componentSampleModel.getPixelStride();
            final int scanlineStride = componentSampleModel.getScanlineStride();
            long n = Math.min(pixelStride, scanlineStride);
            if (pixelStride > 0) {
                n += pixelStride * (sampleModel.getWidth() - 1);
            }
            if (scanlineStride > 0) {
                n += scanlineStride * (sampleModel.getHeight() - 1);
            }
            return n * ((dataTypeSize + 7) / 8);
        }
        return getTileSize(sampleModel);
    }
    
    public static boolean isIndicesForGrayscale(final byte[] array, final byte[] array2, final byte[] array3) {
        if (array.length != array2.length || array.length != array3.length) {
            return false;
        }
        final int length = array.length;
        if (length != 256) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final byte b = (byte)i;
            if (array[i] != b || array2[i] != b || array3[i] != b) {
                return false;
            }
        }
        return true;
    }
    
    public static String convertObjectToString(final Object o) {
        if (o == null) {
            return "";
        }
        String s = "";
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            for (int i = 0; i < array.length; ++i) {
                s = s + array[i] + " ";
            }
            return s;
        }
        if (o instanceof int[]) {
            final int[] array2 = (int[])o;
            for (int j = 0; j < array2.length; ++j) {
                s = s + array2[j] + " ";
            }
            return s;
        }
        if (o instanceof short[]) {
            final short[] array3 = (short[])o;
            for (int k = 0; k < array3.length; ++k) {
                s = s + array3[k] + " ";
            }
            return s;
        }
        return o.toString();
    }
    
    public static final void canEncodeImage(final ImageWriter imageWriter, final ImageTypeSpecifier imageTypeSpecifier) throws IIOException {
        final ImageWriterSpi originatingProvider = imageWriter.getOriginatingProvider();
        if (imageTypeSpecifier != null && originatingProvider != null && !originatingProvider.canEncodeImage(imageTypeSpecifier)) {
            throw new IIOException(I18N.getString("ImageUtil2") + " " + imageWriter.getClass().getName());
        }
    }
    
    public static final void canEncodeImage(final ImageWriter imageWriter, final ColorModel colorModel, final SampleModel sampleModel) throws IIOException {
        ImageTypeSpecifier imageTypeSpecifier = null;
        if (colorModel != null && sampleModel != null) {
            imageTypeSpecifier = new ImageTypeSpecifier(colorModel, sampleModel);
        }
        canEncodeImage(imageWriter, imageTypeSpecifier);
    }
    
    public static final boolean imageIsContiguous(final RenderedImage renderedImage) {
        SampleModel sampleModel;
        if (renderedImage instanceof BufferedImage) {
            sampleModel = ((BufferedImage)renderedImage).getRaster().getSampleModel();
        }
        else {
            sampleModel = renderedImage.getSampleModel();
        }
        if (!(sampleModel instanceof ComponentSampleModel)) {
            return isBinary(sampleModel);
        }
        final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel;
        if (componentSampleModel.getPixelStride() != componentSampleModel.getNumBands()) {
            return false;
        }
        final int[] bandOffsets = componentSampleModel.getBandOffsets();
        for (int i = 0; i < bandOffsets.length; ++i) {
            if (bandOffsets[i] != i) {
                return false;
            }
        }
        final int[] bankIndices = componentSampleModel.getBankIndices();
        for (int j = 0; j < bandOffsets.length; ++j) {
            if (bankIndices[j] != 0) {
                return false;
            }
        }
        return true;
    }
}
