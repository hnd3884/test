package java.awt.image;

public abstract class SampleModel
{
    protected int width;
    protected int height;
    protected int numBands;
    protected int dataType;
    
    private static native void initIDs();
    
    public SampleModel(final int dataType, final int width, final int height, final int numBands) {
        final long n = width * (long)height;
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width (" + width + ") and height (" + height + ") must be > 0");
        }
        if (n >= 2147483647L) {
            throw new IllegalArgumentException("Dimensions (width=" + width + " height=" + height + ") are too large");
        }
        if (dataType < 0 || (dataType > 5 && dataType != 32)) {
            throw new IllegalArgumentException("Unsupported dataType: " + dataType);
        }
        if (numBands <= 0) {
            throw new IllegalArgumentException("Number of bands must be > 0");
        }
        this.dataType = dataType;
        this.width = width;
        this.height = height;
        this.numBands = numBands;
    }
    
    public final int getWidth() {
        return this.width;
    }
    
    public final int getHeight() {
        return this.height;
    }
    
    public final int getNumBands() {
        return this.numBands;
    }
    
    public abstract int getNumDataElements();
    
    public final int getDataType() {
        return this.dataType;
    }
    
    public int getTransferType() {
        return this.dataType;
    }
    
    public int[] getPixel(final int n, final int n2, final int[] array, final DataBuffer dataBuffer) {
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[this.numBands];
        }
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = this.getSample(n, n2, i, dataBuffer);
        }
        return array2;
    }
    
    public abstract Object getDataElements(final int p0, final int p1, final Object p2, final DataBuffer p3);
    
    public Object getDataElements(final int n, final int n2, final int n3, final int n4, Object o, final DataBuffer dataBuffer) {
        final int transferType = this.getTransferType();
        final int numDataElements = this.getNumDataElements();
        int n5 = 0;
        Object o2 = null;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        switch (transferType) {
            case 0: {
                byte[] array;
                if (o == null) {
                    array = new byte[numDataElements * n3 * n4];
                }
                else {
                    array = (byte[])o;
                }
                for (int i = n2; i < n7; ++i) {
                    for (int j = n; j < n6; ++j) {
                        o2 = this.getDataElements(j, i, o2, dataBuffer);
                        final byte[] array2 = (byte[])o2;
                        for (int k = 0; k < numDataElements; ++k) {
                            array[n5++] = array2[k];
                        }
                    }
                }
                o = array;
                break;
            }
            case 1:
            case 2: {
                short[] array3;
                if (o == null) {
                    array3 = new short[numDataElements * n3 * n4];
                }
                else {
                    array3 = (short[])o;
                }
                for (int l = n2; l < n7; ++l) {
                    for (int n8 = n; n8 < n6; ++n8) {
                        o2 = this.getDataElements(n8, l, o2, dataBuffer);
                        final short[] array4 = (short[])o2;
                        for (int n9 = 0; n9 < numDataElements; ++n9) {
                            array3[n5++] = array4[n9];
                        }
                    }
                }
                o = array3;
                break;
            }
            case 3: {
                int[] array5;
                if (o == null) {
                    array5 = new int[numDataElements * n3 * n4];
                }
                else {
                    array5 = (int[])o;
                }
                for (int n10 = n2; n10 < n7; ++n10) {
                    for (int n11 = n; n11 < n6; ++n11) {
                        o2 = this.getDataElements(n11, n10, o2, dataBuffer);
                        final int[] array6 = (int[])o2;
                        for (int n12 = 0; n12 < numDataElements; ++n12) {
                            array5[n5++] = array6[n12];
                        }
                    }
                }
                o = array5;
                break;
            }
            case 4: {
                float[] array7;
                if (o == null) {
                    array7 = new float[numDataElements * n3 * n4];
                }
                else {
                    array7 = (float[])o;
                }
                for (int n13 = n2; n13 < n7; ++n13) {
                    for (int n14 = n; n14 < n6; ++n14) {
                        o2 = this.getDataElements(n14, n13, o2, dataBuffer);
                        final float[] array8 = (float[])o2;
                        for (int n15 = 0; n15 < numDataElements; ++n15) {
                            array7[n5++] = array8[n15];
                        }
                    }
                }
                o = array7;
                break;
            }
            case 5: {
                double[] array9;
                if (o == null) {
                    array9 = new double[numDataElements * n3 * n4];
                }
                else {
                    array9 = (double[])o;
                }
                for (int n16 = n2; n16 < n7; ++n16) {
                    for (int n17 = n; n17 < n6; ++n17) {
                        o2 = this.getDataElements(n17, n16, o2, dataBuffer);
                        final double[] array10 = (double[])o2;
                        for (int n18 = 0; n18 < numDataElements; ++n18) {
                            array9[n5++] = array10[n18];
                        }
                    }
                }
                o = array9;
                break;
            }
        }
        return o;
    }
    
    public abstract void setDataElements(final int p0, final int p1, final Object p2, final DataBuffer p3);
    
    public void setDataElements(final int n, final int n2, final int n3, final int n4, final Object o, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int transferType = this.getTransferType();
        final int numDataElements = this.getNumDataElements();
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        switch (transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                final byte[] array2 = new byte[numDataElements];
                for (int i = n2; i < n7; ++i) {
                    for (int j = n; j < n6; ++j) {
                        for (int k = 0; k < numDataElements; ++k) {
                            array2[k] = array[n5++];
                        }
                        this.setDataElements(j, i, array2, dataBuffer);
                    }
                }
                break;
            }
            case 1:
            case 2: {
                final short[] array3 = (short[])o;
                final short[] array4 = new short[numDataElements];
                for (int l = n2; l < n7; ++l) {
                    for (int n8 = n; n8 < n6; ++n8) {
                        for (int n9 = 0; n9 < numDataElements; ++n9) {
                            array4[n9] = array3[n5++];
                        }
                        this.setDataElements(n8, l, array4, dataBuffer);
                    }
                }
                break;
            }
            case 3: {
                final int[] array5 = (int[])o;
                final int[] array6 = new int[numDataElements];
                for (int n10 = n2; n10 < n7; ++n10) {
                    for (int n11 = n; n11 < n6; ++n11) {
                        for (int n12 = 0; n12 < numDataElements; ++n12) {
                            array6[n12] = array5[n5++];
                        }
                        this.setDataElements(n11, n10, array6, dataBuffer);
                    }
                }
                break;
            }
            case 4: {
                final float[] array7 = (float[])o;
                final float[] array8 = new float[numDataElements];
                for (int n13 = n2; n13 < n7; ++n13) {
                    for (int n14 = n; n14 < n6; ++n14) {
                        for (int n15 = 0; n15 < numDataElements; ++n15) {
                            array8[n15] = array7[n5++];
                        }
                        this.setDataElements(n14, n13, array8, dataBuffer);
                    }
                }
                break;
            }
            case 5: {
                final double[] array9 = (double[])o;
                final double[] array10 = new double[numDataElements];
                for (int n16 = n2; n16 < n7; ++n16) {
                    for (int n17 = n; n17 < n6; ++n17) {
                        for (int n18 = 0; n18 < numDataElements; ++n18) {
                            array10[n18] = array9[n5++];
                        }
                        this.setDataElements(n17, n16, array10, dataBuffer);
                    }
                }
                break;
            }
        }
    }
    
    public float[] getPixel(final int n, final int n2, final float[] array, final DataBuffer dataBuffer) {
        float[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new float[this.numBands];
        }
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = this.getSampleFloat(n, n2, i, dataBuffer);
        }
        return array2;
    }
    
    public double[] getPixel(final int n, final int n2, final double[] array, final DataBuffer dataBuffer) {
        double[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new double[this.numBands];
        }
        for (int i = 0; i < this.numBands; ++i) {
            array2[i] = this.getSampleDouble(n, n2, i, dataBuffer);
        }
        return array2;
    }
    
    public int[] getPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[this.numBands * n3 * n4];
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    array2[n5++] = this.getSample(j, i, k, dataBuffer);
                }
            }
        }
        return array2;
    }
    
    public float[] getPixels(final int n, final int n2, final int n3, final int n4, final float[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        float[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new float[this.numBands * n3 * n4];
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    array2[n5++] = this.getSampleFloat(j, i, k, dataBuffer);
                }
            }
        }
        return array2;
    }
    
    public double[] getPixels(final int n, final int n2, final int n3, final int n4, final double[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        double[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new double[this.numBands * n3 * n4];
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    array2[n5++] = this.getSampleDouble(j, i, k, dataBuffer);
                }
            }
        }
        return array2;
    }
    
    public abstract int getSample(final int p0, final int p1, final int p2, final DataBuffer p3);
    
    public float getSampleFloat(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        return (float)this.getSample(n, n2, n3, dataBuffer);
    }
    
    public double getSampleDouble(final int n, final int n2, final int n3, final DataBuffer dataBuffer) {
        return this.getSample(n, n2, n3, dataBuffer);
    }
    
    public int[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n7 < n || n7 > this.width || n2 < 0 || n8 < n2 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        int[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new int[n3 * n4];
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                array2[n6++] = this.getSample(j, i, n5, dataBuffer);
            }
        }
        return array2;
    }
    
    public float[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final float[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n7 < n || n7 > this.width || n2 < 0 || n8 < n2 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
        }
        float[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new float[n3 * n4];
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                array2[n6++] = this.getSampleFloat(j, i, n5, dataBuffer);
            }
        }
        return array2;
    }
    
    public double[] getSamples(final int n, final int n2, final int n3, final int n4, final int n5, final double[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n7 < n || n7 > this.width || n2 < 0 || n8 < n2 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates");
        }
        double[] array2;
        if (array != null) {
            array2 = array;
        }
        else {
            array2 = new double[n3 * n4];
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                array2[n6++] = this.getSampleDouble(j, i, n5, dataBuffer);
            }
        }
        return array2;
    }
    
    public void setPixel(final int n, final int n2, final int[] array, final DataBuffer dataBuffer) {
        for (int i = 0; i < this.numBands; ++i) {
            this.setSample(n, n2, i, array[i], dataBuffer);
        }
    }
    
    public void setPixel(final int n, final int n2, final float[] array, final DataBuffer dataBuffer) {
        for (int i = 0; i < this.numBands; ++i) {
            this.setSample(n, n2, i, array[i], dataBuffer);
        }
    }
    
    public void setPixel(final int n, final int n2, final double[] array, final DataBuffer dataBuffer) {
        for (int i = 0; i < this.numBands; ++i) {
            this.setSample(n, n2, i, array[i], dataBuffer);
        }
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    this.setSample(j, i, k, array[n5++], dataBuffer);
                }
            }
        }
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final float[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    this.setSample(j, i, k, array[n5++], dataBuffer);
                }
            }
        }
    }
    
    public void setPixels(final int n, final int n2, final int n3, final int n4, final double[] array, final DataBuffer dataBuffer) {
        int n5 = 0;
        final int n6 = n + n3;
        final int n7 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n6 < 0 || n6 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n7 < 0 || n7 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n7; ++i) {
            for (int j = n; j < n6; ++j) {
                for (int k = 0; k < this.numBands; ++k) {
                    this.setSample(j, i, k, array[n5++], dataBuffer);
                }
            }
        }
    }
    
    public abstract void setSample(final int p0, final int p1, final int p2, final int p3, final DataBuffer p4);
    
    public void setSample(final int n, final int n2, final int n3, final float n4, final DataBuffer dataBuffer) {
        this.setSample(n, n2, n3, (int)n4, dataBuffer);
    }
    
    public void setSample(final int n, final int n2, final int n3, final double n4, final DataBuffer dataBuffer) {
        this.setSample(n, n2, n3, (int)n4, dataBuffer);
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final int[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n7 < 0 || n7 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n8 < 0 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                this.setSample(j, i, n5, array[n6++], dataBuffer);
            }
        }
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final float[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n7 < 0 || n7 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n8 < 0 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                this.setSample(j, i, n5, array[n6++], dataBuffer);
            }
        }
    }
    
    public void setSamples(final int n, final int n2, final int n3, final int n4, final int n5, final double[] array, final DataBuffer dataBuffer) {
        int n6 = 0;
        final int n7 = n + n3;
        final int n8 = n2 + n4;
        if (n < 0 || n >= this.width || n3 > this.width || n7 < 0 || n7 > this.width || n2 < 0 || n2 >= this.height || n4 > this.height || n8 < 0 || n8 > this.height) {
            throw new ArrayIndexOutOfBoundsException("Invalid coordinates.");
        }
        for (int i = n2; i < n8; ++i) {
            for (int j = n; j < n7; ++j) {
                this.setSample(j, i, n5, array[n6++], dataBuffer);
            }
        }
    }
    
    public abstract SampleModel createCompatibleSampleModel(final int p0, final int p1);
    
    public abstract SampleModel createSubsetSampleModel(final int[] p0);
    
    public abstract DataBuffer createDataBuffer();
    
    public abstract int[] getSampleSize();
    
    public abstract int getSampleSize(final int p0);
    
    static {
        ColorModel.loadLibraries();
        initIDs();
    }
}
