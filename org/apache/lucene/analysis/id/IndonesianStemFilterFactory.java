package org.apache.lucene.analysis.id;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class IndonesianStemFilterFactory extends TokenFilterFactory
{
    private final boolean stemDerivational;
    
    public IndonesianStemFilterFactory(final Map<String, String> args) {
        super(args);
        this.stemDerivational = this.getBoolean(args, "stemDerivational", true);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new IndonesianStemFilter(input, this.stemDerivational);
    }
}
