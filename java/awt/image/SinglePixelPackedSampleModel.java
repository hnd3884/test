package java.awt.image;

import java.util.Arrays;

public class SinglePixelPackedSampleModel extends SampleModel
{
    private int[] bitMasks;
    private int[] bitOffsets;
    private int[] bitSizes;
    private int maxBitSize;
    private int scanlineStride;
    
    private static native void initIDs();
    
    public SinglePixelPackedSampleModel(final int n, final int n2, final int n3, final int[] array) {
        this(n, n2, n3, n2, array);
        if (n != 0 && n != 1 && n != 3) {
            throw new IllegalArgumentException("Unsupported data type " + n);
        }
    }
    
    public SinglePixelPackedSampleModel(final int dataType, final int n, final int n2, final int scanlineStride, final int[] array) {
        super(dataType, n, n2, array.length);
        if (dataType != 0 && dataType != 1 && dataType != 3) {
            throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
        this.dataType = dataType;
        this.bitMasks = array.clone();
        this.scanlineStride = scanlineStride;
        this.bitOffsets = new int[this.numBands];
        this.bitSizes = new int[this.numBands];
        final int n3 = (int)((1L << DataBuffer.getDataTypeSize(dataType)) - 1L);
        this.maxBitSize = 0;
        for (int i = 0; i < this.numBands; ++i) {
            int n4 = 0;
            int maxBitSize = 0;
            final int[] bitMasks = this.bitMasks;
            final int n5 = i;
            bitMasks[n5] &= n3;
            int n6 = this.bitMasks[i];
            if (n6 != 0) {
                while ((n6 & 0x1) == 0x0) {
                    n6 >>>= 1;
                    ++n4;
                }
                while ((n6 & 0x1) == 0x1) {
                    n6 >>>= 1;
                    ++maxBitSize;
                }
                if (n6 != 0) {
                    throw new IllegalArgumentException("Mask " + array[i] + " must be contiguous");
                }
            }
            this.bitOffsets[i] = n4;
            if ((this.bitSizes[i] = maxBitSize) > this.maxBitSize) {
                this.maxBitSize = maxBitSize;
            }
        }
    }
    
    @Override
    public int getNumDataElements() {
        return 1;
    }
    
    private long getBufferSize() {
        return this.scanlineStride * (this.height - 1) + this.width;
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        return new SinglePixelPackedSampleModel(this.dataType, n, n2, this.bitMasks);
    }
    
    @Override
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;
        final int n = (int)this.getBufferSize();
        switch (this.dataType) {
            case 0: {
                dataBuffer = new DataBufferByte(n);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(n);
                break;
            }
        }
        return dataBuffer;
    }
    
    @Override
    public int[] getSampleSize() {
        return this.bitSizes.clone();
    }
    
    @Override
    public int getSampleSize(final int n) {
        return this.bitSizes[n];
    }
    
    public int getOffset(final int n, final int n2) {
        return n2 * this.scanlineStride + n;
    }
    
    public int[] getBitOffsets() {
        return this.bitOffsets.clone();
    }
    
    public int[] getBitMasks() {
        return this.bitMasks.clone();
    }
    
    public int getScanlineStride() {
        return this.scanlineStride;
    }
    
