package org.apache.lucene.analysis.compound;

import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.TokenStream;

public class DictionaryCompoundWordTokenFilter extends CompoundWordTokenFilterBase
{
    public DictionaryCompoundWordTokenFilter(final TokenStream input, final CharArraySet dictionary) {
        super(input, dictionary);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary cannot be null");
        }
    }
    
    public DictionaryCompoundWordTokenFilter(final TokenStream input, final CharArraySet dictionary, final int minWordSize, final int minSubwordSize, final int maxSubwordSize, final boolean onlyLongestMatch) {
        super(input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary cannot be null");
        }
    }
    
    @Override
    protected void decompose() {
        for (int len = this.termAtt.length(), i = 0; i <= len - this.minSubwordSize; ++i) {
            CompoundToken longestMatchToken = null;
            for (int j = this.minSubwordSize; j <= this.maxSubwordSize && i + j <= len; ++j) {
                if (this.dictionary.contains(this.termAtt.buffer(), i, j)) {
                    if (this.onlyLongestMatch) {
                        if (longestMatchToken != null) {
                            if (longestMatchToken.txt.length() < j) {
                                longestMatchToken = new CompoundToken(i, j);
                            }
                        }
                        else {
                            longestMatchToken = new CompoundToken(i, j);
                        }
                    }
                    else {
                        this.tokens.add(new CompoundToken(i, j));
                    }
                }
            }
            if (this.onlyLongestMatch && longestMatchToken != null) {
                this.tokens.add(longestMatchToken);
            }
        }
    }
}
