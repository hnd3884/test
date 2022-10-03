package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class RemoveDuplicatesTokenFilterFactory extends TokenFilterFactory
{
    public RemoveDuplicatesTokenFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public RemoveDuplicatesTokenFilter create(final TokenStream input) {
        return new RemoveDuplicatesTokenFilter(input);
    }
}
