package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ScandinavianNormalizationFilterFactory extends TokenFilterFactory
{
    public ScandinavianNormalizationFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ScandinavianNormalizationFilter create(final TokenStream input) {
        return new ScandinavianNormalizationFilter(input);
    }
}
