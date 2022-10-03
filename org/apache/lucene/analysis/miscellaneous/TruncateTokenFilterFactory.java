package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TruncateTokenFilterFactory extends TokenFilterFactory
{
    public static final String PREFIX_LENGTH_KEY = "prefixLength";
    private final byte prefixLength;
    
    public TruncateTokenFilterFactory(final Map<String, String> args) {
        super(args);
        this.prefixLength = Byte.parseByte(this.get(args, "prefixLength", "5"));
        if (this.prefixLength < 1) {
            throw new IllegalArgumentException("prefixLength parameter must be a positive number: " + this.prefixLength);
        }
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameter(s): " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new TruncateTokenFilter(input, this.prefixLength);
    }
}
