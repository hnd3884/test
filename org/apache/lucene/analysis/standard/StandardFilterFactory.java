package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StandardFilterFactory extends TokenFilterFactory
{
    public StandardFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public StandardFilter create(final TokenStream input) {
        return new StandardFilter(input);
    }
}
