package sun.security.util;

import java.util.Date;
import java.util.Vector;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.io.InputStream;
import java.io.IOException;

public class DerInputStream
{
    DerInputBuffer buffer;
    public byte tag;
    
    public DerInputStream(final byte[] array) throws IOException {
        this.init(array, 0, array.length, true);
    }
    
    public DerInputStream(final byte[] array, final int n, final int n2, final boolean b) throws IOException {
        this.init(array, n, n2, b);
    }
    
    public DerInputStream(final byte[] array, final int n, final int n2) throws IOException {
        this.init(array, n, n2, true);
    }
    
    private void init(final byte[] array, final int n, final int n2, final boolean b) throws IOException {
        if (n + 2 > array.length || n + n2 > array.length) {
            throw new IOException("Encoding bytes too short");
        }
        if (DerIndefLenConverter.isIndefinite(array[n + 1])) {
            if (!b) {
                throw new IOException("Indefinite length BER encoding found");
            }
            final byte[] array2 = new byte[n2];
            System.arraycopy(array, n, array2, 0, n2);
            this.buffer = new DerInputBuffer(new DerIndefLenConverter().convert(array2), b);
        }
        else {
            this.buffer = new DerInputBuffer(array, n, n2, b);
        }
        this.buffer.mark(Integer.MAX_VALUE);
    }
    
    DerInputStream(final DerInputBuffer buffer) {
        (this.buffer = buffer).mark(Integer.MAX_VALUE);
    }
    
    public DerInputStream subStream(final int n, final boolean b) throws IOException {
        final DerInputBuffer dup = this.buffer.dup();
        dup.truncate(n);
        if (b) {
            this.buffer.skip(n);
        }
        return new DerInputStream(dup);
    }
    
    public byte[] toByteArray() {
        return this.buffer.toByteArray();
    }
    
