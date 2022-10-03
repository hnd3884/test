package org.apache.lucene.analysis.commongrams;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class CommonGramsQueryFilter extends TokenFilter
{
    private final TypeAttribute typeAttribute;
    private final PositionIncrementAttribute posIncAttribute;
    private AttributeSource.State previous;
    private String previousType;
    private boolean exhausted;
    
    public CommonGramsQueryFilter(final CommonGramsFilter input) {
        super((TokenStream)input);
        this.typeAttribute = (TypeAttribute)this.addAttribute((Class)TypeAttribute.class);
        this.posIncAttribute = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
    }
    
    public void reset() throws IOException {
        super.reset();
        this.previous = null;
        this.previousType = null;
        this.exhausted = false;
    }
    
    public boolean incrementToken() throws IOException {
        while (!this.exhausted && this.input.incrementToken()) {
            final AttributeSource.State current = this.captureState();
            if (this.previous != null && !this.isGramType()) {
                this.restoreState(this.previous);
                this.previous = current;
                this.previousType = this.typeAttribute.type();
                if (this.isGramType()) {
                    this.posIncAttribute.setPositionIncrement(1);
                }
                return true;
            }
            this.previous = current;
        }
        this.exhausted = true;
        if (this.previous == null || "gram".equals(this.previousType)) {
            return false;
        }
        this.restoreState(this.previous);
        this.previous = null;
        if (this.isGramType()) {
            this.posIncAttribute.setPositionIncrement(1);
        }
        return true;
    }
    
    public boolean isGramType() {
        return "gram".equals(this.typeAttribute.type());
    }
}
