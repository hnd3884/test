package org.htmlparser.filters;

import org.htmlparser.Tag;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class HasParentFilter implements NodeFilter
{
    protected NodeFilter mParentFilter;
    protected boolean mRecursive;
    
    public HasParentFilter() {
        this(null);
    }
    
    public HasParentFilter(final NodeFilter filter) {
        this(filter, false);
    }
    
    public HasParentFilter(final NodeFilter filter, final boolean recursive) {
        this.setParentFilter(filter);
        this.setRecursive(recursive);
    }
    
    public NodeFilter getParentFilter() {
        return this.mParentFilter;
    }
    
    public void setParentFilter(final NodeFilter filter) {
        this.mParentFilter = filter;
    }
    
    public boolean getRecursive() {
        return this.mRecursive;
    }
    
    public void setRecursive(final boolean recursive) {
        this.mRecursive = recursive;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (!(node instanceof Tag) || !((Tag)node).isEndTag()) {
            final Node parent = node.getParent();
            if (null != parent && null != this.getParentFilter()) {
                ret = this.getParentFilter().accept(parent);
                if (!ret && this.getRecursive()) {
                    ret = this.accept(parent);
                }
            }
        }
        return ret;
    }
}
