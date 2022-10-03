package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

public class LexerDFASerializer extends DFASerializer
{
    public LexerDFASerializer(final DFA dfa) {
        super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
    }
    
    @Override
    protected String getEdgeLabel(final int i) {
        return "'" + (char)i + "'";
    }
}
