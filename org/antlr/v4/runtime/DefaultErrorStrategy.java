package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.IntSet;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;

public class DefaultErrorStrategy implements ANTLRErrorStrategy
{
    protected boolean errorRecoveryMode;
    protected int lastErrorIndex;
    protected IntervalSet lastErrorStates;
    
    public DefaultErrorStrategy() {
        this.errorRecoveryMode = false;
        this.lastErrorIndex = -1;
    }
    
    @Override
    public void reset(final Parser recognizer) {
        this.endErrorCondition(recognizer);
    }
    
    protected void beginErrorCondition(final Parser recognizer) {
        this.errorRecoveryMode = true;
    }
    
    @Override
    public boolean inErrorRecoveryMode(final Parser recognizer) {
        return this.errorRecoveryMode;
    }
    
    protected void endErrorCondition(final Parser recognizer) {
        this.errorRecoveryMode = false;
        this.lastErrorStates = null;
        this.lastErrorIndex = -1;
    }
    
    @Override
    public void reportMatch(final Parser recognizer) {
        this.endErrorCondition(recognizer);
    }
    
    @Override
    public void reportError(final Parser recognizer, final RecognitionException e) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        if (e instanceof NoViableAltException) {
            this.reportNoViableAlternative(recognizer, (NoViableAltException)e);
        }
        else if (e instanceof InputMismatchException) {
            this.reportInputMismatch(recognizer, (InputMismatchException)e);
        }
        else if (e instanceof FailedPredicateException) {
            this.reportFailedPredicate(recognizer, (FailedPredicateException)e);
        }
        else {
            System.err.println("unknown recognition error type: " + e.getClass().getName());
            recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
        }
    }
    
    @Override
    public void recover(final Parser recognizer, final RecognitionException e) {
        if (this.lastErrorIndex == recognizer.getInputStream().index() && this.lastErrorStates != null && this.lastErrorStates.contains(recognizer.getState())) {
            recognizer.consume();
        }
        this.lastErrorIndex = recognizer.getInputStream().index();
        if (this.lastErrorStates == null) {
            this.lastErrorStates = new IntervalSet(new int[0]);
        }
        this.lastErrorStates.add(recognizer.getState());
        final IntervalSet followSet = this.getErrorRecoverySet(recognizer);
        this.consumeUntil(recognizer, followSet);
    }
    
    @Override
    public void sync(final Parser recognizer) throws RecognitionException {
        final ATNState s = ((Recognizer<Symbol, ParserATNSimulator>)recognizer).getInterpreter().atn.states.get(recognizer.getState());
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        final TokenStream tokens = recognizer.getInputStream();
        final int la = tokens.LA(1);
        if (recognizer.getATN().nextTokens(s).contains(la) || la == -1) {
            return;
        }
        if (recognizer.isExpectedToken(la)) {
            return;
        }
        switch (s.getStateType()) {
            case 3:
            case 4:
            case 5:
            case 10: {
                if (this.singleTokenDeletion(recognizer) != null) {
                    return;
                }
                throw new InputMismatchException(recognizer);
            }
            case 9:
            case 11: {
                this.reportUnwantedToken(recognizer);
                final IntervalSet expecting = recognizer.getExpectedTokens();
                final IntervalSet whatFollowsLoopIterationOrRule = expecting.or((IntSet)this.getErrorRecoverySet(recognizer));
                this.consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
                break;
            }
        }
    }
    
    protected void reportNoViableAlternative(final Parser recognizer, final NoViableAltException e) {
        final TokenStream tokens = recognizer.getInputStream();
        String input;
        if (tokens != null) {
            if (e.getStartToken().getType() == -1) {
                input = "<EOF>";
            }
            else {
                input = tokens.getText(e.getStartToken(), e.getOffendingToken());
            }
        }
        else {
            input = "<unknown input>";
        }
        final String msg = "no viable alternative at input " + this.escapeWSAndQuote(input);
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
    
    protected void reportInputMismatch(final Parser recognizer, final InputMismatchException e) {
        final String msg = "mismatched input " + this.getTokenErrorDisplay(e.getOffendingToken()) + " expecting " + e.getExpectedTokens().toString(recognizer.getVocabulary());
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
    
    protected void reportFailedPredicate(final Parser recognizer, final FailedPredicateException e) {
        final String ruleName = recognizer.getRuleNames()[recognizer._ctx.getRuleIndex()];
        final String msg = "rule " + ruleName + " " + e.getMessage();
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
    }
    
    protected void reportUnwantedToken(final Parser recognizer) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        final Token t = recognizer.getCurrentToken();
        final String tokenName = this.getTokenErrorDisplay(t);
        final IntervalSet expecting = this.getExpectedTokens(recognizer);
        final String msg = "extraneous input " + tokenName + " expecting " + expecting.toString(recognizer.getVocabulary());
        recognizer.notifyErrorListeners(t, msg, null);
    }
    
    protected void reportMissingToken(final Parser recognizer) {
        if (this.inErrorRecoveryMode(recognizer)) {
            return;
        }
        this.beginErrorCondition(recognizer);
        final Token t = recognizer.getCurrentToken();
        final IntervalSet expecting = this.getExpectedTokens(recognizer);
        final String msg = "missing " + expecting.toString(recognizer.getVocabulary()) + " at " + this.getTokenErrorDisplay(t);
        recognizer.notifyErrorListeners(t, msg, null);
    }
    
    @Override
    public Token recoverInline(final Parser recognizer) throws RecognitionException {
        final Token matchedSymbol = this.singleTokenDeletion(recognizer);
        if (matchedSymbol != null) {
            recognizer.consume();
            return matchedSymbol;
        }
        if (this.singleTokenInsertion(recognizer)) {
            return this.getMissingSymbol(recognizer);
        }
        throw new InputMismatchException(recognizer);
    }
    
    protected boolean singleTokenInsertion(final Parser recognizer) {
        final int currentSymbolType = recognizer.getInputStream().LA(1);
        final ATNState currentState = ((Recognizer<Symbol, ParserATNSimulator>)recognizer).getInterpreter().atn.states.get(recognizer.getState());
        final ATNState next = currentState.transition(0).target;
        final ATN atn = ((Recognizer<Symbol, ParserATNSimulator>)recognizer).getInterpreter().atn;
        final IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer._ctx);
        if (expectingAtLL2.contains(currentSymbolType)) {
            this.reportMissingToken(recognizer);
            return true;
        }
        return false;
    }
    
    protected Token singleTokenDeletion(final Parser recognizer) {
        final int nextTokenType = recognizer.getInputStream().LA(2);
        final IntervalSet expecting = this.getExpectedTokens(recognizer);
        if (expecting.contains(nextTokenType)) {
            this.reportUnwantedToken(recognizer);
            recognizer.consume();
            final Token matchedSymbol = recognizer.getCurrentToken();
            this.reportMatch(recognizer);
            return matchedSymbol;
        }
        return null;
    }
    
    protected Token getMissingSymbol(final Parser recognizer) {
        final Token currentSymbol = recognizer.getCurrentToken();
        final IntervalSet expecting = this.getExpectedTokens(recognizer);
        final int expectedTokenType = expecting.getMinElement();
        String tokenText;
        if (expectedTokenType == -1) {
            tokenText = "<missing EOF>";
        }
        else {
            tokenText = "<missing " + recognizer.getVocabulary().getDisplayName(expectedTokenType) + ">";
        }
        Token current = currentSymbol;
        final Token lookback = recognizer.getInputStream().LT(-1);
        if (current.getType() == -1 && lookback != null) {
            current = lookback;
        }
        return (Token)recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType, tokenText, 0, -1, -1, current.getLine(), current.getCharPositionInLine());
    }
    
    protected IntervalSet getExpectedTokens(final Parser recognizer) {
        return recognizer.getExpectedTokens();
    }
    
    protected String getTokenErrorDisplay(final Token t) {
        if (t == null) {
            return "<no token>";
        }
        String s = this.getSymbolText(t);
        if (s == null) {
            if (this.getSymbolType(t) == -1) {
                s = "<EOF>";
            }
            else {
                s = "<" + this.getSymbolType(t) + ">";
            }
        }
        return this.escapeWSAndQuote(s);
    }
    
    protected String getSymbolText(final Token symbol) {
        return symbol.getText();
    }
    
    protected int getSymbolType(final Token symbol) {
        return symbol.getType();
    }
    
    protected String escapeWSAndQuote(String s) {
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return "'" + s + "'";
    }
    
    protected IntervalSet getErrorRecoverySet(final Parser recognizer) {
        final ATN atn = ((Recognizer<Symbol, ParserATNSimulator>)recognizer).getInterpreter().atn;
        RuleContext ctx = recognizer._ctx;
        final IntervalSet recoverSet = new IntervalSet(new int[0]);
        while (ctx != null && ctx.invokingState >= 0) {
            final ATNState invokingState = atn.states.get(ctx.invokingState);
            final RuleTransition rt = (RuleTransition)invokingState.transition(0);
            final IntervalSet follow = atn.nextTokens(rt.followState);
            recoverSet.addAll((IntSet)follow);
            ctx = ctx.parent;
        }
        recoverSet.remove(-2);
        return recoverSet;
    }
    
    protected void consumeUntil(final Parser recognizer, final IntervalSet set) {
        for (int ttype = recognizer.getInputStream().LA(1); ttype != -1 && !set.contains(ttype); ttype = recognizer.getInputStream().LA(1)) {
            recognizer.consume();
        }
    }
}
