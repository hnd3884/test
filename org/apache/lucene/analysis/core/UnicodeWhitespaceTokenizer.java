package org.apache.lucene.analysis.core;

import org.apache.lucene.analysis.util.UnicodeProps;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.util.CharTokenizer;

public final class UnicodeWhitespaceTokenizer extends CharTokenizer
{
    public UnicodeWhitespaceTokenizer() {
    }
    
    public UnicodeWhitespaceTokenizer(final AttributeFactory factory) {
        super(factory);
    }
    
    @Override
    protected boolean isTokenChar(final int c) {
        return !UnicodeProps.WHITESPACE.get(c);
    }
}
