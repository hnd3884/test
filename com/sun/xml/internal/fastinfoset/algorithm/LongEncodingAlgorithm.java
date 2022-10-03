package com.sun.xml.internal.fastinfoset.algorithm;

import java.io.EOFException;
import java.util.List;
import java.util.ArrayList;
import java.nio.CharBuffer;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class LongEncodingAlgorithm extends IntegerEncodingAlgorithm
{
    @Override
    public int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 8 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfLong", new Object[] { 8 }));
        }
        return octetLength / 8;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength * 8;
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final long[] data = new long[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToLongArray(data, 0, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        return this.decodeFromInputStreamToIntArray(s);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
        }
        final long[] ldata = (long[])data;
        this.encodeToOutputStreamFromLongArray(ldata, s);
    }
    
    @Override
    public Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List longList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String lStringValue = cb.subSequence(start, end).toString();
                longList.add(Long.valueOf(lStringValue));
            }
        });
        return this.generateArrayFromList(longList);
    }
    
    @Override
    public void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
        }
        final long[] ldata = (long[])data;
        this.convertToCharactersFromLongArray(ldata, s);
    }
    
    public final void decodeFromBytesToLongArray(final long[] ldata, int istart, final byte[] b, int start, final int length) {
        for (int size = length / 8, i = 0; i < size; ++i) {
            ldata[istart++] = ((long)(b[start++] & 0xFF) << 56 | (long)(b[start++] & 0xFF) << 48 | (long)(b[start++] & 0xFF) << 40 | (long)(b[start++] & 0xFF) << 32 | (long)(b[start++] & 0xFF) << 24 | (long)(b[start++] & 0xFF) << 16 | (long)(b[start++] & 0xFF) << 8 | (long)(b[start++] & 0xFF));
        }
    }
    
    public final long[] decodeFromInputStreamToIntArray(final InputStream s) throws IOException {
        final List longList = new ArrayList();
        final byte[] b = new byte[8];
        while (true) {
            int n = s.read(b);
            if (n != 8) {
                if (n == -1) {
                    return this.generateArrayFromList(longList);
                }
                while (n != 8) {
                    final int m = s.read(b, n, 8 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            final long l = ((long)b[0] << 56) + ((long)(b[1] & 0xFF) << 48) + ((long)(b[2] & 0xFF) << 40) + ((long)(b[3] & 0xFF) << 32) + ((long)(b[4] & 0xFF) << 24) + ((b[5] & 0xFF) << 16) + ((b[6] & 0xFF) << 8) + ((b[7] & 0xFF) << 0);
            longList.add(l);
        }
    }
    
    public final void encodeToOutputStreamFromLongArray(final long[] ldata, final OutputStream s) throws IOException {
        for (int i = 0; i < ldata.length; ++i) {
            final long bits = ldata[i];
            s.write((int)(bits >>> 56 & 0xFFL));
            s.write((int)(bits >>> 48 & 0xFFL));
            s.write((int)(bits >>> 40 & 0xFFL));
            s.write((int)(bits >>> 32 & 0xFFL));
            s.write((int)(bits >>> 24 & 0xFFL));
            s.write((int)(bits >>> 16 & 0xFFL));
            s.write((int)(bits >>> 8 & 0xFFL));
            s.write((int)(bits & 0xFFL));
        }
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        this.encodeToBytesFromLongArray((long[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromLongArray(final long[] ldata, final int lstart, final int llength, final byte[] b, int start) {
        for (int lend = lstart + llength, i = lstart; i < lend; ++i) {
            final long bits = ldata[i];
            b[start++] = (byte)(bits >>> 56 & 0xFFL);
            b[start++] = (byte)(bits >>> 48 & 0xFFL);
            b[start++] = (byte)(bits >>> 40 & 0xFFL);
            b[start++] = (byte)(bits >>> 32 & 0xFFL);
            b[start++] = (byte)(bits >>> 24 & 0xFFL);
            b[start++] = (byte)(bits >>> 16 & 0xFFL);
            b[start++] = (byte)(bits >>> 8 & 0xFFL);
            b[start++] = (byte)(bits & 0xFFL);
        }
    }
    
    public final void convertToCharactersFromLongArray(final long[] ldata, final StringBuffer s) {
        for (int end = ldata.length - 1, i = 0; i <= end; ++i) {
            s.append(Long.toString(ldata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public final long[] generateArrayFromList(final List array) {
        final long[] ldata = new long[array.size()];
        for (int i = 0; i < ldata.length; ++i) {
            ldata[i] = array.get(i);
        }
        return ldata;
    }
}
