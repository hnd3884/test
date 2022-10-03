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

public class FloatEncodingAlgorithm extends IEEE754FloatingPointEncodingAlgorithm
{
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 4 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfFloat", new Object[] { 4 }));
        }
        return octetLength / 4;
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        return primitiveLength * 4;
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final float[] data = new float[this.getPrimtiveLengthFromOctetLength(length)];
        this.decodeFromBytesToFloatArray(data, 0, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        return this.decodeFromInputStreamToFloatArray(s);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
        }
        final float[] fdata = (float[])data;
        this.encodeToOutputStreamFromFloatArray(fdata, s);
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List floatList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String fStringValue = cb.subSequence(start, end).toString();
                floatList.add(Float.valueOf(fStringValue));
            }
        });
        return this.generateArrayFromList(floatList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof float[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotFloat"));
        }
        final float[] fdata = (float[])data;
        this.convertToCharactersFromFloatArray(fdata, s);
    }
    
    public final void decodeFromBytesToFloatArray(final float[] data, int fstart, final byte[] b, int start, final int length) {
        for (int size = length / 4, i = 0; i < size; ++i) {
            final int bits = (b[start++] & 0xFF) << 24 | (b[start++] & 0xFF) << 16 | (b[start++] & 0xFF) << 8 | (b[start++] & 0xFF);
            data[fstart++] = Float.intBitsToFloat(bits);
        }
    }
    
    public final float[] decodeFromInputStreamToFloatArray(final InputStream s) throws IOException {
        final List floatList = new ArrayList();
        final byte[] b = new byte[4];
        while (true) {
            int n = s.read(b);
            if (n != 4) {
                if (n == -1) {
                    return this.generateArrayFromList(floatList);
                }
                while (n != 4) {
                    final int m = s.read(b, n, 4 - n);
                    if (m == -1) {
                        throw new EOFException();
                    }
                    n += m;
                }
            }
            final int bits = (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
            floatList.add(Float.intBitsToFloat(bits));
        }
    }
    
    public final void encodeToOutputStreamFromFloatArray(final float[] fdata, final OutputStream s) throws IOException {
        for (int i = 0; i < fdata.length; ++i) {
            final int bits = Float.floatToIntBits(fdata[i]);
            s.write(bits >>> 24 & 0xFF);
            s.write(bits >>> 16 & 0xFF);
            s.write(bits >>> 8 & 0xFF);
            s.write(bits & 0xFF);
        }
    }
    
    @Override
    public final void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        this.encodeToBytesFromFloatArray((float[])array, astart, alength, b, start);
    }
    
    public final void encodeToBytesFromFloatArray(final float[] fdata, final int fstart, final int flength, final byte[] b, int start) {
        for (int fend = fstart + flength, i = fstart; i < fend; ++i) {
            final int bits = Float.floatToIntBits(fdata[i]);
            b[start++] = (byte)(bits >>> 24 & 0xFF);
            b[start++] = (byte)(bits >>> 16 & 0xFF);
            b[start++] = (byte)(bits >>> 8 & 0xFF);
            b[start++] = (byte)(bits & 0xFF);
        }
    }
    
    public final void convertToCharactersFromFloatArray(final float[] fdata, final StringBuffer s) {
        for (int end = fdata.length - 1, i = 0; i <= end; ++i) {
            s.append(Float.toString(fdata[i]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public final float[] generateArrayFromList(final List array) {
        final float[] fdata = new float[array.size()];
        for (int i = 0; i < fdata.length; ++i) {
            fdata[i] = array.get(i);
        }
        return fdata;
    }
}
