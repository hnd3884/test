package org.apache.lucene.analysis.tr;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class TurkishLowerCaseFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    public TurkishLowerCaseFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new TurkishLowerCaseFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
