package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.Lucene43FilteringTokenFilter;

@Deprecated
public final class Lucene43KeepWordFilter extends Lucene43FilteringTokenFilter
{
    private final CharTermAttribute termAtt;
    private final CharArraySet words;
    
    public Lucene43KeepWordFilter(final boolean enablePositionIncrements, final TokenStream in, final CharArraySet words) {
        super(enablePositionIncrements, in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.words = words;
    }
    
    public boolean accept() {
        return this.words.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}
