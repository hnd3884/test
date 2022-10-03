package sun.security.util;

import java.util.Date;
import java.math.BigInteger;
import sun.misc.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

public class DerValue
{
    public static final byte TAG_UNIVERSAL = 0;
    public static final byte TAG_APPLICATION = 64;
    public static final byte TAG_CONTEXT = Byte.MIN_VALUE;
    public static final byte TAG_PRIVATE = -64;
    public byte tag;
    protected DerInputBuffer buffer;
    public final DerInputStream data;
    private int length;
    public static final byte tag_Boolean = 1;
    public static final byte tag_Integer = 2;
    public static final byte tag_BitString = 3;
    public static final byte tag_OctetString = 4;
    public static final byte tag_Null = 5;
    public static final byte tag_ObjectId = 6;
    public static final byte tag_Enumerated = 10;
    public static final byte tag_UTF8String = 12;
    public static final byte tag_PrintableString = 19;
    public static final byte tag_T61String = 20;
    public static final byte tag_IA5String = 22;
    public static final byte tag_UtcTime = 23;
    public static final byte tag_GeneralizedTime = 24;
    public static final byte tag_GeneralString = 27;
    public static final byte tag_UniversalString = 28;
    public static final byte tag_BMPString = 30;
    public static final byte tag_Sequence = 48;
    public static final byte tag_SequenceOf = 48;
    public static final byte tag_Set = 49;
    public static final byte tag_SetOf = 49;
    
    public boolean isUniversal() {
        return (this.tag & 0xC0) == 0x0;
    }
    
    public boolean isApplication() {
        return (this.tag & 0xC0) == 0x40;
    }
    
    public boolean isContextSpecific() {
        return (this.tag & 0xC0) == 0x80;
    }
    
    public boolean isContextSpecific(final byte b) {
        return this.isContextSpecific() && (this.tag & 0x1F) == b;
    }
    
    boolean isPrivate() {
        return (this.tag & 0xC0) == 0xC0;
    }
    
    public boolean isConstructed() {
        return (this.tag & 0x20) == 0x20;
    }
    
    public boolean isConstructed(final byte b) {
        return this.isConstructed() && (this.tag & 0x1F) == b;
    }
    
    public DerValue(final String s) throws IOException {
        boolean b = true;
        for (int i = 0; i < s.length(); ++i) {
            if (!isPrintableStringChar(s.charAt(i))) {
                b = false;
                break;
            }
        }
        this.data = this.init((byte)(b ? 19 : 12), s);
    }
    
    public DerValue(final byte b, final String s) throws IOException {
        this.data = this.init(b, s);
    }
    
    DerValue(final byte tag, final byte[] array, final boolean b) {
        this.tag = tag;
        this.buffer = new DerInputBuffer(array.clone(), b);
        this.length = array.length;
        (this.data = new DerInputStream(this.buffer)).mark(Integer.MAX_VALUE);
    }
    
    public DerValue(final byte b, final byte[] array) {
        this(b, array, true);
    }
    
