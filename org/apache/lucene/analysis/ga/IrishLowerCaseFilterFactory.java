package org.apache.lucene.analysis.ga;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class IrishLowerCaseFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    public IrishLowerCaseFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        return (TokenStream)new IrishLowerCaseFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
