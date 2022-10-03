package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class FingerprintFilterFactory extends TokenFilterFactory
{
    public static final String MAX_OUTPUT_TOKEN_SIZE_KEY = "maxOutputTokenSize";
    public static final String SEPARATOR_KEY = "separator";
    final int maxOutputTokenSize;
    final char separator;
    
    public FingerprintFilterFactory(final Map<String, String> args) {
        super(args);
        this.maxOutputTokenSize = this.getInt(args, "maxOutputTokenSize", 1024);
        this.separator = this.getChar(args, "separator", ' ');
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new FingerprintFilter(input, this.maxOutputTokenSize, this.separator);
    }
}
