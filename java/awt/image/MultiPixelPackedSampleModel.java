package java.awt.image;

public class MultiPixelPackedSampleModel extends SampleModel
{
    int pixelBitStride;
    int bitMask;
    int pixelsPerDataElement;
    int dataElementSize;
    int dataBitOffset;
    int scanlineStride;
    
    public MultiPixelPackedSampleModel(final int n, final int n2, final int n3, final int n4) {
        this(n, n2, n3, n4, (n2 * n4 + DataBuffer.getDataTypeSize(n) - 1) / DataBuffer.getDataTypeSize(n), 0);
        if (n != 0 && n != 1 && n != 3) {
            throw new IllegalArgumentException("Unsupported data type " + n);
        }
    }
    
    public MultiPixelPackedSampleModel(final int dataType, final int n, final int n2, final int pixelBitStride, final int scanlineStride, final int dataBitOffset) {
        super(dataType, n, n2, 1);
        if (dataType != 0 && dataType != 1 && dataType != 3) {
            throw new IllegalArgumentException("Unsupported data type " + dataType);
        }
        this.dataType = dataType;
        this.pixelBitStride = pixelBitStride;
        this.scanlineStride = scanlineStride;
        this.dataBitOffset = dataBitOffset;
        this.dataElementSize = DataBuffer.getDataTypeSize(dataType);
        this.pixelsPerDataElement = this.dataElementSize / pixelBitStride;
        if (this.pixelsPerDataElement * pixelBitStride != this.dataElementSize) {
            throw new RasterFormatException("MultiPixelPackedSampleModel does not allow pixels to span data element boundaries");
        }
        this.bitMask = (1 << pixelBitStride) - 1;
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        return new MultiPixelPackedSampleModel(this.dataType, n, n2, this.pixelBitStride);
    }
    
