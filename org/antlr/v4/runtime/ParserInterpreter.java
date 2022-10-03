package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.atn.PrecedencePredicateTransition;
import org.antlr.v4.runtime.atn.ActionTransition;
import org.antlr.v4.runtime.atn.PredicateTransition;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.LoopEndState;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.RuleStartState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import java.util.ArrayDeque;
import java.util.Collection;
import org.antlr.v4.runtime.misc.Pair;
import java.util.Deque;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.atn.ATN;

public class ParserInterpreter extends Parser
{
    protected final String grammarFileName;
    protected final ATN atn;
    protected final DFA[] decisionToDFA;
    protected final PredictionContextCache sharedContextCache;
    @Deprecated
    protected final String[] tokenNames;
    protected final String[] ruleNames;
    private final Vocabulary vocabulary;
    protected final Deque<Pair<ParserRuleContext, Integer>> _parentContextStack;
    protected int overrideDecision;
    protected int overrideDecisionInputIndex;
    protected int overrideDecisionAlt;
    protected boolean overrideDecisionReached;
    protected InterpreterRuleContext overrideDecisionRoot;
    protected InterpreterRuleContext rootContext;
    
    @Deprecated
    public ParserInterpreter(final String grammarFileName, final Collection<String> tokenNames, final Collection<String> ruleNames, final ATN atn, final TokenStream input) {
        this(grammarFileName, VocabularyImpl.fromTokenNames(tokenNames.toArray(new String[tokenNames.size()])), ruleNames, atn, input);
    }
    
    public ParserInterpreter(final String grammarFileName, final Vocabulary vocabulary, final Collection<String> ruleNames, final ATN atn, final TokenStream input) {
        super(input);
        this.sharedContextCache = new PredictionContextCache();
        this._parentContextStack = new ArrayDeque<Pair<ParserRuleContext, Integer>>();
        this.overrideDecision = -1;
        this.overrideDecisionInputIndex = -1;
        this.overrideDecisionAlt = -1;
        this.overrideDecisionReached = false;
        this.overrideDecisionRoot = null;
        this.grammarFileName = grammarFileName;
        this.atn = atn;
        this.tokenNames = new String[atn.maxTokenType];
        for (int i = 0; i < this.tokenNames.length; ++i) {
            this.tokenNames[i] = vocabulary.getDisplayName(i);
        }
        this.ruleNames = ruleNames.toArray(new String[ruleNames.size()]);
        this.vocabulary = vocabulary;
        final int numberOfDecisions = atn.getNumberOfDecisions();
        this.decisionToDFA = new DFA[numberOfDecisions];
        for (int j = 0; j < numberOfDecisions; ++j) {
            final DecisionState decisionState = atn.getDecisionState(j);
            this.decisionToDFA[j] = new DFA(decisionState, j);
        }
        ((Recognizer<Symbol, ParserATNSimulator>)this).setInterpreter(new ParserATNSimulator(this, atn, this.decisionToDFA, this.sharedContextCache));
    }
    
    @Override
    public void reset() {
        super.reset();
        this.overrideDecisionReached = false;
        this.overrideDecisionRoot = null;
    }
    
    @Override
    public ATN getATN() {
        return this.atn;
    }
    
    @Deprecated
    @Override
    public String[] getTokenNames() {
        return this.tokenNames;
    }
    
    @Override
    public Vocabulary getVocabulary() {
        return this.vocabulary;
    }
    
    @Override
    public String[] getRuleNames() {
        return this.ruleNames;
    }
    
    @Override
    public String getGrammarFileName() {
        return this.grammarFileName;
    }
    
    public ParserRuleContext parse(final int startRuleIndex) {
        final RuleStartState startRuleStartState = this.atn.ruleToStartState[startRuleIndex];
        this.rootContext = this.createInterpreterRuleContext(null, -1, startRuleIndex);
        if (startRuleStartState.isLeftRecursiveRule) {
            this.enterRecursionRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
        }
        else {
            this.enterRule(this.rootContext, startRuleStartState.stateNumber, startRuleIndex);
        }
    Block_2:
        while (true) {
            final ATNState p = this.getATNState();
            switch (p.getStateType()) {
                case 7: {
                    if (this._ctx.isEmpty()) {
                        break Block_2;
                    }
                    this.visitRuleStopState(p);
                    continue;
                }
                default: {
                    try {
                        this.visitState(p);
                    }
                    catch (final RecognitionException e) {
                        this.setState(this.atn.ruleToStopState[p.ruleIndex].stateNumber);
                        this.getContext().exception = e;
                        this.getErrorHandler().reportError(this, e);
                        this.recover(e);
                    }
                    continue;
                }
            }
        }
        if (startRuleStartState.isLeftRecursiveRule) {
            final ParserRuleContext result = this._ctx;
            final Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
            this.unrollRecursionContexts(parentContext.a);
            return result;
        }
        this.exitRule();
        return this.rootContext;
    }
    
    @Override
    public void enterRecursionRule(final ParserRuleContext localctx, final int state, final int ruleIndex, final int precedence) {
        final Pair<ParserRuleContext, Integer> pair = new Pair<ParserRuleContext, Integer>(this._ctx, localctx.invokingState);
        this._parentContextStack.push(pair);
        super.enterRecursionRule(localctx, state, ruleIndex, precedence);
    }
    
    protected ATNState getATNState() {
        return this.atn.states.get(this.getState());
    }
    
