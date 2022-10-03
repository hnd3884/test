package org.apache.commons.compress.harmony.pack200;

import java.io.IOException;
import java.io.InputStream;

public abstract class Codec
{
    public static final BHSDCodec BCI5;
    public static final BHSDCodec BRANCH5;
    public static final BHSDCodec BYTE1;
    public static final BHSDCodec CHAR3;
    public static final BHSDCodec DELTA5;
    public static final BHSDCodec MDELTA5;
    public static final BHSDCodec SIGNED5;
    public static final BHSDCodec UDELTA5;
    public static final BHSDCodec UNSIGNED5;
    public int lastBandLength;
    
    public abstract int decode(final InputStream p0) throws IOException, Pack200Exception;
    
    public abstract byte[] encode(final int p0, final int p1) throws Pack200Exception;
    
    public abstract byte[] encode(final int p0) throws Pack200Exception;
    
    public abstract int decode(final InputStream p0, final long p1) throws IOException, Pack200Exception;
    
    public int[] decodeInts(final int n, final InputStream in) throws IOException, Pack200Exception {
        this.lastBandLength = 0;
        final int[] result = new int[n];
        int last = 0;
        for (int i = 0; i < n; ++i) {
            last = (result[i] = this.decode(in, last));
        }
        return result;
    }
    
    public int[] decodeInts(final int n, final InputStream in, final int firstValue) throws IOException, Pack200Exception {
        final int[] result = new int[n + 1];
        result[0] = firstValue;
        int last = firstValue;
        for (int i = 1; i < n + 1; ++i) {
            last = (result[i] = this.decode(in, last));
        }
        return result;
    }
    
    public byte[] encode(final int[] ints) throws Pack200Exception {
        int total = 0;
        final byte[][] bytes = new byte[ints.length][];
        for (int i = 0; i < ints.length; ++i) {
            bytes[i] = this.encode(ints[i], (i > 0) ? ints[i - 1] : 0);
            total += bytes[i].length;
        }
        final byte[] encoded = new byte[total];
        int index = 0;
        for (int j = 0; j < bytes.length; ++j) {
            System.arraycopy(bytes[j], 0, encoded, index, bytes[j].length);
            index += bytes[j].length;
        }
        return encoded;
    }
    
    static {
        BCI5 = new BHSDCodec(5, 4);
        BRANCH5 = new BHSDCodec(5, 4, 2);
        BYTE1 = new BHSDCodec(1, 256);
        CHAR3 = new BHSDCodec(3, 128);
        DELTA5 = new BHSDCodec(5, 64, 1, 1);
        MDELTA5 = new BHSDCodec(5, 64, 2, 1);
        SIGNED5 = new BHSDCodec(5, 64, 1);
        UDELTA5 = new BHSDCodec(5, 64, 0, 1);
        UNSIGNED5 = new BHSDCodec(5, 64);
    }
}
