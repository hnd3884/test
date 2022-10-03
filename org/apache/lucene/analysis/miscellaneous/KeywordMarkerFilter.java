package org.apache.lucene.analysis.miscellaneous;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.TokenFilter;

public abstract class KeywordMarkerFilter extends TokenFilter
{
    private final KeywordAttribute keywordAttr;
    
    protected KeywordMarkerFilter(final TokenStream in) {
        super(in);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (this.isKeyword()) {
                this.keywordAttr.setKeyword(true);
            }
            return true;
        }
        return false;
    }
    
    protected abstract boolean isKeyword();
}
