package sun.security.util;

import java.util.Arrays;
import java.math.BigInteger;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public final class ObjectIdentifier implements Serializable
{
    private static final int MAXIMUM_OID_SIZE = 4096;
    private byte[] encoding;
    private transient volatile String stringForm;
    private static final long serialVersionUID = 8697030238860181294L;
    private Object components;
    private int componentLen;
    private transient boolean componentsCalculated;
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.encoding == null) {
            final int[] array = (int[])this.components;
            if (this.componentLen > array.length) {
                this.componentLen = array.length;
            }
            checkOidSize(this.componentLen);
            this.init(array, this.componentLen);
        }
        else {
            checkOidSize(this.encoding.length);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (!this.componentsCalculated) {
            final int[] intArray = this.toIntArray();
            if (intArray != null) {
                this.components = intArray;
                this.componentLen = intArray.length;
            }
            else {
                this.components = HugeOidNotSupportedByOldJDK.theOne;
            }
            this.componentsCalculated = true;
        }
        objectOutputStream.defaultWriteObject();
    }
    
    public ObjectIdentifier(final String stringForm) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        final int n = 46;
        int n2 = 0;
        int n3 = 0;
        final byte[] array = new byte[stringForm.length()];
        int intValue = 0;
        int n4 = 0;
        try {
            int i;
            do {
                i = stringForm.indexOf(n, n2);
                String s;
                int n5;
                if (i == -1) {
                    s = stringForm.substring(n2);
                    n5 = stringForm.length() - n2;
                }
                else {
                    s = stringForm.substring(n2, i);
                    n5 = i - n2;
                }
                if (n5 > 9) {
                    BigInteger add = new BigInteger(s);
                    if (n4 == 0) {
                        checkFirstComponent(add);
                        intValue = add.intValue();
                    }
                    else {
                        if (n4 == 1) {
                            checkSecondComponent(intValue, add);
                            add = add.add(BigInteger.valueOf(40 * intValue));
                        }
                        else {
                            checkOtherComponent(n4, add);
                        }
                        n3 += pack7Oid(add, array, n3);
                    }
                }
                else {
                    int int1 = Integer.parseInt(s);
                    if (n4 == 0) {
                        checkFirstComponent(int1);
                        intValue = int1;
                    }
                    else {
                        if (n4 == 1) {
                            checkSecondComponent(intValue, int1);
                            int1 += 40 * intValue;
                        }
                        else {
                            checkOtherComponent(n4, int1);
                        }
                        n3 += pack7Oid(int1, array, n3);
                    }
                }
                n2 = i + 1;
                ++n4;
                checkOidSize(n3);
            } while (i != -1);
            checkCount(n4);
            System.arraycopy(array, 0, this.encoding = new byte[n3], 0, n3);
            this.stringForm = stringForm;
        }
        catch (final IOException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new IOException("ObjectIdentifier() -- Invalid format: " + ex2.toString(), ex2);
        }
    }
    
    public ObjectIdentifier(final int[] array) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        checkCount(array.length);
        checkFirstComponent(array[0]);
        checkSecondComponent(array[0], array[1]);
        for (int i = 2; i < array.length; ++i) {
            checkOtherComponent(i, array[i]);
        }
        this.init(array, array.length);
    }
    
    public ObjectIdentifier(final DerInputStream derInputStream) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        final byte b = (byte)derInputStream.getByte();
        if (b != 6) {
            throw new IOException("ObjectIdentifier() -- data isn't an object ID (tag = " + b + ")");
        }
        final int definiteLength = derInputStream.getDefiniteLength();
        checkOidSize(definiteLength);
        if (definiteLength > derInputStream.available()) {
            throw new IOException("ObjectIdentifier length exceeds data available.  Length: " + definiteLength + ", Available: " + derInputStream.available());
        }
        derInputStream.getBytes(this.encoding = new byte[definiteLength]);
        check(this.encoding);
    }
    
    ObjectIdentifier(final DerInputBuffer derInputBuffer) throws IOException {
        this.encoding = null;
        this.components = null;
        this.componentLen = -1;
        this.componentsCalculated = false;
        final DerInputStream derInputStream = new DerInputStream(derInputBuffer);
        final int available = derInputStream.available();
        checkOidSize(available);
        derInputStream.getBytes(this.encoding = new byte[available]);
        check(this.encoding);
    }
    
    private void init(final int[] array, final int n) throws IOException {
        final int n2 = 0;
        final byte[] array2 = new byte[n * 5 + 1];
        int n3;
        if (array[1] < Integer.MAX_VALUE - array[0] * 40) {
            n3 = n2 + pack7Oid(array[0] * 40 + array[1], array2, n2);
        }
        else {
            n3 = n2 + pack7Oid(BigInteger.valueOf(array[1]).add(BigInteger.valueOf(array[0] * 40)), array2, n2);
        }
        for (int i = 2; i < n; ++i) {
            n3 += pack7Oid(array[i], array2, n3);
            checkOidSize(n3);
        }
        System.arraycopy(array2, 0, this.encoding = new byte[n3], 0, n3);
    }
    
    public static ObjectIdentifier newInternal(final int[] array) {
        try {
            return new ObjectIdentifier(array);
        }
        catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.write((byte)6, this.encoding);
    }
    
    @Deprecated
    public boolean equals(final ObjectIdentifier objectIdentifier) {
        return this.equals((Object)objectIdentifier);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ObjectIdentifier && Arrays.equals(this.encoding, ((ObjectIdentifier)o).encoding));
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.encoding);
    }
    
    private int[] toIntArray() {
        final int length = this.encoding.length;
        int[] copy = new int[20];
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < length; ++i) {
            if ((this.encoding[i] & 0x80) == 0x0) {
                if (i - n2 + 1 > 4) {
                    final BigInteger bigInteger = new BigInteger(pack(this.encoding, n2, i - n2 + 1, 7, 8));
                    if (n2 == 0) {
                        copy[n++] = 2;
                        final BigInteger subtract = bigInteger.subtract(BigInteger.valueOf(80L));
                        if (subtract.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
                            return null;
                        }
                        copy[n++] = subtract.intValue();
                    }
                    else {
                        if (bigInteger.compareTo(BigInteger.valueOf(2147483647L)) == 1) {
                            return null;
                        }
                        copy[n++] = bigInteger.intValue();
                    }
                }
                else {
                    int n3 = 0;
                    for (int j = n2; j <= i; ++j) {
                        n3 = (n3 << 7 | (this.encoding[j] & 0x7F));
                    }
                    if (n2 == 0) {
                        if (n3 < 80) {
                            copy[n++] = n3 / 40;
                            copy[n++] = n3 % 40;
                        }
                        else {
                            copy[n++] = 2;
                            copy[n++] = n3 - 80;
                        }
                    }
                    else {
                        copy[n++] = n3;
                    }
                }
                n2 = i + 1;
            }
            if (n >= copy.length) {
                copy = Arrays.copyOf(copy, n + 10);
            }
        }
        return Arrays.copyOf(copy, n);
    }
    
    @Override
    public String toString() {
        String stringForm = this.stringForm;
        if (stringForm == null) {
            final int length = this.encoding.length;
            final StringBuffer sb = new StringBuffer(length * 4);
            int n = 0;
            for (int i = 0; i < length; ++i) {
                if ((this.encoding[i] & 0x80) == 0x0) {
                    if (n != 0) {
                        sb.append('.');
                    }
                    if (i - n + 1 > 4) {
                        final BigInteger bigInteger = new BigInteger(pack(this.encoding, n, i - n + 1, 7, 8));
                        if (n == 0) {
                            sb.append("2.");
                            sb.append(bigInteger.subtract(BigInteger.valueOf(80L)));
                        }
                        else {
                            sb.append(bigInteger);
                        }
                    }
                    else {
                        int n2 = 0;
                        for (int j = n; j <= i; ++j) {
                            n2 = (n2 << 7 | (this.encoding[j] & 0x7F));
                        }
                        if (n == 0) {
                            if (n2 < 80) {
                                sb.append(n2 / 40);
                                sb.append('.');
                                sb.append(n2 % 40);
                            }
                            else {
                                sb.append("2.");
                                sb.append(n2 - 80);
                            }
                        }
                        else {
                            sb.append(n2);
                        }
                    }
                    n = i + 1;
                }
            }
            stringForm = sb.toString();
            this.stringForm = stringForm;
        }
        return stringForm;
    }
    
    private static byte[] pack(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        assert n3 > 0 && n3 <= 8 : "input NUB must be between 1 and 8";
        assert n4 > 0 && n4 <= 8 : "output NUB must be between 1 and 8";
        if (n3 == n4) {
            return array.clone();
        }
        final int n5 = n2 * n3;
        final byte[] array2 = new byte[(n5 + n4 - 1) / n4];
        int n7;
        for (int i = 0, n6 = (n5 + n4 - 1) / n4 * n4 - n5; i < n5; i += n7, n6 += n7) {
            n7 = n3 - i % n3;
            if (n7 > n4 - n6 % n4) {
                n7 = n4 - n6 % n4;
            }
            final byte[] array3 = array2;
            final int n8 = n6 / n4;
            array3[n8] |= (byte)((array[n + i / n3] + 256 >> n3 - i % n3 - n7 & (1 << n7) - 1) << n4 - n6 % n4 - n7);
        }
        return array2;
    }
    
    private static int pack7Oid(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        final byte[] pack = pack(array, n, n2, 8, 7);
        int n4 = pack.length - 1;
        for (int i = pack.length - 2; i >= 0; --i) {
            if (pack[i] != 0) {
                n4 = i;
            }
            final byte[] array3 = pack;
            final int n5 = i;
            array3[n5] |= (byte)128;
        }
        System.arraycopy(pack, n4, array2, n3, pack.length - n4);
        return pack.length - n4;
    }
    
    private static int pack8(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        final byte[] pack = pack(array, n, n2, 7, 8);
        int n4 = pack.length - 1;
        for (int i = pack.length - 2; i >= 0; --i) {
            if (pack[i] != 0) {
                n4 = i;
            }
        }
        System.arraycopy(pack, n4, array2, n3, pack.length - n4);
        return pack.length - n4;
    }
    
    private static int pack7Oid(final int n, final byte[] array, final int n2) {
        return pack7Oid(new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n }, 0, 4, array, n2);
    }
    
    private static int pack7Oid(final BigInteger bigInteger, final byte[] array, final int n) {
        final byte[] byteArray = bigInteger.toByteArray();
        return pack7Oid(byteArray, 0, byteArray.length, array, n);
    }
    
    private static void check(final byte[] array) throws IOException {
        final int length = array.length;
        if (length < 1 || (array[length - 1] & 0x80) != 0x0) {
            throw new IOException("ObjectIdentifier() -- Invalid DER encoding, not ended");
        }
        for (int i = 0; i < length; ++i) {
            if (array[i] == -128 && (i == 0 || (array[i - 1] & 0x80) == 0x0)) {
                throw new IOException("ObjectIdentifier() -- Invalid DER encoding, useless extra octet detected");
            }
        }
    }
    
    private static void checkCount(final int n) throws IOException {
        if (n < 2) {
            throw new IOException("ObjectIdentifier() -- Must be at least two oid components ");
        }
    }
    
    private static void checkFirstComponent(final int n) throws IOException {
        if (n < 0 || n > 2) {
            throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
        }
    }
    
    private static void checkFirstComponent(final BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1 || bigInteger.compareTo(BigInteger.valueOf(2L)) == 1) {
            throw new IOException("ObjectIdentifier() -- First oid component is invalid ");
        }
    }
    
    private static void checkSecondComponent(final int n, final int n2) throws IOException {
        if (n2 < 0 || (n != 2 && n2 > 39)) {
            throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
        }
    }
    
    private static void checkSecondComponent(final int n, final BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1 || (n != 2 && bigInteger.compareTo(BigInteger.valueOf(39L)) == 1)) {
            throw new IOException("ObjectIdentifier() -- Second oid component is invalid ");
        }
    }
    
    private static void checkOtherComponent(final int n, final int n2) throws IOException {
        if (n2 < 0) {
            throw new IOException("ObjectIdentifier() -- oid component #" + (n + 1) + " must be non-negative ");
        }
    }
    
    private static void checkOtherComponent(final int n, final BigInteger bigInteger) throws IOException {
        if (bigInteger.signum() == -1) {
            throw new IOException("ObjectIdentifier() -- oid component #" + (n + 1) + " must be non-negative ");
        }
    }
    
    private static void checkOidSize(final int n) throws IOException {
        if (n > 4096) {
            throw new IOException("ObjectIdentifier encoded length exceeds the restriction in JDK (OId length(>=): " + n + ", Restriction: " + 4096 + ")");
        }
    }
    
    static class HugeOidNotSupportedByOldJDK implements Serializable
    {
        private static final long serialVersionUID = 1L;
        static HugeOidNotSupportedByOldJDK theOne;
        
        static {
            HugeOidNotSupportedByOldJDK.theOne = new HugeOidNotSupportedByOldJDK();
        }
    }
}
