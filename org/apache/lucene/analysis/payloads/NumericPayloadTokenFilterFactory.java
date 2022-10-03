package org.apache.lucene.analysis.payloads;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class NumericPayloadTokenFilterFactory extends TokenFilterFactory
{
    private final float payload;
    private final String typeMatch;
    
    public NumericPayloadTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.payload = this.requireFloat(args, "payload");
        this.typeMatch = this.require(args, "typeMatch");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public NumericPayloadTokenFilter create(final TokenStream input) {
        return new NumericPayloadTokenFilter(input, this.payload, this.typeMatch);
    }
}
