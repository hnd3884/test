package org.apache.lucene.analysis.miscellaneous;

import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.analysis.TokenStream;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class ASCIIFoldingFilterFactory extends TokenFilterFactory implements MultiTermAwareComponent
{
    private final boolean preserveOriginal;
    
    public ASCIIFoldingFilterFactory(final Map<String, String> args) {
        super(args);
        this.preserveOriginal = this.getBoolean(args, "preserveOriginal", false);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    public ASCIIFoldingFilter create(final TokenStream input) {
        return new ASCIIFoldingFilter(input, this.preserveOriginal);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return this;
    }
}
