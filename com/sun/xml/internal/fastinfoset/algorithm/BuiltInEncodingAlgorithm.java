package com.sun.xml.internal.fastinfoset.algorithm;

import java.util.regex.Matcher;
import java.nio.CharBuffer;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithmException;
import java.util.regex.Pattern;
import com.sun.xml.internal.org.jvnet.fastinfoset.EncodingAlgorithm;

public abstract class BuiltInEncodingAlgorithm implements EncodingAlgorithm
{
    protected static final Pattern SPACE_PATTERN;
    
    public abstract int getPrimtiveLengthFromOctetLength(final int p0) throws EncodingAlgorithmException;
    
    public abstract int getOctetLengthFromPrimitiveLength(final int p0);
    
    public abstract void encodeToBytes(final Object p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    public void matchWhiteSpaceDelimnatedWords(final CharBuffer cb, final WordListener wl) {
        final Matcher m = BuiltInEncodingAlgorithm.SPACE_PATTERN.matcher(cb);
        int i = 0;
        int s = 0;
        while (m.find()) {
            s = m.start();
            if (s != i) {
                wl.word(i, s);
            }
            i = m.end();
        }
        if (i != cb.length()) {
            wl.word(i, cb.length());
        }
    }
    
    public StringBuilder removeWhitespace(final char[] ch, final int start, final int length) {
        final StringBuilder buf = new StringBuilder();
        int firstNonWS = 0;
        int idx;
        for (idx = 0; idx < length; ++idx) {
            if (Character.isWhitespace(ch[idx + start])) {
                if (firstNonWS < idx) {
                    buf.append(ch, firstNonWS + start, idx - firstNonWS);
                }
                firstNonWS = idx + 1;
            }
        }
        if (firstNonWS < idx) {
            buf.append(ch, firstNonWS + start, idx - firstNonWS);
        }
        return buf;
    }
    
    static {
        SPACE_PATTERN = Pattern.compile("\\s");
    }
    
    public interface WordListener
    {
        void word(final int p0, final int p1);
    }
}
