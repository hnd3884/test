package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class LowerCaseFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    public LowerCaseFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public LowerCaseFilter create(final TokenStream input) {
        return new LowerCaseFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
