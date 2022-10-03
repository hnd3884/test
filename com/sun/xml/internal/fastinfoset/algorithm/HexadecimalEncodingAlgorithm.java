package com.sun.xml.internal.fastinfoset.algorithm;

import java.io.OutputStream;
import java.io.IOException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;

public class HexadecimalEncodingAlgorithm extends BuiltInEncodingAlgorithm
{
    private static final char[] NIBBLE_TO_HEXADECIMAL_TABLE;
    private static final int[] HEXADECIMAL_TO_NIBBLE_TABLE;
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final byte[] data = new byte[length];
        System.arraycopy(b, start, data, 0, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof byte[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotByteArray"));
        }
        s.write((byte[])data);
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        if (length == 0) {
            return new byte[0];
        }
        final StringBuilder encodedValue = this.removeWhitespace(ch, start, length);
        final int encodedLength = encodedValue.length();
        if (encodedLength == 0) {
            return new byte[0];
        }
        final int valueLength = encodedValue.length() / 2;
        final byte[] value = new byte[valueLength];
        int encodedIdx = 0;
        for (int i = 0; i < valueLength; ++i) {
            final int nibble1 = HexadecimalEncodingAlgorithm.HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            final int nibble2 = HexadecimalEncodingAlgorithm.HEXADECIMAL_TO_NIBBLE_TABLE[encodedValue.charAt(encodedIdx++) - '0'];
            value[i] = (byte)(nibble1 << 4 | nibble2);
        }
        return value;
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (data == null) {
            return;
        }
        final byte[] value = (byte[])data;
        if (value.length == 0) {
            return;
        }
        s.ensureCapacity(value.length * 2);
        for (int i = 0; i < value.length; ++i) {
            s.append(HexadecimalEncodingAlgorithm.NIBBLE_TO_HEXADECIMAL_TABLE[value[i] >>> 4 & 0xF]);
            s.append(HexadecimalEncodingAlgorithm.NIBBLE_TO_HEXADECIMAL_TABLE[value[i] & 0xF]);
        }
    }
    
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        return octetLength * 2;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength / 2;
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        System.arraycopy(array, astart, b, start, alength);
    }
    
    static {
        NIBBLE_TO_HEXADECIMAL_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        HEXADECIMAL_TO_NIBBLE_TABLE = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15 };
    }
}