    protected void visitState(final ATNState p) {
        int predictedAlt = 1;
        if (p instanceof DecisionState) {
            predictedAlt = this.visitDecisionState((DecisionState)p);
        }
        final Transition transition = p.transition(predictedAlt - 1);
        switch (transition.getSerializationType()) {
            case 1: {
                if (p.getStateType() == 10 && ((StarLoopEntryState)p).isPrecedenceDecision && !(transition.target instanceof LoopEndState)) {
                    final InterpreterRuleContext localctx = this.createInterpreterRuleContext((ParserRuleContext)this._parentContextStack.peek().a, (int)this._parentContextStack.peek().b, this._ctx.getRuleIndex());
                    this.pushNewRecursionContext(localctx, this.atn.ruleToStartState[p.ruleIndex].stateNumber, this._ctx.getRuleIndex());
                    break;
                }
                break;
            }
            case 5: {
                this.match(((AtomTransition)transition).label);
                break;
            }
            case 2:
            case 7:
            case 8: {
                if (!transition.matches(this._input.LA(1), 1, 65535)) {
                    this.recoverInline();
                }
                this.matchWildcard();
                break;
            }
            case 9: {
                this.matchWildcard();
                break;
            }
            case 3: {
                final RuleStartState ruleStartState = (RuleStartState)transition.target;
                final int ruleIndex = ruleStartState.ruleIndex;
                final InterpreterRuleContext newctx = this.createInterpreterRuleContext(this._ctx, p.stateNumber, ruleIndex);
                if (ruleStartState.isLeftRecursiveRule) {
                    this.enterRecursionRule(newctx, ruleStartState.stateNumber, ruleIndex, ((RuleTransition)transition).precedence);
                    break;
                }
                this.enterRule(newctx, transition.target.stateNumber, ruleIndex);
                break;
            }
            case 4: {
                final PredicateTransition predicateTransition = (PredicateTransition)transition;
                if (!this.sempred(this._ctx, predicateTransition.ruleIndex, predicateTransition.predIndex)) {
                    throw new FailedPredicateException(this);
                }
                break;
            }
            case 6: {
                final ActionTransition actionTransition = (ActionTransition)transition;
                this.action(this._ctx, actionTransition.ruleIndex, actionTransition.actionIndex);
                break;
            }
            case 10: {
                if (!this.precpred(this._ctx, ((PrecedencePredicateTransition)transition).precedence)) {
                    throw new FailedPredicateException(this, String.format("precpred(_ctx, %d)", ((PrecedencePredicateTransition)transition).precedence));
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unrecognized ATN transition type.");
            }
        }
        this.setState(transition.target.stateNumber);
    }
    
    protected int visitDecisionState(final DecisionState p) {
        int predictedAlt = 1;
        if (p.getNumberOfTransitions() > 1) {
            this.getErrorHandler().sync(this);
            final int decision = p.decision;
            if (decision == this.overrideDecision && this._input.index() == this.overrideDecisionInputIndex && !this.overrideDecisionReached) {
                predictedAlt = this.overrideDecisionAlt;
                this.overrideDecisionReached = true;
            }
            else {
                predictedAlt = ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter().adaptivePredict(this._input, decision, this._ctx);
            }
        }
        return predictedAlt;
    }
    
    protected InterpreterRuleContext createInterpreterRuleContext(final ParserRuleContext parent, final int invokingStateNumber, final int ruleIndex) {
        return new InterpreterRuleContext(parent, invokingStateNumber, ruleIndex);
    }
    
    protected void visitRuleStopState(final ATNState p) {
        final RuleStartState ruleStartState = this.atn.ruleToStartState[p.ruleIndex];
        if (ruleStartState.isLeftRecursiveRule) {
            final Pair<ParserRuleContext, Integer> parentContext = this._parentContextStack.pop();
            this.unrollRecursionContexts(parentContext.a);
            this.setState(parentContext.b);
        }
        else {
            this.exitRule();
        }
        final RuleTransition ruleTransition = (RuleTransition)this.atn.states.get(this.getState()).transition(0);
        this.setState(ruleTransition.followState.stateNumber);
    }
    
    public void addDecisionOverride(final int decision, final int tokenIndex, final int forcedAlt) {
        this.overrideDecision = decision;
        this.overrideDecisionInputIndex = tokenIndex;
        this.overrideDecisionAlt = forcedAlt;
    }
    
    public InterpreterRuleContext getOverrideDecisionRoot() {
        return this.overrideDecisionRoot;
    }
    
    protected void recover(final RecognitionException e) {
        final int i = this._input.index();
        this.getErrorHandler().recover(this, e);
        if (this._input.index() == i) {
            if (e instanceof InputMismatchException) {
                final InputMismatchException ime = (InputMismatchException)e;
                final Token tok = e.getOffendingToken();
                final int expectedTokenType = ime.getExpectedTokens().getMinElement();
                final Token errToken = (Token)this.getTokenFactory().create(new Pair<TokenSource, CharStream>(tok.getTokenSource(), tok.getTokenSource().getInputStream()), expectedTokenType, tok.getText(), 0, -1, -1, tok.getLine(), tok.getCharPositionInLine());
                this._ctx.addErrorNode(errToken);
            }
            else {
                final Token tok2 = e.getOffendingToken();
                final Token errToken2 = (Token)this.getTokenFactory().create(new Pair<TokenSource, CharStream>(tok2.getTokenSource(), tok2.getTokenSource().getInputStream()), 0, tok2.getText(), 0, -1, -1, tok2.getLine(), tok2.getCharPositionInLine());
                this._ctx.addErrorNode(errToken2);
            }
        }
    }
    
    protected Token recoverInline() {
        return this._errHandler.recoverInline(this);
    }
    
    public InterpreterRuleContext getRootContext() {
        return this.rootContext;
    }
}
