package com.sun.xml.internal.fastinfoset.algorithm;

import java.io.OutputStream;
import java.io.IOException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;

public class BASE64EncodingAlgorithm extends BuiltInEncodingAlgorithm
{
    static final char[] encodeBase64;
    static final int[] decodeBase64;
    
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
        final int blockCount = encodedLength / 4;
        int partialBlockLength = 3;
        if (encodedValue.charAt(encodedLength - 1) == '=') {
            --partialBlockLength;
            if (encodedValue.charAt(encodedLength - 2) == '=') {
                --partialBlockLength;
            }
        }
        final int valueLength = (blockCount - 1) * 3 + partialBlockLength;
        final byte[] value = new byte[valueLength];
        int idx = 0;
        int encodedIdx = 0;
        for (int i = 0; i < blockCount; ++i) {
            final int x1 = BASE64EncodingAlgorithm.decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            final int x2 = BASE64EncodingAlgorithm.decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            final int x3 = BASE64EncodingAlgorithm.decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            final int x4 = BASE64EncodingAlgorithm.decodeBase64[encodedValue.charAt(encodedIdx++) - '+'];
            value[idx++] = (byte)(x1 << 2 | x2 >> 4);
            if (idx < valueLength) {
                value[idx++] = (byte)((x2 & 0xF) << 4 | x3 >> 2);
            }
            if (idx < valueLength) {
                value[idx++] = (byte)((x3 & 0x3) << 6 | x4);
            }
        }
        return value;
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (data == null) {
            return;
        }
        final byte[] value = (byte[])data;
        this.convertToCharacters(value, 0, value.length, s);
    }
    
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        return octetLength;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength;
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        System.arraycopy(array, astart, b, start, alength);
    }
    
    public final void convertToCharacters(final byte[] data, final int offset, final int length, final StringBuffer s) {
        if (data == null) {
            return;
        }
        final byte[] value = data;
        if (length == 0) {
            return;
        }
        final int partialBlockLength = length % 3;
        final int blockCount = (partialBlockLength != 0) ? (length / 3 + 1) : (length / 3);
        final int encodedLength = blockCount * 4;
        final int originalBufferSize = s.length();
        s.ensureCapacity(encodedLength + originalBufferSize);
        int idx = offset;
        final int lastIdx = offset + length;
        for (int i = 0; i < blockCount; ++i) {
            final int b1 = value[idx++] & 0xFF;
            final int b2 = (idx < lastIdx) ? (value[idx++] & 0xFF) : 0;
            final int b3 = (idx < lastIdx) ? (value[idx++] & 0xFF) : 0;
            s.append(BASE64EncodingAlgorithm.encodeBase64[b1 >> 2]);
            s.append(BASE64EncodingAlgorithm.encodeBase64[(b1 & 0x3) << 4 | b2 >> 4]);
            s.append(BASE64EncodingAlgorithm.encodeBase64[(b2 & 0xF) << 2 | b3 >> 6]);
            s.append(BASE64EncodingAlgorithm.encodeBase64[b3 & 0x3F]);
        }
        switch (partialBlockLength) {
            case 1: {
                s.setCharAt(originalBufferSize + encodedLength - 1, '=');
                s.setCharAt(originalBufferSize + encodedLength - 2, '=');
                break;
            }
            case 2: {
                s.setCharAt(originalBufferSize + encodedLength - 1, '=');
                break;
            }
        }
    }
    
    static {
        encodeBase64 = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        decodeBase64 = new int[] { 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51 };
    }
}
