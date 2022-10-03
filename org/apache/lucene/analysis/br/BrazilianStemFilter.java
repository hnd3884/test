package org.apache.lucene.analysis.br;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import java.util.Set;
import org.apache.lucene.analysis.TokenFilter;

public final class BrazilianStemFilter extends TokenFilter
{
    private BrazilianStemmer stemmer;
    private Set<?> exclusions;
    private final CharTermAttribute termAtt;
    private final KeywordAttribute keywordAttr;
    
    public BrazilianStemFilter(final TokenStream in) {
        super(in);
        this.stemmer = new BrazilianStemmer();
        this.exclusions = null;
        this.termAtt = (CharTermAttribute)this.addAttribute((Class)CharTermAttribute.class);
        this.keywordAttr = (KeywordAttribute)this.addAttribute((Class)KeywordAttribute.class);
    }
    
    public boolean incrementToken() throws IOException {
        if (this.input.incrementToken()) {
            final String term = this.termAtt.toString();
            if (!this.keywordAttr.isKeyword() && (this.exclusions == null || !this.exclusions.contains(term))) {
                final String s = this.stemmer.stem(term);
                if (s != null && !s.equals(term)) {
                    this.termAtt.setEmpty().append(s);
                }
            }
            return true;
        }
        return false;
    }
}
