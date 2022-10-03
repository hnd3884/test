package org.apache.lucene.analysis.en;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.TokenFilter;

public final class KStemFilter extends TokenFilter
{
    private final KStemmer stemmer;
    private final CharTermAttribute termAttribute;
    private final KeywordAttribute keywordAtt;
    
    public KStemFilter(final TokenStream in) {
        super(in);
        this.stemmer = new KStemmer();
        this.termAttribute = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAtt = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (!this.input.incrementToken()) {
            return false;
        }
        final char[] term = this.termAttribute.buffer();
        final int len = this.termAttribute.length();
        if (!this.keywordAtt.isKeyword() && this.stemmer.stem(term, len)) {
            this.termAttribute.setEmpty().append(this.stemmer.asCharSequence());
        }
        return true;
    }
}
