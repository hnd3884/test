package org.apache.lucene.analysis.gl;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GalicianMinimalStemFilterFactory extends TokenFilterFactory
{
    public GalicianMinimalStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new GalicianMinimalStemFilter(input);
    }
}
