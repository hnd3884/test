package org.htmlparser.filters;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class IsEqualFilter implements NodeFilter
{
    protected Node mNode;
    
    public IsEqualFilter(final Node node) {
        this.mNode = node;
    }
    
    public boolean accept(final Node node) {
        return this.mNode == node;
    }
}
