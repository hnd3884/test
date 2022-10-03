package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;

public interface ParseTreeListener
{
    void visitTerminal(final TerminalNode p0);
    
    void visitErrorNode(final ErrorNode p0);
    
    void enterEveryRule(final ParserRuleContext p0);
    
    void exitEveryRule(final ParserRuleContext p0);
}
