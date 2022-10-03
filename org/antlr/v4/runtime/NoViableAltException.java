package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;

public class NoViableAltException extends RecognitionException
{
    private final ATNConfigSet deadEndConfigs;
    private final Token startToken;
    
    public NoViableAltException(final Parser recognizer) {
        this(recognizer, recognizer.getInputStream(), recognizer.getCurrentToken(), recognizer.getCurrentToken(), null, recognizer._ctx);
    }
    
    public NoViableAltException(final Parser recognizer, final TokenStream input, final Token startToken, final Token offendingToken, final ATNConfigSet deadEndConfigs, final ParserRuleContext ctx) {
        super(recognizer, input, ctx);
        this.deadEndConfigs = deadEndConfigs;
        this.startToken = startToken;
        this.setOffendingToken(offendingToken);
    }
    
    public Token getStartToken() {
        return this.startToken;
    }
    
    public ATNConfigSet getDeadEndConfigs() {
        return this.deadEndConfigs;
    }
}
