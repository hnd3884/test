package org.apache.lucene.analysis.ar;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ArabicStemFilterFactory extends TokenFilterFactory
{
    public ArabicStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ArabicStemFilter create(final TokenStream input) {
        return new ArabicStemFilter(input);
    }
}
