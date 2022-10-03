package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class TruncateTokenFilter extends TokenFilter
{
    private final CharTermAttribute termAttribute;
    private final KeywordAttribute keywordAttr;
    private final int length;
    
    public TruncateTokenFilter(final TokenStream input, final int length) {
        super(input);
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        if (length < 1) {
            throw new IllegalArgumentException("length parameter must be a positive number: " + length);
        }
        this.length = length;
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword() && this.termAttribute.length() > this.length) {
                this.termAttribute.setLength(this.length);
            }
            return true;
        }
        return false;
    }
}
