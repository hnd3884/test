package org.apache.lucene.analysis.ngram;

import org.apache.lucene.util.AttributeFactory;

public class EdgeNGramTokenizer extends NGramTokenizer
{
    public static final int DEFAULT_MAX_GRAM_SIZE = 1;
    public static final int DEFAULT_MIN_GRAM_SIZE = 1;
    
    public EdgeNGramTokenizer(final int minGram, final int maxGram) {
        super(minGram, maxGram, true);
    }
    
    public EdgeNGramTokenizer(final AttributeFactory factory, final int minGram, final int maxGram) {
        super(factory, minGram, maxGram, true);
    }
}
