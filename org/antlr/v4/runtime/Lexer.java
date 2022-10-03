package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import java.util.ArrayList;
import java.util.List;
import java.util.EmptyStackException;
import org.antlr.v4.runtime.misc.IntegerStack;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.atn.LexerATNSimulator;

public abstract class Lexer extends Recognizer<Integer, LexerATNSimulator> implements TokenSource
{
    public static final int DEFAULT_MODE = 0;
    public static final int MORE = -2;
    public static final int SKIP = -3;
    public static final int DEFAULT_TOKEN_CHANNEL = 0;
    public static final int HIDDEN = 1;
    public static final int MIN_CHAR_VALUE = 0;
    public static final int MAX_CHAR_VALUE = 65534;
    public CharStream _input;
    protected Pair<TokenSource, CharStream> _tokenFactorySourcePair;
    protected TokenFactory<?> _factory;
    public Token _token;
    public int _tokenStartCharIndex;
    public int _tokenStartLine;
    public int _tokenStartCharPositionInLine;
    public boolean _hitEOF;
    public int _channel;
    public int _type;
    public final IntegerStack _modeStack;
    public int _mode;
    public String _text;
    
    public Lexer() {
        this._factory = CommonTokenFactory.DEFAULT;
        this._tokenStartCharIndex = -1;
        this._modeStack = new IntegerStack();
        this._mode = 0;
    }
    
