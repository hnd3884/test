package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LimitTokenPositionFilterFactory extends TokenFilterFactory
{
    public static final String MAX_TOKEN_POSITION_KEY = "maxTokenPosition";
    public static final String CONSUME_ALL_TOKENS_KEY = "consumeAllTokens";
    final int maxTokenPosition;
    final boolean consumeAllTokens;
    
    public LimitTokenPositionFilterFactory(final Map<String, String> args) {
        super(args);
        this.maxTokenPosition = this.requireInt(args, "maxTokenPosition");
        this.consumeAllTokens = this.getBoolean(args, "consumeAllTokens", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new LimitTokenPositionFilter(input, this.maxTokenPosition, this.consumeAllTokens);
    }
}
