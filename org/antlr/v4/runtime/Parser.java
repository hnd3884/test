package org.antlr.v4.runtime;

import java.util.WeakHashMap;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.atn.ProfilingATNSimulator;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNDeserializationOptions;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import org.antlr.v4.runtime.atn.ATNSimulator;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import java.util.List;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.antlr.v4.runtime.atn.ATN;
import java.util.Map;
import org.antlr.v4.runtime.atn.ParserATNSimulator;

public abstract class Parser extends Recognizer<Token, ParserATNSimulator>
{
    private static final Map<String, ATN> bypassAltsAtnCache;
    protected ANTLRErrorStrategy _errHandler;
    protected TokenStream _input;
    protected final IntegerStack _precedenceStack;
    protected ParserRuleContext _ctx;
    protected boolean _buildParseTrees;
    private TraceListener _tracer;
    protected List<ParseTreeListener> _parseListeners;
    protected int _syntaxErrors;
    protected boolean matchedEOF;
    
    public Parser(final TokenStream input) {
        this._errHandler = new DefaultErrorStrategy();
        (this._precedenceStack = new IntegerStack()).push(0);
        this._buildParseTrees = true;
        this.setInputStream(input);
    }
    
    public void reset() {
        if (this.getInputStream() != null) {
            this.getInputStream().seek(0);
        }
        this._errHandler.reset(this);
        this._ctx = null;
        this._syntaxErrors = 0;
        this.setTrace(this.matchedEOF = false);
        this._precedenceStack.clear();
        this._precedenceStack.push(0);
        final ATNSimulator interpreter = ((Recognizer<Symbol, ATNSimulator>)this).getInterpreter();
        if (interpreter != null) {
            interpreter.reset();
        }
    }
    
    public Token match(final int ttype) throws RecognitionException {
        Token t = this.getCurrentToken();
        if (t.getType() == ttype) {
            if (ttype == -1) {
                this.matchedEOF = true;
            }
            this._errHandler.reportMatch(this);
            this.consume();
        }
        else {
            t = this._errHandler.recoverInline(this);
            if (this._buildParseTrees && t.getTokenIndex() == -1) {
                this._ctx.addErrorNode(t);
            }
        }
        return t;
    }
    
    public Token matchWildcard() throws RecognitionException {
        Token t = this.getCurrentToken();
        if (t.getType() > 0) {
            this._errHandler.reportMatch(this);
            this.consume();
        }
        else {
            t = this._errHandler.recoverInline(this);
            if (this._buildParseTrees && t.getTokenIndex() == -1) {
                this._ctx.addErrorNode(t);
            }
        }
        return t;
    }
    
    public void setBuildParseTree(final boolean buildParseTrees) {
        this._buildParseTrees = buildParseTrees;
    }
    
    public boolean getBuildParseTree() {
        return this._buildParseTrees;
    }
    
    public void setTrimParseTree(final boolean trimParseTrees) {
        if (trimParseTrees) {
            if (this.getTrimParseTree()) {
                return;
            }
            this.addParseListener(TrimToSizeListener.INSTANCE);
        }
        else {
            this.removeParseListener(TrimToSizeListener.INSTANCE);
        }
    }
    
    public boolean getTrimParseTree() {
        return this.getParseListeners().contains(TrimToSizeListener.INSTANCE);
    }
    
    public List<ParseTreeListener> getParseListeners() {
        final List<ParseTreeListener> listeners = this._parseListeners;
        if (listeners == null) {
            return Collections.emptyList();
        }
        return listeners;
    }
    
    public void addParseListener(final ParseTreeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        if (this._parseListeners == null) {
            this._parseListeners = new ArrayList<ParseTreeListener>();
        }
        this._parseListeners.add(listener);
    }
    
    public void removeParseListener(final ParseTreeListener listener) {
        if (this._parseListeners != null && this._parseListeners.remove(listener) && this._parseListeners.isEmpty()) {
            this._parseListeners = null;
        }
    }
    
    public void removeParseListeners() {
        this._parseListeners = null;
    }
    
    protected void triggerEnterRuleEvent() {
        for (final ParseTreeListener listener : this._parseListeners) {
            listener.enterEveryRule(this._ctx);
            this._ctx.enterRule(listener);
        }
    }
    
    protected void triggerExitRuleEvent() {
        for (int i = this._parseListeners.size() - 1; i >= 0; --i) {
            final ParseTreeListener listener = this._parseListeners.get(i);
            this._ctx.exitRule(listener);
            listener.exitEveryRule(this._ctx);
        }
    }
    
