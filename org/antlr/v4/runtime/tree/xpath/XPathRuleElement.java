package org.antlr.v4.runtime.tree.xpath;

import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathRuleElement extends XPathElement
{
    protected int ruleIndex;
    
    public XPathRuleElement(final String ruleName, final int ruleIndex) {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        final List<ParseTree> nodes = new ArrayList<ParseTree>();
        for (final Tree c : Trees.getChildren(t)) {
            if (c instanceof ParserRuleContext) {
                final ParserRuleContext ctx = (ParserRuleContext)c;
                if ((ctx.getRuleIndex() != this.ruleIndex || this.invert) && (ctx.getRuleIndex() == this.ruleIndex || !this.invert)) {
                    continue;
                }
                nodes.add(ctx);
            }
        }
        return nodes;
    }
}
