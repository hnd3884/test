package sun.security.util;

import java.util.Arrays;
import java.io.ByteArrayOutputStream;

public class BitArray
{
    private byte[] repn;
    private int length;
    private static final int BITS_PER_UNIT = 8;
    private static final byte[][] NYBBLE;
    private static final int BYTES_PER_LINE = 8;
    
    private static int subscript(final int n) {
        return n / 8;
    }
    
    private static int position(final int n) {
        return 1 << 7 - n % 8;
    }
    
    public BitArray(final int length) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        this.length = length;
        this.repn = new byte[(length + 8 - 1) / 8];
    }
    
    public BitArray(final int length, final byte[] array) throws IllegalArgumentException {
        if (length < 0) {
            throw new IllegalArgumentException("Negative length for BitArray");
        }
        if (array.length * 8 < length) {
            throw new IllegalArgumentException("Byte array too short to represent bit array of given length");
        }
        this.length = length;
        final int n = (length + 8 - 1) / 8;
        final byte b = (byte)(255 << n * 8 - length);
        System.arraycopy(array, 0, this.repn = new byte[n], 0, n);
        if (n > 0) {
            final byte[] repn = this.repn;
            final int n2 = n - 1;
            repn[n2] &= b;
        }
    }
    
    public BitArray(final boolean[] array) {
        this.length = array.length;
        this.repn = new byte[(this.length + 7) / 8];
        for (int i = 0; i < this.length; ++i) {
            this.set(i, array[i]);
        }
    }
    
    private BitArray(final BitArray bitArray) {
        this.length = bitArray.length;
        this.repn = bitArray.repn.clone();
    }
    
    public boolean get(final int n) throws ArrayIndexOutOfBoundsException {
        if (n < 0 || n >= this.length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(n));
        }
        return (this.repn[subscript(n)] & position(n)) != 0x0;
    }
    
    public void set(final int n, final boolean b) throws ArrayIndexOutOfBoundsException {
        if (n < 0 || n >= this.length) {
            throw new ArrayIndexOutOfBoundsException(Integer.toString(n));
        }
        final int subscript = subscript(n);
        final int position = position(n);
        if (b) {
            final byte[] repn = this.repn;
            final int n2 = subscript;
            repn[n2] |= (byte)position;
        }
        else {
            final byte[] repn2 = this.repn;
            final int n3 = subscript;
            repn2[n3] &= (byte)~position;
        }
    }
    
    public int length() {
        return this.length;
    }
    
    public byte[] toByteArray() {
        return this.repn.clone();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof BitArray)) {
            return false;
        }
        final BitArray bitArray = (BitArray)o;
        if (bitArray.length != this.length) {
            return false;
        }
        for (int i = 0; i < this.repn.length; ++i) {
            if (this.repn[i] != bitArray.repn[i]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean[] toBooleanArray() {
        final boolean[] array = new boolean[this.length];
        for (int i = 0; i < this.length; ++i) {
            array[i] = this.get(i);
        }
        return array;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        for (int i = 0; i < this.repn.length; ++i) {
            n = 31 * n + this.repn[i];
        }
        return n ^ this.length;
    }
    
    public Object clone() {
        return new BitArray(this);
    }
    
    @Override
    public String toString() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < this.repn.length - 1; ++i) {
            byteArrayOutputStream.write(BitArray.NYBBLE[this.repn[i] >> 4 & 0xF], 0, 4);
            byteArrayOutputStream.write(BitArray.NYBBLE[this.repn[i] & 0xF], 0, 4);
            if (i % 8 == 7) {
                byteArrayOutputStream.write(10);
            }
            else {
                byteArrayOutputStream.write(32);
            }
        }
        for (int j = 8 * (this.repn.length - 1); j < this.length; ++j) {
            byteArrayOutputStream.write(this.get(j) ? 49 : 48);
        }
        return new String(byteArrayOutputStream.toByteArray());
    }
    
    public BitArray truncate() {
        for (int i = this.length - 1; i >= 0; --i) {
            if (this.get(i)) {
                return new BitArray(i + 1, Arrays.copyOf(this.repn, (i + 8) / 8));
            }
        }
        return new BitArray(1);
    }
    
    static {
        NYBBLE = new byte[][] { { 48, 48, 48, 48 }, { 48, 48, 48, 49 }, { 48, 48, 49, 48 }, { 48, 48, 49, 49 }, { 48, 49, 48, 48 }, { 48, 49, 48, 49 }, { 48, 49, 49, 48 }, { 48, 49, 49, 49 }, { 49, 48, 48, 48 }, { 49, 48, 48, 49 }, { 49, 48, 49, 48 }, { 49, 48, 49, 49 }, { 49, 49, 48, 48 }, { 49, 49, 48, 49 }, { 49, 49, 49, 48 }, { 49, 49, 49, 49 } };
    }
}
