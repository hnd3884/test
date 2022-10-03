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

public class DoubleEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm
{
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 8 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthIsNotMultipleOfDouble", new Object[] { 8 }));
        }
        return octetLength / 8;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength * 8;
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final double[] data = new double[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToDoubleArray(data, 0, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        return this.decodeFromInputStreamToDoubleArray(s);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof double[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotDouble"));
        }
        final double[] fdata = (double[])data;
        this.encodeToOutputStreamFromDoubleArray(fdata, s);
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List doubleList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String fStringValue = cb.subSequence(start, end).toString();
                doubleList.add(Double.valueOf(fStringValue));
            }
        });
        return this.generateArrayFromList(doubleList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof double[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotDouble"));
        }
        final double[] fdata = (double[])data;
        this.convertToCharactersFromDoubleArray(fdata, s);
    }
    
    public final void decodeFromBytesToDoubleArray(final double[] data, int fstart, final byte[] b, int start, final int length) {
        for (int size = length / 8, i = 0; i < size; ++i) {
            final long bits = (long)(b[start++] & 0xFF) << 56 | (long)(b[start++] & 0xFF) << 48 | (long)(b[start++] & 0xFF) << 40 | (long)(b[start++] & 0xFF) << 32 | (long)(b[start++] & 0xFF) << 24 | (long)(b[start++] & 0xFF) << 16 | (long)(b[start++] & 0xFF) << 8 | (long)(b[start++] & 0xFF);
            data[fstart++] = Double.longBitsToDouble(bits);
        }
    }
    
    public final double[] decodeFromInputStreamToDoubleArray(final InputStream s) throws IOException {
        final List doubleList = new ArrayList();
        final byte[] b = new byte[8];
        while (true) {
            int n = s.read(b);
            if (n != 8) {
                if (n == -1) {
                    return this.generateArrayFromList(doubleList);
                }
                while (n != 8) {
                    final int m = s.read(b, n, 8 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            final long bits = (long)(b[0] & 0xFF) << 56 | (long)(b[1] & 0xFF) << 48 | (long)(b[2] & 0xFF) << 40 | (long)(b[3] & 0xFF) << 32 | (long)((b[4] & 0xFF) << 24) | (long)((b[5] & 0xFF) << 16) | (long)((b[6] & 0xFF) << 8) | (long)(b[7] & 0xFF);
            doubleList.add(Double.longBitsToDouble(bits));
        }
    }
    
    public final void encodeToOutputStreamFromDoubleArray(final double[] fdata, final OutputStream s) throws IOException {
        for (int i = 0; i < fdata.length; ++i) {
            final long bits = Double.doubleToLongBits(fdata[i]);
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
        this.encodeToBytesFromDoubleArray((double[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromDoubleArray(final double[] fdata, final int fstart, final int flength, final byte[] b, int start) {
        for (int fend = fstart + flength, i = fstart; i < fend; ++i) {
            final long bits = Double.doubleToLongBits(fdata[i]);
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
    
    public final void convertToCharactersFromDoubleArray(final double[] fdata, final StringBuffer s) {
        for (int end = fdata.length - 1, i = 0; i <= end; ++i) {
            s.append(Double.toString(fdata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public final double[] generateArrayFromList(final List array) {
        final double[] fdata = new double[array.size()];
        for (int i = 0; i < fdata.length; ++i) {
            fdata[i] = array.get(i);
        }
        return fdata;
    }
}
