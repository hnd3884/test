package org.antlr.v4.runtime;

import java.util.WeakHashMap;
import java.util.Collection;
import org.antlr.v4.runtime.atn.ParseInfo;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Utils;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.atn.ATNSimulator;

public abstract class Recognizer<Symbol, ATNInterpreter extends ATNSimulator>
{
    public static final int EOF = -1;
    private static final Map<Vocabulary, Map<String, Integer>> tokenTypeMapCache;
    private static final Map<String[], Map<String, Integer>> ruleIndexMapCache;
    private List<ANTLRErrorListener> _listeners;
    protected ATNInterpreter _interp;
    private int _stateNumber;
    
    public Recognizer() {
        this._listeners = new CopyOnWriteArrayList<ANTLRErrorListener>() {
            {
                ((CopyOnWriteArrayList<ConsoleErrorListener>)this).add(ConsoleErrorListener.INSTANCE);
            }
        };
        this._stateNumber = -1;
    }
    
    @Deprecated
    public abstract String[] getTokenNames();
    
    public abstract String[] getRuleNames();
    
    public Vocabulary getVocabulary() {
        return VocabularyImpl.fromTokenNames(this.getTokenNames());
    }
    
    public Map<String, Integer> getTokenTypeMap() {
        final Vocabulary vocabulary = this.getVocabulary();
        synchronized (Recognizer.tokenTypeMapCache) {
            Map<String, Integer> result = Recognizer.tokenTypeMapCache.get(vocabulary);
            if (result == null) {
                result = new HashMap<String, Integer>();
                for (int i = 0; i < this.getATN().maxTokenType; ++i) {
                    final String literalName = vocabulary.getLiteralName(i);
                    if (literalName != null) {
                        result.put(literalName, i);
                    }
                    final String symbolicName = vocabulary.getSymbolicName(i);
                    if (symbolicName != null) {
                        result.put(symbolicName, i);
                    }
                }
                result.put("EOF", -1);
                result = Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)result);
                Recognizer.tokenTypeMapCache.put(vocabulary, result);
            }
            return result;
        }
    }
    
    public Map<String, Integer> getRuleIndexMap() {
        final String[] ruleNames = this.getRuleNames();
        if (ruleNames == null) {
            throw new UnsupportedOperationException("The current recognizer does not provide a list of rule names.");
        }
        synchronized (Recognizer.ruleIndexMapCache) {
            Map<String, Integer> result = Recognizer.ruleIndexMapCache.get(ruleNames);
            if (result == null) {
                result = Collections.unmodifiableMap((Map<? extends String, ? extends Integer>)Utils.toMap(ruleNames));
                Recognizer.ruleIndexMapCache.put(ruleNames, result);
            }
            return result;
        }
    }
    
    public int getTokenType(final String tokenName) {
        final Integer ttype = this.getTokenTypeMap().get(tokenName);
        if (ttype != null) {
            return ttype;
        }
        return 0;
    }
    
    public String getSerializedATN() {
        throw new UnsupportedOperationException("there is no serialized ATN");
    }
    
    public abstract String getGrammarFileName();
    
    public abstract ATN getATN();
    
    public ATNInterpreter getInterpreter() {
        return this._interp;
    }
    
    public ParseInfo getParseInfo() {
        return null;
    }
    
    public void setInterpreter(final ATNInterpreter interpreter) {
        this._interp = interpreter;
    }
    
    public String getErrorHeader(final RecognitionException e) {
        final int line = e.getOffendingToken().getLine();
        final int charPositionInLine = e.getOffendingToken().getCharPositionInLine();
        return "line " + line + ":" + charPositionInLine;
    }
    
    @Deprecated
    public String getTokenErrorDisplay(final Token t) {
        if (t == null) {
            return "<no token>";
        }
        String s = t.getText();
        if (s == null) {
            if (t.getType() == -1) {
                s = "<EOF>";
            }
            else {
                s = "<" + t.getType() + ">";
            }
        }
        s = s.replace("\n", "\\n");
        s = s.replace("\r", "\\r");
        s = s.replace("\t", "\\t");
        return "'" + s + "'";
    }
    
    public void addErrorListener(final ANTLRErrorListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener cannot be null.");
        }
        this._listeners.add(listener);
    }
    
    public void removeErrorListener(final ANTLRErrorListener listener) {
        this._listeners.remove(listener);
    }
    
    public void removeErrorListeners() {
        this._listeners.clear();
    }
    
    public List<? extends ANTLRErrorListener> getErrorListeners() {
        return this._listeners;
    }
    
    public ANTLRErrorListener getErrorListenerDispatch() {
        return new ProxyErrorListener(this.getErrorListeners());
    }
    
    public boolean sempred(final RuleContext _localctx, final int ruleIndex, final int actionIndex) {
        return true;
    }
    
    public boolean precpred(final RuleContext localctx, final int precedence) {
        return true;
    }
    
    public void action(final RuleContext _localctx, final int ruleIndex, final int actionIndex) {
    }
    
    public final int getState() {
        return this._stateNumber;
    }
    
    public final void setState(final int atnState) {
        this._stateNumber = atnState;
    }
    
    public abstract IntStream getInputStream();
    
    public abstract void setInputStream(final IntStream p0);
    
    public abstract TokenFactory<?> getTokenFactory();
    
    public abstract void setTokenFactory(final TokenFactory<?> p0);
    
    static {
        tokenTypeMapCache = new WeakHashMap<Vocabulary, Map<String, Integer>>();
        ruleIndexMapCache = new WeakHashMap<String[], Map<String, Integer>>();
    }
}
