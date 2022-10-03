package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TypeAsPayloadTokenFilterFactory extends TokenFilterFactory
{
    public TypeAsPayloadTokenFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TypeAsPayloadTokenFilter create(final TokenStream input) {
        return new TypeAsPayloadTokenFilter(input);
    }
}
