package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.Tokenizer;
import java.util.HashMap;
import org.apache.lucene.analysis.util.AbstractAnalysisFactory;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.MultiTermAwareComponent;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class LowerCaseTokenizerFactory extends TokenizerFactory implements MultiTermAwareComponent
{
    public LowerCaseTokenizerFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public LowerCaseTokenizer create(final AttributeFactory factory) {
        return new LowerCaseTokenizer(factory);
    }
    
    @Override
    public AbstractAnalysisFactory getMultiTermComponent() {
        return new LowerCaseFilterFactory(new HashMap<String, String>(this.getOriginalArgs()));
    }
}
