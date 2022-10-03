package org.antlr.v4.runtime.tree.xpath;

import java.util.Collection;
import org.antlr.v4.runtime.tree.ParseTree;

public abstract class XPathElement
{
    protected String nodeName;
    protected boolean invert;
    
    public XPathElement(final String nodeName) {
        this.nodeName = nodeName;
    }
    
    public abstract Collection<ParseTree> evaluate(final ParseTree p0);
    
    @Override
    public String toString() {
        final String inv = this.invert ? "!" : "";
        return this.getClass().getSimpleName() + "[" + inv + this.nodeName + "]";
    }
}
