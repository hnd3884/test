package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Token;

public class ErrorNodeImpl extends TerminalNodeImpl implements ErrorNode
{
    public ErrorNodeImpl(final Token token) {
        super(token);
    }
    
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
        return (T)visitor.visitErrorNode(this);
    }
}
