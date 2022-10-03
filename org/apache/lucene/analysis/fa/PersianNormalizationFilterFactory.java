package org.apache.lucene.analysis.fa;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class PersianNormalizationFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    public PersianNormalizationFilterFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public PersianNormalizationFilter create(final TokenStream input) {
        return new PersianNormalizationFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