    public int getInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getInteger(getDefiniteLength(this.buffer));
    }
    
    public BigInteger getBigInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getBigInteger(getDefiniteLength(this.buffer), false);
    }
    
    public BigInteger getPositiveBigInteger() throws IOException {
        if (this.buffer.read() != 2) {
            throw new IOException("DER input, Integer tag error");
        }
        return this.buffer.getBigInteger(getDefiniteLength(this.buffer), true);
    }
    
    public int getEnumerated() throws IOException {
        if (this.buffer.read() != 10) {
            throw new IOException("DER input, Enumerated tag error");
        }
        return this.buffer.getInteger(getDefiniteLength(this.buffer));
    }
    
    public byte[] getBitString() throws IOException {
        if (this.buffer.read() != 3) {
            throw new IOException("DER input not an bit string");
        }
        return this.buffer.getBitString(getDefiniteLength(this.buffer));
    }
    
    public BitArray getUnalignedBitString() throws IOException {
        if (this.buffer.read() != 3) {
            throw new IOException("DER input not a bit string");
        }
        int definiteLength = getDefiniteLength(this.buffer);
        if (definiteLength == 0) {
            return new BitArray(0);
        }
        --definiteLength;
        final int read = this.buffer.read();
        if (read < 0) {
            throw new IOException("Unused bits of bit string invalid");
        }
        final int n = definiteLength * 8 - read;
        if (n < 0) {
            throw new IOException("Valid bits of bit string invalid");
        }
        final byte[] array = new byte[definiteLength];
        if (definiteLength != 0 && this.buffer.read(array) != definiteLength) {
            throw new IOException("Short read of DER bit string");
        }
        return new BitArray(n, array);
    }
    
    public byte[] getOctetString() throws IOException {
        if (this.buffer.read() != 4) {
            throw new IOException("DER input not an octet string");
        }
        final int definiteLength = getDefiniteLength(this.buffer);
        final byte[] array = new byte[definiteLength];
        if (definiteLength != 0 && this.buffer.read(array) != definiteLength) {
            throw new IOException("Short read of DER octet string");
        }
        return array;
    }
    
    public void getBytes(final byte[] array) throws IOException {
        if (array.length != 0 && this.buffer.read(array) != array.length) {
            throw new IOException("Short read of DER octet string");
        }
    }
    
    public void getNull() throws IOException {
        if (this.buffer.read() != 5 || this.buffer.read() != 0) {
            throw new IOException("getNull, bad data");
        }
    }
    
    public ObjectIdentifier getOID() throws IOException {
        return new ObjectIdentifier(this);
    }
    
    public DerValue[] getSequence(final int n) throws IOException {
        this.tag = (byte)this.buffer.read();
        if (this.tag != 48) {
            throw new IOException("Sequence tag error");
        }
        return this.readVector(n);
    }
    
    public DerValue[] getSet(final int n) throws IOException {
        this.tag = (byte)this.buffer.read();
        if (this.tag != 49) {
            throw new IOException("Set tag error");
        }
        return this.readVector(n);
    }
    
    public DerValue[] getSet(final int n, final boolean b) throws IOException {
        this.tag = (byte)this.buffer.read();
        if (!b && this.tag != 49) {
            throw new IOException("Set tag error");
        }
        return this.readVector(n);
    }
    
    protected DerValue[] readVector(final int n) throws IOException {
        final byte b = (byte)this.buffer.read();
        int n2 = getLength(b, this.buffer);
        if (n2 == -1) {
            final int available = this.buffer.available();
            final int n3 = 2;
            final byte[] array = new byte[available + n3];
            array[0] = this.tag;
            array[1] = b;
            final DataInputStream dataInputStream = new DataInputStream(this.buffer);
            dataInputStream.readFully(array, n3, available);
            dataInputStream.close();
            this.buffer = new DerInputBuffer(new DerIndefLenConverter().convert(array), this.buffer.allowBER);
            if (this.tag != this.buffer.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            n2 = getDefiniteLength(this.buffer);
        }
        if (n2 == 0) {
            return new DerValue[0];
        }
        DerInputStream subStream;
        if (this.buffer.available() == n2) {
            subStream = this;
        }
        else {
            subStream = this.subStream(n2, true);
        }
        final Vector<DerValue> vector = new Vector<DerValue>(n);
        do {
            vector.addElement(new DerValue(subStream.buffer, this.buffer.allowBER));
        } while (subStream.available() > 0);
        if (subStream.available() != 0) {
            throw new IOException("Extra data at end of vector");
        }
        final int size = vector.size();
        final DerValue[] array2 = new DerValue[size];
        for (int i = 0; i < size; ++i) {
            array2[i] = vector.elementAt(i);
        }
        return array2;
    }
    
    public DerValue getDerValue() throws IOException {
        return new DerValue(this.buffer);
    }
    
    public String getUTF8String() throws IOException {
        return this.readString((byte)12, "UTF-8", "UTF8");
    }
    
    public String getPrintableString() throws IOException {
        return this.readString((byte)19, "Printable", "ASCII");
    }
    
    public String getT61String() throws IOException {
        return this.readString((byte)20, "T61", "ISO-8859-1");
    }
    
    public String getIA5String() throws IOException {
        return this.readString((byte)22, "IA5", "ASCII");
    }
    
    public String getBMPString() throws IOException {
        return this.readString((byte)30, "BMP", "UnicodeBigUnmarked");
    }
    
    public String getGeneralString() throws IOException {
        return this.readString((byte)27, "General", "ASCII");
    }
    
    private String readString(final byte b, final String s, final String s2) throws IOException {
        if (this.buffer.read() != b) {
            throw new IOException("DER input not a " + s + " string");
        }
        final int definiteLength = getDefiniteLength(this.buffer);
        final byte[] array = new byte[definiteLength];
        if (definiteLength != 0 && this.buffer.read(array) != definiteLength) {
            throw new IOException("Short read of DER " + s + " string");
        }
        return new String(array, s2);
    }
    
    public Date getUTCTime() throws IOException {
        if (this.buffer.read() != 23) {
            throw new IOException("DER input, UTCtime tag invalid ");
        }
        return this.buffer.getUTCTime(getDefiniteLength(this.buffer));
    }
    
    public Date getGeneralizedTime() throws IOException {
        if (this.buffer.read() != 24) {
            throw new IOException("DER input, GeneralizedTime tag invalid ");
        }
        return this.buffer.getGeneralizedTime(getDefiniteLength(this.buffer));
    }
    
    int getByte() throws IOException {
        return 0xFF & this.buffer.read();
    }
    
    public int peekByte() throws IOException {
        return this.buffer.peek();
    }
    
    int getLength() throws IOException {
        return getLength(this.buffer);
    }
    
    static int getLength(final InputStream inputStream) throws IOException {
        return getLength(inputStream.read(), inputStream);
    }
    
    static int getLength(final int n, final InputStream inputStream) throws IOException {
        if (n == -1) {
            throw new IOException("Short read of DER length");
        }
        final String s = "DerInputStream.getLength(): ";
        int n2;
        if ((n & 0x80) == 0x0) {
            n2 = n;
        }
        else {
            int n3 = n & 0x7F;
            if (n3 == 0) {
                return -1;
            }
            if (n3 < 0 || n3 > 4) {
                throw new IOException(s + "lengthTag=" + n3 + ", " + ((n3 < 0) ? "incorrect DER encoding." : "too big."));
            }
            n2 = (0xFF & inputStream.read());
            --n3;
            if (n2 == 0) {
                throw new IOException(s + "Redundant length bytes found");
            }
            while (n3-- > 0) {
                n2 = (n2 << 8) + (0xFF & inputStream.read());
            }
            if (n2 < 0) {
                throw new IOException(s + "Invalid length bytes");
            }
            if (n2 <= 127) {
                throw new IOException(s + "Should use short form for length");
            }
        }
        return n2;
    }
    
    int getDefiniteLength() throws IOException {
        return getDefiniteLength(this.buffer);
    }
    
    static int getDefiniteLength(final InputStream inputStream) throws IOException {
        final int length = getLength(inputStream);
        if (length < 0) {
            throw new IOException("Indefinite length encoding not supported");
        }
        return length;
    }
    
    public void mark(final int n) {
        this.buffer.mark(n);
    }
    
    public void reset() {
        this.buffer.reset();
    }
    
    public int available() {
        return this.buffer.available();
    }
}
