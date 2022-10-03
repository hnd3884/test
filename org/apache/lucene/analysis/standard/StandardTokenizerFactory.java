package org.apache.lucene.analysis.standard;

import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import java.util.Map;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class StandardTokenizerFactory extends TokenizerFactory
{
    private final int maxTokenLength;
    
    public StandardTokenizerFactory(final Map<String, String> args) {
        super(args);
        this.maxTokenLength = this.getInt(args, "maxTokenLength", 255);
        if (!args.isEmpty()) {
            throw new IllegalArgumentException("Unknown parameters: " + args);
        }
    }
    
    @Override
    public Tokenizer create(final AttributeFactory factory) {
        if (this.luceneMatchVersion.onOrAfter(Version.LUCENE_4_7_0)) {
            final StandardTokenizer tokenizer = new StandardTokenizer(factory);
            tokenizer.setMaxTokenLength(this.maxTokenLength);
            return tokenizer;
        }
        final StandardTokenizer40 tokenizer2 = new StandardTokenizer40(factory);
        tokenizer2.setMaxTokenLength(this.maxTokenLength);
        return tokenizer2;
    }
}
