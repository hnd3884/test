package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.analysis.util.StemmerUtil;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class DecimalDigitFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    
    public DecimalDigitFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final char[] buffer = this.termAtt.buffer();
            for (int length = this.termAtt.length(), i = 0; i < length; ++i) {
                final int ch = Character.codePointAt(buffer, i, length);
                if (ch > 127 && Character.isDigit(ch)) {
                    buffer[i] = (char)(48 + Character.getNumericValue(ch));
                    if (ch > 65535) {
                        length = StemmerUtil.delete(buffer, ++i, length);
                        this.termAtt.setLength(length);
                    }
                }
            }
            return true;
        }
        return false;
    }
}
