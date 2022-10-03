package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public final class CodepointCountFilter extends FilteringTokenFilter
{
    private final int min;
    private final int max;
    private final CharTermAttribute termAtt;
    
    public CodepointCountFilter(final TokenStream in, final int min, final int max) {
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
        final int max32 = this.termAtt.length();
        final int min32 = max32 >> 1;
        if (min32 >= this.min && max32 <= this.max) {
            return true;
        }
        if (min32 > this.max || max32 < this.min) {
            return false;
        }
        final int len = Character.codePointCount(this.termAtt.buffer(), 0, this.termAtt.length());
        return len >= this.min && len <= this.max;
    }
}
