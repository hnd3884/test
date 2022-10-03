package org.antlr.v4.runtime.tree.xpath;

import java.util.Iterator;
import java.util.List;
import org.antlr.v4.runtime.tree.Tree;
import org.antlr.v4.runtime.tree.Trees;
import java.util.ArrayList;
import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public class XPathWildcardElement extends XPathElement
{
    public XPathWildcardElement() {
        super("*");
    }
    
    @Override
    public Collection<ParseTree> evaluate(final ParseTree t) {
        if (this.invert) {
            return new ArrayList<ParseTree>();
        }
        final List<ParseTree> kids = new ArrayList<ParseTree>();
        for (final Tree c : Trees.getChildren(t)) {
            kids.add((ParseTree)c);
        }
        return kids;
    }
}
