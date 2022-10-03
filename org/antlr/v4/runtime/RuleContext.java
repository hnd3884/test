package org.antlr.v4.runtime;

import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.RuleNode;

public class RuleContext implements RuleNode
{
    public static final ParserRuleContext EMPTY;
    public RuleContext parent;
    public int invokingState;
    
    public RuleContext() {
        this.invokingState = -1;
    }
    
    public RuleContext(final RuleContext parent, final int invokingState) {
        this.invokingState = -1;
        this.parent = parent;
        this.invokingState = invokingState;
    }
    
    public int depth() {
        int n = 0;
        for (RuleContext p = this; p != null; p = p.parent, ++n) {}
        return n;
    }
    
    public boolean isEmpty() {
        return this.invokingState == -1;
    }
    
    @Override
    public Interval getSourceInterval() {
        return Interval.INVALID;
    }
    
    @Override
    public RuleContext getRuleContext() {
        return this;
    }
    
    @Override
    public RuleContext getParent() {
        return this.parent;
    }
    
    @Override
    public RuleContext getPayload() {
        return this;
    }
    
    @Override
    public String getText() {
        if (this.getChildCount() == 0) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.getChildCount(); ++i) {
            builder.append(this.getChild(i).getText());
        }
        return builder.toString();
    }
    
    public int getRuleIndex() {
        return -1;
    }
    
    public int getAltNumber() {
        return 0;
    }
    
    public void setAltNumber(final int altNumber) {
    }
    
    @Override
    public ParseTree getChild(final int i) {
        return null;
    }
    
    @Override
    public int getChildCount() {
        return 0;
    }
    
    @Override
    public <T> T accept(final ParseTreeVisitor<? extends T> visitor) {
        return (T)visitor.visitChildren(this);
    }
    
    @Override
    public String toStringTree(final Parser recog) {
        return Trees.toStringTree(this, recog);
    }
    
    public String toStringTree(final List<String> ruleNames) {
        return Trees.toStringTree(this, ruleNames);
    }
    
    @Override
    public String toStringTree() {
        return this.toStringTree((List<String>)null);
    }
    
    @Override
    public String toString() {
        return this.toString((List<String>)null, null);
    }
    
    public final String toString(final Recognizer<?, ?> recog) {
        return this.toString(recog, ParserRuleContext.EMPTY);
    }
    
    public final String toString(final List<String> ruleNames) {
        return this.toString(ruleNames, null);
    }
    
    public String toString(final Recognizer<?, ?> recog, final RuleContext stop) {
        final String[] ruleNames = (String[])((recog != null) ? recog.getRuleNames() : null);
        final List<String> ruleNamesList = (ruleNames != null) ? Arrays.asList(ruleNames) : null;
        return this.toString(ruleNamesList, stop);
    }
    
    public String toString(final List<String> ruleNames, final RuleContext stop) {
        final StringBuilder buf = new StringBuilder();
        RuleContext p = this;
        buf.append("[");
        while (p != null && p != stop) {
            if (ruleNames == null) {
                if (!p.isEmpty()) {
                    buf.append(p.invokingState);
                }
            }
            else {
                final int ruleIndex = p.getRuleIndex();
                final String ruleName = (ruleIndex >= 0 && ruleIndex < ruleNames.size()) ? ruleNames.get(ruleIndex) : Integer.toString(ruleIndex);
                buf.append(ruleName);
            }
            if (p.parent != null && (ruleNames != null || !p.parent.isEmpty())) {
                buf.append(" ");
            }
            p = p.parent;
        }
        buf.append("]");
        return buf.toString();
    }
    
    static {
        EMPTY = new ParserRuleContext();
    }
}
