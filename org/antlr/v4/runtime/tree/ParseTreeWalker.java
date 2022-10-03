package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;

public class ParseTreeWalker
{
    public static final ParseTreeWalker DEFAULT;
    
    public void walk(final ParseTreeListener listener, final ParseTree t) {
        if (t instanceof ErrorNode) {
            listener.visitErrorNode((ErrorNode)t);
            return;
        }
        if (t instanceof TerminalNode) {
            listener.visitTerminal((TerminalNode)t);
            return;
        }
        final RuleNode r = (RuleNode)t;
        this.enterRule(listener, r);
        for (int n = r.getChildCount(), i = 0; i < n; ++i) {
            this.walk(listener, r.getChild(i));
        }
        this.exitRule(listener, r);
    }
    
    protected void enterRule(final ParseTreeListener listener, final RuleNode r) {
        final ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
        listener.enterEveryRule(ctx);
        ctx.enterRule(listener);
    }
    
    protected void exitRule(final ParseTreeListener listener, final RuleNode r) {
        final ParserRuleContext ctx = (ParserRuleContext)r.getRuleContext();
        ctx.exitRule(listener);
        listener.exitEveryRule(ctx);
    }
    
    static {
        DEFAULT = new ParseTreeWalker();
    }
}
