package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class HyphenatedWordsFilterFactory extends TokenFilterFactory
{
    public HyphenatedWordsFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public HyphenatedWordsFilter create(final TokenStream input) {
        return new HyphenatedWordsFilter(input);
    }
}
