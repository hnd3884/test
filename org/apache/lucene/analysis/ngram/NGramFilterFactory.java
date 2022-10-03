package org.apache.lucene.analysis.ngram;

import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NGramFilterFactory extends TokenFilterFactory
{
    private final int maxGramSize;
    private final int minGramSize;
    
    public NGramFilterFactory(final Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 2);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new NGramTokenFilter(input, this.minGramSize, this.maxGramSize);
        }
        return new Lucene43NGramTokenFilter(input, this.minGramSize, this.maxGramSize);
    }
}
