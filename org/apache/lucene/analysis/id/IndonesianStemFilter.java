package org.apache.lucene.analysis.id;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class IndonesianStemFilter extends TokenFilter
{
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAtt;
    private final IndonesianStemmer stemmer;
    private final boolean stemDerivational;
    
    public IndonesianStemFilter(final TokenStream input) {
        this(input, true);
    }
    
    public IndonesianStemFilter(final TokenStream input, final boolean stemDerivational) {
        super(input);
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
        this.stemmer = new IndonesianStemmer();
        this.stemDerivational = stemDerivational;
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            if (!this.keywordAtt.isKeyword()) {
                final int newlen = this.stemmer.stem(this.termAtt.buffer(), this.termAtt.length(), this.stemDerivational);
                this.termAtt.setLength(newlen);
            }
            return true;
        }
        return false;
    }
}
