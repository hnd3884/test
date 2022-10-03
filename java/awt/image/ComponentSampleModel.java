package java.awt.image;

import java.util.Arrays;

public class ComponentSampleModel extends SampleModel
{
    protected int[] bandOffsets;
    protected int[] bankIndices;
    protected int numBands;
    protected int numBanks;
    protected int scanlineStride;
    protected int pixelStride;
    
    private static native void initIDs();
    
    public ComponentSampleModel(final int dataType, final int n, final int n2, final int pixelStride, final int scanlineStride, final int[] array) {
        super(dataType, n, n2, array.length);
        this.numBands = 1;
        this.numBanks = 1;
        this.dataType = dataType;
        this.pixelStride = pixelStride;
        this.scanlineStride = scanlineStride;
        this.bandOffsets = array.clone();
        this.numBands = this.bandOffsets.length;
        if (pixelStride < 0) {
            throw new IllegalArgumentException("Pixel stride must be >= 0");
        }
        if (scanlineStride < 0) {
            throw new IllegalArgumentException("Scanline stride must be >= 0");
        }
        if (this.numBands < 1) {
            throw new IllegalArgumentException("Must have at least one band.");
        }
        if (dataType < 0 || dataType > 5) {
            throw new IllegalArgumentException("Unsupported dataType.");
        }
        this.bankIndices = new int[this.numBands];
        for (int i = 0; i < this.numBands; ++i) {
            this.bankIndices[i] = 0;
        }
        this.verify();
    }
    
    public ComponentSampleModel(final int dataType, final int n, final int n2, final int pixelStride, final int scanlineStride, final int[] array, final int[] array2) {
        super(dataType, n, n2, array2.length);
        this.numBands = 1;
        this.numBanks = 1;
        this.dataType = dataType;
        this.pixelStride = pixelStride;
        this.scanlineStride = scanlineStride;
        this.bandOffsets = array2.clone();
        this.bankIndices = array.clone();
        if (pixelStride < 0) {
            throw new IllegalArgumentException("Pixel stride must be >= 0");
        }
        if (scanlineStride < 0) {
            throw new IllegalArgumentException("Scanline stride must be >= 0");
        }
        if (dataType < 0 || dataType > 5) {
            throw new IllegalArgumentException("Unsupported dataType.");
        }
        int n3 = this.bankIndices[0];
        if (n3 < 0) {
            throw new IllegalArgumentException("Index of bank 0 is less than 0 (" + n3 + ")");
        }
        for (int i = 1; i < this.bankIndices.length; ++i) {
            if (this.bankIndices[i] > n3) {
                n3 = this.bankIndices[i];
            }
            else if (this.bankIndices[i] < 0) {
                throw new IllegalArgumentException("Index of bank " + i + " is less than 0 (" + n3 + ")");
            }
        }
        this.numBanks = n3 + 1;
        this.numBands = this.bandOffsets.length;
        if (this.bandOffsets.length != this.bankIndices.length) {
            throw new IllegalArgumentException("Length of bandOffsets must equal length of bankIndices.");
        }
        this.verify();
    }
    
    private void verify() {
        this.getBufferSize();
    }
    
