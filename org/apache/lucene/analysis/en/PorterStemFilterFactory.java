package org.apache.lucene.analysis.en;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PorterStemFilterFactory extends TokenFilterFactory
{
    public PorterStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public PorterStemFilter create(final TokenStream input) {
        return new PorterStemFilter(input);
    }
}
