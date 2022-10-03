package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Pair;
import java.util.List;

public class ListTokenSource implements TokenSource
{
    protected final List<? extends Token> tokens;
    private final String sourceName;
    protected int i;
    protected Token eofToken;
    private TokenFactory<?> _factory;
    
    public ListTokenSource(final List<? extends Token> tokens) {
        this(tokens, null);
    }
    
    public ListTokenSource(final List<? extends Token> tokens, final String sourceName) {
        this._factory = CommonTokenFactory.DEFAULT;
        if (tokens == null) {
            throw new NullPointerException("tokens cannot be null");
        }
        this.tokens = tokens;
        this.sourceName = sourceName;
    }
    
    @Override
    public int getCharPositionInLine() {
        if (this.i < this.tokens.size()) {
            return ((Token)this.tokens.get(this.i)).getCharPositionInLine();
        }
        if (this.eofToken != null) {
            return this.eofToken.getCharPositionInLine();
        }
        if (this.tokens.size() > 0) {
            final Token lastToken = (Token)this.tokens.get(this.tokens.size() - 1);
            final String tokenText = lastToken.getText();
            if (tokenText != null) {
                final int lastNewLine = tokenText.lastIndexOf(10);
                if (lastNewLine >= 0) {
                    return tokenText.length() - lastNewLine - 1;
                }
            }
            return lastToken.getCharPositionInLine() + lastToken.getStopIndex() - lastToken.getStartIndex() + 1;
        }
        return 0;
    }
    
    @Override
    public Token nextToken() {
        if (this.i >= this.tokens.size()) {
            if (this.eofToken == null) {
                int start = -1;
                if (this.tokens.size() > 0) {
                    final int previousStop = ((Token)this.tokens.get(this.tokens.size() - 1)).getStopIndex();
                    if (previousStop != -1) {
                        start = previousStop + 1;
                    }
                }
                final int stop = Math.max(-1, start - 1);
                this.eofToken = (Token)this._factory.create(new Pair<TokenSource, CharStream>(this, this.getInputStream()), -1, "EOF", 0, start, stop, this.getLine(), this.getCharPositionInLine());
            }
            return this.eofToken;
        }
        final Token t = (Token)this.tokens.get(this.i);
        if (this.i == this.tokens.size() - 1 && t.getType() == -1) {
            this.eofToken = t;
        }
        ++this.i;
        return t;
    }
    
    @Override
    public int getLine() {
        if (this.i < this.tokens.size()) {
            return ((Token)this.tokens.get(this.i)).getLine();
        }
        if (this.eofToken != null) {
            return this.eofToken.getLine();
        }
        if (this.tokens.size() > 0) {
            final Token lastToken = (Token)this.tokens.get(this.tokens.size() - 1);
            int line = lastToken.getLine();
            final String tokenText = lastToken.getText();
            if (tokenText != null) {
                for (int i = 0; i < tokenText.length(); ++i) {
                    if (tokenText.charAt(i) == '\n') {
                        ++line;
                    }
                }
            }
            return line;
        }
        return 1;
    }
    
    @Override
    public CharStream getInputStream() {
        if (this.i < this.tokens.size()) {
            return ((Token)this.tokens.get(this.i)).getInputStream();
        }
        if (this.eofToken != null) {
            return this.eofToken.getInputStream();
        }
        if (this.tokens.size() > 0) {
            return ((Token)this.tokens.get(this.tokens.size() - 1)).getInputStream();
        }
        return null;
    }
    
    @Override
    public String getSourceName() {
        if (this.sourceName != null) {
            return this.sourceName;
        }
        final CharStream inputStream = this.getInputStream();
        if (inputStream != null) {
            return inputStream.getSourceName();
        }
        return "List";
    }
    
    @Override
    public void setTokenFactory(final TokenFactory<?> factory) {
        this._factory = factory;
    }
    
    @Override
    public TokenFactory<?> getTokenFactory() {
        return this._factory;
    }
}
