package org.apache.lucene.analysis.gl;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class GalicianStemFilter extends TokenFilter
{
    private final GalicianStemmer stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public GalicianStemFilter(final TokenStream input) {
        super(input);
        this.stemmer = new GalicianStemmer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAttr.isKeyword()) {
                final int len = this.termAtt.length();
                final int newlen = this.stemmer.stem(this.termAtt.resizeBuffer(len + 1), len);
                this.termAtt.setLength(newlen);
            }
            return true;
        }
        return false;
    }
}
