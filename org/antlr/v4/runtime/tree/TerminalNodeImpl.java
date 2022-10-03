package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.Token;

public class TerminalNodeImpl implements TerminalNode
{
    public Token symbol;
    public ParseTree parent;
    
    public TerminalNodeImpl(final Token symbol) {
        this.symbol = symbol;
    }
    
    @Override
    public ParseTree getChild(final int i) {
        return null;
    }
    
    @Override
    public Token getSymbol() {
        return this.symbol;
    }
    
    @Override
    public ParseTree getParent() {
        return this.parent;
    }
    
    @Override
    public Token getPayload() {
        return this.symbol;
    }
    
    @Override
    public Interval getSourceInterval() {
        if (this.symbol == null) {
            return Interval.INVALID;
        }
        final int tokenIndex = this.symbol.getTokenIndex();
        return new Interval(tokenIndex, tokenIndex);
    }
    
    @Override
    public int getChildCount() {
        return 0;
    }
    
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
        return (T)visitor.visitTerminal(this);
    }
    
    @Override
    public String getText() {
        return this.symbol.getText();
    }
    
    @Override
    public String toStringTree(final Parser parser) {
        return this.toString();
    }
    
    @Override
    public String toString() {
        if (this.symbol.getType() == -1) {
            return "<EOF>";
        }
        return this.symbol.getText();
    }
    
    @Override
    public String toStringTree() {
        return this.toString();
    }
}