    DerValue(final DerInputBuffer derInputBuffer) throws IOException {
        this.tag = (byte)derInputBuffer.read();
        final byte b = (byte)derInputBuffer.read();
        this.length = DerInputStream.getLength(b, derInputBuffer);
        if (this.length == -1) {
            final DerInputBuffer dup = derInputBuffer.dup();
            final int available = dup.available();
            final int n = 2;
            final byte[] array = new byte[available + n];
            array[0] = this.tag;
            array[1] = b;
            final DataInputStream dataInputStream = new DataInputStream(dup);
            dataInputStream.readFully(array, n, available);
            dataInputStream.close();
            final DerInputBuffer derInputBuffer2 = new DerInputBuffer(new DerIndefLenConverter().convert(array), derInputBuffer.allowBER);
            if (this.tag != derInputBuffer2.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            this.length = DerInputStream.getDefiniteLength(derInputBuffer2);
            (this.buffer = derInputBuffer2.dup()).truncate(this.length);
            this.data = new DerInputStream(this.buffer);
            derInputBuffer.skip(this.length + n);
        }
        else {
            (this.buffer = derInputBuffer.dup()).truncate(this.length);
            this.data = new DerInputStream(this.buffer);
            derInputBuffer.skip(this.length);
        }
    }
    
    DerValue(final byte[] array, final boolean b) throws IOException {
        this.data = this.init(true, new ByteArrayInputStream(array), b);
    }
    
    public DerValue(final byte[] array) throws IOException {
        this(array, true);
    }
    
    DerValue(final byte[] array, final int n, final int n2, final boolean b) throws IOException {
        this.data = this.init(true, new ByteArrayInputStream(array, n, n2), b);
    }
    
    public DerValue(final byte[] array, final int n, final int n2) throws IOException {
        this(array, n, n2, true);
    }
    
    DerValue(final InputStream inputStream, final boolean b) throws IOException {
        this.data = this.init(false, inputStream, b);
    }
    
    public DerValue(final InputStream inputStream) throws IOException {
        this(inputStream, true);
    }
    
    private DerInputStream init(final byte tag, final String s) throws IOException {
        String s2 = null;
        switch (this.tag = tag) {
            case 19:
            case 22:
            case 27: {
                s2 = "ASCII";
                break;
            }
            case 20: {
                s2 = "ISO-8859-1";
                break;
            }
            case 30: {
                s2 = "UnicodeBigUnmarked";
                break;
            }
            case 12: {
                s2 = "UTF8";
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DER string type");
            }
        }
        final byte[] bytes = s.getBytes(s2);
        this.length = bytes.length;
        this.buffer = new DerInputBuffer(bytes, true);
        final DerInputStream derInputStream = new DerInputStream(this.buffer);
        derInputStream.mark(Integer.MAX_VALUE);
        return derInputStream;
    }
    
    private DerInputStream init(final boolean b, InputStream inputStream, final boolean b2) throws IOException {
        this.tag = (byte)inputStream.read();
        final byte b3 = (byte)inputStream.read();
        this.length = DerInputStream.getLength(b3, inputStream);
        if (this.length == -1) {
            final int available = inputStream.available();
            final int n = 2;
            final byte[] array = new byte[available + n];
            array[0] = this.tag;
            array[1] = b3;
            final DataInputStream dataInputStream = new DataInputStream(inputStream);
            dataInputStream.readFully(array, n, available);
            dataInputStream.close();
            inputStream = new ByteArrayInputStream(new DerIndefLenConverter().convert(array));
            if (this.tag != inputStream.read()) {
                throw new IOException("Indefinite length encoding not supported");
            }
            this.length = DerInputStream.getDefiniteLength(inputStream);
        }
        if (b && inputStream.available() != this.length) {
            throw new IOException("extra data given to DerValue constructor");
        }
        this.buffer = new DerInputBuffer(IOUtils.readExactlyNBytes(inputStream, this.length), b2);
        return new DerInputStream(this.buffer);
    }
    
    public void encode(final DerOutputStream derOutputStream) throws IOException {
        derOutputStream.write(this.tag);
        derOutputStream.putLength(this.length);
        if (this.length > 0) {
            final byte[] array = new byte[this.length];
            synchronized (this.data) {
                this.buffer.reset();
                if (this.buffer.read(array) != this.length) {
                    throw new IOException("short DER value read (encode)");
                }
                derOutputStream.write(array);
            }
        }
    }
    
    public final DerInputStream getData() {
        return this.data;
    }
    
    public final byte getTag() {
        return this.tag;
    }
    
    public boolean getBoolean() throws IOException {
        if (this.tag != 1) {
            throw new IOException("DerValue.getBoolean, not a BOOLEAN " + this.tag);
        }
        if (this.length != 1) {
            throw new IOException("DerValue.getBoolean, invalid length " + this.length);
        }
        return this.buffer.read() != 0;
    }
    
    public ObjectIdentifier getOID() throws IOException {
        if (this.tag != 6) {
            throw new IOException("DerValue.getOID, not an OID " + this.tag);
        }
        return new ObjectIdentifier(this.buffer);
    }
    
    private byte[] append(final byte[] array, final byte[] array2) {
        if (array == null) {
            return array2;
        }
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public byte[] getOctetString() throws IOException {
        if (this.tag != 4 && !this.isConstructed((byte)4)) {
            throw new IOException("DerValue.getOctetString, not an Octet String: " + this.tag);
        }
        if (this.length == 0) {
            return new byte[0];
        }
        final DerInputBuffer buffer = this.buffer;
        if (buffer.available() < this.length) {
            throw new IOException("short read on DerValue buffer");
        }
        byte[] append = new byte[this.length];
        buffer.read(append);
        if (this.isConstructed()) {
            final DerInputStream derInputStream = new DerInputStream(append, 0, append.length, this.buffer.allowBER);
            append = null;
            while (derInputStream.available() != 0) {
                append = this.append(append, derInputStream.getOctetString());
            }
        }
        return append;
    }
    
    public int getInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getInteger, not an int " + this.tag);
        }
        return this.buffer.getInteger(this.data.available());
    }
    
