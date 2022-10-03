package org.apache.lucene.analysis.de;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class GermanStemFilter extends TokenFilter
{
    private GermanStemmer stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public GermanStemFilter(final TokenStream in) {
        super(in);
        this.stemmer = new GermanStemmer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final String term = this.termAtt.toString();
            if (!this.keywordAttr.isKeyword()) {
                final String s = this.stemmer.stem(term);
                if (s != null && !s.equals(term)) {
                    this.termAtt.setEmpty().append(s);
                }
            }
            return true;
        }
        return false;
    }
    
    public void setStemmer(final GermanStemmer stemmer) {
        if (stemmer != null) {
            this.stemmer = stemmer;
        }
    }
}
