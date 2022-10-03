package org.apache.lucene.analysis.core;

import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.analysis.util.CharTokenizer;

public class LetterTokenizer extends CharTokenizer
{
    public LetterTokenizer() {
    }
    
    public LetterTokenizer(final AttributeFactory factory) {
        super(factory);
    }
    
    @Override
    protected boolean isTokenChar(final int c) {
        return Character.isLetter(c);
    }
}
