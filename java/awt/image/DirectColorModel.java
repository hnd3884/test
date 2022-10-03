package java.awt.image;

import java.util.Arrays;
import java.awt.Point;
import java.awt.color.ColorSpace;

public class DirectColorModel extends PackedColorModel
{
    private int red_mask;
    private int green_mask;
    private int blue_mask;
    private int alpha_mask;
    private int red_offset;
    private int green_offset;
    private int blue_offset;
    private int alpha_offset;
    private int red_scale;
    private int green_scale;
    private int blue_scale;
    private int alpha_scale;
    private boolean is_LinearRGB;
    private int lRGBprecision;
    private byte[] tosRGB8LUT;
    private byte[] fromsRGB8LUT8;
    private short[] fromsRGB8LUT16;
    
    public DirectColorModel(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, 0);
    }
    
    public DirectColorModel(final int n, final int n2, final int n3, final int n4, final int n5) {
        super(ColorSpace.getInstance(1000), n, n2, n3, n4, n5, false, (n5 == 0) ? 1 : 3, ColorModel.getDefaultTransferType(n));
        this.setFields();
    }
    
    public DirectColorModel(final ColorSpace colorSpace, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final int n6) {
        super(colorSpace, n, n2, n3, n4, n5, b, (n5 == 0) ? 1 : 3, n6);
        if (ColorModel.isLinearRGBspace(this.colorSpace)) {
            this.is_LinearRGB = true;
            if (this.maxBits <= 8) {
                this.lRGBprecision = 8;
                this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
                this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
            }
            else {
                this.lRGBprecision = 16;
                this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
                this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
            }
        }
        else if (!this.is_sRGB) {
            for (int i = 0; i < 3; ++i) {
                if (colorSpace.getMinValue(i) != 0.0f || colorSpace.getMaxValue(i) != 1.0f) {
                    throw new IllegalArgumentException("Illegal min/max RGB component value");
                }
            }
        }
        this.setFields();
    }
    
    public final int getRedMask() {
        return this.maskArray[0];
    }
    
    public final int getGreenMask() {
        return this.maskArray[1];
    }
    
    public final int getBlueMask() {
        return this.maskArray[2];
    }
    
    public final int getAlphaMask() {
        if (this.supportsAlpha) {
            return this.maskArray[3];
        }
        return 0;
    }
    
    private float[] getDefaultRGBComponents(final int n) {
        return this.colorSpace.toRGB(this.getNormalizedComponents(this.getComponents(n, null, 0), 0, null, 0));
    }
    
    private int getsRGBComponentFromsRGB(final int n, final int n2) {
        int n3 = (n & this.maskArray[n2]) >>> this.maskOffsets[n2];
        if (this.isAlphaPremultiplied) {
            final int n4 = (n & this.maskArray[3]) >>> this.maskOffsets[3];
            n3 = ((n4 == 0) ? 0 : ((int)(n3 * this.scaleFactors[n2] * 255.0f / (n4 * this.scaleFactors[3]) + 0.5f)));
        }
        else if (this.scaleFactors[n2] != 1.0f) {
            n3 = (int)(n3 * this.scaleFactors[n2] + 0.5f);
        }
        return n3;
    }
    
    private int getsRGBComponentFromLinearRGB(final int n, final int n2) {
        int n3 = (n & this.maskArray[n2]) >>> this.maskOffsets[n2];
        if (this.isAlphaPremultiplied) {
            final float n4 = (float)((1 << this.lRGBprecision) - 1);
            final int n5 = (n & this.maskArray[3]) >>> this.maskOffsets[3];
            n3 = ((n5 == 0) ? 0 : ((int)(n3 * this.scaleFactors[n2] * n4 / (n5 * this.scaleFactors[3]) + 0.5f)));
        }
        else if (this.nBits[n2] != this.lRGBprecision) {
            if (this.lRGBprecision == 16) {
                n3 = (int)(n3 * this.scaleFactors[n2] * 257.0f + 0.5f);
            }
            else {
                n3 = (int)(n3 * this.scaleFactors[n2] + 0.5f);
            }
        }
        return this.tosRGB8LUT[n3] & 0xFF;
    }
    
    @Override
    public final int getRed(final int n) {
        if (this.is_sRGB) {
            return this.getsRGBComponentFromsRGB(n, 0);
        }
        if (this.is_LinearRGB) {
            return this.getsRGBComponentFromLinearRGB(n, 0);
        }
        return (int)(this.getDefaultRGBComponents(n)[0] * 255.0f + 0.5f);
    }
    
    @Override
    public final int getGreen(final int n) {
        if (this.is_sRGB) {
            return this.getsRGBComponentFromsRGB(n, 1);
        }
        if (this.is_LinearRGB) {
            return this.getsRGBComponentFromLinearRGB(n, 1);
        }
        return (int)(this.getDefaultRGBComponents(n)[1] * 255.0f + 0.5f);
    }
    
    @Override
    public final int getBlue(final int n) {
        if (this.is_sRGB) {
            return this.getsRGBComponentFromsRGB(n, 2);
        }
        if (this.is_LinearRGB) {
            return this.getsRGBComponentFromLinearRGB(n, 2);
        }
        return (int)(this.getDefaultRGBComponents(n)[2] * 255.0f + 0.5f);
    }
    
    @Override
    public final int getAlpha(final int n) {
        if (!this.supportsAlpha) {
            return 255;
        }
        int n2 = (n & this.maskArray[3]) >>> this.maskOffsets[3];
        if (this.scaleFactors[3] != 1.0f) {
            n2 = (int)(n2 * this.scaleFactors[3] + 0.5f);
        }
        return n2;
    }
    
    @Override
    public final int getRGB(final int n) {
        if (this.is_sRGB || this.is_LinearRGB) {
            return this.getAlpha(n) << 24 | this.getRed(n) << 16 | this.getGreen(n) << 8 | this.getBlue(n) << 0;
        }
        final float[] defaultRGBComponents = this.getDefaultRGBComponents(n);
        return this.getAlpha(n) << 24 | (int)(defaultRGBComponents[0] * 255.0f + 0.5f) << 16 | (int)(defaultRGBComponents[1] * 255.0f + 0.5f) << 8 | (int)(defaultRGBComponents[2] * 255.0f + 0.5f) << 0;
    }
    
    @Override
    public int getRed(final Object o) {
        int n = 0;
        switch (this.transferType) {
            case 0: {
                n = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getRed(n);
    }
    
    @Override
    public int getGreen(final Object o) {
        int n = 0;
        switch (this.transferType) {
            case 0: {
                n = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getGreen(n);
    }
    
    @Override
    public int getBlue(final Object o) {
        int n = 0;
        switch (this.transferType) {
            case 0: {
                n = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getBlue(n);
    }
    
    @Override
    public int getAlpha(final Object o) {
        int n = 0;
        switch (this.transferType) {
            case 0: {
                n = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getAlpha(n);
    }
    
    @Override
    public int getRGB(final Object o) {
        int n = 0;
        switch (this.transferType) {
            case 0: {
                n = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getRGB(n);
    }
    
    @Override
    public Object getDataElements(final int n, final Object o) {
        int[] array;
        if (this.transferType == 3 && o != null) {
            array = (int[])o;
            array[0] = 0;
        }
        else {
            array = new int[] { 0 };
        }
        final ColorModel rgBdefault = ColorModel.getRGBdefault();
        if (this == rgBdefault || this.equals(rgBdefault)) {
            array[0] = n;
            return array;
        }
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        if (this.is_sRGB || this.is_LinearRGB) {
            int n5;
            float n6;
            if (this.is_LinearRGB) {
                if (this.lRGBprecision == 8) {
                    n2 = (this.fromsRGB8LUT8[n2] & 0xFF);
                    n3 = (this.fromsRGB8LUT8[n3] & 0xFF);
                    n4 = (this.fromsRGB8LUT8[n4] & 0xFF);
                    n5 = 8;
                    n6 = 0.003921569f;
                }
                else {
                    n2 = (this.fromsRGB8LUT16[n2] & 0xFFFF);
                    n3 = (this.fromsRGB8LUT16[n3] & 0xFFFF);
                    n4 = (this.fromsRGB8LUT16[n4] & 0xFFFF);
                    n5 = 16;
                    n6 = 1.5259022E-5f;
                }
            }
            else {
                n5 = 8;
                n6 = 0.003921569f;
            }
            if (this.supportsAlpha) {
                int n7 = n >> 24 & 0xFF;
                if (this.isAlphaPremultiplied) {
                    n6 *= n7 * 0.003921569f;
                    n5 = -1;
                }
                if (this.nBits[3] != 8) {
                    n7 = (int)(n7 * 0.003921569f * ((1 << this.nBits[3]) - 1) + 0.5f);
                    if (n7 > (1 << this.nBits[3]) - 1) {
                        n7 = (1 << this.nBits[3]) - 1;
                    }
                }
                array[0] = n7 << this.maskOffsets[3];
            }
            if (this.nBits[0] != n5) {
                n2 = (int)(n2 * n6 * ((1 << this.nBits[0]) - 1) + 0.5f);
            }
            if (this.nBits[1] != n5) {
                n3 = (int)(n3 * n6 * ((1 << this.nBits[1]) - 1) + 0.5f);
            }
            if (this.nBits[2] != n5) {
                n4 = (int)(n4 * n6 * ((1 << this.nBits[2]) - 1) + 0.5f);
            }
        }
        else {
            final float[] array2 = new float[3];
            final float n8 = 0.003921569f;
            array2[0] = n2 * n8;
            array2[1] = n3 * n8;
            array2[2] = n4 * n8;
            final float[] fromRGB = this.colorSpace.fromRGB(array2);
            if (this.supportsAlpha) {
                int n9 = n >> 24 & 0xFF;
                if (this.isAlphaPremultiplied) {
                    final float n10 = n8 * n9;
                    for (int i = 0; i < 3; ++i) {
                        final float[] array3 = fromRGB;
                        final int n11 = i;
                        array3[n11] *= n10;
                    }
                }
                if (this.nBits[3] != 8) {
                    n9 = (int)(n9 * 0.003921569f * ((1 << this.nBits[3]) - 1) + 0.5f);
                    if (n9 > (1 << this.nBits[3]) - 1) {
                        n9 = (1 << this.nBits[3]) - 1;
                    }
                }
                array[0] = n9 << this.maskOffsets[3];
            }
            n2 = (int)(fromRGB[0] * ((1 << this.nBits[0]) - 1) + 0.5f);
            n3 = (int)(fromRGB[1] * ((1 << this.nBits[1]) - 1) + 0.5f);
            n4 = (int)(fromRGB[2] * ((1 << this.nBits[2]) - 1) + 0.5f);
        }
        if (this.maxBits > 23) {
            if (n2 > (1 << this.nBits[0]) - 1) {
                n2 = (1 << this.nBits[0]) - 1;
            }
            if (n3 > (1 << this.nBits[1]) - 1) {
                n3 = (1 << this.nBits[1]) - 1;
            }
            if (n4 > (1 << this.nBits[2]) - 1) {
                n4 = (1 << this.nBits[2]) - 1;
            }
        }
        final int[] array4 = array;
        final int n12 = 0;
        array4[n12] |= (n2 << this.maskOffsets[0] | n3 << this.maskOffsets[1] | n4 << this.maskOffsets[2]);
        switch (this.transferType) {
            case 0: {
                byte[] array5;
                if (o == null) {
                    array5 = new byte[] { 0 };
                }
                else {
                    array5 = (byte[])o;
                }
                array5[0] = (byte)(0xFF & array[0]);
                return array5;
            }
            case 1: {
                short[] array6;
                if (o == null) {
                    array6 = new short[] { 0 };
                }
                else {
                    array6 = (short[])o;
                }
                array6[0] = (short)(array[0] & 0xFFFF);
                return array6;
            }
            case 3: {
                return array;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public final int[] getComponents(final int n, int[] array, final int n2) {
        if (array == null) {
            array = new int[n2 + this.numComponents];
        }
        for (int i = 0; i < this.numComponents; ++i) {
            array[n2 + i] = (n & this.maskArray[i]) >>> this.maskOffsets[i];
        }
        return array;
    }
    
    @Override
    public final int[] getComponents(final Object o, final int[] array, final int n) {
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                n2 = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n2 = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n2 = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getComponents(n2, array, n);
    }
    
    @Override
    public final WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        if (n <= 0 || n2 <= 0) {
            throw new IllegalArgumentException("Width (" + n + ") and height (" + n2 + ") cannot be <= 0");
        }
        int[] array;
        if (this.supportsAlpha) {
            array = new int[] { 0, 0, 0, this.alpha_mask };
        }
        else {
            array = new int[3];
        }
        array[0] = this.red_mask;
        array[1] = this.green_mask;
        array[2] = this.blue_mask;
        if (this.pixel_bits > 16) {
            return Raster.createPackedRaster(3, n, n2, array, null);
        }
        if (this.pixel_bits > 8) {
            return Raster.createPackedRaster(1, n, n2, array, null);
        }
        return Raster.createPackedRaster(0, n, n2, array, null);
    }
    
    @Override
    public int getDataElement(final int[] array, final int n) {
        int n2 = 0;
        for (int i = 0; i < this.numComponents; ++i) {
            n2 |= (array[n + i] << this.maskOffsets[i] & this.maskArray[i]);
        }
        return n2;
    }
    
    @Override
    public Object getDataElements(final int[] array, final int n, final Object o) {
        int n2 = 0;
        for (int i = 0; i < this.numComponents; ++i) {
            n2 |= (array[n + i] << this.maskOffsets[i] & this.maskArray[i]);
        }
        switch (this.transferType) {
            case 0: {
                if (o instanceof byte[]) {
                    final byte[] array2 = (byte[])o;
                    array2[0] = (byte)(n2 & 0xFF);
                    return array2;
                }
                return new byte[] { (byte)(n2 & 0xFF) };
            }
            case 1: {
                if (o instanceof short[]) {
                    final short[] array3 = (short[])o;
                    array3[0] = (short)(n2 & 0xFFFF);
                    return array3;
                }
                return new short[] { (short)(n2 & 0xFFFF) };
            }
            case 3: {
                if (o instanceof int[]) {
                    final int[] array4 = (int[])o;
                    array4[0] = n2;
                    return array4;
                }
                return new int[] { n2 };
            }
            default: {
                throw new ClassCastException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public final ColorModel coerceData(final WritableRaster writableRaster, final boolean b) {
        if (!this.supportsAlpha || this.isAlphaPremultiplied() == b) {
            return this;
        }
        final int width = writableRaster.getWidth();
        final int height = writableRaster.getHeight();
        final int numColorComponents = this.numColorComponents;
        final float n = 1.0f / ((1 << this.nBits[numColorComponents]) - 1);
        final int minX = writableRaster.getMinX();
        int minY = writableRaster.getMinY();
        int[] array = null;
        int[] array2 = null;
        if (b) {
            switch (this.transferType) {
                case 0: {
                    for (int i = 0; i < height; ++i, ++minY) {
                        for (int n2 = minX, j = 0; j < width; ++j, ++n2) {
                            array = writableRaster.getPixel(n2, minY, array);
                            final float n3 = array[numColorComponents] * n;
                            if (n3 != 0.0f) {
                                for (int k = 0; k < numColorComponents; ++k) {
                                    array[k] = (int)(array[k] * n3 + 0.5f);
                                }
                                writableRaster.setPixel(n2, minY, array);
                            }
                            else {
                                if (array2 == null) {
                                    array2 = new int[this.numComponents];
                                    Arrays.fill(array2, 0);
                                }
                                writableRaster.setPixel(n2, minY, array2);
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    for (int l = 0; l < height; ++l, ++minY) {
                        for (int n4 = minX, n5 = 0; n5 < width; ++n5, ++n4) {
                            array = writableRaster.getPixel(n4, minY, array);
                            final float n6 = array[numColorComponents] * n;
                            if (n6 != 0.0f) {
                                for (int n7 = 0; n7 < numColorComponents; ++n7) {
                                    array[n7] = (int)(array[n7] * n6 + 0.5f);
                                }
                                writableRaster.setPixel(n4, minY, array);
                            }
                            else {
                                if (array2 == null) {
                                    array2 = new int[this.numComponents];
                                    Arrays.fill(array2, 0);
                                }
                                writableRaster.setPixel(n4, minY, array2);
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    for (int n8 = 0; n8 < height; ++n8, ++minY) {
                        for (int n9 = minX, n10 = 0; n10 < width; ++n10, ++n9) {
                            array = writableRaster.getPixel(n9, minY, array);
                            final float n11 = array[numColorComponents] * n;
                            if (n11 != 0.0f) {
                                for (int n12 = 0; n12 < numColorComponents; ++n12) {
                                    array[n12] = (int)(array[n12] * n11 + 0.5f);
                                }
                                writableRaster.setPixel(n9, minY, array);
                            }
                            else {
                                if (array2 == null) {
                                    array2 = new int[this.numComponents];
                                    Arrays.fill(array2, 0);
                                }
                                writableRaster.setPixel(n9, minY, array2);
                            }
                        }
                    }
                    break;
                }
                default: {
                    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
                }
            }
        }
        else {
            switch (this.transferType) {
                case 0: {
                    for (int n13 = 0; n13 < height; ++n13, ++minY) {
                        for (int n14 = minX, n15 = 0; n15 < width; ++n15, ++n14) {
                            array = writableRaster.getPixel(n14, minY, array);
                            final float n16 = array[numColorComponents] * n;
                            if (n16 != 0.0f) {
                                final float n17 = 1.0f / n16;
                                for (int n18 = 0; n18 < numColorComponents; ++n18) {
                                    array[n18] = (int)(array[n18] * n17 + 0.5f);
                                }
                                writableRaster.setPixel(n14, minY, array);
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    for (int n19 = 0; n19 < height; ++n19, ++minY) {
                        for (int n20 = minX, n21 = 0; n21 < width; ++n21, ++n20) {
                            array = writableRaster.getPixel(n20, minY, array);
                            final float n22 = array[numColorComponents] * n;
                            if (n22 != 0.0f) {
                                final float n23 = 1.0f / n22;
                                for (int n24 = 0; n24 < numColorComponents; ++n24) {
                                    array[n24] = (int)(array[n24] * n23 + 0.5f);
                                }
                                writableRaster.setPixel(n20, minY, array);
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    for (int n25 = 0; n25 < height; ++n25, ++minY) {
                        for (int n26 = minX, n27 = 0; n27 < width; ++n27, ++n26) {
                            array = writableRaster.getPixel(n26, minY, array);
                            final float n28 = array[numColorComponents] * n;
                            if (n28 != 0.0f) {
                                final float n29 = 1.0f / n28;
                                for (int n30 = 0; n30 < numColorComponents; ++n30) {
                                    array[n30] = (int)(array[n30] * n29 + 0.5f);
                                }
                                writableRaster.setPixel(n26, minY, array);
                            }
                        }
                    }
                    break;
                }
                default: {
                    throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
                }
            }
        }
        return new DirectColorModel(this.colorSpace, this.pixel_bits, this.maskArray[0], this.maskArray[1], this.maskArray[2], this.maskArray[3], b, this.transferType);
    }
    
    @Override
    public boolean isCompatibleRaster(final Raster raster) {
        final SampleModel sampleModel = raster.getSampleModel();
        if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)sampleModel;
        if (singlePixelPackedSampleModel.getNumBands() != this.getNumComponents()) {
            return false;
        }
        final int[] bitMasks = singlePixelPackedSampleModel.getBitMasks();
        for (int i = 0; i < this.numComponents; ++i) {
            if (bitMasks[i] != this.maskArray[i]) {
                return false;
            }
        }
        return raster.getTransferType() == this.transferType;
    }
    
    private void setFields() {
        this.red_mask = this.maskArray[0];
        this.red_offset = this.maskOffsets[0];
        this.green_mask = this.maskArray[1];
        this.green_offset = this.maskOffsets[1];
        this.blue_mask = this.maskArray[2];
        this.blue_offset = this.maskOffsets[2];
        if (this.nBits[0] < 8) {
            this.red_scale = (1 << this.nBits[0]) - 1;
        }
        if (this.nBits[1] < 8) {
            this.green_scale = (1 << this.nBits[1]) - 1;
        }
        if (this.nBits[2] < 8) {
            this.blue_scale = (1 << this.nBits[2]) - 1;
        }
        if (this.supportsAlpha) {
            this.alpha_mask = this.maskArray[3];
            this.alpha_offset = this.maskOffsets[3];
            if (this.nBits[3] < 8) {
                this.alpha_scale = (1 << this.nBits[3]) - 1;
            }
        }
    }
    
    @Override
    public String toString() {
        return new String("DirectColorModel: rmask=" + Integer.toHexString(this.red_mask) + " gmask=" + Integer.toHexString(this.green_mask) + " bmask=" + Integer.toHexString(this.blue_mask) + " amask=" + Integer.toHexString(this.alpha_mask));
    }
}
