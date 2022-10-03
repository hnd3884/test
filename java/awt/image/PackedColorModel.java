package java.awt.image;

import java.awt.color.ColorSpace;

public abstract class PackedColorModel extends ColorModel
{
    int[] maskArray;
    int[] maskOffsets;
    float[] scaleFactors;
    
    public PackedColorModel(final ColorSpace colorSpace, final int n, final int[] array, final int n2, final boolean b, final int n3, final int n4) {
        super(n, createBitsArray(array, n2), colorSpace, n2 != 0, b, n3, n4);
        if (n < 1 || n > 32) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 32.");
        }
        this.maskArray = new int[this.numComponents];
        this.maskOffsets = new int[this.numComponents];
        this.scaleFactors = new float[this.numComponents];
        for (int i = 0; i < this.numColorComponents; ++i) {
            this.DecomposeMask(array[i], i, colorSpace.getName(i));
        }
        if (n2 != 0) {
            this.DecomposeMask(n2, this.numColorComponents, "alpha");
            if (this.nBits[this.numComponents - 1] == 1) {
                this.transparency = 2;
            }
        }
    }
    
    public PackedColorModel(final ColorSpace colorSpace, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final int n6, final int n7) {
        super(n, createBitsArray(n2, n3, n4, n5), colorSpace, n5 != 0, b, n6, n7);
        if (colorSpace.getType() != 5) {
            throw new IllegalArgumentException("ColorSpace must be TYPE_RGB.");
        }
        this.maskArray = new int[this.numComponents];
        this.maskOffsets = new int[this.numComponents];
        this.scaleFactors = new float[this.numComponents];
        this.DecomposeMask(n2, 0, "red");
        this.DecomposeMask(n3, 1, "green");
        this.DecomposeMask(n4, 2, "blue");
        if (n5 != 0) {
            this.DecomposeMask(n5, 3, "alpha");
            if (this.nBits[3] == 1) {
                this.transparency = 2;
            }
        }
    }
    
    public final int getMask(final int n) {
        return this.maskArray[n];
    }
    
    public final int[] getMasks() {
        return this.maskArray.clone();
    }
    
    private void DecomposeMask(int n, final int n2, final String s) {
        int n3 = 0;
        final int n4 = this.nBits[n2];
        this.maskArray[n2] = n;
        if (n != 0) {
            while ((n & 0x1) == 0x0) {
                n >>>= 1;
                ++n3;
            }
        }
        if (n3 + n4 > this.pixel_bits) {
            throw new IllegalArgumentException(s + " mask " + Integer.toHexString(this.maskArray[n2]) + " overflows pixel (expecting " + this.pixel_bits + " bits");
        }
        this.maskOffsets[n2] = n3;
        if (n4 == 0) {
            this.scaleFactors[n2] = 256.0f;
        }
        else {
            this.scaleFactors[n2] = 255.0f / ((1 << n4) - 1);
        }
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        return new SinglePixelPackedSampleModel(this.transferType, n, n2, this.maskArray);
    }
    
    @Override
    public boolean isCompatibleSampleModel(final SampleModel sampleModel) {
        if (!(sampleModel instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (this.numComponents != sampleModel.getNumBands()) {
            return false;
        }
        if (sampleModel.getTransferType() != this.transferType) {
            return false;
        }
        final int[] bitMasks = ((SinglePixelPackedSampleModel)sampleModel).getBitMasks();
        if (bitMasks.length != this.maskArray.length) {
            return false;
        }
        final int n = (int)((1L << DataBuffer.getDataTypeSize(this.transferType)) - 1L);
        for (int i = 0; i < bitMasks.length; ++i) {
            if ((n & bitMasks[i]) != (n & this.maskArray[i])) {
                return false;
            }
        }
        return true;
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
        if (!(o instanceof PackedColorModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final PackedColorModel packedColorModel = (PackedColorModel)o;
        final int numComponents = packedColorModel.getNumComponents();
        if (numComponents != this.numComponents) {
            return false;
        }
        for (int i = 0; i < numComponents; ++i) {
            if (this.maskArray[i] != packedColorModel.getMask(i)) {
                return false;
            }
        }
        return true;
    }
    
    private static final int[] createBitsArray(final int[] array, final int n) {
        final int length = array.length;
        final int[] array2 = new int[length + ((n != 0) ? 1 : 0)];
        for (int i = 0; i < length; ++i) {
            array2[i] = countBits(array[i]);
            if (array2[i] < 0) {
                throw new IllegalArgumentException("Noncontiguous color mask (" + Integer.toHexString(array[i]) + "at index " + i);
            }
        }
        if (n != 0) {
            array2[length] = countBits(n);
            if (array2[length] < 0) {
                throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(n));
            }
        }
        return array2;
    }
    
    private static final int[] createBitsArray(final int n, final int n2, final int n3, final int n4) {
        final int[] array = new int[3 + ((n4 != 0) ? 1 : 0)];
        array[0] = countBits(n);
        array[1] = countBits(n2);
        array[2] = countBits(n3);
        if (array[0] < 0) {
            throw new IllegalArgumentException("Noncontiguous red mask (" + Integer.toHexString(n));
        }
        if (array[1] < 0) {
            throw new IllegalArgumentException("Noncontiguous green mask (" + Integer.toHexString(n2));
        }
        if (array[2] < 0) {
            throw new IllegalArgumentException("Noncontiguous blue mask (" + Integer.toHexString(n3));
        }
        if (n4 != 0) {
            array[3] = countBits(n4);
            if (array[3] < 0) {
                throw new IllegalArgumentException("Noncontiguous alpha mask (" + Integer.toHexString(n4));
            }
        }
        return array;
    }
    
    private static final int countBits(int n) {
        int n2 = 0;
        if (n != 0) {
            while ((n & 0x1) == 0x0) {
                n >>>= 1;
            }
            while ((n & 0x1) == 0x1) {
                n >>>= 1;
                ++n2;
            }
        }
        if (n != 0) {
            return -1;
        }
        return n2;
    }
}
