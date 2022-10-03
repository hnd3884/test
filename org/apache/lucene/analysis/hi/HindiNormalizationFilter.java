package org.apache.lucene.analysis.hi;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class HindiNormalizationFilter extends TokenFilter
{
    private final HindiNormalizer normalizer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAtt;
    
    public HindiNormalizationFilter(final TokenStream input) {
        super(input);
        this.normalizer = new HindiNormalizer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAtt.isKeyword()) {
                this.termAtt.setLength(this.normalizer.normalize(this.termAtt.buffer(), this.termAtt.length()));
            }
            return true;
        }
        return false;
    }
}
