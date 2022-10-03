package java.awt.image;

public final class BandedSampleModel extends ComponentSampleModel
{
    public BandedSampleModel(final int n, final int n2, final int n3, final int n4) {
        super(n, n2, n3, 1, n2, createIndicesArray(n4), createOffsetArray(n4));
    }
    
    public BandedSampleModel(final int n, final int n2, final int n3, final int n4, final int[] array, final int[] array2) {
        super(n, n2, n3, 1, n4, array, array2);
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        int[] orderBands;
        if (this.numBanks == 1) {
            orderBands = this.orderBands(this.bandOffsets, n * n2);
        }
        else {
            orderBands = new int[this.bandOffsets.length];
        }
        return new BandedSampleModel(this.dataType, n, n2, n, this.bankIndices, orderBands);
    }
    
    @Override
    public SampleModel createSubsetSampleModel(final int[] array) {
        if (array.length > this.bankIndices.length) {
            throw new RasterFormatException("There are only " + this.bankIndices.length + " bands");
        }
        final int[] array2 = new int[array.length];
        final int[] array3 = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = this.bankIndices[array[i]];
            array3[i] = this.bandOffsets[array[i]];
        }
        return new BandedSampleModel(this.dataType, this.width, this.height, this.scanlineStride, array2, array3);
    }
    
    @Override
    public DataBuffer createDataBuffer() {
        final int n = this.scanlineStride * this.height;
        DataBuffer dataBuffer = null;
        switch (this.dataType) {
            case 0: {
                dataBuffer = new DataBufferByte(n, this.numBanks);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(n, this.numBanks);
                break;
            }
            case 2: {
                dataBuffer = new DataBufferShort(n, this.numBanks);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(n, this.numBanks);
                break;
            }
            case 4: {
                dataBuffer = new DataBufferFloat(n, this.numBanks);
                break;
            }
            case 5: {
                dataBuffer = new DataBufferDouble(n, this.numBanks);
                break;
            }
            default: {
                throw new IllegalArgumentException("dataType is not one of the supported types.");
            }
        }
        return dataBuffer;
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int transferType = this.getTransferType();
        final int numDataElements = this.getNumDataElements();
        final int n3 = n2 * this.scanlineStride + n;
        switch (transferType) {
            case 0: {
                byte[] array;
                if (o == null) {
                    array = new byte[numDataElements];
                }
                else {
                    array = (byte[])o;
                }
                for (int i = 0; i < numDataElements; ++i) {
                    array[i] = (byte)dataBuffer.getElem(this.bankIndices[i], n3 + this.bandOffsets[i]);
                }
                o = array;
                break;
            }
            case 1:
            case 2: {
                short[] array2;
                if (o == null) {
                    array2 = new short[numDataElements];
                }
                else {
                    array2 = (short[])o;
                }
                for (int j = 0; j < numDataElements; ++j) {
                    array2[j] = (short)dataBuffer.getElem(this.bankIndices[j], n3 + this.bandOffsets[j]);
                }
                o = array2;
                break;
            }
            case 3: {
                int[] array3;
                if (o == null) {
                    array3 = new int[numDataElements];
                }
                else {
                    array3 = (int[])o;
                }
                for (int k = 0; k < numDataElements; ++k) {
                    array3[k] = dataBuffer.getElem(this.bankIndices[k], n3 + this.bandOffsets[k]);
                }
                o = array3;
                break;
            }
            case 4: {
                float[] array4;
                if (o == null) {
                    array4 = new float[numDataElements];
                }
                else {
                    array4 = (float[])o;
                }
                for (int l = 0; l < numDataElements; ++l) {
                    array4[l] = dataBuffer.getElemFloat(this.bankIndices[l], n3 + this.bandOffsets[l]);
                }
                o = array4;
                break;
            }
            case 5: {
                double[] array5;
                if (o == null) {
                    array5 = new double[numDataElements];
                }
                else {
                    array5 = (double[])o;
                }
                for (int n4 = 0; n4 < numDataElements; ++n4) {
                    array5[n4] = dataBuffer.getElemDouble(this.bankIndices[n4], n3 + this.bandOffsets[n4]);
                }
                o = array5;
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
        final int n3 = n2 * this.scanlineStride + n;
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = dataBuffer.getElem(this.bankIndices[i], n3 + this.bandOffsets[i]);
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
        for (int i = 0; i < this.numBands; ++i) {
            int n7 = n2 * this.scanlineStride + n + this.bandOffsets[i];
            int n8 = i;
            final int n9 = this.bankIndices[i];
            for (int j = 0; j < n4; ++j) {
                int n10 = n7;
                for (int k = 0; k < n3; ++k) {
                    array2[n8] = dataBuffer.getElem(n9, n10++);
                    n8 += this.numBands;
                }
                n7 += this.scanlineStride;
            }
        }
        return array2;
    }
    
    @Override
    public int getSample(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return dataBuffer.getElem(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3]);
    }
    
    @Override
    public float getSampleFloat(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return dataBuffer.getElemFloat(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3]);
    }
    
    @Override
    public double getSampleDouble(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return dataBuffer.getElemDouble(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3]);
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
        int n6 = n2 * this.scanlineStride + n + this.bandOffsets[n5];
        int n7 = 0;
        final int n8 = this.bankIndices[n5];
        for (int i = 0; i < n4; ++i) {
            int n9 = n6;
            for (int j = 0; j < n3; ++j) {
                array2[n7++] = dataBuffer.getElem(n8, n9++);
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
        final int transferType = this.getTransferType();
        final int numDataElements = this.getNumDataElements();
        final int n3 = n2 * this.scanlineStride + n;
        switch (transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                for (int i = 0; i < numDataElements; ++i) {
                    dataBuffer.setElem(this.bankIndices[i], n3 + this.bandOffsets[i], array[i] & 0xFF);
                }
                break;
            }
            case 1:
            case 2: {
                final short[] array2 = (short[])o;
                for (int j = 0; j < numDataElements; ++j) {
                    dataBuffer.setElem(this.bankIndices[j], n3 + this.bandOffsets[j], array2[j] & 0xFFFF);
                }
                break;
            }
            case 3: {
                final int[] array3 = (int[])o;
                for (int k = 0; k < numDataElements; ++k) {
                    dataBuffer.setElem(this.bankIndices[k], n3 + this.bandOffsets[k], array3[k]);
                }
                break;
            }
            case 4: {
                final float[] array4 = (float[])o;
                for (int l = 0; l < numDataElements; ++l) {
                    dataBuffer.setElemFloat(this.bankIndices[l], n3 + this.bandOffsets[l], array4[l]);
                }
                break;
            }
            case 5: {
                final double[] array5 = (double[])o;
                for (int n4 = 0; n4 < numDataElements; ++n4) {
                    dataBuffer.setElemDouble(this.bankIndices[n4], n3 + this.bandOffsets[n4], array5[n4]);
                }
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
        for (int i = 0; i < this.numBands; ++i) {
            dataBuffer.setElem(this.bankIndices[i], n3 + this.bandOffsets[i], array[i]);
        }
    }
    
    @Override
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        final int n5 = n + n3;
        final int n6 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n5 < 0 || n5 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n6 < 0 || n6 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        for (int i = 0; i < this.numBands; ++i) {
            int n7 = n2 * this.scanlineStride + n + this.bandOffsets[i];
            int n8 = i;
            final int n9 = this.bankIndices[i];
            for (int j = 0; j < n4; ++j) {
                int n10 = n7;
                for (int k = 0; k < n3; ++k) {
                    dataBuffer.setElem(n9, n10++, array[n8]);
                    n8 += this.numBands;
                }
                n7 += this.scanlineStride;
            }
        }
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final int n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElem(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final float n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElemFloat(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final double n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElemDouble(this.bankIndices[n3], n2 * this.scanlineStride + n + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n + n3 > this.width || n2 + n4 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n6 = n2 * this.scanlineStride + n + this.bandOffsets[n5];
        int n7 = 0;
        final int n8 = this.bankIndices[n5];
        for (int i = 0; i < n4; ++i) {
            int n9 = n6;
            for (int j = 0; j < n3; ++j) {
                dataBuffer.setElem(n8, n9++, array[n7++]);
            }
            n6 += this.scanlineStride;
        }
    }
    
    private static int[] createOffsetArray(final int n) {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = 0;
        }
        return array;
    }
    
    private static int[] createIndicesArray(final int n) {
        final int[] array = new int[n];
        for (int i = 0; i < n; ++i) {
            array[i] = i;
        }
        return array;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() ^ 0x2;
    }
}
