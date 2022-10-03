package org.apache.lucene.analysis.ckb;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SoraniStemFilterFactory extends TokenFilterFactory
{
    public SoraniStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public SoraniStemFilter create(final TokenStream input) {
        return new SoraniStemFilter(input);
    }
}
