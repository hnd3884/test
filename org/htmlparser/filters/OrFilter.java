package org.htmlparser.filters;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class OrFilter implements NodeFilter
{
    protected NodeFilter[] mPredicates;
    
    public OrFilter() {
        this.setPredicates(null);
    }
    
    public OrFilter(final NodeFilter left, final NodeFilter right) {
        final NodeFilter[] predicates = { left, right };
        this.setPredicates(predicates);
    }
    
    public OrFilter(final NodeFilter[] predicates) {
        this.setPredicates(predicates);
    }
    
    public NodeFilter[] getPredicates() {
        return this.mPredicates;
    }
    
    public void setPredicates(NodeFilter[] predicates) {
        if (null == predicates) {
            predicates = new NodeFilter[0];
        }
        this.mPredicates = predicates;
    }
    
    public boolean accept(final Node node) {
        boolean ret = false;
        for (int i = 0; !ret && i < this.mPredicates.length; ++i) {
            if (this.mPredicates[i].accept(node)) {
                ret = true;
            }
        }
        return ret;
    }
}
