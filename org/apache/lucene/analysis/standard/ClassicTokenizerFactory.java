package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class ClassicTokenizerFactory extends TokenizerFactory
{
    private final int maxTokenLength;
    
    public ClassicTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.maxTokenLength = this.getInt(args, "maxTokenLength", 255);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public ClassicTokenizer create(final AttributeFactory factory) {
        final ClassicTokenizer tokenizer = new ClassicTokenizer(factory);
        tokenizer.setMaxTokenLength(this.maxTokenLength);
        return tokenizer;
    }
}
