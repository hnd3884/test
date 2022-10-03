package org.antlr.v4.runtime;

import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.misc.Interval;
import java.util.Collections;
import java.util.Iterator;
import org.antlr.v4.runtime.tree.ErrorNodeImpl;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import java.util.ArrayList;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTree;
import java.util.List;

public class ParserRuleContext extends RuleContext
{
    public List<ParseTree> children;
    public Token start;
    public Token stop;
    public RecognitionException exception;
    
    public ParserRuleContext() {
    }
    
    public void copyFrom(final ParserRuleContext ctx) {
        this.parent = ctx.parent;
        this.invokingState = ctx.invokingState;
        this.start = ctx.start;
        this.stop = ctx.stop;
    }
    
    public ParserRuleContext(final ParserRuleContext parent, final int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }
    
    public void enterRule(final ParseTreeListener listener) {
    }
    
    public void exitRule(final ParseTreeListener listener) {
    }
    
    public TerminalNode addChild(final TerminalNode t) {
        if (this.children == null) {
            this.children = new ArrayList<ParseTree>();
        }
        this.children.add(t);
        return t;
    }
    
    public RuleContext addChild(final RuleContext ruleInvocation) {
        if (this.children == null) {
            this.children = new ArrayList<ParseTree>();
        }
        this.children.add(ruleInvocation);
        return ruleInvocation;
    }
    
    public void removeLastChild() {
        if (this.children != null) {
            this.children.remove(this.children.size() - 1);
        }
    }
    
    public TerminalNode addChild(final Token matchedToken) {
        final TerminalNodeImpl t = new TerminalNodeImpl(matchedToken);
        this.addChild(t);
        t.parent = this;
        return t;
    }
    
    public ErrorNode addErrorNode(final Token badToken) {
        final ErrorNodeImpl t = new ErrorNodeImpl(badToken);
        this.addChild(t);
        t.parent = this;
        return t;
    }
    
    @Override
    public ParserRuleContext getParent() {
        return (ParserRuleContext)super.getParent();
    }
    
    @Override
    public ParseTree getChild(final int i) {
        return (this.children != null && i >= 0 && i < this.children.size()) ? this.children.get(i) : null;
    }
    
    public <T extends ParseTree> T getChild(final Class<? extends T> ctxType, final int i) {
        if (this.children == null || i < 0 || i >= this.children.size()) {
            return null;
        }
        int j = -1;
        for (final ParseTree o : this.children) {
            if (ctxType.isInstance(o) && ++j == i) {
                return (T)ctxType.cast(o);
            }
        }
        return null;
    }
    
    public TerminalNode getToken(final int ttype, final int i) {
        if (this.children == null || i < 0 || i >= this.children.size()) {
            return null;
        }
        int j = -1;
        for (final ParseTree o : this.children) {
            if (o instanceof TerminalNode) {
                final TerminalNode tnode = (TerminalNode)o;
                final Token symbol = tnode.getSymbol();
                if (symbol.getType() == ttype && ++j == i) {
                    return tnode;
                }
                continue;
            }
        }
        return null;
    }
    
    public List<TerminalNode> getTokens(final int ttype) {
        if (this.children == null) {
            return Collections.emptyList();
        }
        List<TerminalNode> tokens = null;
        for (final ParseTree o : this.children) {
            if (o instanceof TerminalNode) {
                final TerminalNode tnode = (TerminalNode)o;
                final Token symbol = tnode.getSymbol();
                if (symbol.getType() != ttype) {
                    continue;
                }
                if (tokens == null) {
                    tokens = new ArrayList<TerminalNode>();
                }
                tokens.add(tnode);
            }
        }
        if (tokens == null) {
            return Collections.emptyList();
        }
        return tokens;
    }
    
    public <T extends ParserRuleContext> T getRuleContext(final Class<? extends T> ctxType, final int i) {
        return this.getChild(ctxType, i);
    }
    
    public <T extends ParserRuleContext> List<T> getRuleContexts(final Class<? extends T> ctxType) {
        if (this.children == null) {
            return Collections.emptyList();
        }
        List<T> contexts = null;
        for (final ParseTree o : this.children) {
            if (ctxType.isInstance(o)) {
                if (contexts == null) {
                    contexts = new ArrayList<T>();
                }
                contexts.add((T)ctxType.cast(o));
            }
        }
        if (contexts == null) {
            return Collections.emptyList();
        }
        return contexts;
    }
    
    @Override
    public int getChildCount() {
        return (this.children != null) ? this.children.size() : 0;
    }
    
    @Override
    public Interval getSourceInterval() {
        if (this.start == null) {
            return Interval.INVALID;
        }
        if (this.stop == null || this.stop.getTokenIndex() < this.start.getTokenIndex()) {
            return Interval.of(this.start.getTokenIndex(), this.start.getTokenIndex() - 1);
        }
        return Interval.of(this.start.getTokenIndex(), this.stop.getTokenIndex());
    }
    
    public Token getStart() {
        return this.start;
    }
    
    public Token getStop() {
        return this.stop;
    }
    
    public String toInfoString(final Parser recognizer) {
        final List<String> rules = recognizer.getRuleInvocationStack(this);
        Collections.reverse(rules);
        return "ParserRuleContext" + rules + "{" + "start=" + this.start + ", stop=" + this.stop + '}';
    }
}
