package org.apache.lucene.analysis.no;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class NorwegianMinimalStemFilter extends TokenFilter
{
    private final NorwegianMinimalStemmer stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public NorwegianMinimalStemFilter(final TokenStream input) {
        this(input, 1);
    }
    
    public NorwegianMinimalStemFilter(final TokenStream input, final int flags) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.stemmer = new NorwegianMinimalStemmer(flags);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword()) {
                final int newlen = this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length());
                this.termAtt.setLength(newlen);
            }
            return true;
        }
        return false;
    }
}
