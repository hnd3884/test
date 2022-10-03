package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LimitTokenOffsetFilterFactory extends TokenFilterFactory
{
    public static final String MAX_START_OFFSET = "maxStartOffset";
    public static final String CONSUME_ALL_TOKENS_KEY = "consumeAllTokens";
    private int maxStartOffset;
    private boolean consumeAllTokens;
    
    public LimitTokenOffsetFilterFactory(final Map<String, String> args) {
        super(args);
        this.maxStartOffset = this.requireInt(args, "maxStartOffset");
        this.consumeAllTokens = this.getBoolean(args, "consumeAllTokens", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new LimitTokenOffsetFilter(input, this.maxStartOffset, this.consumeAllTokens);
    }
}