    public Lexer(final CharStream input) {
        this._factory = CommonTokenFactory.DEFAULT;
        this._tokenStartCharIndex = -1;
        this._modeStack = new IntegerStack();
        this._mode = 0;
        this._input = input;
        this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, input);
    }
    
    public void reset() {
        if (this._input != null) {
            this._input.seek(0);
        }
        this._token = null;
        this._type = 0;
        this._channel = 0;
        this._tokenStartCharIndex = -1;
        this._tokenStartCharPositionInLine = -1;
        this._tokenStartLine = -1;
        this._text = null;
        this._hitEOF = false;
        this._mode = 0;
        this._modeStack.clear();
        ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().reset();
    }
    
    @Override
    public Token nextToken() {
        if (this._input == null) {
            throw new IllegalStateException("nextToken requires a non-null input stream.");
        }
        final int tokenStartMarker = this._input.mark();
        try {
        Label_0027:
            while (!this._hitEOF) {
                this._token = null;
                this._channel = 0;
                this._tokenStartCharIndex = this._input.index();
                this._tokenStartCharPositionInLine = ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().getCharPositionInLine();
                this._tokenStartLine = ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().getLine();
                this._text = null;
                do {
                    this._type = 0;
                    int ttype;
                    try {
                        ttype = ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().match(this._input, this._mode);
                    }
                    catch (final LexerNoViableAltException e) {
                        this.notifyListeners(e);
                        this.recover(e);
                        ttype = -3;
                    }
                    if (this._input.LA(1) == -1) {
                        this._hitEOF = true;
                    }
                    if (this._type == 0) {
                        this._type = ttype;
                    }
                    if (this._type == -3) {
                        continue Label_0027;
                    }
                } while (this._type == -2);
                if (this._token == null) {
                    this.emit();
                }
                return this._token;
            }
            this.emitEOF();
            return this._token;
        }
        finally {
            this._input.release(tokenStartMarker);
        }
    }
    
    public void skip() {
        this._type = -3;
    }
    
    public void more() {
        this._type = -2;
    }
    
    public void mode(final int m) {
        this._mode = m;
    }
    
    public void pushMode(final int m) {
        this._modeStack.push(this._mode);
        this.mode(m);
    }
    
    public int popMode() {
        if (this._modeStack.isEmpty()) {
            throw new EmptyStackException();
        }
        this.mode(this._modeStack.pop());
        return this._mode;
    }
    
    @Override
    public void setTokenFactory(final TokenFactory<?> factory) {
        this._factory = factory;
    }
    
    @Override
    public TokenFactory<? extends Token> getTokenFactory() {
        return (TokenFactory<? extends Token>)this._factory;
    }
    
    @Override
    public void setInputStream(final IntStream input) {
        this._input = null;
        this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, this._input);
        this.reset();
        this._input = (CharStream)input;
        this._tokenFactorySourcePair = new Pair<TokenSource, CharStream>(this, this._input);
    }
    
    @Override
    public String getSourceName() {
        return this._input.getSourceName();
    }
    
    @Override
    public CharStream getInputStream() {
        return this._input;
    }
    
    public void emit(final Token token) {
        this._token = token;
    }
    
    public Token emit() {
        final Token t = (Token)this._factory.create(this._tokenFactorySourcePair, this._type, this._text, this._channel, this._tokenStartCharIndex, this.getCharIndex() - 1, this._tokenStartLine, this._tokenStartCharPositionInLine);
        this.emit(t);
        return t;
    }
    
    public Token emitEOF() {
        final int cpos = this.getCharPositionInLine();
        final int line = this.getLine();
        final Token eof = (Token)this._factory.create(this._tokenFactorySourcePair, -1, null, 0, this._input.index(), this._input.index() - 1, line, cpos);
        this.emit(eof);
        return eof;
    }
    
    @Override
    public int getLine() {
        return ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().getLine();
    }
    
    @Override
    public int getCharPositionInLine() {
        return ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().getCharPositionInLine();
    }
    
    public void setLine(final int line) {
        ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().setLine(line);
    }
    
    public void setCharPositionInLine(final int charPositionInLine) {
        ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().setCharPositionInLine(charPositionInLine);
    }
    
    public int getCharIndex() {
        return this._input.index();
    }
    
    public String getText() {
        if (this._text != null) {
            return this._text;
        }
        return ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().getText(this._input);
    }
    
    public void setText(final String text) {
        this._text = text;
    }
    
    public Token getToken() {
        return this._token;
    }
    
    public void setToken(final Token _token) {
        this._token = _token;
    }
    
    public void setType(final int ttype) {
        this._type = ttype;
    }
    
    public int getType() {
        return this._type;
    }
    
    public void setChannel(final int channel) {
        this._channel = channel;
    }
    
    public int getChannel() {
        return this._channel;
    }
    
    public String[] getModeNames() {
        return null;
    }
    
    @Deprecated
    @Override
    public String[] getTokenNames() {
        return null;
    }
    
    public List<? extends Token> getAllTokens() {
        final List<Token> tokens = new ArrayList<Token>();
        for (Token t = this.nextToken(); t.getType() != -1; t = this.nextToken()) {
            tokens.add(t);
        }
        return tokens;
    }
    
    public void recover(final LexerNoViableAltException e) {
        if (this._input.LA(1) != -1) {
            ((Recognizer<Symbol, LexerATNSimulator>)this).getInterpreter().consume(this._input);
        }
    }
    
    public void notifyListeners(final LexerNoViableAltException e) {
        final String text = this._input.getText(Interval.of(this._tokenStartCharIndex, this._input.index()));
        final String msg = "token recognition error at: '" + this.getErrorDisplay(text) + "'";
        final ANTLRErrorListener listener = this.getErrorListenerDispatch();
        listener.syntaxError(this, null, this._tokenStartLine, this._tokenStartCharPositionInLine, msg, e);
    }
    
    public String getErrorDisplay(final String s) {
        final StringBuilder buf = new StringBuilder();
        for (final char c : s.toCharArray()) {
            buf.append(this.getErrorDisplay(c));
        }
        return buf.toString();
    }
    
    public String getErrorDisplay(final int c) {
        String s = String.valueOf((char)c);
        switch (c) {
            case -1: {
                s = "<EOF>";
                break;
            }
            case 10: {
                s = "\\n";
                break;
            }
            case 9: {
                s = "\\t";
                break;
            }
            case 13: {
                s = "\\r";
                break;
            }
        }
        return s;
    }
    
    public String getCharErrorDisplay(final int c) {
        final String s = this.getErrorDisplay(c);
        return "'" + s + "'";
    }
    
    public void recover(final RecognitionException re) {
        this._input.consume();
    }
}
