package org.apache.lucene.analysis.tr;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class ApostropheFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    
    public ApostropheFilter(final TokenStream in) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] buffer = this.termAtt.buffer();
        for (int length = this.termAtt.length(), i = 0; i < length; ++i) {
            if (buffer[i] == '\'' || buffer[i] == '\u2019') {
                this.termAtt.setLength(i);
                return true;
            }
        }
        return true;
    }
}
