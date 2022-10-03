package org.apache.lucene.analysis.th;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class ThaiTokenizerFactory extends TokenizerFactory
{
    public ThaiTokenizerFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public Tokenizer create(final AttributeFactory factory) {
        return new ThaiTokenizer(factory);
    }
}
