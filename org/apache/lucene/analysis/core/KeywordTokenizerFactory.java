package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class KeywordTokenizerFactory extends TokenizerFactory
{
    public KeywordTokenizerFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public KeywordTokenizer create(final AttributeFactory factory) {
        return new KeywordTokenizer(factory, 256);
    }
}
