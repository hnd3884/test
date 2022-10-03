package com.sun.xml.internal.fastinfoset.algorithm;

import java.util.List;
import java.util.ArrayList;
import java.nio.CharBuffer;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import com.sun.xml.internal.fastinfoset.CommonResourceBundle;

public class UUIDEncodingAlgorithm extends LongEncodingAlgorithm
{
    private long _msb;
    private long _lsb;
    
    @Override
    public final int getPrimtiveLengthFromOctetLength(final int octetLength) throws EncodingAlgorithmException {
        if (octetLength % 16 != 0) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.lengthNotMultipleOfUUID", new Object[] { 16 }));
        }
        return octetLength / 8;
    }
    
    @Override
    public final Object convertFromCharacters(final char[] ch, final int start, final int length) {
        final CharBuffer cb = CharBuffer.wrap(ch, start, length);
        final List longList = new ArrayList();
        this.matchWhiteSpaceDelimnatedWords(cb, new WordListener() {
            @Override
            public void word(final int start, final int end) {
                final String uuidValue = cb.subSequence(start, end).toString();
                UUIDEncodingAlgorithm.this.fromUUIDString(uuidValue);
                longList.add(UUIDEncodingAlgorithm.this._msb);
                longList.add(UUIDEncodingAlgorithm.this._lsb);
            }
        });
        return this.generateArrayFromList(longList);
    }
    
    @Override
    public final void convertToCharacters(final Object data, final StringBuffer s) {
        if (!(data instanceof long[])) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.dataNotLongArray"));
        }
        final long[] ldata = (long[])data;
        for (int end = ldata.length - 2, i = 0; i <= end; i += 2) {
            s.append(this.toUUIDString(ldata[i], ldata[i + 1]));
            if (i != end) {
                s.append(' ');
            }
        }
    }
    
    final void fromUUIDString(final String name) {
        final String[] components = name.split("-");
        if (components.length != 5) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.invalidUUID", new Object[] { name }));
        }
        for (int i = 0; i < 5; ++i) {
            components[i] = "0x" + components[i];
        }
        this._msb = Long.parseLong(components[0], 16);
        this._msb <<= 16;
        this._msb |= Long.parseLong(components[1], 16);
        this._msb <<= 16;
        this._msb |= Long.parseLong(components[2], 16);
        this._lsb = Long.parseLong(components[3], 16);
        this._lsb <<= 48;
        this._lsb |= Long.parseLong(components[4], 16);
    }
    
    final String toUUIDString(final long msb, final long lsb) {
        return this.digits(msb >> 32, 8) + "-" + this.digits(msb >> 16, 4) + "-" + this.digits(msb, 4) + "-" + this.digits(lsb >> 48, 4) + "-" + this.digits(lsb, 12);
    }
    
    final String digits(final long val, final int digits) {
        final long hi = 1L << digits * 4;
        return Long.toHexString(hi | (val & hi - 1L)).substring(1);
    }
}
