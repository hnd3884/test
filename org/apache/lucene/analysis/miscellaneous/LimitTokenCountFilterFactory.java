package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LimitTokenCountFilterFactory extends TokenFilterFactory
{
    public static final String MAX_TOKEN_COUNT_KEY = "maxTokenCount";
    public static final String CONSUME_ALL_TOKENS_KEY = "consumeAllTokens";
    final int maxTokenCount;
    final boolean consumeAllTokens;
    
    public LimitTokenCountFilterFactory(final Map<String, String> args) {
        super(args);
        this.maxTokenCount = this.requireInt(args, "maxTokenCount");
        this.consumeAllTokens = this.getBoolean(args, "consumeAllTokens", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new LimitTokenCountFilter(input, this.maxTokenCount, this.consumeAllTokens);
    }
}
