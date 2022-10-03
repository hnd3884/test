package org.apache.lucene.analysis.no;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NorwegianMinimalStemFilterFactory extends TokenFilterFactory
{
    private final int flags;
    
    public NorwegianMinimalStemFilterFactory(final Map<String, String> args) {
        super(args);
        final String variant = this.get(args, "variant");
        if (variant == null || "nb".equals(variant)) {
            this.flags = 1;
        }
        else if ("nn".equals(variant)) {
            this.flags = 2;
        }
        else {
            if (!"no".equals(variant)) {
                throw new IllegalArgumentException("invalid variant: " + variant);
            }
            this.flags = 3;
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new NorwegianMinimalStemFilter(input, this.flags);
    }
}
