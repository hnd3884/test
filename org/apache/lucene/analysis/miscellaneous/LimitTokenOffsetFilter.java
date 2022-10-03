package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class LimitTokenOffsetFilter extends TokenFilter
{
    private final OffsetAttribute offsetAttrib;
    private int maxStartOffset;
    private final boolean consumeAllTokens;
    
    public LimitTokenOffsetFilter(final TokenStream input, final int maxStartOffset) {
        this(input, maxStartOffset, false);
    }
    
    public LimitTokenOffsetFilter(final TokenStream input, final int maxStartOffset, final boolean consumeAllTokens) {
        super(input);
        this.offsetAttrib = (OffsetAttribute)this.addAttribute((Class)OffsetAttribute.class);
        if (maxStartOffset < 0) {
            throw new IllegalArgumentException("maxStartOffset must be >= zero");
        }
        this.maxStartOffset = maxStartOffset;
        this.consumeAllTokens = consumeAllTokens;
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        if (this.offsetAttrib.startOffset() <= this.maxStartOffset) {
            return true;
        }
        if (this.consumeAllTokens) {
            while (this.input.incrementToken()) {}
        }
        return false;
    }
}
