package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.Lucene43FilteringTokenFilter;

@Deprecated
public final class Lucene43LengthFilter extends Lucene43FilteringTokenFilter
{
    private final int min;
    private final int max;
    private final CharTermAttribute termAtt;
    
    public Lucene43LengthFilter(final boolean enablePositionIncrements, final TokenStream in, final int min, final int max) {
        super(enablePositionIncrements, in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.min = min;
        this.max = max;
    }
    
    public boolean accept() throws IOException {
        final int len = this.termAtt.length();
        return len >= this.min && len <= this.max;
    }
}
