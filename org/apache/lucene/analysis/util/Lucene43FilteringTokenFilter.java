package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.TokenFilter;

@Deprecated
public abstract class Lucene43FilteringTokenFilter extends TokenFilter
{
    private final PositionIncrementAttribute posIncrAtt;
    private boolean enablePositionIncrements;
    private boolean first;
    
    public Lucene43FilteringTokenFilter(final boolean enablePositionIncrements, final TokenStream input) {
        super(input);
        this.posIncrAtt = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
        this.first = true;
        this.enablePositionIncrements = enablePositionIncrements;
    }
    
    protected abstract boolean accept() throws IOException;
    
    public final boolean incrementToken() throws IOException {
        if (this.enablePositionIncrements) {
            int skippedPositions = 0;
            while (this.input.incrementToken()) {
                if (this.accept()) {
                    if (skippedPositions != 0) {
                        this.posIncrAtt.setPositionIncrement(this.posIncrAtt.getPositionIncrement() + skippedPositions);
                    }
                    return true;
                }
                skippedPositions += this.posIncrAtt.getPositionIncrement();
            }
        }
        else {
            while (this.input.incrementToken()) {
                if (this.accept()) {
                    if (this.first) {
                        if (this.posIncrAtt.getPositionIncrement() == 0) {
                            this.posIncrAtt.setPositionIncrement(1);
                        }
                        this.first = false;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.first = true;
    }
}
