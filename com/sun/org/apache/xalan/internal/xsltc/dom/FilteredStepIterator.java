package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class FilteredStepIterator extends StepIterator
{
    private Filter _filter;
    
    public FilteredStepIterator(final DTMAxisIterator source, final DTMAxisIterator iterator, final Filter filter) {
        super(source, iterator);
        this._filter = filter;
    }
    
    @Override
    public int next() {
        int node;
        while ((node = super.next()) != -1) {
            if (this._filter.test(node)) {
                return this.returnNode(node);
            }
        }
        return node;
    }
}
