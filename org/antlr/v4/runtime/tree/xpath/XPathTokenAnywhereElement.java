package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.Trees;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathTokenAnywhereElement extends XPathElement
{
    protected int tokenType;
    
    public XPathTokenAnywhereElement(final String tokenName, final int tokenType) {
        super(tokenName);
        this.tokenType = tokenType;
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        return Trees.findAllTokenNodes(t, this.tokenType);
    }
}