    @Override
    public SampleModel createSubsetSampleModel(final int[] array) {
        if (array.length > this.numBands) {
            throw new RasterFormatException("There are only " + this.numBands + " bands");
        }
        final int[] array2 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.bitMasks[array[i]];
        }
        return new SinglePixelPackedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, array2);
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        switch (this.getTransferType()) {
            case 0: {
                byte[] array;
                if (o == null) {
                    array = new byte[] { 0 };
                }
                else {
                    array = (byte[])o;
                }
                array[0] = (byte)dataBuffer.getElem(n2 * this.scanlineStride + n);
                o = array;
                break;
            }
            case 1: {
                short[] array2;
                if (o == null) {
                    array2 = new short[] { 0 };
                }
                else {
                    array2 = (short[])o;
                }
                array2[0] = (short)dataBuffer.getElem(n2 * this.scanlineStride + n);
                o = array2;
                break;
            }
            case 3: {
                int[] array3;
                if (o == null) {
                    array3 = new int[] { 0 };
                }
                else {
                    array3 = (int[])o;
                }
                array3[0] = dataBuffer.getElem(n2 * this.scanlineStride + n);
                o = array3;
                break;
            }
        }
        return o;
    }
    
    @Override
    public int[] getPixel(final int n, final int n2, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array == null) {
            array2 = new int[this.numBands];
        }
        else {
            array2 = array;
        }
        final int elem = dataBuffer.getElem(n2 * this.scanlineStride + n);
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = (elem & this.bitMasks[i]) >>> this.bitOffsets[i];
        }
        return array2;
    }
    
    @Override
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        final int n5 = n + n3;
        final int n6 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n5 < 0 || n5 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n6 < 0 || n6 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4 * this.numBands];
        }
        int n7 = n2 * this.scanlineStride + n;
        int n8 = 0;
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n3; ++j) {
                final int elem = dataBuffer.getElem(n7 + j);
                for (int k = 0; k < this.numBands; ++k) {
                    array2[n8++] = (elem & this.bitMasks[k]) >>> this.bitOffsets[k];
                }
            }
            n7 += this.scanlineStride;
        }
        return array2;
    }
    
    @Override
    public int getSample(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return (dataBuffer.getElem(n2 * this.scanlineStride + n) & this.bitMasks[n3]) >>> this.bitOffsets[n3];
    }
    
    @Override
    public int[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n + n3 > this.width || n2 + n4 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4];
        }
        int n6 = n2 * this.scanlineStride + n;
        int n7 = 0;
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n3; ++j) {
                array2[n7++] = (dataBuffer.getElem(n6 + j) & this.bitMasks[n5]) >>> this.bitOffsets[n5];
            }
            n6 += this.scanlineStride;
        }
        return array2;
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        switch (this.getTransferType()) {
            case 0: {
                dataBuffer.setElem(n2 * this.scanlineStride + n, ((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                dataBuffer.setElem(n2 * this.scanlineStride + n, ((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                dataBuffer.setElem(n2 * this.scanlineStride + n, ((int[])o)[0]);
                break;
            }
        }
    }
    
    @Override
    public void setPixel(final int n, final int n2, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n3 = n2 * this.scanlineStride + n;
        int elem = dataBuffer.getElem(n3);
        for (int i = 0; i < this.numBands; ++i) {
            elem = ((elem & ~this.bitMasks[i]) | (array[i] << this.bitOffsets[i] & this.bitMasks[i]));
        }
        dataBuffer.setElem(n3, elem);
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        final int n5 = n + n3;
        final int n6 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n5 < 0 || n5 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n6 < 0 || n6 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n7 = n2 * this.scanlineStride + n;
        int n8 = 0;
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n3; ++j) {
                int elem = dataBuffer.getElem(n7 + j);
                for (int k = 0; k < this.numBands; ++k) {
                    elem = ((elem & ~this.bitMasks[k]) | (array[n8++] << this.bitOffsets[k] & this.bitMasks[k]));
                }
                dataBuffer.setElem(n7 + j, elem);
            }
            n7 += this.scanlineStride;
        }
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final int n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElem(n2 * this.scanlineStride + n, (dataBuffer.getElem(n2 * this.scanlineStride + n) & ~this.bitMasks[n3]) | (n4 << this.bitOffsets[n3] & this.bitMasks[n3]));
    }
    
    @Override
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n + n3 > this.width || n2 + n4 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n6 = n2 * this.scanlineStride + n;
        int n7 = 0;
        for (int i = 0; i < n4; ++i) {
            for (int j = 0; j < n3; ++j) {
                dataBuffer.setElem(n6 + j, (dataBuffer.getElem(n6 + j) & ~this.bitMasks[n5]) | (array[n7++] << this.bitOffsets[n5] & this.bitMasks[n5]));
            }
            n6 += this.scanlineStride;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        final SinglePixelPackedSampleModel singlePixelPackedSampleModel = (SinglePixelPackedSampleModel)o;
        return this.width == singlePixelPackedSampleModel.width && this.height == singlePixelPackedSampleModel.height && this.numBands == singlePixelPackedSampleModel.numBands && this.dataType == singlePixelPackedSampleModel.dataType && Arrays.equals(this.bitMasks, singlePixelPackedSampleModel.bitMasks) && Arrays.equals(this.bitOffsets, singlePixelPackedSampleModel.bitOffsets) && Arrays.equals(this.bitSizes, singlePixelPackedSampleModel.bitSizes) && this.maxBitSize == singlePixelPackedSampleModel.maxBitSize && this.scanlineStride == singlePixelPackedSampleModel.scanlineStride;
    }
    
    @Override
    public int hashCode() {
        int n = (((this.width << 8 ^ this.height) << 8 ^ this.numBands) << 8 ^ this.dataType) << 8;
        for (int i = 0; i < this.bitMasks.length; ++i) {
            n = (n ^ this.bitMasks[i]) << 8;
        }
        for (int j = 0; j < this.bitOffsets.length; ++j) {
            n = (n ^ this.bitOffsets[j]) << 8;
        }
        for (int k = 0; k < this.bitSizes.length; ++k) {
            n = (n ^ this.bitSizes[k]) << 8;
        }
        return (n ^ this.maxBitSize) << 8 ^ this.scanlineStride;
    }
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}
