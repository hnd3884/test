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

public class ShortEncodingAlgorithm extends IntegerEncodingAlgorithm
{
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 2 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfShort", new Object[] { 2 }));
        }
        return octetLength / 2;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength * 2;
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final short[] data = new short[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToShortArray(data, 0, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        return this.decodeFromInputStreamToShortArray(s);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
        }
        final short[] idata = (short[])data;
        this.encodeToOutputStreamFromShortArray(idata, s);
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List shortList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String iStringValue = cb.subSequence(start, end).toString();
                shortList.add(Short.valueOf(iStringValue));
            }
        });
        return this.generateArrayFromList(shortList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof short[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotShortArray"));
        }
        final short[] idata = (short[])data;
        this.convertToCharactersFromShortArray(idata, s);
    }
    
    public final void decodeFromBytesToShortArray(final short[] sdata, int istart, final byte[] b, int start, final int length) {
        for (int size = length / 2, i = 0; i < size; ++i) {
            sdata[istart++] = (short)((b[start++] & 0xFF) << 8 | (b[start++] & 0xFF));
        }
    }
    
    public final short[] decodeFromInputStreamToShortArray(final InputStream s) throws IOException {
        final List shortList = new ArrayList();
        final byte[] b = new byte[2];
        while (true) {
            int n = s.read(b);
            if (n != 2) {
                if (n == -1) {
                    return this.generateArrayFromList(shortList);
                }
                while (n != 2) {
                    final int m = s.read(b, n, 2 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            final int i = (b[0] & 0xFF) << 8 | (b[1] & 0xFF);
            shortList.add((short)i);
        }
    }
    
    public final void encodeToOutputStreamFromShortArray(final short[] idata, final OutputStream s) throws IOException {
        for (int i = 0; i < idata.length; ++i) {
            final int bits = idata[i];
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        this.encodeToBytesFromShortArray((short[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromShortArray(final short[] sdata, final int istart, final int ilength, final byte[] b, int start) {
        for (int iend = istart + ilength, i = istart; i < iend; ++i) {
            final short bits = sdata[i];
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }
    
    public final void convertToCharactersFromShortArray(final short[] sdata, final StringBuffer s) {
        for (int end = sdata.length - 1, i = 0; i <= end; ++i) {
            s.append(Short.toString(sdata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public final short[] generateArrayFromList(final List array) {
        final short[] sdata = new short[array.size()];
        for (int i = 0; i < sdata.length; ++i) {
            sdata[i] = array.get(i);
        }
        return sdata;
    }
}
