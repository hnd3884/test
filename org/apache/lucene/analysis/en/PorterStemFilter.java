package org.apache.lucene.analysis.en;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class PorterStemFilter extends TokenFilter
{
    private final PorterStemmer stemmer;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public PorterStemFilter(final TokenStream in) {
        super(in);
        this.stemmer = new PorterStemmer();
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public final boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        if (!this.keywordAttr.isKeyword() && this.stemmer.stem(this.termAtt.buffer(), 0, this.termAtt.length())) {
            this.termAtt.copyBuffer(this.stemmer.getResultBuffer(), 0, this.stemmer.getResultLength());
        }
        return true;
    }
}
