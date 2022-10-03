package com.sun.xml.internal.fastinfoset.algorithm;

import java.nio.CharBuffer;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;
import java.io.EOFException;
import java.util.ArrayList;
import java.io.InputStream;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;

public class BooleanEncodingAlgorithm extends BuiltInEncodingAlgorithm
{
    private static final int[] BIT_TABLE;
    
    @Override
    public int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int getOctetLengthFromPrimitiveLength(final int primitiveLength) {
        if (primitiveLength < 5) {
            return 1;
        }
        final int div = primitiveLength / 8;
        return (div == 0) ? 2 : (1 + div);
    }
    
    @Override
    public final Object decodeFromBytes(final byte[] b, final int start, final int length) throws EncodingAlgorithmException {
        final int blength = this.getPrimtiveLengthFromOctetLength(length, b[start]);
        final boolean[] data = new boolean[blength];
        this.decodeFromBytesToBooleanArray(data, 0, blength, b, start, length);
        return data;
    }
    
    @Override
    public final Object decodeFromInputStream(final InputStream s) throws IOException {
        final List booleanList = new ArrayList();
        int value = s.read();
        if (value == -1) {
            throw new EOFException();
        }
        final int unusedBits = value >> 4 & 0xFF;
        int bitPosition = 4;
        int bitPositionEnd = 8;
        int valueNext = 0;
        do {
            valueNext = s.read();
            if (valueNext == -1) {
                bitPositionEnd -= unusedBits;
            }
            while (bitPosition < bitPositionEnd) {
                booleanList.add((value & BooleanEncodingAlgorithm.BIT_TABLE[bitPosition++]) > 0);
            }
            value = valueNext;
        } while (value != -1);
        return this.generateArrayFromList(booleanList);
    }
    
    @Override
    public void encodeToOutputStream(final Object data, final OutputStream s) throws IOException {
        if (!(data instanceof boolean[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
        }
        final boolean[] array = (boolean[])data;
        final int alength = array.length;
        final int mod = (alength + 4) % 8;
        final int unusedBits = (mod == 0) ? 0 : (8 - mod);
        int bitPosition = 4;
        int value = unusedBits << 4;
        int astart = 0;
        while (astart < alength) {
            if (array[astart++]) {
                value |= BooleanEncodingAlgorithm.BIT_TABLE[bitPosition];
            }
            if (++bitPosition == 8) {
                s.write(value);
                value = (bitPosition = 0);
            }
        }
        if (bitPosition != 8) {
            s.write(value);
        }
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        if (length == 0) {
            return new boolean[0];
        }
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List booleanList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                if (cb.charAt(start) == 't') {
                    booleanList.add(Boolean.TRUE);
                }
                else {
                    booleanList.add(Boolean.FALSE);
                }
            }
        });
        return this.generateArrayFromList(booleanList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (data == null) {
            return;
        }
        final boolean[] value = (boolean[])data;
        if (value.length == 0) {
            return;
        }
        s.ensureCapacity(value.length * 5);
        for (int end = value.length - 1, i = 0; i <= end; ++i) {
            if (value[i]) {
                s.append("true");
            }
            else {
                s.append("false");
            }
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    public int getPrimtiveLengthFromOctetLength(final int octetLength, final int firstOctet) throws EncodingAlgorithmException {
        final int unusedBits = firstOctet >> 4 & 0xFF;
        if (octetLength == 1) {
            if (unusedBits > 3) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits4"));
            }
            return 4 - unusedBits;
        }
        else {
            if (unusedBits > 7) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.unusedBits8"));
            }
            return octetLength * 8 - 4 - unusedBits;
        }
    }
    
    public final void decodeFromBytesToBooleanArray(final boolean[] bdata, int bstart, final int blength, final byte[] b, int start, final int length) {
        for (int value = b[start++] & 0xFF, bitPosition = 4, bend = bstart + blength; bstart < bend; bdata[bstart++] = ((value & BooleanEncodingAlgorithm.BIT_TABLE[bitPosition++]) > 0)) {
            if (bitPosition == 8) {
                value = (b[start++] & 0xFF);
                bitPosition = 0;
            }
        }
    }
    
    @Override
    public void encodeToBytes(final Object array, final int astart, final int alength, final byte[] b, final int start) {
        if (!(array instanceof boolean[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotBoolean"));
        }
        this.encodeToBytesFromBooleanArray((boolean[])array, astart, alength, b, start);
    }
    
    public void encodeToBytesFromBooleanArray(final boolean[] array, int astart, final int alength, final byte[] b, int start) {
        final int mod = (alength + 4) % 8;
        final int unusedBits = (mod == 0) ? 0 : (8 - mod);
        int bitPosition = 4;
        int value = unusedBits << 4;
        final int aend = astart + alength;
        while (astart < aend) {
            if (array[astart++]) {
                value |= BooleanEncodingAlgorithm.BIT_TABLE[bitPosition];
            }
            if (++bitPosition == 8) {
                b[start++] = (byte)value;
                value = (bitPosition = 0);
            }
        }
        if (bitPosition > 0) {
            b[start] = (byte)value;
        }
    }
    
    private boolean[] generateArrayFromList(final List array) {
        final boolean[] bdata = new boolean[array.size()];
        for (int i = 0; i < bdata.length; ++i) {
            bdata[i] = array.get(i);
        }
        return bdata;
    }
    
    static {
        BIT_TABLE = new int[] { 128, 64, 32, 16, 8, 4, 2, 1 };
    }
}
