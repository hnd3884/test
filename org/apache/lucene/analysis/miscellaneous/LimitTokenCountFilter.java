package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.TokenFilter;

public final class LimitTokenCountFilter extends TokenFilter
{
    private final int maxTokenCount;
    private final boolean consumeAllTokens;
    private int tokenCount;
    private boolean exhausted;
    
    public LimitTokenCountFilter(final TokenStream in, final int maxTokenCount) {
        this(in, maxTokenCount, false);
    }
    
    public LimitTokenCountFilter(final TokenStream in, final int maxTokenCount, final boolean consumeAllTokens) {
        super(in);
        this.tokenCount = 0;
        this.exhausted = false;
        if (maxTokenCount < 1) {
            throw new IllegalArgumentException("maxTokenCount must be greater than zero");
        }
        this.maxTokenCount = maxTokenCount;
        this.consumeAllTokens = consumeAllTokens;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.exhausted) {
            return false;
        }
        if (this.tokenCount >= this.maxTokenCount) {
            while (this.consumeAllTokens && this.input.incrementToken()) {}
            return false;
        }
        if (this.input.incrementToken()) {
            ++this.tokenCount;
            return true;
        }
        this.exhausted = true;
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.tokenCount = 0;
        this.exhausted = false;
    }
}
