package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.analysis.TokenFilter;

public final class UpperCaseFilter extends TokenFilter
{
    private final CharacterUtils charUtils;
    private final CharTermAttribute termAtt;
    
    public UpperCaseFilter(final TokenStream in) {
        super(in);
        this.charUtils = CharacterUtils.getInstance();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            this.charUtils.toUpperCase(this.termAtt.buffer(), 0, this.termAtt.length());
            return true;
        }
        return false;
    }
}
