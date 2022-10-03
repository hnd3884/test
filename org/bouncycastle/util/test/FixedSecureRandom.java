package org.bouncycastle.util.test;

import org.bouncycastle.util.encoders.Hex;
import java.util.Random;
import org.bouncycastle.util.Pack;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.security.Provider;
import java.security.SecureRandomSpi;
import java.math.BigInteger;
import java.security.SecureRandom;

public class FixedSecureRandom extends SecureRandom
{
    private static java.math.BigInteger REGULAR;
    private static java.math.BigInteger ANDROID;
    private static java.math.BigInteger CLASSPATH;
    private static final boolean isAndroidStyle;
    private static final boolean isClasspathStyle;
    private static final boolean isRegularStyle;
    private byte[] _data;
    private int _index;
    
    public FixedSecureRandom(final byte[] array) {
        this(new Source[] { new Data(array) });
    }
    
    public FixedSecureRandom(final byte[][] array) {
        this(buildDataArray(array));
    }
    
    private static Data[] buildDataArray(final byte[][] array) {
        final Data[] array2 = new Data[array.length];
        for (int i = 0; i != array.length; ++i) {
            array2[i] = new Data(array[i]);
        }
        return array2;
    }
    
    public FixedSecureRandom(final Source[] array) {
        super(null, new DummyProvider());
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (FixedSecureRandom.isRegularStyle) {
            if (FixedSecureRandom.isClasspathStyle) {
                for (int i = 0; i != array.length; ++i) {
                    try {
                        if (array[i] instanceof BigInteger) {
                            final byte[] data = array[i].data;
                            final int n = data.length - data.length % 4;
                            for (int j = data.length - n - 1; j >= 0; --j) {
                                byteArrayOutputStream.write(data[j]);
                            }
                            for (int k = data.length - n; k < data.length; k += 4) {
                                byteArrayOutputStream.write(data, k, 4);
                            }
                        }
                        else {
                            byteArrayOutputStream.write(array[i].data);
                        }
                    }
                    catch (final IOException ex) {
                        throw new IllegalArgumentException("can't save value source.");
                    }
                }
            }
            else {
                for (int l = 0; l != array.length; ++l) {
                    try {
                        byteArrayOutputStream.write(array[l].data);
                    }
                    catch (final IOException ex2) {
                        throw new IllegalArgumentException("can't save value source.");
                    }
                }
            }
        }
        else {
            if (!FixedSecureRandom.isAndroidStyle) {
                throw new IllegalStateException("Unrecognized BigInteger implementation");
            }
            for (int n2 = 0; n2 != array.length; ++n2) {
                try {
                    if (array[n2] instanceof BigInteger) {
                        final byte[] data2 = array[n2].data;
                        final int n3 = data2.length - data2.length % 4;
                        for (int n4 = 0; n4 < n3; n4 += 4) {
                            byteArrayOutputStream.write(data2, data2.length - (n4 + 4), 4);
                        }
                        if (data2.length - n3 != 0) {
                            for (int n5 = 0; n5 != 4 - (data2.length - n3); ++n5) {
                                byteArrayOutputStream.write(0);
                            }
                        }
                        for (int n6 = 0; n6 != data2.length - n3; ++n6) {
                            byteArrayOutputStream.write(data2[n3 + n6]);
                        }
                    }
                    else {
                        byteArrayOutputStream.write(array[n2].data);
                    }
                }
                catch (final IOException ex3) {
                    throw new IllegalArgumentException("can't save value source.");
                }
            }
        }
        this._data = byteArrayOutputStream.toByteArray();
    }
    
    @Override
    public void nextBytes(final byte[] array) {
        System.arraycopy(this._data, this._index, array, 0, array.length);
        this._index += array.length;
    }
    
    @Override
    public byte[] generateSeed(final int n) {
        final byte[] array = new byte[n];
        this.nextBytes(array);
        return array;
    }
    
    @Override
    public int nextInt() {
        return 0x0 | this.nextValue() << 24 | this.nextValue() << 16 | this.nextValue() << 8 | this.nextValue();
    }
    
    @Override
    public long nextLong() {
        return 0x0L | (long)this.nextValue() << 56 | (long)this.nextValue() << 48 | (long)this.nextValue() << 40 | (long)this.nextValue() << 32 | (long)this.nextValue() << 24 | (long)this.nextValue() << 16 | (long)this.nextValue() << 8 | (long)this.nextValue();
    }
    
    public boolean isExhausted() {
        return this._index == this._data.length;
    }
    
    private int nextValue() {
        return this._data[this._index++] & 0xFF;
    }
    
    private static byte[] expandToBitLength(final int n, final byte[] array) {
        if ((n + 7) / 8 > array.length) {
            final byte[] array2 = new byte[(n + 7) / 8];
            System.arraycopy(array, 0, array2, array2.length - array.length, array.length);
            if (FixedSecureRandom.isAndroidStyle && n % 8 != 0) {
                Pack.intToBigEndian(Pack.bigEndianToInt(array2, 0) << 8 - n % 8, array2, 0);
            }
            return array2;
        }
        if (FixedSecureRandom.isAndroidStyle && n < array.length * 8 && n % 8 != 0) {
            Pack.intToBigEndian(Pack.bigEndianToInt(array, 0) << 8 - n % 8, array, 0);
        }
        return array;
    }
    
    static {
        FixedSecureRandom.REGULAR = new java.math.BigInteger("01020304ffffffff0506070811111111", 16);
        FixedSecureRandom.ANDROID = new java.math.BigInteger("1111111105060708ffffffff01020304", 16);
        FixedSecureRandom.CLASSPATH = new java.math.BigInteger("3020104ffffffff05060708111111", 16);
        final java.math.BigInteger bigInteger = new java.math.BigInteger(128, new RandomChecker());
        final java.math.BigInteger bigInteger2 = new java.math.BigInteger(120, new RandomChecker());
        isAndroidStyle = bigInteger.equals(FixedSecureRandom.ANDROID);
        isRegularStyle = bigInteger.equals(FixedSecureRandom.REGULAR);
        isClasspathStyle = bigInteger2.equals(FixedSecureRandom.CLASSPATH);
    }
    
    public static class BigInteger extends Source
    {
        public BigInteger(final byte[] array) {
            super(array);
        }
        
        public BigInteger(final int n, final byte[] array) {
            super(expandToBitLength(n, array));
        }
        
        public BigInteger(final String s) {
            this(Hex.decode(s));
        }
        
        public BigInteger(final int n, final String s) {
            super(expandToBitLength(n, Hex.decode(s)));
        }
    }
    
    public static class Data extends Source
    {
        public Data(final byte[] array) {
            super(array);
        }
    }
    
    public static class Source
    {
        byte[] data;
        
        Source(final byte[] data) {
            this.data = data;
        }
    }
    
    private static class DummyProvider extends Provider
    {
        DummyProvider() {
            super("BCFIPS_FIXED_RNG", 1.0, "BCFIPS Fixed Secure Random Provider");
        }
    }
    
    private static class RandomChecker extends SecureRandom
    {
        byte[] data;
        int index;
        
        RandomChecker() {
            super(null, new DummyProvider());
            this.data = Hex.decode("01020304ffffffff0506070811111111");
            this.index = 0;
        }
        
        @Override
        public void nextBytes(final byte[] array) {
            System.arraycopy(this.data, this.index, array, 0, array.length);
            this.index += array.length;
        }
    }
}
