package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class CodepointCountFilterFactory extends TokenFilterFactory
{
    final int min;
    final int max;
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    
    public CodepointCountFilterFactory(final Map<String, String> args) {
        super(args);
        this.min = this.requireInt(args, "min");
        this.max = this.requireInt(args, "max");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public CodepointCountFilter create(final TokenStream input) {
        return new CodepointCountFilter(input, this.min, this.max);
    }
}