    public int getNumberOfSyntaxErrors() {
        return this._syntaxErrors;
    }
    
    @Override
    public TokenFactory<?> getTokenFactory() {
        return this._input.getTokenSource().getTokenFactory();
    }
    
    @Override
    public void setTokenFactory(final TokenFactory<?> factory) {
        this._input.getTokenSource().setTokenFactory(factory);
    }
    
    public ATN getATNWithBypassAlts() {
        final String serializedAtn = this.getSerializedATN();
        if (serializedAtn == null) {
            throw new UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
        }
        synchronized (Parser.bypassAltsAtnCache) {
            ATN result = Parser.bypassAltsAtnCache.get(serializedAtn);
            if (result == null) {
                final ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
                deserializationOptions.setGenerateRuleBypassTransitions(true);
                result = new ATNDeserializer(deserializationOptions).deserialize(serializedAtn.toCharArray());
                Parser.bypassAltsAtnCache.put(serializedAtn, result);
            }
            return result;
        }
    }
    
    public ParseTreePattern compileParseTreePattern(final String pattern, final int patternRuleIndex) {
        if (this.getTokenStream() != null) {
            final TokenSource tokenSource = this.getTokenStream().getTokenSource();
            if (tokenSource instanceof Lexer) {
                final Lexer lexer = (Lexer)tokenSource;
                return this.compileParseTreePattern(pattern, patternRuleIndex, lexer);
            }
        }
        throw new UnsupportedOperationException("Parser can't discover a lexer to use");
    }
    
    public ParseTreePattern compileParseTreePattern(final String pattern, final int patternRuleIndex, final Lexer lexer) {
        final ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
        return m.compile(pattern, patternRuleIndex);
    }
    
    public ANTLRErrorStrategy getErrorHandler() {
        return this._errHandler;
    }
    
    public void setErrorHandler(final ANTLRErrorStrategy handler) {
        this._errHandler = handler;
    }
    
    @Override
    public TokenStream getInputStream() {
        return this.getTokenStream();
    }
    
    @Override
    public final void setInputStream(final IntStream input) {
        this.setTokenStream((TokenStream)input);
    }
    
    public TokenStream getTokenStream() {
        return this._input;
    }
    
    public void setTokenStream(final TokenStream input) {
        this._input = null;
        this.reset();
        this._input = input;
    }
    
    public Token getCurrentToken() {
        return this._input.LT(1);
    }
    
    public final void notifyErrorListeners(final String msg) {
        this.notifyErrorListeners(this.getCurrentToken(), msg, null);
    }
    
    public void notifyErrorListeners(final Token offendingToken, final String msg, final RecognitionException e) {
        ++this._syntaxErrors;
        int line = -1;
        int charPositionInLine = -1;
        line = offendingToken.getLine();
        charPositionInLine = offendingToken.getCharPositionInLine();
        final ANTLRErrorListener listener = this.getErrorListenerDispatch();
        listener.syntaxError(this, offendingToken, line, charPositionInLine, msg, e);
    }
    
    public Token consume() {
        final Token o = this.getCurrentToken();
        if (o.getType() != -1) {
            this.getInputStream().consume();
        }
        final boolean hasListener = this._parseListeners != null && !this._parseListeners.isEmpty();
        if (this._buildParseTrees || hasListener) {
            if (this._errHandler.inErrorRecoveryMode(this)) {
                final ErrorNode node = this._ctx.addErrorNode(o);
                if (this._parseListeners != null) {
                    for (final ParseTreeListener listener : this._parseListeners) {
                        listener.visitErrorNode(node);
                    }
                }
            }
            else {
                final TerminalNode node2 = this._ctx.addChild(o);
                if (this._parseListeners != null) {
                    for (final ParseTreeListener listener : this._parseListeners) {
                        listener.visitTerminal(node2);
                    }
                }
            }
        }
        return o;
    }
    
    protected void addContextToParseTree() {
        final ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
        if (parent != null) {
            parent.addChild(this._ctx);
        }
    }
    
    public void enterRule(final ParserRuleContext localctx, final int state, final int ruleIndex) {
        this.setState(state);
        this._ctx = localctx;
        this._ctx.start = this._input.LT(1);
        if (this._buildParseTrees) {
            this.addContextToParseTree();
        }
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }
    
    public void exitRule() {
        if (this.matchedEOF) {
            this._ctx.stop = this._input.LT(1);
        }
        else {
            this._ctx.stop = this._input.LT(-1);
        }
        if (this._parseListeners != null) {
            this.triggerExitRuleEvent();
        }
        this.setState(this._ctx.invokingState);
        this._ctx = (ParserRuleContext)this._ctx.parent;
    }
    
