package org.apache.lucene.analysis.core;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.Lucene43FilteringTokenFilter;

@Deprecated
public final class Lucene43StopFilter extends Lucene43FilteringTokenFilter
{
    private final CharArraySet stopWords;
    private final CharTermAttribute termAtt;
    
    public Lucene43StopFilter(final boolean enablePositionIncrements, final TokenStream in, final CharArraySet stopWords) {
        super(enablePositionIncrements, in);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.stopWords = stopWords;
    }
    
    @Override
    protected boolean accept() throws IOException {
        return !this.stopWords.contains(this.termAtt.buffer(), 0, this.termAtt.length());
    }
}
