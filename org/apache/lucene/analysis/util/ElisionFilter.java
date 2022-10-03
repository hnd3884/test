package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class ElisionFilter extends TokenFilter
{
    private final CharArraySet articles;
    private final CharTermAttribute termAtt;
    
    public ElisionFilter(final TokenStream input, final CharArraySet articles) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.articles = articles;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final char[] termBuffer = this.termAtt.buffer();
            final int termLength = this.termAtt.length();
            int index = -1;
            for (int i = 0; i < termLength; ++i) {
                final char ch = termBuffer[i];
                if (ch == '\'' || ch == '\u2019') {
                    index = i;
                    break;
                }
            }
            if (index >= 0 && this.articles.contains(termBuffer, 0, index)) {
                this.termAtt.copyBuffer(termBuffer, index + 1, termLength - (index + 1));
            }
            return true;
        }
        return false;
    }
}
