package org.apache.lucene.analysis.fa;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class PersianNormalizationFilter extends TokenFilter
{
    private final PersianNormalizer normalizer;
    private final CharTermAttribute termAtt;
    
    public PersianNormalizationFilter(final TokenStream input) {
        super(input);
        this.normalizer = new PersianNormalizer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final int newlen = this.normalizer.normalize(this.termAtt.buffer(), this.termAtt.length());
            this.termAtt.setLength(newlen);
            return true;
        }
        return false;
    }
}
