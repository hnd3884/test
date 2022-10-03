package org.apache.lucene.analysis.core;

import org.apache.lucene.util.AttributeFactory;

public final class LowerCaseTokenizer extends LetterTokenizer
{
    public LowerCaseTokenizer() {
    }
    
    public LowerCaseTokenizer(final AttributeFactory factory) {
        super(factory);
    }
    
    @Override
    protected int normalize(final int c) {
        return Character.toLowerCase(c);
    }
}
