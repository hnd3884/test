package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class LetterTokenizerFactory extends TokenizerFactory
{
    public LetterTokenizerFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public LetterTokenizer create(final AttributeFactory factory) {
        return new LetterTokenizer(factory);
    }
}
