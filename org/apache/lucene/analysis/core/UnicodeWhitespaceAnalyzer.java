package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer;

public final class UnicodeWhitespaceAnalyzer extends Analyzer
{
    protected Analyzer.TokenStreamComponents createComponents(final String fieldName) {
        return new Analyzer.TokenStreamComponents((Tokenizer)new UnicodeWhitespaceTokenizer());
    }
}
