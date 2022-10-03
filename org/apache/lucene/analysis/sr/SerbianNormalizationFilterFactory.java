package org.apache.lucene.analysis.sr;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class SerbianNormalizationFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    final String haircut;
    
    public SerbianNormalizationFilterFactory(final Map<String, String> args) {
        super(args);
        this.haircut = this.get(args, "haircut", Arrays.asList("bald", "regular"), "bald");
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public TokenStream create(final TokenStream input) {
        if (this.haircut.equals("regular")) {
            return (TokenStream)new SerbianNormalizationRegularFilter(input);
        }
        return (TokenStream)new SerbianNormalizationFilter(input);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