    public void enterOuterAlt(final ParserRuleContext localctx, final int altNum) {
        localctx.setAltNumber(altNum);
        if (this._buildParseTrees && this._ctx != localctx) {
            final ParserRuleContext parent = (ParserRuleContext)this._ctx.parent;
            if (parent != null) {
                parent.removeLastChild();
                parent.addChild(localctx);
            }
        }
        this._ctx = localctx;
    }
    
    public final int getPrecedence() {
        if (this._precedenceStack.isEmpty()) {
            return -1;
        }
        return this._precedenceStack.peek();
    }
    
    @Deprecated
    public void enterRecursionRule(final ParserRuleContext localctx, final int ruleIndex) {
        this.enterRecursionRule(localctx, this.getATN().ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0);
    }
    
    public void enterRecursionRule(final ParserRuleContext localctx, final int state, final int ruleIndex, final int precedence) {
        this.setState(state);
        this._precedenceStack.push(precedence);
        this._ctx = localctx;
        this._ctx.start = this._input.LT(1);
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }
    
    public void pushNewRecursionContext(final ParserRuleContext localctx, final int state, final int ruleIndex) {
        final ParserRuleContext previous = this._ctx;
        previous.parent = localctx;
        previous.invokingState = state;
        previous.stop = this._input.LT(-1);
        this._ctx = localctx;
        this._ctx.start = previous.start;
        if (this._buildParseTrees) {
            this._ctx.addChild(previous);
        }
        if (this._parseListeners != null) {
            this.triggerEnterRuleEvent();
        }
    }
    
    public void unrollRecursionContexts(final ParserRuleContext _parentctx) {
        this._precedenceStack.pop();
        this._ctx.stop = this._input.LT(-1);
        final ParserRuleContext retctx = this._ctx;
        if (this._parseListeners != null) {
            while (this._ctx != _parentctx) {
                this.triggerExitRuleEvent();
                this._ctx = (ParserRuleContext)this._ctx.parent;
            }
        }
        else {
            this._ctx = _parentctx;
        }
        retctx.parent = _parentctx;
        if (this._buildParseTrees && _parentctx != null) {
            _parentctx.addChild(retctx);
        }
    }
    
    public ParserRuleContext getInvokingContext(final int ruleIndex) {
        for (ParserRuleContext p = this._ctx; p != null; p = (ParserRuleContext)p.parent) {
            if (p.getRuleIndex() == ruleIndex) {
                return p;
            }
        }
        return null;
    }
    
    public ParserRuleContext getContext() {
        return this._ctx;
    }
    
    public void setContext(final ParserRuleContext ctx) {
        this._ctx = ctx;
    }
    
    @Override
    public boolean precpred(final RuleContext localctx, final int precedence) {
        return precedence >= this._precedenceStack.peek();
    }
    
    public boolean inContext(final String context) {
        return false;
    }
    
    public boolean isExpectedToken(final int symbol) {
        final ATN atn = ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter().atn;
        ParserRuleContext ctx = this._ctx;
        final ATNState s = atn.states.get(this.getState());
        IntervalSet following = atn.nextTokens(s);
        if (following.contains(symbol)) {
            return true;
        }
        if (!following.contains(-2)) {
            return false;
        }
        while (ctx != null && ctx.invokingState >= 0 && following.contains(-2)) {
            final ATNState invokingState = atn.states.get(ctx.invokingState);
            final RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            if (following.contains(symbol)) {
                return true;
            }
            ctx = (ParserRuleContext)ctx.parent;
        }
        return following.contains(-2) && symbol == -1;
    }
    
    public boolean isMatchedEOF() {
        return this.matchedEOF;
    }
    
    public IntervalSet getExpectedTokens() {
        return this.getATN().getExpectedTokens(this.getState(), this.getContext());
    }
    
    public IntervalSet getExpectedTokensWithinCurrentRule() {
        final ATN atn = ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter().atn;
        final ATNState s = atn.states.get(this.getState());
        return atn.nextTokens(s);
    }
    
    public int getRuleIndex(final String ruleName) {
        final Integer ruleIndex = this.getRuleIndexMap().get(ruleName);
        if (ruleIndex != null) {
            return ruleIndex;
        }
        return -1;
    }
    
    public ParserRuleContext getRuleContext() {
        return this._ctx;
    }
    
    public List<String> getRuleInvocationStack() {
        return this.getRuleInvocationStack(this._ctx);
    }
    
