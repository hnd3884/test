package sun.security.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import java.util.Arrays;
import java.io.OutputStream;
import java.util.Comparator;
import java.math.BigInteger;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class DerOutputStream extends ByteArrayOutputStream implements DerEncoder
{
    private static ByteArrayLexOrder lexOrder;
    private static ByteArrayTagOrder tagOrder;
    
    public DerOutputStream(final int n) {
        super(n);
    }
    
    public DerOutputStream() {
    }
    
    public void write(final byte b, final byte[] array) throws IOException {
        this.write(b);
        this.putLength(array.length);
        this.write(array, 0, array.length);
    }
    
    public void write(final byte b, final DerOutputStream derOutputStream) throws IOException {
        this.write(b);
        this.putLength(derOutputStream.count);
        this.write(derOutputStream.buf, 0, derOutputStream.count);
    }
    
    public void writeImplicit(final byte b, final DerOutputStream derOutputStream) throws IOException {
        this.write(b);
        this.write(derOutputStream.buf, 1, derOutputStream.count - 1);
    }
    
    public void putDerValue(final DerValue derValue) throws IOException {
        derValue.encode(this);
    }
    
    public void putBoolean(final boolean b) throws IOException {
        this.write(1);
        this.putLength(1);
        if (b) {
            this.write(255);
        }
        else {
            this.write(0);
        }
    }
    
    public void putEnumerated(final int n) throws IOException {
        this.write(10);
        this.putIntegerContents(n);
    }
    
    public void putInteger(final BigInteger bigInteger) throws IOException {
        this.write(2);
        final byte[] byteArray = bigInteger.toByteArray();
        this.putLength(byteArray.length);
        this.write(byteArray, 0, byteArray.length);
    }
    
    public void putInteger(final Integer n) throws IOException {
        this.putInteger((int)n);
    }
    
    public void putInteger(final int n) throws IOException {
        this.write(2);
        this.putIntegerContents(n);
    }
    
    private void putIntegerContents(final int n) throws IOException {
        final byte[] array = new byte[4];
        int n2 = 0;
        array[3] = (byte)(n & 0xFF);
        array[2] = (byte)((n & 0xFF00) >>> 8);
        array[1] = (byte)((n & 0xFF0000) >>> 16);
        array[0] = (byte)((n & 0xFF000000) >>> 24);
        if (array[0] == -1) {
            for (int n3 = 0; n3 < 3 && array[n3] == -1 && (array[n3 + 1] & 0x80) == 0x80; ++n3) {
                ++n2;
            }
        }
        else if (array[0] == 0) {
            for (int n4 = 0; n4 < 3 && array[n4] == 0 && (array[n4 + 1] & 0x80) == 0x0; ++n4) {
                ++n2;
            }
        }
        this.putLength(4 - n2);
        for (int i = n2; i < 4; ++i) {
            this.write(array[i]);
        }
    }
    
    public void putBitString(final byte[] array) throws IOException {
        this.write(3);
        this.putLength(array.length + 1);
        this.write(0);
        this.write(array);
    }
    
    public void putUnalignedBitString(final BitArray bitArray) throws IOException {
        final byte[] byteArray = bitArray.toByteArray();
        this.write(3);
        this.putLength(byteArray.length + 1);
        this.write(byteArray.length * 8 - bitArray.length());
        this.write(byteArray);
    }
    
    public void putTruncatedUnalignedBitString(final BitArray bitArray) throws IOException {
        this.putUnalignedBitString(bitArray.truncate());
    }
    
    public void putOctetString(final byte[] array) throws IOException {
        this.write((byte)4, array);
    }
    
    public void putNull() throws IOException {
        this.write(5);
        this.putLength(0);
    }
    
    public void putOID(final ObjectIdentifier objectIdentifier) throws IOException {
        objectIdentifier.encode(this);
    }
    
    public void putSequence(final DerValue[] array) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        for (int i = 0; i < array.length; ++i) {
            array[i].encode(derOutputStream);
        }
        this.write((byte)48, derOutputStream);
    }
    
    public void putSet(final DerValue[] array) throws IOException {
        final DerOutputStream derOutputStream = new DerOutputStream();
        for (int i = 0; i < array.length; ++i) {
            array[i].encode(derOutputStream);
        }
        this.write((byte)49, derOutputStream);
    }
    
    public void putOrderedSetOf(final byte b, final DerEncoder[] array) throws IOException {
        this.putOrderedSet(b, array, DerOutputStream.lexOrder);
    }
    
    public void putOrderedSet(final byte b, final DerEncoder[] array) throws IOException {
        this.putOrderedSet(b, array, DerOutputStream.tagOrder);
    }
    
    private void putOrderedSet(final byte b, final DerEncoder[] array, final Comparator<byte[]> comparator) throws IOException {
        final DerOutputStream[] array2 = new DerOutputStream[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new DerOutputStream();
            array[i].derEncode(array2[i]);
        }
        final byte[][] array3 = new byte[array2.length][];
        for (int j = 0; j < array2.length; ++j) {
            array3[j] = array2[j].toByteArray();
        }
        Arrays.sort(array3, comparator);
        final DerOutputStream derOutputStream = new DerOutputStream();
        for (int k = 0; k < array2.length; ++k) {
            derOutputStream.write(array3[k]);
        }
        this.write(b, derOutputStream);
    }
    
    public void putUTF8String(final String s) throws IOException {
        this.writeString(s, (byte)12, "UTF8");
    }
    
    public void putPrintableString(final String s) throws IOException {
        this.writeString(s, (byte)19, "ASCII");
    }
    
    public void putT61String(final String s) throws IOException {
        this.writeString(s, (byte)20, "ISO-8859-1");
    }
    
    public void putIA5String(final String s) throws IOException {
        this.writeString(s, (byte)22, "ASCII");
    }
    
    public void putBMPString(final String s) throws IOException {
        this.writeString(s, (byte)30, "UnicodeBigUnmarked");
    }
    
    public void putGeneralString(final String s) throws IOException {
        this.writeString(s, (byte)27, "ASCII");
    }
    
    private void writeString(final String s, final byte b, final String s2) throws IOException {
        final byte[] bytes = s.getBytes(s2);
        this.write(b);
        this.putLength(bytes.length);
        this.write(bytes);
    }
    
    public void putUTCTime(final Date date) throws IOException {
        this.putTime(date, (byte)23);
    }
    
    public void putGeneralizedTime(final Date date) throws IOException {
        this.putTime(date, (byte)24);
    }
    
    private void putTime(final Date date, byte b) throws IOException {
        final TimeZone timeZone = TimeZone.getTimeZone("GMT");
        String s;
        if (b == 23) {
            s = "yyMMddHHmmss'Z'";
        }
        else {
            b = 24;
            s = "yyyyMMddHHmmss'Z'";
        }
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(s, Locale.US);
        simpleDateFormat.setTimeZone(timeZone);
        final byte[] bytes = simpleDateFormat.format(date).getBytes("ISO-8859-1");
        this.write(b);
        this.putLength(bytes.length);
        this.write(bytes);
    }
    
    public void putLength(final int n) throws IOException {
        if (n < 128) {
            this.write((byte)n);
        }
        else if (n < 256) {
            this.write(-127);
            this.write((byte)n);
        }
        else if (n < 65536) {
            this.write(-126);
            this.write((byte)(n >> 8));
            this.write((byte)n);
        }
        else if (n < 16777216) {
            this.write(-125);
            this.write((byte)(n >> 16));
            this.write((byte)(n >> 8));
            this.write((byte)n);
        }
        else {
            this.write(-124);
            this.write((byte)(n >> 24));
            this.write((byte)(n >> 16));
            this.write((byte)(n >> 8));
            this.write((byte)n);
        }
    }
    
    public void putTag(final byte b, final boolean b2, final byte b3) {
        byte b4 = (byte)(b | b3);
        if (b2) {
            b4 |= 0x20;
        }
        this.write(b4);
    }
    
    @Override
    public void derEncode(final OutputStream outputStream) throws IOException {
        outputStream.write(this.toByteArray());
    }
    
    static {
        DerOutputStream.lexOrder = new ByteArrayLexOrder();
        DerOutputStream.tagOrder = new ByteArrayTagOrder();
    }
}
