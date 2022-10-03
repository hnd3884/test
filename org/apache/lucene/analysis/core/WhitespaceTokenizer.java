package org.apache.lucene.analysis.core;

import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.util.CharTokenizer;

public final class WhitespaceTokenizer extends CharTokenizer
{
    public WhitespaceTokenizer() {
    }
    
    public WhitespaceTokenizer(final AttributeFactory factory) {
        super(factory);
    }
    
    @Override
    protected boolean isTokenChar(final int c) {
        return !Character.isWhitespace(c);
    }
}
