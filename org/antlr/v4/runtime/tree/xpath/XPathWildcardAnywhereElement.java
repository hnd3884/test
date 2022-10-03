package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathWildcardAnywhereElement extends XPathElement
{
    public XPathWildcardAnywhereElement() {
        super("*");
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        return Trees.getDescendants(t);
    }
}
