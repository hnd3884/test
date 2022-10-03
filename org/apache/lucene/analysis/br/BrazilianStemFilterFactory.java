package org.apache.lucene.analysis.br;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class BrazilianStemFilterFactory extends TokenFilterFactory
{
    public BrazilianStemFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public BrazilianStemFilter create(final TokenStream in) {
        return new BrazilianStemFilter(in);
    }
}
