package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenFilter;

public abstract class FilteringTokenFilter extends TokenFilter
{
    private final PositionIncrementAttribute posIncrAtt;
    private int skippedPositions;
    
    public FilteringTokenFilter(final TokenStream in) {
        super(in);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
    }
    
    protected abstract boolean accept() throws IOException;
    
    public final boolean incrementToken() throws IOException {
        this.skippedPositions = 0;
        while (this.input.incrementToken()) {
            if (this.accept()) {
                if (this.skippedPositions != 0) {
                    this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + this.skippedPositions);
                }
                return true;
            }
            this.skippedPositions += this.posIncrAtt.getPositionIncrement();
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.skippedPositions = 0;
    }
    
    public void end() throws IOException {
        super.end();
        this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + this.skippedPositions);
    }
}
