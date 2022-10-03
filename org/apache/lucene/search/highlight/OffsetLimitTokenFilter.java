package org.apache.lucene.search.highlight;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class OffsetLimitTokenFilter extends TokenFilter
{
    private int offsetCount;
    private OffsetAttribute offsetAttrib;
    private int offsetLimit;
    
    public OffsetLimitTokenFilter(final TokenStream input, final int offsetLimit) {
        super(input);
        this.offsetAttrib = (OffsetAttribute)this.getAttribute((Class)OffsetAttribute.class);
        this.offsetLimit = offsetLimit;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.offsetCount < this.offsetLimit && this.input.incrementToken()) {
            final int offsetLength = this.offsetAttrib.endOffset() - this.offsetAttrib.startOffset();
            this.offsetCount += offsetLength;
            return true;
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.offsetCount = 0;
    }
}
