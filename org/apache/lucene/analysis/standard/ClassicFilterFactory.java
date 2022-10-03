package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ClassicFilterFactory extends TokenFilterFactory
{
    public ClassicFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenFilter create(final TokenStream input) {
        return new ClassicFilter(input);
    }
}
