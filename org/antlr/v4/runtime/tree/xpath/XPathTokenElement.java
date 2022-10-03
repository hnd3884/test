package org.antlr.v4.runtime.tree.xpath;

import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathTokenElement extends XPathElement
{
    protected int tokenType;
    
    public XPathTokenElement(final String tokenName, final int tokenType) {
        super(tokenName);
        this.tokenType = tokenType;
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        final List<ParseTree> nodes = new ArrayList<ParseTree>();
        for (final Tree c : Trees.getChildren(t)) {
            if (c instanceof TerminalNode) {
                final TerminalNode tnode = (TerminalNode)c;
                if ((tnode.getSymbol().getType() != this.tokenType || this.invert) && (tnode.getSymbol().getType() == this.tokenType || !this.invert)) {
                    continue;
                }
                nodes.add(tnode);
            }
        }
        return nodes;
    }
}