    public List<String> getRuleInvocationStack(RuleContext p) {
        final String[] ruleNames = this.getRuleNames();
        final List<String> stack = new ArrayList<String>();
        while (p != null) {
            final int ruleIndex = p.getRuleIndex();
            if (ruleIndex < 0) {
                stack.add("n/a");
            }
            else {
                stack.add(ruleNames[ruleIndex]);
            }
            p = p.parent;
        }
        return stack;
    }
    
    public List<String> getDFAStrings() {
        synchronized (((ParserATNSimulator)this._interp).decisionToDFA) {
            final List<String> s = new ArrayList<String>();
            for (int d = 0; d < ((ParserATNSimulator)this._interp).decisionToDFA.length; ++d) {
                final DFA dfa = ((ParserATNSimulator)this._interp).decisionToDFA[d];
                s.add(dfa.toString(this.getVocabulary()));
            }
            return s;
        }
    }
    
    public void dumpDFA() {
        synchronized (((ParserATNSimulator)this._interp).decisionToDFA) {
            boolean seenOne = false;
            for (int d = 0; d < ((ParserATNSimulator)this._interp).decisionToDFA.length; ++d) {
                final DFA dfa = ((ParserATNSimulator)this._interp).decisionToDFA[d];
                if (!dfa.states.isEmpty()) {
                    if (seenOne) {
                        System.out.println();
                    }
                    System.out.println("Decision " + dfa.decision + ":");
                    System.out.print(dfa.toString(this.getVocabulary()));
                    seenOne = true;
                }
            }
        }
    }
    
    public String getSourceName() {
        return this._input.getSourceName();
    }
    
    @Override
    public ParseInfo getParseInfo() {
        final ParserATNSimulator interp = ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter();
        if (interp instanceof ProfilingATNSimulator) {
            return new ParseInfo((ProfilingATNSimulator)interp);
        }
        return null;
    }
    
    public void setProfile(final boolean profile) {
        final ParserATNSimulator interp = ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter();
        final PredictionMode saveMode = interp.getPredictionMode();
        if (profile) {
            if (!(interp instanceof ProfilingATNSimulator)) {
                ((Recognizer<Symbol, ProfilingATNSimulator>)this).setInterpreter(new ProfilingATNSimulator(this));
            }
        }
        else if (interp instanceof ProfilingATNSimulator) {
            final ParserATNSimulator sim = new ParserATNSimulator(this, this.getATN(), interp.decisionToDFA, interp.getSharedContextCache());
            ((Recognizer<Symbol, ParserATNSimulator>)this).setInterpreter(sim);
        }
        ((Recognizer<Symbol, ParserATNSimulator>)this).getInterpreter().setPredictionMode(saveMode);
    }
    
    public void setTrace(final boolean trace) {
        if (!trace) {
            this.removeParseListener(this._tracer);
            this._tracer = null;
        }
        else {
            if (this._tracer != null) {
                this.removeParseListener(this._tracer);
            }
            else {
                this._tracer = new TraceListener();
            }
            this.addParseListener(this._tracer);
        }
    }
    
    public boolean isTrace() {
        return this._tracer != null;
    }
    
    static {
        bypassAltsAtnCache = new WeakHashMap<String, ATN>();
    }
    
    public class TraceListener implements ParseTreeListener
    {
        @Override
        public void enterEveryRule(final ParserRuleContext ctx) {
            System.out.println("enter   " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
        }
        
        @Override
        public void visitTerminal(final TerminalNode node) {
            System.out.println("consume " + node.getSymbol() + " rule " + Parser.this.getRuleNames()[Parser.this._ctx.getRuleIndex()]);
        }
        
        @Override
        public void visitErrorNode(final ErrorNode node) {
        }
        
        @Override
        public void exitEveryRule(final ParserRuleContext ctx) {
            System.out.println("exit    " + Parser.this.getRuleNames()[ctx.getRuleIndex()] + ", LT(1)=" + Parser.this._input.LT(1).getText());
        }
    }
    
    public static class TrimToSizeListener implements ParseTreeListener
    {
        public static final TrimToSizeListener INSTANCE;
        
        @Override
        public void enterEveryRule(final ParserRuleContext ctx) {
        }
        
        @Override
        public void visitTerminal(final TerminalNode node) {
        }
        
        @Override
        public void visitErrorNode(final ErrorNode node) {
        }
        
        @Override
        public void exitEveryRule(final ParserRuleContext ctx) {
            if (ctx.children instanceof ArrayList) {
                ((ArrayList)ctx.children).trimToSize();
            }
        }
        
        static {
            INSTANCE = new TrimToSizeListener();
        }
    }
}
