package org.apache.lucene.analysis.th;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

@Deprecated
public class ThaiWordFilterFactory extends TokenFilterFactory
{
    public ThaiWordFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ThaiWordFilter create(final TokenStream input) {
        return new ThaiWordFilter(input);
    }
}
