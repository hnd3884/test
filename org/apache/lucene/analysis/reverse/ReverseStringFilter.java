package org.apache.lucene.analysis.reverse;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class ReverseStringFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final char marker;
    private static final char NOMARKER = '\uffff';
    public static final char START_OF_HEADING_MARKER = '\u0001';
    public static final char INFORMATION_SEPARATOR_MARKER = '\u001f';
    public static final char PUA_EC00_MARKER = '\uec00';
    public static final char RTL_DIRECTION_MARKER = '\u200f';
    
    public ReverseStringFilter(final TokenStream in) {
        this(in, '\uffff');
    }
    
    public ReverseStringFilter(final TokenStream in, final char marker) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.marker = marker;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            int len = this.termAtt.length();
            if (this.marker != '\uffff') {
                ++len;
                this.termAtt.resizeBuffer(len);
                this.termAtt.buffer()[len - 1] = this.marker;
            }
            reverse(this.termAtt.buffer(), 0, len);
            this.termAtt.setLength(len);
            return true;
        }
        return false;
    }
    
    public static String reverse(final String input) {
        final char[] charInput = input.toCharArray();
        reverse(charInput, 0, charInput.length);
        return new String(charInput);
    }
    
    public static void reverse(final char[] buffer) {
        reverse(buffer, 0, buffer.length);
    }
    
    public static void reverse(final char[] buffer, final int len) {
        reverse(buffer, 0, len);
    }
    
    public static void reverse(final char[] buffer, final int start, final int len) {
        if (len < 2) {
            return;
        }
        int end = start + len - 1;
        char frontHigh = buffer[start];
        char endLow = buffer[end];
        boolean allowFrontSur = true;
        boolean allowEndSur = true;
        for (int mid = start + (len >> 1), i = start; i < mid; ++i, --end) {
            final char frontLow = buffer[i + 1];
            final char endHigh = buffer[end - 1];
            final boolean surAtFront = allowFrontSur && Character.isSurrogatePair(frontHigh, frontLow);
            if (surAtFront && len < 3) {
                return;
            }
            final boolean surAtEnd = allowEndSur && Character.isSurrogatePair(endHigh, endLow);
            allowEndSur = (allowFrontSur = true);
            if (surAtFront == surAtEnd) {
                if (surAtFront) {
                    buffer[end] = frontLow;
                    buffer[--end] = frontHigh;
                    buffer[i] = endHigh;
                    buffer[++i] = endLow;
                    frontHigh = buffer[i + 1];
                    endLow = buffer[end - 1];
                }
                else {
                    buffer[end] = frontHigh;
                    buffer[i] = endLow;
                    frontHigh = frontLow;
                    endLow = endHigh;
                }
            }
            else if (surAtFront) {
                buffer[end] = frontLow;
                buffer[i] = endLow;
                endLow = endHigh;
                allowFrontSur = false;
            }
            else {
                buffer[end] = frontHigh;
                buffer[i] = endHigh;
                frontHigh = frontLow;
                allowEndSur = false;
            }
        }
        if ((len & 0x1) == 0x1 && (!allowFrontSur || !allowEndSur)) {
            buffer[end] = (allowFrontSur ? endLow : frontHigh);
        }
    }
}
