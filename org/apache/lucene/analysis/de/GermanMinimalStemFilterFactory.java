package org.apache.lucene.analysis.de;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GermanMinimalStemFilterFactory extends TokenFilterFactory
{
    public GermanMinimalStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new GermanMinimalStemFilter(input);
    }
}
