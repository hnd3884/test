package org.apache.lucene.analysis.hi;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class HindiStemFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAtt;
    private final HindiStemmer stemmer;
    
    public HindiStemFilter(final TokenStream input) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.stemmer = new HindiStemmer();
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAtt.isKeyword()) {
                this.termAtt.setLength(this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length()));
            }
            return true;
        }
        return false;
    }
}
