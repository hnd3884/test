package java.awt.image;

import java.awt.Point;
import java.util.Arrays;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ColorSpace;

public class ComponentColorModel extends ColorModel
{
    private boolean signed;
    private boolean is_sRGB_stdScale;
    private boolean is_LinearRGB_stdScale;
    private boolean is_LinearGray_stdScale;
    private boolean is_ICCGray_stdScale;
    private byte[] tosRGB8LUT;
    private byte[] fromsRGB8LUT8;
    private short[] fromsRGB8LUT16;
    private byte[] fromLinearGray16ToOtherGray8LUT;
    private short[] fromLinearGray16ToOtherGray16LUT;
    private boolean needScaleInit;
    private boolean noUnnorm;
    private boolean nonStdScale;
    private float[] min;
    private float[] diffMinMax;
    private float[] compOffset;
    private float[] compScale;
    
    public ComponentColorModel(final ColorSpace colorSpace, final int[] array, final boolean b, final boolean b2, final int n, final int n2) {
        super(bitsHelper(n2, colorSpace, b), bitsArrayHelper(array, n2, colorSpace, b), colorSpace, b, b2, n, n2);
        switch (n2) {
            case 0:
            case 1:
            case 3: {
                this.signed = false;
                this.needScaleInit = true;
                break;
            }
            case 2: {
                this.signed = true;
                this.needScaleInit = true;
                break;
            }
            case 4:
            case 5: {
                this.signed = true;
                this.needScaleInit = false;
                this.noUnnorm = true;
                this.nonStdScale = false;
                break;
            }
            default: {
                throw new IllegalArgumentException("This constructor is not compatible with transferType " + n2);
            }
        }
        this.setupLUTs();
    }
    
    public ComponentColorModel(final ColorSpace colorSpace, final boolean b, final boolean b2, final int n, final int n2) {
        this(colorSpace, null, b, b2, n, n2);
    }
    
    private static int bitsHelper(final int n, final ColorSpace colorSpace, final boolean b) {
        final int dataTypeSize = DataBuffer.getDataTypeSize(n);
        int numComponents = colorSpace.getNumComponents();
        if (b) {
            ++numComponents;
        }
        return dataTypeSize * numComponents;
    }
    
    private static int[] bitsArrayHelper(final int[] array, final int n, final ColorSpace colorSpace, final boolean b) {
        switch (n) {
            case 0:
            case 1:
            case 3: {
                if (array != null) {
                    return array;
                }
                break;
            }
        }
        final int dataTypeSize = DataBuffer.getDataTypeSize(n);
        int numComponents = colorSpace.getNumComponents();
        if (b) {
            ++numComponents;
        }
        final int[] array2 = new int[numComponents];
        for (int i = 0; i < numComponents; ++i) {
            array2[i] = dataTypeSize;
        }
        return array2;
    }
    
    private void setupLUTs() {
        if (this.is_sRGB) {
            this.is_sRGB_stdScale = true;
            this.nonStdScale = false;
        }
        else if (ColorModel.isLinearRGBspace(this.colorSpace)) {
            this.is_LinearRGB_stdScale = true;
            this.nonStdScale = false;
            if (this.transferType == 0) {
                this.tosRGB8LUT = ColorModel.getLinearRGB8TosRGB8LUT();
                this.fromsRGB8LUT8 = ColorModel.getsRGB8ToLinearRGB8LUT();
            }
            else {
                this.tosRGB8LUT = ColorModel.getLinearRGB16TosRGB8LUT();
                this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
            }
        }
        else if (this.colorSpaceType == 6 && this.colorSpace instanceof ICC_ColorSpace && this.colorSpace.getMinValue(0) == 0.0f && this.colorSpace.getMaxValue(0) == 1.0f) {
            final ICC_ColorSpace icc_ColorSpace = (ICC_ColorSpace)this.colorSpace;
            this.is_ICCGray_stdScale = true;
            this.nonStdScale = false;
            this.fromsRGB8LUT16 = ColorModel.getsRGB8ToLinearRGB16LUT();
            if (ColorModel.isLinearGRAYspace(icc_ColorSpace)) {
                this.is_LinearGray_stdScale = true;
                if (this.transferType == 0) {
                    this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(icc_ColorSpace);
                }
                else {
                    this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(icc_ColorSpace);
                }
            }
            else if (this.transferType == 0) {
                this.tosRGB8LUT = ColorModel.getGray8TosRGB8LUT(icc_ColorSpace);
                this.fromLinearGray16ToOtherGray8LUT = ColorModel.getLinearGray16ToOtherGray8LUT(icc_ColorSpace);
            }
            else {
                this.tosRGB8LUT = ColorModel.getGray16TosRGB8LUT(icc_ColorSpace);
                this.fromLinearGray16ToOtherGray16LUT = ColorModel.getLinearGray16ToOtherGray16LUT(icc_ColorSpace);
            }
        }
        else if (this.needScaleInit) {
            this.nonStdScale = false;
            for (int i = 0; i < this.numColorComponents; ++i) {
                if (this.colorSpace.getMinValue(i) != 0.0f || this.colorSpace.getMaxValue(i) != 1.0f) {
                    this.nonStdScale = true;
                    break;
                }
            }
            if (this.nonStdScale) {
                this.min = new float[this.numColorComponents];
                this.diffMinMax = new float[this.numColorComponents];
                for (int j = 0; j < this.numColorComponents; ++j) {
                    this.min[j] = this.colorSpace.getMinValue(j);
                    this.diffMinMax[j] = this.colorSpace.getMaxValue(j) - this.min[j];
                }
            }
        }
    }
    
