package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.FilteringTokenFilter;

public final class KeepWordFilter extends FilteringTokenFilter
{
    private final CharArraySet words;
    private final CharTermAttribute termAtt;
    
    public KeepWordFilter(final TokenStream in, final CharArraySet words) {
        super(in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.words = words;
    }
    
    public boolean accept() {
        return this.words.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}
