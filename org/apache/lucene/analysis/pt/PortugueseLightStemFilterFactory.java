package org.apache.lucene.analysis.pt;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PortugueseLightStemFilterFactory extends TokenFilterFactory
{
    public PortugueseLightStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new PortugueseLightStemFilter(input);
    }
}
