package org.apache.lucene.analysis.el;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class GreekLowerCaseFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    public GreekLowerCaseFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public GreekLowerCaseFilter create(final TokenStream in) {
        return new GreekLowerCaseFilter(in);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
