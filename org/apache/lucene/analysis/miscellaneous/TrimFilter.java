package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class TrimFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    
    public TrimFilter(final TokenStream in) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] termBuffer = this.termAtt.buffer();
        final int len = this.termAtt.length();
        if (len == 0) {
            return true;
        }
        int start = 0;
        int end = 0;
        for (start = 0; start < len && Character.isWhitespace(termBuffer[start]); ++start) {}
        for (end = len; end >= start && Character.isWhitespace(termBuffer[end - 1]); --end) {}
        if (start > 0 || end < len) {
            if (start < end) {
                this.termAtt.copyBuffer(termBuffer, start, end - start);
            }
            else {
                this.termAtt.setEmpty();
            }
        }
        return true;
    }
}
