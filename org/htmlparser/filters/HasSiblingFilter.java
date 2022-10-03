package org.htmlparser.filters;

import org.htmlparser.util.NodeList;
import org.htmlparser.Tag;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class HasSiblingFilter implements NodeFilter
{
    protected NodeFilter mSiblingFilter;
    
    public HasSiblingFilter() {
        this(null);
    }
    
    public HasSiblingFilter(final NodeFilter filter) {
        this.setSiblingFilter(filter);
    }
    
    public NodeFilter getSiblingFilter() {
        return this.mSiblingFilter;
    }
    
    public void setSiblingFilter(final NodeFilter filter) {
        this.mSiblingFilter = filter;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (!(node instanceof Tag) || !((Tag)node).isEndTag()) {
            final Node parent = node.getParent();
            if (null != parent) {
                final NodeList siblings = parent.getChildren();
                if (null != siblings) {
                    for (int count = siblings.size(), i = 0; !ret && i < count; ++i) {
                        if (this.getSiblingFilter().accept(siblings.elementAt(i))) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }
}
