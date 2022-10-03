package org.apache.lucene.analysis.es;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class SpanishLightStemFilter extends TokenFilter
{
    private final SpanishLightStemmer stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public SpanishLightStemFilter(final TokenStream input) {
        super(input);
        this.stemmer = new SpanishLightStemmer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
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
