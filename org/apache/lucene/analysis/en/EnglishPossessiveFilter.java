package org.apache.lucene.analysis.en;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class EnglishPossessiveFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    
    public EnglishPossessiveFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] buffer = this.termAtt.buffer();
        final int bufferLength = this.termAtt.length();
        if (bufferLength >= 2 && (buffer[bufferLength - 2] == '\'' || buffer[bufferLength - 2] == '\u2019' || buffer[bufferLength - 2] == '\uff07') && (buffer[bufferLength - 1] == 's' || buffer[bufferLength - 1] == 'S')) {
            this.termAtt.setLength(bufferLength - 2);
        }
        return true;
    }
}
