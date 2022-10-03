package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public final class LengthFilter extends FilteringTokenFilter
{
    private final int min;
    private final int max;
    private final CharTermAttribute termAtt;
    
    public LengthFilter(final TokenStream in, final int min, final int max) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        if (min < 0) {
            throw new IllegalArgumentException("minimum length must be greater than or equal to zero");
        }
        if (min > max) {
            throw new IllegalArgumentException("maximum length must not be greater than minimum length");
        }
        this.min = min;
        this.max = max;
    }
    
    public boolean accept() {
        final int len = this.termAtt.length();
        return len >= this.min && len <= this.max;
    }
}
