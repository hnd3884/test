package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class LimitTokenPositionFilter extends TokenFilter
{
    private final int maxTokenPosition;
    private final boolean consumeAllTokens;
    private int tokenPosition;
    private boolean exhausted;
    private final PositionIncrementAttribute posIncAtt;
    
    public LimitTokenPositionFilter(final TokenStream in, final int maxTokenPosition) {
        this(in, maxTokenPosition, false);
    }
    
    public LimitTokenPositionFilter(final TokenStream in, final int maxTokenPosition, final boolean consumeAllTokens) {
        super(in);
        this.tokenPosition = 0;
        this.exhausted = false;
        this.posIncAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        if (maxTokenPosition < 1) {
            throw new IllegalArgumentException("maxTokenPosition must be greater than zero");
        }
        this.maxTokenPosition = maxTokenPosition;
        this.consumeAllTokens = consumeAllTokens;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.exhausted) {
            return false;
        }
        if (!this.input.incrementToken()) {
            this.exhausted = true;
            return false;
        }
        this.tokenPosition += this.posIncAtt.getPositionIncrement();
        if (this.tokenPosition <= this.maxTokenPosition) {
            return true;
        }
        while (this.consumeAllTokens && this.input.incrementToken()) {}
        this.exhausted = true;
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.tokenPosition = 0;
        this.exhausted = false;
    }
}
