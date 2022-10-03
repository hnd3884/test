package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class SetKeywordMarkerFilter extends KeywordMarkerFilter
{
    private final CharTermAttribute termAtt;
    private final CharArraySet keywordSet;
    
    public SetKeywordMarkerFilter(final TokenStream in, final CharArraySet keywordSet) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordSet = keywordSet;
    }
    
    @Override
    protected boolean isKeyword() {
        return this.keywordSet.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}
