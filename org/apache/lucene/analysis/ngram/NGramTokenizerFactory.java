package org.apache.lucene.analysis.ngram;

import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class NGramTokenizerFactory extends TokenizerFactory
{
    private final int maxGramSize;
    private final int minGramSize;
    
    public NGramTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 2);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public Tokenizer create(final AttributeFactory factory) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new NGramTokenizer(factory, this.minGramSize, this.maxGramSize);
        }
        return new Lucene43NGramTokenizer(factory, this.minGramSize, this.maxGramSize);
    }
}
