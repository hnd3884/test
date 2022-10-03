package org.apache.lucene.analysis.reverse;

import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ReverseStringFilterFactory extends TokenFilterFactory
{
    public ReverseStringFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ReverseStringFilter create(final TokenStream in) {
        return new ReverseStringFilter(in);
    }
}
