package org.htmlparser.filters;

import org.htmlparser.tags.Html;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class NodeClassFilter implements NodeFilter
{
    protected Class mClass;
    
    public NodeClassFilter() {
        this(Html.class);
    }
    
    public NodeClassFilter(final Class cls) {
        this.mClass = cls;
    }
    
    public Class getMatchClass() {
        return this.mClass;
    }
    
    public void setMatchClass(final Class cls) {
        this.mClass = cls;
    }
    
    public boolean accept(final Node node) {
        return null != this.mClass && this.mClass.isAssignableFrom(node.getClass());
    }
}
