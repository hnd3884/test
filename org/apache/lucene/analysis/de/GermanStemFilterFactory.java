package org.apache.lucene.analysis.de;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GermanStemFilterFactory extends TokenFilterFactory
{
    public GermanStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public GermanStemFilter create(final TokenStream in) {
        return new GermanStemFilter(in);
    }
}
