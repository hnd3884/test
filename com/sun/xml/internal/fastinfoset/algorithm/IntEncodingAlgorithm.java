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

public class IntEncodingAlgorithm extends IntegerEncodingAlgorithm
{
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 4 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfInt", new Object[] { 4 }));
        }
        return octetLength / 4;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength * 4;
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final int[] data = new int[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToIntArray(data, 0, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        return this.decodeFromInputStreamToIntArray(s);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
        }
        final int[] idata = (int[])data;
        this.encodeToOutputStreamFromIntArray(idata, s);
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List integerList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String iStringValue = cb.subSequence(start, end).toString();
                integerList.add(Integer.valueOf(iStringValue));
            }
        });
        return this.generateArrayFromList(integerList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof int[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotIntArray"));
        }
        final int[] idata = (int[])data;
        this.convertToCharactersFromIntArray(idata, s);
    }
    
    public final void decodeFromBytesToIntArray(final int[] idata, int istart, final byte[] b, int start, final int length) {
        for (int size = length / 4, i = 0; i < size; ++i) {
            idata[istart++] = ((b[start++] & 0xFF) << 24 | (b[start++] & 0xFF) << 16 | (b[start++] & 0xFF) << 8 | (b[start++] & 0xFF));
        }
    }
    
    public final int[] decodeFromInputStreamToIntArray(final InputStream s) throws IOException {
        final List integerList = new ArrayList();
        final byte[] b = new byte[4];
        while (true) {
            int n = s.read(b);
            if (n != 4) {
                if (n == -1) {
                    return this.generateArrayFromList(integerList);
                }
                while (n != 4) {
                    final int m = s.read(b, n, 4 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            final int i = (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
            integerList.add(i);
        }
    }
    
    public final void encodeToOutputStreamFromIntArray(final int[] idata, final OutputStream s) throws IOException {
        for (int i = 0; i < idata.length; ++i) {
            final int bits = idata[i];
            s.write(bits >>> 24 & 0xFF);
            s.write(bits >>> 16 & 0xFF);
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        this.encodeToBytesFromIntArray((int[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromIntArray(final int[] idata, final int istart, final int ilength, final byte[] b, int start) {
        for (int iend = istart + ilength, i = istart; i < iend; ++i) {
            final int bits = idata[i];
            b[start++] = (byte)(bits >>> 24 & 0xFF);
            b[start++] = (byte)(bits >>> 16 & 0xFF);
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }
    
    public final void convertToCharactersFromIntArray(final int[] idata, final StringBuffer s) {
        for (int end = idata.length - 1, i = 0; i <= end; ++i) {
            s.append(Integer.toString(idata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public final int[] generateArrayFromList(final List array) {
        final int[] idata = new int[array.size()];
        for (int i = 0; i < idata.length; ++i) {
            idata[i] = array.get(i);
        }
        return idata;
    }
}