    @Override
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;
        final int n = this.scanlineStride * this.height;
        switch (this.dataType) {
            case 0: {
                dataBuffer = new DataBufferByte(n + (this.dataBitOffset + 7) / 8);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n + (this.dataBitOffset + 15) / 16);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(n + (this.dataBitOffset + 31) / 32);
                break;
            }
        }
        return dataBuffer;
    }
    
    @Override
    public int getNumDataElements() {
        return 1;
    }
    
    @Override
    public int[] getSampleSize() {
        return new int[] { this.pixelBitStride };
    }
    
    @Override
    public int getSampleSize(final int n) {
        return this.pixelBitStride;
    }
    
    public int getOffset(final int n, final int n2) {
        return n2 * this.scanlineStride + (n * this.pixelBitStride + this.dataBitOffset) / this.dataElementSize;
    }
    
    public int getBitOffset(final int n) {
        return (n * this.pixelBitStride + this.dataBitOffset) % this.dataElementSize;
    }
    
    public int getScanlineStride() {
        return this.scanlineStride;
    }
    
    public int getPixelBitStride() {
        return this.pixelBitStride;
    }
    
    public int getDataBitOffset() {
        return this.dataBitOffset;
    }
    
    @Override
    public int getTransferType() {
        if (this.pixelBitStride > 16) {
            return 3;
        }
        if (this.pixelBitStride > 8) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public SampleModel createSubsetSampleModel(final int[] array) {
        if (array != null && array.length != 1) {
            throw new RasterFormatException("MultiPixelPackedSampleModel has only one band.");
        }
        return this.createCompatibleSampleModel(this.width, this.height);
    }
    
    @Override
    public int getSample(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height || n3 != 0) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n4 = this.dataBitOffset + n * this.pixelBitStride;
        return dataBuffer.getElem(n2 * this.scanlineStride + n4 / this.dataElementSize) >> this.dataElementSize - (n4 & this.dataElementSize - 1) - this.pixelBitStride & this.bitMask;
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final int n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height || n3 != 0) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n5 = this.dataBitOffset + n * this.pixelBitStride;
        final int n6 = n2 * this.scanlineStride + n5 / this.dataElementSize;
        final int n7 = this.dataElementSize - (n5 & this.dataElementSize - 1) - this.pixelBitStride;
        dataBuffer.setElem(n6, (dataBuffer.getElem(n6) & ~(this.bitMask << n7)) | (n4 & this.bitMask) << n7);
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int transferType = this.getTransferType();
        final int n3 = this.dataBitOffset + n * this.pixelBitStride;
        final int n4 = this.dataElementSize - (n3 & this.dataElementSize - 1) - this.pixelBitStride;
        switch (transferType) {
            case 0: {
                byte[] array;
                if (o == null) {
                    array = new byte[] { 0 };
                }
                else {
                    array = (byte[])o;
                }
                array[0] = (byte)(dataBuffer.getElem(n2 * this.scanlineStride + n3 / this.dataElementSize) >> n4 & this.bitMask);
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
                array2[0] = (short)(dataBuffer.getElem(n2 * this.scanlineStride + n3 / this.dataElementSize) >> n4 & this.bitMask);
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
                array3[0] = (dataBuffer.getElem(n2 * this.scanlineStride + n3 / this.dataElementSize) >> n4 & this.bitMask);
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
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[this.numBands];
        }
        final int n3 = this.dataBitOffset + n * this.pixelBitStride;
        array2[0] = (dataBuffer.getElem(n2 * this.scanlineStride + n3 / this.dataElementSize) >> this.dataElementSize - (n3 & this.dataElementSize - 1) - this.pixelBitStride & this.bitMask);
        return array2;
    }
    
    @Override
    public void setDataElements(final int n, final int n2, final Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int transferType = this.getTransferType();
        final int n3 = this.dataBitOffset + n * this.pixelBitStride;
        final int n4 = n2 * this.scanlineStride + n3 / this.dataElementSize;
        final int n5 = this.dataElementSize - (n3 & this.dataElementSize - 1) - this.pixelBitStride;
        final int n6 = dataBuffer.getElem(n4) & ~(this.bitMask << n5);
        switch (transferType) {
            case 0: {
                dataBuffer.setElem(n4, n6 | (((byte[])o)[0] & 0xFF & this.bitMask) << n5);
                break;
            }
            case 1: {
                dataBuffer.setElem(n4, n6 | (((short[])o)[0] & 0xFFFF & this.bitMask) << n5);
                break;
            }
            case 3: {
                dataBuffer.setElem(n4, n6 | (((int[])o)[0] & this.bitMask) << n5);
                break;
            }
        }
    }
    
    @Override
    public void setPixel(final int n, final int n2, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int n3 = this.dataBitOffset + n * this.pixelBitStride;
        final int n4 = n2 * this.scanlineStride + n3 / this.dataElementSize;
        final int n5 = this.dataElementSize - (n3 & this.dataElementSize - 1) - this.pixelBitStride;
        dataBuffer.setElem(n4, (dataBuffer.getElem(n4) & ~(this.bitMask << n5)) | (array[0] & this.bitMask) << n5);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof MultiPixelPackedSampleModel)) {
            return false;
        }
        final MultiPixelPackedSampleModel multiPixelPackedSampleModel = (MultiPixelPackedSampleModel)o;
        return this.width == multiPixelPackedSampleModel.width && this.height == multiPixelPackedSampleModel.height && this.numBands == multiPixelPackedSampleModel.numBands && this.dataType == multiPixelPackedSampleModel.dataType && this.pixelBitStride == multiPixelPackedSampleModel.pixelBitStride && this.bitMask == multiPixelPackedSampleModel.bitMask && this.pixelsPerDataElement == multiPixelPackedSampleModel.pixelsPerDataElement && this.dataElementSize == multiPixelPackedSampleModel.dataElementSize && this.dataBitOffset == multiPixelPackedSampleModel.dataBitOffset && this.scanlineStride == multiPixelPackedSampleModel.scanlineStride;
    }
    
    @Override
    public int hashCode() {
        return ((((((((this.width << 8 ^ this.height) << 8 ^ this.numBands) << 8 ^ this.dataType) << 8 ^ this.pixelBitStride) << 8 ^ this.bitMask) << 8 ^ this.pixelsPerDataElement) << 8 ^ this.dataElementSize) << 8 ^ this.dataBitOffset) << 8 ^ this.scanlineStride;
    }
}
