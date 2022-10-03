package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ScandinavianFoldingFilterFactory extends TokenFilterFactory
{
    public ScandinavianFoldingFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ScandinavianFoldingFilter create(final TokenStream input) {
        return new ScandinavianFoldingFilter(input);
    }
}
