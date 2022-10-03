package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class MultipleNodeCounter extends NodeCounter
{
    private DTMAxisIterator _precSiblings;
    
    public MultipleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        super(translet, document, iterator);
        this._precSiblings = null;
    }
    
    public MultipleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator, final boolean hasFrom) {
        super(translet, document, iterator, hasFrom);
        this._precSiblings = null;
    }
    
    @Override
    public NodeCounter setStartNode(final int node) {
        this._node = node;
        this._nodeType = this._document.getExpandedTypeID(node);
        this._precSiblings = this._document.getAxisIterator(12);
        return this;
    }
    
    @Override
    public String getCounter() {
        if (this._value == -2.147483648E9) {
            final IntegerArray ancestors = new IntegerArray();
            int next = this._node;
            ancestors.add(next);
            while ((next = this._document.getParent(next)) > -1 && !this.matchesFrom(next)) {
                ancestors.add(next);
            }
            final int nAncestors = ancestors.cardinality();
            final int[] counters = new int[nAncestors];
            for (int i = 0; i < nAncestors; ++i) {
                counters[i] = Integer.MIN_VALUE;
            }
            for (int j = 0, k = nAncestors - 1; k >= 0; --k, ++j) {
                final int counter = counters[j];
                final int ancestor = ancestors.at(k);
                if (this.matchesCount(ancestor)) {
                    this._precSiblings.setStartNode(ancestor);
                    while ((next = this._precSiblings.next()) != -1) {
                        if (this.matchesCount(next)) {
                            counters[j] = ((counters[j] == Integer.MIN_VALUE) ? 1 : (counters[j] + 1));
                        }
                    }
                    counters[j] = ((counters[j] == Integer.MIN_VALUE) ? 1 : (counters[j] + 1));
                }
            }
            return this.formatNumbers(counters);
        }
        if (this._value == 0.0) {
            return "0";
        }
        if (Double.isNaN(this._value)) {
            return "NaN";
        }
        if (this._value < 0.0 && Double.isInfinite(this._value)) {
            return "-Infinity";
        }
        if (Double.isInfinite(this._value)) {
            return "Infinity";
        }
        return this.formatNumbers((int)this._value);
    }
    
    public static NodeCounter getDefaultNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        return new DefaultMultipleNodeCounter(translet, document, iterator);
    }
    
    static class DefaultMultipleNodeCounter extends MultipleNodeCounter
    {
        public DefaultMultipleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
            super(translet, document, iterator);
        }
    }
}