    private void initScale() {
        this.needScaleInit = false;
        if (this.nonStdScale || this.signed) {
            this.noUnnorm = true;
        }
        else {
            this.noUnnorm = false;
        }
        float[] array2 = null;
        float[] array3 = null;
        switch (this.transferType) {
            case 0: {
                final byte[] array = new byte[this.numComponents];
                for (int i = 0; i < this.numColorComponents; ++i) {
                    array[i] = 0;
                }
                if (this.supportsAlpha) {
                    array[this.numColorComponents] = (byte)((1 << this.nBits[this.numColorComponents]) - 1);
                }
                array2 = this.getNormalizedComponents(array, null, 0);
                for (int j = 0; j < this.numColorComponents; ++j) {
                    array[j] = (byte)((1 << this.nBits[j]) - 1);
                }
                array3 = this.getNormalizedComponents(array, null, 0);
                break;
            }
            case 1: {
                final short[] array4 = new short[this.numComponents];
                for (int k = 0; k < this.numColorComponents; ++k) {
                    array4[k] = 0;
                }
                if (this.supportsAlpha) {
                    array4[this.numColorComponents] = (short)((1 << this.nBits[this.numColorComponents]) - 1);
                }
                array2 = this.getNormalizedComponents(array4, null, 0);
                for (int l = 0; l < this.numColorComponents; ++l) {
                    array4[l] = (short)((1 << this.nBits[l]) - 1);
                }
                array3 = this.getNormalizedComponents(array4, null, 0);
                break;
            }
            case 3: {
                final int[] array5 = new int[this.numComponents];
                for (int n = 0; n < this.numColorComponents; ++n) {
                    array5[n] = 0;
                }
                if (this.supportsAlpha) {
                    array5[this.numColorComponents] = (1 << this.nBits[this.numColorComponents]) - 1;
                }
                array2 = this.getNormalizedComponents(array5, null, 0);
                for (int n2 = 0; n2 < this.numColorComponents; ++n2) {
                    array5[n2] = (1 << this.nBits[n2]) - 1;
                }
                array3 = this.getNormalizedComponents(array5, null, 0);
                break;
            }
            case 2: {
                final short[] array6 = new short[this.numComponents];
                for (int n3 = 0; n3 < this.numColorComponents; ++n3) {
                    array6[n3] = 0;
                }
                if (this.supportsAlpha) {
                    array6[this.numColorComponents] = 32767;
                }
                array2 = this.getNormalizedComponents(array6, null, 0);
                for (int n4 = 0; n4 < this.numColorComponents; ++n4) {
                    array6[n4] = 32767;
                }
                array3 = this.getNormalizedComponents(array6, null, 0);
                break;
            }
            default: {
                array3 = (array2 = null);
                break;
            }
        }
        this.nonStdScale = false;
        for (int n5 = 0; n5 < this.numColorComponents; ++n5) {
            if (array2[n5] != 0.0f || array3[n5] != 1.0f) {
                this.nonStdScale = true;
                break;
            }
        }
        if (this.nonStdScale) {
            this.noUnnorm = true;
            this.is_sRGB_stdScale = false;
            this.is_LinearRGB_stdScale = false;
            this.is_LinearGray_stdScale = false;
            this.is_ICCGray_stdScale = false;
            this.compOffset = new float[this.numColorComponents];
            this.compScale = new float[this.numColorComponents];
            for (int n6 = 0; n6 < this.numColorComponents; ++n6) {
                this.compOffset[n6] = array2[n6];
                this.compScale[n6] = 1.0f / (array3[n6] - array2[n6]);
            }
        }
    }
    
    private int getRGBComponent(final int n, final int n2) {
        if (this.numComponents > 1) {
            throw new IllegalArgumentException("More than one component per pixel");
        }
        if (this.signed) {
            throw new IllegalArgumentException("Component value is signed");
        }
        if (this.needScaleInit) {
            this.initScale();
        }
        Object o = null;
        switch (this.transferType) {
            case 0: {
                o = new byte[] { (byte)n };
                break;
            }
            case 1: {
                o = new short[] { (short)n };
                break;
            }
            case 3: {
                o = new int[] { n };
                break;
            }
        }
        return (int)(this.colorSpace.toRGB(this.getNormalizedComponents(o, null, 0))[n2] * 255.0f + 0.5f);
    }
    
    @Override
    public int getRed(final int n) {
        return this.getRGBComponent(n, 0);
    }
    
    @Override
    public int getGreen(final int n) {
        return this.getRGBComponent(n, 1);
    }
    
    @Override
    public int getBlue(final int n) {
        return this.getRGBComponent(n, 2);
    }
    
    @Override
    public int getAlpha(final int n) {
        if (!this.supportsAlpha) {
            return 255;
        }
        if (this.numComponents > 1) {
            throw new IllegalArgumentException("More than one component per pixel");
        }
        if (this.signed) {
            throw new IllegalArgumentException("Component value is signed");
        }
        return (int)(n / (float)((1 << this.nBits[0]) - 1) * 255.0f + 0.5f);
    }
    
    @Override
    public int getRGB(final int n) {
        if (this.numComponents > 1) {
            throw new IllegalArgumentException("More than one component per pixel");
        }
        if (this.signed) {
            throw new IllegalArgumentException("Component value is signed");
        }
        return this.getAlpha(n) << 24 | this.getRed(n) << 16 | this.getGreen(n) << 8 | this.getBlue(n) << 0;
    }
    
