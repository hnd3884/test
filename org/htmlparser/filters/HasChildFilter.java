package org.htmlparser.filters;

import org.htmlparser.util.NodeList;
import org.htmlparser.tags.CompositeTag;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class HasChildFilter implements NodeFilter
{
    protected NodeFilter mChildFilter;
    protected boolean mRecursive;
    
    public HasChildFilter() {
        this(null);
    }
    
    public HasChildFilter(final NodeFilter filter) {
        this(filter, false);
    }
    
    public HasChildFilter(final NodeFilter filter, final boolean recursive) {
        this.setChildFilter(filter);
        this.setRecursive(recursive);
    }
    
    public NodeFilter getChildFilter() {
        return this.mChildFilter;
    }
    
    public void setChildFilter(final NodeFilter filter) {
        this.mChildFilter = filter;
    }
    
    public boolean getRecursive() {
        return this.mRecursive;
    }
    
    public void setRecursive(final boolean recursive) {
        this.mRecursive = recursive;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        if (node instanceof CompositeTag) {
            final CompositeTag tag = (CompositeTag)node;
            final NodeList children = tag.getChildren();
            if (null != children) {
                for (int i = 0; !ret && i < children.size(); ++i) {
                    if (this.getChildFilter().accept(children.elementAt(i))) {
                        ret = true;
                    }
                }
                if (!ret && this.getRecursive()) {
                    for (int i = 0; !ret && i < children.size(); ++i) {
                        if (this.accept(children.elementAt(i))) {
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }
}
