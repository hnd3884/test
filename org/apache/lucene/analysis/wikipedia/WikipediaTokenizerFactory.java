package org.apache.lucene.analysis.wikipedia;

import org.apache.lucene.analysis.Tokenizer;
import java.util.Collections;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class WikipediaTokenizerFactory extends TokenizerFactory
{
    public WikipediaTokenizerFactory(final Map<String, String> args) {
        super(args);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public WikipediaTokenizer create(final AttributeFactory factory) {
        return new WikipediaTokenizer(factory, 0, Collections.emptySet());
    }
}