    private int extractComponent(final Object o, final int n, int n2) {
        final boolean b = this.supportsAlpha && this.isAlphaPremultiplied;
        int n3 = 0;
        final int n4 = (1 << this.nBits[n]) - 1;
        int n11 = 0;
        switch (this.transferType) {
            case 2: {
                final short[] array = (short[])o;
                final float n5 = (float)((1 << n2) - 1);
                if (!b) {
                    return (int)(array[n] / 32767.0f * n5 + 0.5f);
                }
                final short n6 = array[this.numColorComponents];
                if (n6 != 0) {
                    return (int)(array[n] / (float)n6 * n5 + 0.5f);
                }
                return 0;
            }
            case 4: {
                final float[] array2 = (float[])o;
                final float n7 = (float)((1 << n2) - 1);
                if (!b) {
                    return (int)(array2[n] * n7 + 0.5f);
                }
                final float n8 = array2[this.numColorComponents];
                if (n8 != 0.0f) {
                    return (int)(array2[n] / n8 * n7 + 0.5f);
                }
                return 0;
            }
            case 5: {
                final double[] array3 = (double[])o;
                final double n9 = (1 << n2) - 1;
                if (!b) {
                    return (int)(array3[n] * n9 + 0.5);
                }
                final double n10 = array3[this.numColorComponents];
                if (n10 != 0.0) {
                    return (int)(array3[n] / n10 * n9 + 0.5);
                }
                return 0;
            }
            case 0: {
                final byte[] array4 = (byte[])o;
                n11 = (array4[n] & n4);
                n2 = 8;
                if (b) {
                    n3 = (array4[this.numColorComponents] & n4);
                    break;
                }
                break;
            }
            case 1: {
                final short[] array5 = (short[])o;
                n11 = (array5[n] & n4);
                if (b) {
                    n3 = (array5[this.numColorComponents] & n4);
                    break;
                }
                break;
            }
            case 3: {
                final int[] array6 = (int[])o;
                n11 = array6[n];
                if (b) {
                    n3 = array6[this.numColorComponents];
                    break;
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (b) {
            if (n3 != 0) {
                return (int)(n11 / (float)n4 * (((1 << this.nBits[this.numColorComponents]) - 1) / (float)n3) * ((1 << n2) - 1) + 0.5f);
            }
            return 0;
        }
        else {
            if (this.nBits[n] != n2) {
                return (int)(n11 / (float)n4 * ((1 << n2) - 1) + 0.5f);
            }
            return n11;
        }
    }
    
    private int getRGBComponent(final Object o, final int n) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.is_sRGB_stdScale) {
            return this.extractComponent(o, n, 8);
        }
        if (this.is_LinearRGB_stdScale) {
            return this.tosRGB8LUT[this.extractComponent(o, n, 16)] & 0xFF;
        }
        if (this.is_ICCGray_stdScale) {
            return this.tosRGB8LUT[this.extractComponent(o, 0, 16)] & 0xFF;
        }
        return (int)(this.colorSpace.toRGB(this.getNormalizedComponents(o, null, 0))[n] * 255.0f + 0.5f);
    }
    
    @Override
    public int getRed(final Object o) {
        return this.getRGBComponent(o, 0);
    }
    
    @Override
    public int getGreen(final Object o) {
        return this.getRGBComponent(o, 1);
    }
    
    @Override
    public int getBlue(final Object o) {
        return this.getRGBComponent(o, 2);
    }
    
    @Override
    public int getAlpha(final Object o) {
        if (!this.supportsAlpha) {
            return 255;
        }
        final int numColorComponents = this.numColorComponents;
        final int n = (1 << this.nBits[numColorComponents]) - 1;
        int n2 = 0;
        switch (this.transferType) {
            case 2: {
                return (int)(((short[])o)[numColorComponents] / 32767.0f * 255.0f + 0.5f);
            }
            case 4: {
                return (int)(((float[])o)[numColorComponents] * 255.0f + 0.5f);
            }
            case 5: {
                return (int)(((double[])o)[numColorComponents] * 255.0 + 0.5);
            }
            case 0: {
                n2 = (((byte[])o)[numColorComponents] & n);
                break;
            }
            case 1: {
                n2 = (((short[])o)[numColorComponents] & n);
                break;
            }
            case 3: {
                n2 = ((int[])o)[numColorComponents];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (this.nBits[numColorComponents] == 8) {
            return n2;
        }
        return (int)(n2 / (float)((1 << this.nBits[numColorComponents]) - 1) * 255.0f + 0.5f);
    }
    
    @Override
    public int getRGB(final Object o) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            return this.getAlpha(o) << 24 | this.getRed(o) << 16 | this.getGreen(o) << 8 | this.getBlue(o);
        }
        if (this.colorSpaceType == 6) {
            final int red = this.getRed(o);
            return this.getAlpha(o) << 24 | red << 16 | red << 8 | red;
        }
        final float[] rgb = this.colorSpace.toRGB(this.getNormalizedComponents(o, null, 0));
        return this.getAlpha(o) << 24 | (int)(rgb[0] * 255.0f + 0.5f) << 16 | (int)(rgb[1] * 255.0f + 0.5f) << 8 | (int)(rgb[2] * 255.0f + 0.5f) << 0;
    }
    
    @Override
    public Object getDataElements(final int n, final Object o) {
        int n2 = n >> 16 & 0xFF;
        int n3 = n >> 8 & 0xFF;
        int n4 = n & 0xFF;
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.signed) {
            switch (this.transferType) {
                case 2: {
                    short[] array;
                    if (o == null) {
                        array = new short[this.numComponents];
                    }
                    else {
                        array = (short[])o;
                    }
                    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
                        float n5 = 128.49803f;
                        if (this.is_LinearRGB_stdScale) {
                            n2 = (this.fromsRGB8LUT16[n2] & 0xFFFF);
                            n3 = (this.fromsRGB8LUT16[n3] & 0xFFFF);
                            n4 = (this.fromsRGB8LUT16[n4] & 0xFFFF);
                            n5 = 0.49999237f;
                        }
                        if (this.supportsAlpha) {
                            final int n6 = n >> 24 & 0xFF;
                            array[3] = (short)(n6 * 128.49803f + 0.5f);
                            if (this.isAlphaPremultiplied) {
                                n5 = n6 * n5 * 0.003921569f;
                            }
                        }
                        array[0] = (short)(n2 * n5 + 0.5f);
                        array[1] = (short)(n3 * n5 + 0.5f);
                        array[2] = (short)(n4 * n5 + 0.5f);
                    }
                    else if (this.is_LinearGray_stdScale) {
                        final float n7 = (0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF)) / 65535.0f;
                        float n8 = 32767.0f;
                        if (this.supportsAlpha) {
                            final int n9 = n >> 24 & 0xFF;
                            array[1] = (short)(n9 * 128.49803f + 0.5f);
                            if (this.isAlphaPremultiplied) {
                                n8 = n9 * n8 * 0.003921569f;
                            }
                        }
                        array[0] = (short)(n7 * n8 + 0.5f);
                    }
                    else if (this.is_ICCGray_stdScale) {
                        final int n10 = this.fromLinearGray16ToOtherGray16LUT[(int)(0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF) + 0.5f)] & 0xFFFF;
                        float n11 = 0.49999237f;
                        if (this.supportsAlpha) {
                            final int n12 = n >> 24 & 0xFF;
                            array[1] = (short)(n12 * 128.49803f + 0.5f);
                            if (this.isAlphaPremultiplied) {
                                n11 = n12 * n11 * 0.003921569f;
                            }
                        }
                        array[0] = (short)(n10 * n11 + 0.5f);
                    }
                    else {
                        final float n13 = 0.003921569f;
                        final float[] fromRGB = this.colorSpace.fromRGB(new float[] { n2 * n13, n3 * n13, n4 * n13 });
                        if (this.nonStdScale) {
                            for (int i = 0; i < this.numColorComponents; ++i) {
                                fromRGB[i] = (fromRGB[i] - this.compOffset[i]) * this.compScale[i];
                                if (fromRGB[i] < 0.0f) {
                                    fromRGB[i] = 0.0f;
                                }
                                if (fromRGB[i] > 1.0f) {
                                    fromRGB[i] = 1.0f;
                                }
                            }
                        }
                        float n14 = 32767.0f;
                        if (this.supportsAlpha) {
                            final int n15 = n >> 24 & 0xFF;
                            array[this.numColorComponents] = (short)(n15 * 128.49803f + 0.5f);
                            if (this.isAlphaPremultiplied) {
                                n14 *= n15 * 0.003921569f;
                            }
                        }
                        for (int j = 0; j < this.numColorComponents; ++j) {
                            array[j] = (short)(fromRGB[j] * n14 + 0.5f);
                        }
                    }
                    return array;
                }
                case 4: {
                    float[] array2;
                    if (o == null) {
                        array2 = new float[this.numComponents];
                    }
                    else {
                        array2 = (float[])o;
                    }
                    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
                        float n16;
                        if (this.is_LinearRGB_stdScale) {
                            n2 = (this.fromsRGB8LUT16[n2] & 0xFFFF);
                            n3 = (this.fromsRGB8LUT16[n3] & 0xFFFF);
                            n4 = (this.fromsRGB8LUT16[n4] & 0xFFFF);
                            n16 = 1.5259022E-5f;
                        }
                        else {
                            n16 = 0.003921569f;
                        }
                        if (this.supportsAlpha) {
                            array2[3] = (n >> 24 & 0xFF) * 0.003921569f;
                            if (this.isAlphaPremultiplied) {
                                n16 *= array2[3];
                            }
                        }
                        array2[0] = n2 * n16;
                        array2[1] = n3 * n16;
                        array2[2] = n4 * n16;
                    }
                    else if (this.is_LinearGray_stdScale) {
                        array2[0] = (0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF)) / 65535.0f;
                        if (this.supportsAlpha) {
                            array2[1] = (n >> 24 & 0xFF) * 0.003921569f;
                            if (this.isAlphaPremultiplied) {
                                final float[] array3 = array2;
                                final int n17 = 0;
                                array3[n17] *= array2[1];
                            }
                        }
                    }
                    else if (this.is_ICCGray_stdScale) {
                        array2[0] = (this.fromLinearGray16ToOtherGray16LUT[(int)(0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF) + 0.5f)] & 0xFFFF) / 65535.0f;
                        if (this.supportsAlpha) {
                            array2[1] = (n >> 24 & 0xFF) * 0.003921569f;
                            if (this.isAlphaPremultiplied) {
                                final float[] array4 = array2;
                                final int n18 = 0;
                                array4[n18] *= array2[1];
                            }
                        }
                    }
                    else {
                        final float[] array5 = new float[3];
                        final float n19 = 0.003921569f;
                        array5[0] = n2 * n19;
                        array5[1] = n3 * n19;
                        array5[2] = n4 * n19;
                        final float[] fromRGB2 = this.colorSpace.fromRGB(array5);
                        if (this.supportsAlpha) {
                            final int n20 = n >> 24 & 0xFF;
                            array2[this.numColorComponents] = n20 * n19;
                            if (this.isAlphaPremultiplied) {
                                final float n21 = n19 * n20;
                                for (int k = 0; k < this.numColorComponents; ++k) {
                                    final float[] array6 = fromRGB2;
                                    final int n22 = k;
                                    array6[n22] *= n21;
                                }
                            }
                        }
                        for (int l = 0; l < this.numColorComponents; ++l) {
                            array2[l] = fromRGB2[l];
                        }
                    }
                    return array2;
                }
                case 5: {
                    double[] array7;
                    if (o == null) {
                        array7 = new double[this.numComponents];
                    }
                    else {
                        array7 = (double[])o;
                    }
                    if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
                        double n23;
                        if (this.is_LinearRGB_stdScale) {
                            n2 = (this.fromsRGB8LUT16[n2] & 0xFFFF);
                            n3 = (this.fromsRGB8LUT16[n3] & 0xFFFF);
                            n4 = (this.fromsRGB8LUT16[n4] & 0xFFFF);
                            n23 = 1.5259021896696422E-5;
                        }
                        else {
                            n23 = 0.00392156862745098;
                        }
                        if (this.supportsAlpha) {
                            array7[3] = (n >> 24 & 0xFF) * 0.00392156862745098;
                            if (this.isAlphaPremultiplied) {
                                n23 *= array7[3];
                            }
                        }
                        array7[0] = n2 * n23;
                        array7[1] = n3 * n23;
                        array7[2] = n4 * n23;
                    }
                    else if (this.is_LinearGray_stdScale) {
                        array7[0] = (0.2125 * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154 * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721 * (this.fromsRGB8LUT16[n4] & 0xFFFF)) / 65535.0;
                        if (this.supportsAlpha) {
                            array7[1] = (n >> 24 & 0xFF) * 0.00392156862745098;
                            if (this.isAlphaPremultiplied) {
                                final double[] array8 = array7;
                                final int n24 = 0;
                                array8[n24] *= array7[1];
                            }
                        }
                    }
                    else if (this.is_ICCGray_stdScale) {
                        array7[0] = (this.fromLinearGray16ToOtherGray16LUT[(int)(0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF) + 0.5f)] & 0xFFFF) / 65535.0;
                        if (this.supportsAlpha) {
                            array7[1] = (n >> 24 & 0xFF) * 0.00392156862745098;
                            if (this.isAlphaPremultiplied) {
                                final double[] array9 = array7;
                                final int n25 = 0;
                                array9[n25] *= array7[1];
                            }
                        }
                    }
                    else {
                        final float n26 = 0.003921569f;
                        final float[] fromRGB3 = this.colorSpace.fromRGB(new float[] { n2 * n26, n3 * n26, n4 * n26 });
                        if (this.supportsAlpha) {
                            final int n27 = n >> 24 & 0xFF;
                            array7[this.numColorComponents] = n27 * 0.00392156862745098;
                            if (this.isAlphaPremultiplied) {
                                final float n28 = n26 * n27;
                                for (int n29 = 0; n29 < this.numColorComponents; ++n29) {
                                    final float[] array10 = fromRGB3;
                                    final int n30 = n29;
                                    array10[n30] *= n28;
                                }
                            }
                        }
                        for (int n31 = 0; n31 < this.numColorComponents; ++n31) {
                            array7[n31] = fromRGB3[n31];
                        }
                    }
                    return array7;
                }
            }
        }
        int[] array11;
        if (this.transferType == 3 && o != null) {
            array11 = (int[])o;
        }
        else {
            array11 = new int[this.numComponents];
        }
        if (this.is_sRGB_stdScale || this.is_LinearRGB_stdScale) {
            int n32;
            float n33;
            if (this.is_LinearRGB_stdScale) {
                if (this.transferType == 0) {
                    n2 = (this.fromsRGB8LUT8[n2] & 0xFF);
                    n3 = (this.fromsRGB8LUT8[n3] & 0xFF);
                    n4 = (this.fromsRGB8LUT8[n4] & 0xFF);
                    n32 = 8;
                    n33 = 0.003921569f;
                }
                else {
                    n2 = (this.fromsRGB8LUT16[n2] & 0xFFFF);
                    n3 = (this.fromsRGB8LUT16[n3] & 0xFFFF);
                    n4 = (this.fromsRGB8LUT16[n4] & 0xFFFF);
                    n32 = 16;
                    n33 = 1.5259022E-5f;
                }
            }
            else {
                n32 = 8;
                n33 = 0.003921569f;
            }
            if (this.supportsAlpha) {
                final int n34 = n >> 24 & 0xFF;
                if (this.nBits[3] == 8) {
                    array11[3] = n34;
                }
                else {
                    array11[3] = (int)(n34 * 0.003921569f * ((1 << this.nBits[3]) - 1) + 0.5f);
                }
                if (this.isAlphaPremultiplied) {
                    n33 *= n34 * 0.003921569f;
                    n32 = -1;
                }
            }
            if (this.nBits[0] == n32) {
                array11[0] = n2;
            }
            else {
                array11[0] = (int)(n2 * n33 * ((1 << this.nBits[0]) - 1) + 0.5f);
            }
            if (this.nBits[1] == n32) {
                array11[1] = n3;
            }
            else {
                array11[1] = (int)(n3 * n33 * ((1 << this.nBits[1]) - 1) + 0.5f);
            }
            if (this.nBits[2] == n32) {
                array11[2] = n4;
            }
            else {
                array11[2] = (int)(n4 * n33 * ((1 << this.nBits[2]) - 1) + 0.5f);
            }
        }
        else if (this.is_LinearGray_stdScale) {
            float n35 = (0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF)) / 65535.0f;
            if (this.supportsAlpha) {
                final int n36 = n >> 24 & 0xFF;
                if (this.nBits[1] == 8) {
                    array11[1] = n36;
                }
                else {
                    array11[1] = (int)(n36 * 0.003921569f * ((1 << this.nBits[1]) - 1) + 0.5f);
                }
                if (this.isAlphaPremultiplied) {
                    n35 *= n36 * 0.003921569f;
                }
            }
            array11[0] = (int)(n35 * ((1 << this.nBits[0]) - 1) + 0.5f);
        }
        else if (this.is_ICCGray_stdScale) {
            float n37 = (this.fromLinearGray16ToOtherGray16LUT[(int)(0.2125f * (this.fromsRGB8LUT16[n2] & 0xFFFF) + 0.7154f * (this.fromsRGB8LUT16[n3] & 0xFFFF) + 0.0721f * (this.fromsRGB8LUT16[n4] & 0xFFFF) + 0.5f)] & 0xFFFF) / 65535.0f;
            if (this.supportsAlpha) {
                final int n38 = n >> 24 & 0xFF;
                if (this.nBits[1] == 8) {
                    array11[1] = n38;
                }
                else {
                    array11[1] = (int)(n38 * 0.003921569f * ((1 << this.nBits[1]) - 1) + 0.5f);
                }
                if (this.isAlphaPremultiplied) {
                    n37 *= n38 * 0.003921569f;
                }
            }
            array11[0] = (int)(n37 * ((1 << this.nBits[0]) - 1) + 0.5f);
        }
        else {
            final float[] array12 = new float[3];
            final float n39 = 0.003921569f;
            array12[0] = n2 * n39;
            array12[1] = n3 * n39;
            array12[2] = n4 * n39;
            final float[] fromRGB4 = this.colorSpace.fromRGB(array12);
            if (this.nonStdScale) {
                for (int n40 = 0; n40 < this.numColorComponents; ++n40) {
                    fromRGB4[n40] = (fromRGB4[n40] - this.compOffset[n40]) * this.compScale[n40];
                    if (fromRGB4[n40] < 0.0f) {
                        fromRGB4[n40] = 0.0f;
                    }
                    if (fromRGB4[n40] > 1.0f) {
                        fromRGB4[n40] = 1.0f;
                    }
                }
            }
            if (this.supportsAlpha) {
                final int n41 = n >> 24 & 0xFF;
                if (this.nBits[this.numColorComponents] == 8) {
                    array11[this.numColorComponents] = n41;
                }
                else {
                    array11[this.numColorComponents] = (int)(n41 * n39 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5f);
                }
                if (this.isAlphaPremultiplied) {
                    final float n42 = n39 * n41;
                    for (int n43 = 0; n43 < this.numColorComponents; ++n43) {
                        final float[] array13 = fromRGB4;
                        final int n44 = n43;
                        array13[n44] *= n42;
                    }
                }
            }
            for (int n45 = 0; n45 < this.numColorComponents; ++n45) {
                array11[n45] = (int)(fromRGB4[n45] * ((1 << this.nBits[n45]) - 1) + 0.5f);
            }
        }
        switch (this.transferType) {
            case 0: {
                byte[] array14;
                if (o == null) {
                    array14 = new byte[this.numComponents];
                }
                else {
                    array14 = (byte[])o;
                }
                for (int n46 = 0; n46 < this.numComponents; ++n46) {
                    array14[n46] = (byte)(0xFF & array11[n46]);
                }
                return array14;
            }
            case 1: {
                short[] array15;
                if (o == null) {
                    array15 = new short[this.numComponents];
                }
                else {
                    array15 = (short[])o;
                }
                for (int n47 = 0; n47 < this.numComponents; ++n47) {
                    array15[n47] = (short)(array11[n47] & 0xFFFF);
                }
                return array15;
            }
            case 3: {
                if (this.maxBits > 23) {
                    for (int n48 = 0; n48 < this.numComponents; ++n48) {
                        if (array11[n48] > (1 << this.nBits[n48]) - 1) {
                            array11[n48] = (1 << this.nBits[n48]) - 1;
                        }
                    }
                }
                return array11;
            }
            default: {
                throw new IllegalArgumentException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public int[] getComponents(final int n, int[] array, final int n2) {
        if (this.numComponents > 1) {
            throw new IllegalArgumentException("More than one component per pixel");
        }
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        if (array == null) {
            array = new int[n2 + 1];
        }
        array[n2 + 0] = (n & (1 << this.nBits[0]) - 1);
        return array;
    }
    
    @Override
    public int[] getComponents(final Object o, int[] array, final int n) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        int[] intArray;
        if (o instanceof int[]) {
            intArray = (int[])o;
        }
        else {
            intArray = DataBuffer.toIntArray(o);
            if (intArray == null) {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (intArray.length < this.numComponents) {
            throw new IllegalArgumentException("Length of pixel array < number of components in model");
        }
        if (array == null) {
            array = new int[n + this.numComponents];
        }
        else if (array.length - n < this.numComponents) {
            throw new IllegalArgumentException("Length of components array < number of components in model");
        }
        System.arraycopy(intArray, 0, array, n, this.numComponents);
        return array;
    }
    
    @Override
    public int[] getUnnormalizedComponents(final float[] array, final int n, final int[] array2, final int n2) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        return super.getUnnormalizedComponents(array, n, array2, n2);
    }
    
    @Override
    public float[] getNormalizedComponents(final int[] array, final int n, final float[] array2, final int n2) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        return super.getNormalizedComponents(array, n, array2, n2);
    }
    
    @Override
    public int getDataElement(final int[] array, final int n) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.numComponents != 1) {
            throw new IllegalArgumentException("This model returns " + this.numComponents + " elements in the pixel array.");
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        return array[n + 0];
    }
    
    @Override
    public Object getDataElements(final int[] array, final int n, final Object o) {
        if (this.needScaleInit) {
            this.initScale();
        }
        if (this.noUnnorm) {
            throw new IllegalArgumentException("This ColorModel does not support the unnormalized form");
        }
        if (array.length - n < this.numComponents) {
            throw new IllegalArgumentException("Component array too small (should be " + this.numComponents);
        }
        switch (this.transferType) {
            case 3: {
                int[] array2;
                if (o == null) {
                    array2 = new int[this.numComponents];
                }
                else {
                    array2 = (int[])o;
                }
                System.arraycopy(array, n, array2, 0, this.numComponents);
                return array2;
            }
            case 0: {
                byte[] array3;
                if (o == null) {
                    array3 = new byte[this.numComponents];
                }
                else {
                    array3 = (byte[])o;
                }
                for (int i = 0; i < this.numComponents; ++i) {
                    array3[i] = (byte)(array[n + i] & 0xFF);
                }
                return array3;
            }
            case 1: {
                short[] array4;
                if (o == null) {
                    array4 = new short[this.numComponents];
                }
                else {
                    array4 = (short[])o;
                }
                for (int j = 0; j < this.numComponents; ++j) {
                    array4[j] = (short)(array[n + j] & 0xFFFF);
                }
                return array4;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public int getDataElement(final float[] array, final int n) {
        if (this.numComponents > 1) {
            throw new IllegalArgumentException("More than one component per pixel");
        }
        if (this.signed) {
            throw new IllegalArgumentException("Component value is signed");
        }
        if (this.needScaleInit) {
            this.initScale();
        }
        final Object dataElements = this.getDataElements(array, n, null);
        switch (this.transferType) {
            case 0: {
                return ((byte[])dataElements)[0] & 0xFF;
            }
            case 1: {
                return ((short[])dataElements)[0] & 0xFFFF;
            }
            case 3: {
                return ((int[])dataElements)[0];
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public Object getDataElements(final float[] array, int n, final Object o) {
        final boolean b = this.supportsAlpha && this.isAlphaPremultiplied;
        if (this.needScaleInit) {
            this.initScale();
        }
        float[] array2;
        if (this.nonStdScale) {
            array2 = new float[this.numComponents];
            for (int i = 0, n2 = n; i < this.numColorComponents; ++i, ++n2) {
                array2[i] = (array[n2] - this.compOffset[i]) * this.compScale[i];
                if (array2[i] < 0.0f) {
                    array2[i] = 0.0f;
                }
                if (array2[i] > 1.0f) {
                    array2[i] = 1.0f;
                }
            }
            if (this.supportsAlpha) {
                array2[this.numColorComponents] = array[this.numColorComponents + n];
            }
            n = 0;
        }
        else {
            array2 = array;
        }
        switch (this.transferType) {
            case 0: {
                byte[] array3;
                if (o == null) {
                    array3 = new byte[this.numComponents];
                }
                else {
                    array3 = (byte[])o;
                }
                if (b) {
                    final float n3 = array2[this.numColorComponents + n];
                    for (int j = 0, n4 = n; j < this.numColorComponents; ++j, ++n4) {
                        array3[j] = (byte)(array2[n4] * n3 * ((1 << this.nBits[j]) - 1) + 0.5f);
                    }
                    array3[this.numColorComponents] = (byte)(n3 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5f);
                }
                else {
                    for (int k = 0, n5 = n; k < this.numComponents; ++k, ++n5) {
                        array3[k] = (byte)(array2[n5] * ((1 << this.nBits[k]) - 1) + 0.5f);
                    }
                }
                return array3;
            }
            case 1: {
                short[] array4;
                if (o == null) {
                    array4 = new short[this.numComponents];
                }
                else {
                    array4 = (short[])o;
                }
                if (b) {
                    final float n6 = array2[this.numColorComponents + n];
                    for (int l = 0, n7 = n; l < this.numColorComponents; ++l, ++n7) {
                        array4[l] = (short)(array2[n7] * n6 * ((1 << this.nBits[l]) - 1) + 0.5f);
                    }
                    array4[this.numColorComponents] = (short)(n6 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5f);
                }
                else {
                    for (int n8 = 0, n9 = n; n8 < this.numComponents; ++n8, ++n9) {
                        array4[n8] = (short)(array2[n9] * ((1 << this.nBits[n8]) - 1) + 0.5f);
                    }
                }
                return array4;
            }
            case 3: {
                int[] array5;
                if (o == null) {
                    array5 = new int[this.numComponents];
                }
                else {
                    array5 = (int[])o;
                }
                if (b) {
                    final float n10 = array2[this.numColorComponents + n];
                    for (int n11 = 0, n12 = n; n11 < this.numColorComponents; ++n11, ++n12) {
                        array5[n11] = (int)(array2[n12] * n10 * ((1 << this.nBits[n11]) - 1) + 0.5f);
                    }
                    array5[this.numColorComponents] = (int)(n10 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5f);
                }
                else {
                    for (int n13 = 0, n14 = n; n13 < this.numComponents; ++n13, ++n14) {
                        array5[n13] = (int)(array2[n14] * ((1 << this.nBits[n13]) - 1) + 0.5f);
                    }
                }
                return array5;
            }
            case 2: {
                short[] array6;
                if (o == null) {
                    array6 = new short[this.numComponents];
                }
                else {
                    array6 = (short[])o;
                }
                if (b) {
                    final float n15 = array2[this.numColorComponents + n];
                    for (int n16 = 0, n17 = n; n16 < this.numColorComponents; ++n16, ++n17) {
                        array6[n16] = (short)(array2[n17] * n15 * 32767.0f + 0.5f);
                    }
                    array6[this.numColorComponents] = (short)(n15 * 32767.0f + 0.5f);
                }
                else {
                    for (int n18 = 0, n19 = n; n18 < this.numComponents; ++n18, ++n19) {
                        array6[n18] = (short)(array2[n19] * 32767.0f + 0.5f);
                    }
                }
                return array6;
            }
            case 4: {
                float[] array7;
                if (o == null) {
                    array7 = new float[this.numComponents];
                }
                else {
                    array7 = (float[])o;
                }
                if (b) {
                    final float n20 = array[this.numColorComponents + n];
                    for (int n21 = 0, n22 = n; n21 < this.numColorComponents; ++n21, ++n22) {
                        array7[n21] = array[n22] * n20;
                    }
                    array7[this.numColorComponents] = n20;
                }
                else {
                    for (int n23 = 0, n24 = n; n23 < this.numComponents; ++n23, ++n24) {
                        array7[n23] = array[n24];
                    }
                }
                return array7;
            }
            case 5: {
                double[] array8;
                if (o == null) {
                    array8 = new double[this.numComponents];
                }
                else {
                    array8 = (double[])o;
                }
                if (b) {
                    final double n25 = array[this.numColorComponents + n];
                    for (int n26 = 0, n27 = n; n26 < this.numColorComponents; ++n26, ++n27) {
                        array8[n26] = array[n27] * n25;
                    }
                    array8[this.numColorComponents] = n25;
                }
                else {
                    for (int n28 = 0, n29 = n; n28 < this.numComponents; ++n28, ++n29) {
                        array8[n28] = array[n29];
                    }
                }
                return array8;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
    }
    
    @Override
    public float[] getNormalizedComponents(final Object o, float[] array, final int n) {
        if (array == null) {
            array = new float[this.numComponents + n];
        }
        switch (this.transferType) {
            case 0: {
                final byte[] array2 = (byte[])o;
                for (int i = 0, n2 = n; i < this.numComponents; ++i, ++n2) {
                    array[n2] = (array2[i] & 0xFF) / (float)((1 << this.nBits[i]) - 1);
                }
                break;
            }
            case 1: {
                final short[] array3 = (short[])o;
                for (int j = 0, n3 = n; j < this.numComponents; ++j, ++n3) {
                    array[n3] = (array3[j] & 0xFFFF) / (float)((1 << this.nBits[j]) - 1);
                }
                break;
            }
            case 3: {
                final int[] array4 = (int[])o;
                for (int k = 0, n4 = n; k < this.numComponents; ++k, ++n4) {
                    array[n4] = array4[k] / (float)((1 << this.nBits[k]) - 1);
                }
                break;
            }
            case 2: {
                final short[] array5 = (short[])o;
                for (int l = 0, n5 = n; l < this.numComponents; ++l, ++n5) {
                    array[n5] = array5[l] / 32767.0f;
                }
                break;
            }
            case 4: {
                final float[] array6 = (float[])o;
                for (int n6 = 0, n7 = n; n6 < this.numComponents; ++n6, ++n7) {
                    array[n7] = array6[n6];
                }
                break;
            }
            case 5: {
                final double[] array7 = (double[])o;
                for (int n8 = 0, n9 = n; n8 < this.numComponents; ++n8, ++n9) {
                    array[n9] = (float)array7[n8];
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (this.supportsAlpha && this.isAlphaPremultiplied) {
            final float n10 = array[this.numColorComponents + n];
            if (n10 != 0.0f) {
                final float n11 = 1.0f / n10;
                for (int n12 = n; n12 < this.numColorComponents + n; ++n12) {
                    final float[] array8 = array;
                    final int n13 = n12;
                    array8[n13] *= n11;
                }
            }
        }
        if (this.min != null) {
            for (int n14 = 0; n14 < this.numColorComponents; ++n14) {
                array[n14 + n] = this.min[n14] + this.diffMinMax[n14] * array[n14 + n];
            }
        }
        return array;
    }
    
    @Override
    public ColorModel coerceData(final WritableRaster writableRaster, final boolean b) {
        if (!this.supportsAlpha || this.isAlphaPremultiplied == b) {
            return this;
        }
        final int width = writableRaster.getWidth();
        final int height = writableRaster.getHeight();
        final int n = writableRaster.getNumBands() - 1;
        final int minX = writableRaster.getMinX();
        int minY = writableRaster.getMinY();
        if (b) {
            switch (this.transferType) {
                case 0: {
                    Object o = null;
                    byte[] array = null;
                    final float n2 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int i = 0; i < height; ++i, ++minY) {
                        for (int n3 = minX, j = 0; j < width; ++j, ++n3) {
                            o = writableRaster.getDataElements(n3, minY, o);
                            final float n4 = (float)(o[n] & 0xFF) * n2;
                            if (n4 != 0.0f) {
                                for (int k = 0; k < n; ++k) {
                                    o[k] = (byte)((float)(o[k] & 0xFF) * n4 + 0.5f);
                                }
                                writableRaster.setDataElements(n3, minY, o);
                            }
                            else {
                                if (array == null) {
                                    array = new byte[this.numComponents];
                                    Arrays.fill(array, (byte)0);
                                }
                                writableRaster.setDataElements(n3, minY, array);
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    Object o2 = null;
                    short[] array2 = null;
                    final float n5 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int l = 0; l < height; ++l, ++minY) {
                        for (int n6 = minX, n7 = 0; n7 < width; ++n7, ++n6) {
                            o2 = writableRaster.getDataElements(n6, minY, o2);
                            final float n8 = (float)(o2[n] & 0xFFFF) * n5;
                            if (n8 != 0.0f) {
                                for (int n9 = 0; n9 < n; ++n9) {
                                    o2[n9] = (short)((float)(o2[n9] & 0xFFFF) * n8 + 0.5f);
                                }
                                writableRaster.setDataElements(n6, minY, o2);
                            }
                            else {
                                if (array2 == null) {
                                    array2 = new short[this.numComponents];
                                    Arrays.fill(array2, (short)0);
                                }
                                writableRaster.setDataElements(n6, minY, array2);
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    Object o3 = null;
                    int[] array3 = null;
                    final float n10 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int n11 = 0; n11 < height; ++n11, ++minY) {
                        for (int n12 = minX, n13 = 0; n13 < width; ++n13, ++n12) {
                            o3 = writableRaster.getDataElements(n12, minY, o3);
                            final float n14 = (float)o3[n] * n10;
                            if (n14 != 0.0f) {
                                for (int n15 = 0; n15 < n; ++n15) {
                                    o3[n15] = (int)((float)o3[n15] * n14 + 0.5f);
                                }
                                writableRaster.setDataElements(n12, minY, o3);
                            }
                            else {
                                if (array3 == null) {
                                    array3 = new int[this.numComponents];
                                    Arrays.fill(array3, 0);
                                }
                                writableRaster.setDataElements(n12, minY, array3);
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    Object o4 = null;
                    short[] array4 = null;
                    final float n16 = 3.051851E-5f;
                    for (int n17 = 0; n17 < height; ++n17, ++minY) {
                        for (int n18 = minX, n19 = 0; n19 < width; ++n19, ++n18) {
                            o4 = writableRaster.getDataElements(n18, minY, o4);
                            final float n20 = (float)o4[n] * n16;
                            if (n20 != 0.0f) {
                                for (int n21 = 0; n21 < n; ++n21) {
                                    o4[n21] = (short)((float)o4[n21] * n20 + 0.5f);
                                }
                                writableRaster.setDataElements(n18, minY, o4);
                            }
                            else {
                                if (array4 == null) {
                                    array4 = new short[this.numComponents];
                                    Arrays.fill(array4, (short)0);
                                }
                                writableRaster.setDataElements(n18, minY, array4);
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    Object o5 = null;
                    float[] array5 = null;
                    for (int n22 = 0; n22 < height; ++n22, ++minY) {
                        for (int n23 = minX, n24 = 0; n24 < width; ++n24, ++n23) {
                            o5 = writableRaster.getDataElements(n23, minY, o5);
                            final float n25 = o5[n];
                            if (n25 != 0.0f) {
                                for (int n26 = 0; n26 < n; ++n26) {
                                    final Object o6 = o5;
                                    final int n27 = n26;
                                    o6[n27] *= n25;
                                }
                                writableRaster.setDataElements(n23, minY, o5);
                            }
                            else {
                                if (array5 == null) {
                                    array5 = new float[this.numComponents];
                                    Arrays.fill(array5, 0.0f);
                                }
                                writableRaster.setDataElements(n23, minY, array5);
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    Object o7 = null;
                    double[] array6 = null;
                    for (int n28 = 0; n28 < height; ++n28, ++minY) {
                        for (int n29 = minX, n30 = 0; n30 < width; ++n30, ++n29) {
                            o7 = writableRaster.getDataElements(n29, minY, o7);
                            final double n31 = o7[n];
                            if (n31 != 0.0) {
                                for (int n32 = 0; n32 < n; ++n32) {
                                    final Object o8 = o7;
                                    final int n33 = n32;
                                    o8[n33] *= n31;
                                }
                                writableRaster.setDataElements(n29, minY, o7);
                            }
                            else {
                                if (array6 == null) {
                                    array6 = new double[this.numComponents];
                                    Arrays.fill(array6, 0.0);
                                }
                                writableRaster.setDataElements(n29, minY, array6);
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
                    Object o9 = null;
                    final float n34 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int n35 = 0; n35 < height; ++n35, ++minY) {
                        for (int n36 = minX, n37 = 0; n37 < width; ++n37, ++n36) {
                            o9 = writableRaster.getDataElements(n36, minY, o9);
                            final float n38 = (float)(o9[n] & 0xFF) * n34;
                            if (n38 != 0.0f) {
                                final float n39 = 1.0f / n38;
                                for (int n40 = 0; n40 < n; ++n40) {
                                    o9[n40] = (byte)((float)(o9[n40] & 0xFF) * n39 + 0.5f);
                                }
                                writableRaster.setDataElements(n36, minY, o9);
                            }
                        }
                    }
                    break;
                }
                case 1: {
                    Object o10 = null;
                    final float n41 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int n42 = 0; n42 < height; ++n42, ++minY) {
                        for (int n43 = minX, n44 = 0; n44 < width; ++n44, ++n43) {
                            o10 = writableRaster.getDataElements(n43, minY, o10);
                            final float n45 = (float)(o10[n] & 0xFFFF) * n41;
                            if (n45 != 0.0f) {
                                final float n46 = 1.0f / n45;
                                for (int n47 = 0; n47 < n; ++n47) {
                                    o10[n47] = (short)((float)(o10[n47] & 0xFFFF) * n46 + 0.5f);
                                }
                                writableRaster.setDataElements(n43, minY, o10);
                            }
                        }
                    }
                    break;
                }
                case 3: {
                    Object o11 = null;
                    final float n48 = 1.0f / ((1 << this.nBits[n]) - 1);
                    for (int n49 = 0; n49 < height; ++n49, ++minY) {
                        for (int n50 = minX, n51 = 0; n51 < width; ++n51, ++n50) {
                            o11 = writableRaster.getDataElements(n50, minY, o11);
                            final float n52 = (float)o11[n] * n48;
                            if (n52 != 0.0f) {
                                final float n53 = 1.0f / n52;
                                for (int n54 = 0; n54 < n; ++n54) {
                                    o11[n54] = (int)((float)o11[n54] * n53 + 0.5f);
                                }
                                writableRaster.setDataElements(n50, minY, o11);
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    Object o12 = null;
                    final float n55 = 3.051851E-5f;
                    for (int n56 = 0; n56 < height; ++n56, ++minY) {
                        for (int n57 = minX, n58 = 0; n58 < width; ++n58, ++n57) {
                            o12 = writableRaster.getDataElements(n57, minY, o12);
                            final float n59 = (float)o12[n] * n55;
                            if (n59 != 0.0f) {
                                final float n60 = 1.0f / n59;
                                for (int n61 = 0; n61 < n; ++n61) {
                                    o12[n61] = (short)((float)o12[n61] * n60 + 0.5f);
                                }
                                writableRaster.setDataElements(n57, minY, o12);
                            }
                        }
                    }
                    break;
                }
                case 4: {
                    Object o13 = null;
                    for (int n62 = 0; n62 < height; ++n62, ++minY) {
                        for (int n63 = minX, n64 = 0; n64 < width; ++n64, ++n63) {
                            o13 = writableRaster.getDataElements(n63, minY, o13);
                            final float n65 = o13[n];
                            if (n65 != 0.0f) {
                                final float n66 = 1.0f / n65;
                                for (int n67 = 0; n67 < n; ++n67) {
                                    final Object o14 = o13;
                                    final int n68 = n67;
                                    o14[n68] *= n66;
                                }
                                writableRaster.setDataElements(n63, minY, o13);
                            }
                        }
                    }
                    break;
                }
                case 5: {
                    Object o15 = null;
                    for (int n69 = 0; n69 < height; ++n69, ++minY) {
                        for (int n70 = minX, n71 = 0; n71 < width; ++n71, ++n70) {
                            o15 = writableRaster.getDataElements(n70, minY, o15);
                            final double n72 = o15[n];
                            if (n72 != 0.0) {
                                final double n73 = 1.0 / n72;
                                for (int n74 = 0; n74 < n; ++n74) {
                                    final Object o16 = o15;
                                    final int n75 = n74;
                                    o16[n75] *= n73;
                                }
                                writableRaster.setDataElements(n70, minY, o15);
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
        if (!this.signed) {
            return new ComponentColorModel(this.colorSpace, this.nBits, this.supportsAlpha, b, this.transparency, this.transferType);
        }
        return new ComponentColorModel(this.colorSpace, this.supportsAlpha, b, this.transparency, this.transferType);
    }
    
    @Override
    public boolean isCompatibleRaster(final Raster raster) {
        final SampleModel sampleModel = raster.getSampleModel();
        if (!(sampleModel instanceof ComponentSampleModel)) {
            return false;
        }
        if (sampleModel.getNumBands() != this.getNumComponents()) {
            return false;
        }
        for (int i = 0; i < this.nBits.length; ++i) {
            if (sampleModel.getSampleSize(i) < this.nBits[i]) {
                return false;
            }
        }
        return raster.getTransferType() == this.transferType;
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        final int n3 = n * n2 * this.numComponents;
        WritableRaster writableRaster = null;
        switch (this.transferType) {
            case 0:
            case 1: {
                writableRaster = Raster.createInterleavedRaster(this.transferType, n, n2, this.numComponents, null);
                break;
            }
            default: {
                final SampleModel compatibleSampleModel = this.createCompatibleSampleModel(n, n2);
                writableRaster = Raster.createWritableRaster(compatibleSampleModel, compatibleSampleModel.createDataBuffer(), null);
                break;
            }
        }
        return writableRaster;
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        final int[] array = new int[this.numComponents];
        for (int i = 0; i < this.numComponents; ++i) {
            array[i] = i;
        }
        switch (this.transferType) {
            case 0:
            case 1: {
                return new PixelInterleavedSampleModel(this.transferType, n, n2, this.numComponents, n * this.numComponents, array);
            }
            default: {
                return new ComponentSampleModel(this.transferType, n, n2, this.numComponents, n * this.numComponents, array);
            }
        }
    }
    
    @Override
    public boolean isCompatibleSampleModel(final SampleModel sampleModel) {
        return sampleModel instanceof ComponentSampleModel && this.numComponents == sampleModel.getNumBands() && sampleModel.getTransferType() == this.transferType;
    }
    
    @Override
    public WritableRaster getAlphaRaster(final WritableRaster writableRaster) {
        if (!this.hasAlpha()) {
            return null;
        }
        final int minX = writableRaster.getMinX();
        final int minY = writableRaster.getMinY();
        return writableRaster.createWritableChild(minX, minY, writableRaster.getWidth(), writableRaster.getHeight(), minX, minY, new int[] { writableRaster.getNumBands() - 1 });
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) && o.getClass() == this.getClass();
    }
}
