package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class KeywordRepeatFilter extends TokenFilter
{
    private final KeywordAttribute keywordAttribute;
    private final PositionIncrementAttribute posIncAttr;
    private AttributeSource.State state;
    
    public KeywordRepeatFilter(final TokenStream input) {
        super(input);
        this.keywordAttribute = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.posIncAttr = (PositionIncrementAttribute)this.addAttribute((Class)PositionIncrementAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.state != null) {
            this.restoreState(this.state);
            this.posIncAttr.setPositionIncrement(0);
            this.keywordAttribute.setKeyword(false);
            this.state = null;
            return true;
        }
        if (this.input.incrementToken()) {
            this.state = this.captureState();
            this.keywordAttribute.setKeyword(true);
            return true;
        }
        return false;
    }
    
    public void reset() throws IOException {
        super.reset();
        this.state = null;
    }
}
