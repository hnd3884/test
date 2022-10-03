package org.apache.lucene.analysis.ckb;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class SoraniNormalizationFilter extends TokenFilter
{
    private final SoraniNormalizer normalizer;
    private final CharTermAttribute termAtt;
    
    public SoraniNormalizationFilter(final TokenStream input) {
        super(input);
        this.normalizer = new SoraniNormalizer();
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
