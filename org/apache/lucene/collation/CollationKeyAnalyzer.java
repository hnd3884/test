package org.apache.lucene.collation;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import java.text.Collator;
import org.apache.lucene.analysis.Analyzer;

public final class CollationKeyAnalyzer extends Analyzer
{
    private final CollationAttributeFactory factory;
    
    public CollationKeyAnalyzer(final Collator collator) {
        this.factory = new CollationAttributeFactory(collator);
    }
    
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        final KeywordTokenizer tokenizer = new KeywordTokenizer((AttributeFactory)this.factory, 256);
        return new Analyzer.TokenStreamComponents((Tokenizer)tokenizer, (TokenStream)tokenizer);
    }
}
