package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TokenOffsetPayloadTokenFilterFactory extends TokenFilterFactory
{
    public TokenOffsetPayloadTokenFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public TokenOffsetPayloadTokenFilter create(final TokenStream input) {
        return new TokenOffsetPayloadTokenFilter(input);
    }
}
