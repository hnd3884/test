package org.htmlparser.filters;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;

public class XorFilter implements NodeFilter
{
    protected NodeFilter[] mPredicates;
    
    public XorFilter() {
        this.setPredicates(null);
    }
    
    public XorFilter(final NodeFilter left, final NodeFilter right) {
        final NodeFilter[] predicates = { left, right };
        this.setPredicates(predicates);
    }
    
    public XorFilter(final NodeFilter[] predicates) {
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
        int countTrue = 0;
        for (int i = 0; i < this.mPredicates.length; ++i) {
            if (this.mPredicates[i].accept(node)) {
                ++countTrue;
            }
        }
        return countTrue % 2 == 1;
    }
}
