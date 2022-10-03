package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.LexerATNSimulator;
import org.antlr.v4.runtime.atn.ATNType;
import java.util.Collection;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.atn.ATN;

public class LexerInterpreter extends Lexer
{
    protected final String grammarFileName;
    protected final ATN atn;
    @Deprecated
    protected final String[] tokenNames;
    protected final String[] ruleNames;
    protected final String[] modeNames;
    private final Vocabulary vocabulary;
    protected final DFA[] _decisionToDFA;
    protected final PredictionContextCache _sharedContextCache;
    
    @Deprecated
    public LexerInterpreter(final String grammarFileName, final Collection<String> tokenNames, final Collection<String> ruleNames, final Collection<String> modeNames, final ATN atn, final CharStream input) {
        this(grammarFileName, VocabularyImpl.fromTokenNames(tokenNames.toArray(new String[tokenNames.size()])), ruleNames, modeNames, atn, input);
    }
    
    public LexerInterpreter(final String grammarFileName, final Vocabulary vocabulary, final Collection<String> ruleNames, final Collection<String> modeNames, final ATN atn, final CharStream input) {
        super(input);
        this._sharedContextCache = new PredictionContextCache();
        if (atn.grammarType != ATNType.LEXER) {
            throw new IllegalArgumentException("The ATN must be a lexer ATN.");
        }
        this.grammarFileName = grammarFileName;
        this.atn = atn;
        this.tokenNames = new String[atn.maxTokenType];
        for (int i = 0; i < this.tokenNames.length; ++i) {
            this.tokenNames[i] = vocabulary.getDisplayName(i);
        }
        this.ruleNames = ruleNames.toArray(new String[ruleNames.size()]);
        this.modeNames = modeNames.toArray(new String[modeNames.size()]);
        this.vocabulary = vocabulary;
        this._decisionToDFA = new DFA[atn.getNumberOfDecisions()];
        for (int i = 0; i < this._decisionToDFA.length; ++i) {
            this._decisionToDFA[i] = new DFA(atn.getDecisionState(i), i);
        }
        this._interp = (ATNInterpreter)new LexerATNSimulator(this, atn, this._decisionToDFA, this._sharedContextCache);
    }
    
    @Override
    public ATN getATN() {
        return this.atn;
    }
    
    @Override
    public String getGrammarFileName() {
        return this.grammarFileName;
    }
    
    @Deprecated
    @Override
    public String[] getTokenNames() {
        return this.tokenNames;
    }
    
    @Override
    public String[] getRuleNames() {
        return this.ruleNames;
    }
    
    @Override
    public String[] getModeNames() {
        return this.modeNames;
    }
    
    @Override
    public Vocabulary getVocabulary() {
        if (this.vocabulary != null) {
            return this.vocabulary;
        }
        return super.getVocabulary();
    }
}
