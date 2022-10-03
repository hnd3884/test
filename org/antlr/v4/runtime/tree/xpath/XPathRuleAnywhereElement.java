package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.Trees;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathRuleAnywhereElement extends XPathElement
{
    protected int ruleIndex;
    
    public XPathRuleAnywhereElement(final String ruleName, final int ruleIndex) {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        return Trees.findAllRuleNodes(t, this.ruleIndex);
    }
}
