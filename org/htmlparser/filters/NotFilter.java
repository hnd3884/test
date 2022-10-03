package org.htmlparser.filters;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class NotFilter implements NodeFilter
{
    protected NodeFilter mPredicate;
    
    public NotFilter() {
        this.setPredicate(null);
    }
    
    public NotFilter(final NodeFilter predicate) {
        this.setPredicate(predicate);
    }
    
    public NodeFilter getPredicate() {
        return this.mPredicate;
    }
    
    public void setPredicate(final NodeFilter predicate) {
        this.mPredicate = predicate;
    }
    
    public boolean accept(final Node node) {
        return null != this.mPredicate && !this.mPredicate.accept(node);
    }
}
