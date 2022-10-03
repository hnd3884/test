package org.antlr.v4.runtime.tree;

public interface ParseTreeVisitor<T>
{
    T visit(final ParseTree p0);
    
    T visitChildren(final RuleNode p0);
    
    T visitTerminal(final TerminalNode p0);
    
    T visitErrorNode(final ErrorNode p0);
}