    private int getBufferSize() {
        int max = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            max = Math.max(max, this.bandOffsets[i]);
        }
        if (max < 0 || max > 2147483646) {
            throw new IllegalArgumentException("Invalid band offset");
        }
        if (this.pixelStride < 0 || this.pixelStride > Integer.MAX_VALUE / this.width) {
            throw new IllegalArgumentException("Invalid pixel stride");
        }
        if (this.scanlineStride < 0 || this.scanlineStride > Integer.MAX_VALUE / this.height) {
            throw new IllegalArgumentException("Invalid scanline stride");
        }
        final int n = max + 1;
        final int n2 = this.pixelStride * (this.width - 1);
        if (n2 > Integer.MAX_VALUE - n) {
            throw new IllegalArgumentException("Invalid pixel stride");
        }
        final int n3 = n + n2;
        final int n4 = this.scanlineStride * (this.height - 1);
        if (n4 > Integer.MAX_VALUE - n3) {
            throw new IllegalArgumentException("Invalid scan stride");
        }
        return n3 + n4;
    }
    
    int[] orderBands(final int[] array, final int n) {
        final int[] array2 = new int[array.length];
        final int[] array3 = new int[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = i;
        }
        for (int j = 0; j < array3.length; ++j) {
            int n2 = j;
            for (int k = j + 1; k < array3.length; ++k) {
                if (array[array2[n2]] > array[array2[k]]) {
                    n2 = k;
                }
            }
            array3[array2[n2]] = j * n;
            array2[n2] = array2[j];
        }
        return array3;
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        int min = this.bandOffsets[0];
        int max = this.bandOffsets[0];
        for (int i = 1; i < this.bandOffsets.length; ++i) {
            min = Math.min(min, this.bandOffsets[i]);
            max = Math.max(max, this.bandOffsets[i]);
        }
        final int n3 = max - min;
        final int length = this.bandOffsets.length;
        int abs = Math.abs(this.pixelStride);
        int abs2 = Math.abs(this.scanlineStride);
        final int abs3 = Math.abs(n3);
        int[] array;
        if (abs > abs2) {
            if (abs > abs3) {
                if (abs2 > abs3) {
                    array = new int[this.bandOffsets.length];
                    for (int j = 0; j < length; ++j) {
                        array[j] = this.bandOffsets[j] - min;
                    }
                    abs2 = abs3 + 1;
                    abs = abs2 * n2;
                }
                else {
                    array = this.orderBands(this.bandOffsets, abs2 * n2);
                    abs = length * abs2 * n2;
                }
            }
            else {
                abs = abs2 * n2;
                array = this.orderBands(this.bandOffsets, abs * n);
            }
        }
        else if (abs > abs3) {
            array = new int[this.bandOffsets.length];
            for (int k = 0; k < length; ++k) {
                array[k] = this.bandOffsets[k] - min;
            }
            abs = abs3 + 1;
            abs2 = abs * n;
        }
        else if (abs2 > abs3) {
            array = this.orderBands(this.bandOffsets, abs * n);
            abs2 = length * abs * n;
        }
        else {
            abs2 = abs * n;
            array = this.orderBands(this.bandOffsets, abs2 * n2);
        }
        int n4 = 0;
        if (this.scanlineStride < 0) {
            n4 += abs2 * n2;
            abs2 *= -1;
        }
        if (this.pixelStride < 0) {
            n4 += abs * n;
            abs *= -1;
        }
        for (int l = 0; l < length; ++l) {
            final int[] array2 = array;
            final int n5 = l;
            array2[n5] += n4;
        }
        return new ComponentSampleModel(this.dataType, n, n2, abs, abs2, this.bankIndices, array);
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
        return new ComponentSampleModel(this.dataType, this.width, this.height, this.pixelStride, this.scanlineStride, array2, array3);
    }
    
    @Override
    public DataBuffer createDataBuffer() {
        DataBuffer dataBuffer = null;
        final int bufferSize = this.getBufferSize();
        switch (this.dataType) {
            case 0: {
                dataBuffer = new DataBufferByte(bufferSize, this.numBanks);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort(bufferSize, this.numBanks);
                break;
            }
            case 2: {
                dataBuffer = new DataBufferShort(bufferSize, this.numBanks);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt(bufferSize, this.numBanks);
                break;
            }
            case 4: {
                dataBuffer = new DataBufferFloat(bufferSize, this.numBanks);
                break;
            }
            case 5: {
                dataBuffer = new DataBufferDouble(bufferSize, this.numBanks);
                break;
            }
        }
        return dataBuffer;
    }
    
    public int getOffset(final int n, final int n2) {
        return n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[0];
    }
    
    public int getOffset(final int n, final int n2, final int n3) {
        return n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3];
    }
    
    @Override
    public final int[] getSampleSize() {
        final int[] array = new int[this.numBands];
        final int sampleSize = this.getSampleSize(0);
        for (int i = 0; i < this.numBands; ++i) {
            array[i] = sampleSize;
        }
        return array;
    }
    
    @Override
    public final int getSampleSize(final int n) {
        return DataBuffer.getDataTypeSize(this.dataType);
    }
    
    public final int[] getBankIndices() {
        return this.bankIndices.clone();
    }
    
    public final int[] getBandOffsets() {
        return this.bandOffsets.clone();
    }
    
    public final int getScanlineStride() {
        return this.scanlineStride;
    }
    
    public final int getPixelStride() {
        return this.pixelStride;
    }
    
    @Override
    public final int getNumDataElements() {
        return this.getNumBands();
    }
    
    @Override
    public Object getDataElements(final int n, final int n2, Object o, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        final int transferType = this.getTransferType();
        final int numDataElements = this.getNumDataElements();
        final int n3 = n2 * this.scanlineStride + n * this.pixelStride;
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
        final int n3 = n2 * this.scanlineStride + n * this.pixelStride;
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = dataBuffer.getElem(this.bankIndices[i], n3 + this.bandOffsets[i]);
        }
        return array2;
    }
    
    @Override
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        final int n5 = n + n3;
        final int n6 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n5 < 0 || n5 > this.width || n2 < 0 || n2 >= this.height || n2 > this.height || n6 < 0 || n6 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4 * this.numBands];
        }
        int n7 = n2 * this.scanlineStride + n * this.pixelStride;
        int n8 = 0;
        for (int i = 0; i < n4; ++i) {
            int n9 = n7;
            for (int j = 0; j < n3; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    array2[n8++] = dataBuffer.getElem(this.bankIndices[k], n9 + this.bandOffsets[k]);
                }
                n9 += this.pixelStride;
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
        return dataBuffer.getElem(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3]);
    }
    
    @Override
    public float getSampleFloat(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return dataBuffer.getElemFloat(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3]);
    }
    
    @Override
    public double getSampleDouble(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        return dataBuffer.getElemDouble(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3]);
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
        int n6 = n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n5];
        int n7 = 0;
        for (int i = 0; i < n4; ++i) {
            int n8 = n6;
            for (int j = 0; j < n3; ++j) {
                array2[n7++] = dataBuffer.getElem(this.bankIndices[n5], n8);
                n8 += this.pixelStride;
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
        final int n3 = n2 * this.scanlineStride + n * this.pixelStride;
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
        final int n3 = n2 * this.scanlineStride + n * this.pixelStride;
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
        int n7 = n2 * this.scanlineStride + n * this.pixelStride;
        int n8 = 0;
        for (int i = 0; i < n4; ++i) {
            int n9 = n7;
            for (int j = 0; j < n3; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    dataBuffer.setElem(this.bankIndices[k], n9 + this.bandOffsets[k], array[n8++]);
                }
                n9 += this.pixelStride;
            }
            n7 += this.scanlineStride;
        }
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final int n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElem(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final float n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElemFloat(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSample(final int n, final int n2, final int n3, final double n4, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        dataBuffer.setElemDouble(this.bankIndices[n3], n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n3], n4);
    }
    
    @Override
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        if (n < 0 || n2 < 0 || n + n3 > this.width || n2 + n4 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Coordinate out of bounds!");
        }
        int n6 = n2 * this.scanlineStride + n * this.pixelStride + this.bandOffsets[n5];
        int n7 = 0;
        for (int i = 0; i < n4; ++i) {
            int n8 = n6;
            for (int j = 0; j < n3; ++j) {
                dataBuffer.setElem(this.bankIndices[n5], n8, array[n7++]);
                n8 += this.pixelStride;
            }
            n6 += this.scanlineStride;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof ComponentSampleModel)) {
            return false;
        }
        final ComponentSampleModel componentSampleModel = (ComponentSampleModel)o;
        return this.width == componentSampleModel.width && this.height == componentSampleModel.height && this.numBands == componentSampleModel.numBands && this.dataType == componentSampleModel.dataType && Arrays.equals(this.bandOffsets, componentSampleModel.bandOffsets) && Arrays.equals(this.bankIndices, componentSampleModel.bankIndices) && this.numBands == componentSampleModel.numBands && this.numBanks == componentSampleModel.numBanks && this.scanlineStride == componentSampleModel.scanlineStride && this.pixelStride == componentSampleModel.pixelStride;
    }
    
    @Override
    public int hashCode() {
        int n = (((this.width << 8 ^ this.height) << 8 ^ this.numBands) << 8 ^ this.dataType) << 8;
        for (int i = 0; i < this.bandOffsets.length; ++i) {
            n = (n ^ this.bandOffsets[i]) << 8;
        }
        for (int j = 0; j < this.bankIndices.length; ++j) {
            n = (n ^ this.bankIndices[j]) << 8;
        }
        return (((n ^ this.numBands) << 8 ^ this.numBanks) << 8 ^ this.scanlineStride) << 8 ^ this.pixelStride;
    }
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}
