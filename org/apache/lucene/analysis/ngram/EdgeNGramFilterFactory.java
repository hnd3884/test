package org.apache.lucene.analysis.ngram;

import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class EdgeNGramFilterFactory extends TokenFilterFactory
{
    private final int maxGramSize;
    private final int minGramSize;
    
    public EdgeNGramFilterFactory(final Map<String, String> args) {
        super(args);
        this.minGramSize = this.getInt(args, "minGramSize", 1);
        this.maxGramSize = this.getInt(args, "maxGramSize", 1);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_4_0)) {
            return new EdgeNGramTokenFilter(input, this.minGramSize, this.maxGramSize);
        }
        return new Lucene43EdgeNGramTokenFilter(input, this.minGramSize, this.maxGramSize);
    }
}