    public BigInteger getBigInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getBigInteger, not an int " + this.tag);
        }
        return this.buffer.getBigInteger(this.data.available(), false);
    }
    
    public BigInteger getPositiveBigInteger() throws IOException {
        if (this.tag != 2) {
            throw new IOException("DerValue.getBigInteger, not an int " + this.tag);
        }
        return this.buffer.getBigInteger(this.data.available(), true);
    }
    
    public int getEnumerated() throws IOException {
        if (this.tag != 10) {
            throw new IOException("DerValue.getEnumerated, incorrect tag: " + this.tag);
        }
        return this.buffer.getInteger(this.data.available());
    }
    
    public byte[] getBitString() throws IOException {
        if (this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
        }
        return this.buffer.getBitString();
    }
    
    public BitArray getUnalignedBitString() throws IOException {
        if (this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
        }
        return this.buffer.getUnalignedBitString();
    }
    
    public String getAsString() throws IOException {
        if (this.tag == 12) {
            return this.getUTF8String();
        }
        if (this.tag == 19) {
            return this.getPrintableString();
        }
        if (this.tag == 20) {
            return this.getT61String();
        }
        if (this.tag == 22) {
            return this.getIA5String();
        }
        if (this.tag == 30) {
            return this.getBMPString();
        }
        if (this.tag == 27) {
            return this.getGeneralString();
        }
        return null;
    }
    
    public byte[] getBitString(final boolean b) throws IOException {
        if (!b && this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
        }
        return this.buffer.getBitString();
    }
    
    public BitArray getUnalignedBitString(final boolean b) throws IOException {
        if (!b && this.tag != 3) {
            throw new IOException("DerValue.getBitString, not a bit string " + this.tag);
        }
        return this.buffer.getUnalignedBitString();
    }
    
    public byte[] getDataBytes() throws IOException {
        final byte[] array = new byte[this.length];
        synchronized (this.data) {
            this.data.reset();
            this.data.getBytes(array);
        }
        return array;
    }
    
    public String getPrintableString() throws IOException {
        if (this.tag != 19) {
            throw new IOException("DerValue.getPrintableString, not a string " + this.tag);
        }
        return new String(this.getDataBytes(), "ASCII");
    }
    
    public String getT61String() throws IOException {
        if (this.tag != 20) {
            throw new IOException("DerValue.getT61String, not T61 " + this.tag);
        }
        return new String(this.getDataBytes(), "ISO-8859-1");
    }
    
    public String getIA5String() throws IOException {
        if (this.tag != 22) {
            throw new IOException("DerValue.getIA5String, not IA5 " + this.tag);
        }
        return new String(this.getDataBytes(), "ASCII");
    }
    
    public String getBMPString() throws IOException {
        if (this.tag != 30) {
            throw new IOException("DerValue.getBMPString, not BMP " + this.tag);
        }
        return new String(this.getDataBytes(), "UnicodeBigUnmarked");
    }
    
    public String getUTF8String() throws IOException {
        if (this.tag != 12) {
            throw new IOException("DerValue.getUTF8String, not UTF-8 " + this.tag);
        }
        return new String(this.getDataBytes(), "UTF8");
    }
    
    public String getGeneralString() throws IOException {
        if (this.tag != 27) {
            throw new IOException("DerValue.getGeneralString, not GeneralString " + this.tag);
        }
        return new String(this.getDataBytes(), "ASCII");
    }
    
    public Date getUTCTime() throws IOException {
        if (this.tag != 23) {
            throw new IOException("DerValue.getUTCTime, not a UtcTime: " + this.tag);
        }
        return this.buffer.getUTCTime(this.data.available());
    }
    
    public Date getGeneralizedTime() throws IOException {
        if (this.tag != 24) {
            throw new IOException("DerValue.getGeneralizedTime, not a GeneralizedTime: " + this.tag);
        }
        return this.buffer.getGeneralizedTime(this.data.available());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DerValue)) {
            return false;
        }
        final DerValue derValue = (DerValue)o;
        return this.tag == derValue.tag && (this.data == derValue.data || ((System.identityHashCode(this.data) > System.identityHashCode(derValue.data)) ? doEquals(this, derValue) : doEquals(derValue, this)));
    }
    
    private static boolean doEquals(final DerValue derValue, final DerValue derValue2) {
        synchronized (derValue.data) {
            synchronized (derValue2.data) {
                derValue.data.reset();
                derValue2.data.reset();
                return derValue.buffer.equals(derValue2.buffer);
            }
        }
    }
    
    @Override
    public String toString() {
        try {
            final String asString = this.getAsString();
            if (asString != null) {
                return "\"" + asString + "\"";
            }
            if (this.tag == 5) {
                return "[DerValue, null]";
            }
            if (this.tag == 6) {
                return "OID." + this.getOID();
            }
            return "[DerValue, tag = " + this.tag + ", length = " + this.length + "]";
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("misformatted DER value");
        }
    }
    
    public byte[] toByteArray() throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        this.encode(derOutputStream);
        this.data.reset();
        return derOutputStream.toByteArray();
    }
    
    public DerInputStream toDerInputStream() throws IOException {
        if (this.tag == 48 || this.tag == 49) {
            return new DerInputStream(this.buffer);
        }
        throw new IOException("toDerInputStream rejects tag type " + this.tag);
    }
    
    public int length() {
        return this.length;
    }
    
    public static boolean isPrintableStringChar(final char c) {
        if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
            return true;
        }
        switch (c) {
            case ' ':
            case '\'':
            case '(':
            case ')':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case '=':
            case '?': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static byte createTag(final byte b, final boolean b2, final byte b3) {
        byte b4 = (byte)(b | b3);
        if (b2) {
            b4 |= 0x20;
        }
        return b4;
    }
    
    public void resetTag(final byte tag) {
        this.tag = tag;
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
