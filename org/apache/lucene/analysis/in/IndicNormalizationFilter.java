package org.apache.lucene.analysis.in;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class IndicNormalizationFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final IndicNormalizer normalizer;
    
    public IndicNormalizationFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.normalizer = new IndicNormalizer();
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            this.termAtt.setLength(this.normalizer.normalize(this.termAtt.buffer(), this.termAtt.length()));
            return true;
        }
        return false;
    }
}
