package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class SingleNodeCounter extends NodeCounter
{
    private static final int[] EmptyArray;
    DTMAxisIterator _countSiblings;
    
    public SingleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        super(translet, document, iterator);
        this._countSiblings = null;
    }
    
    public SingleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator, final boolean hasFrom) {
        super(translet, document, iterator, hasFrom);
        this._countSiblings = null;
    }
    
    @Override
    public NodeCounter setStartNode(final int node) {
        this._node = node;
        this._nodeType = this._document.getExpandedTypeID(node);
        this._countSiblings = this._document.getAxisIterator(12);
        return this;
    }
    
    @Override
    public String getCounter() {
        if (this._value == -2.147483648E9) {
            int next = this._node;
            int result = 0;
            final boolean matchesCount = this.matchesCount(next);
            if (!matchesCount) {
                while ((next = this._document.getParent(next)) > -1) {
                    if (this.matchesCount(next)) {
                        break;
                    }
                    if (this.matchesFrom(next)) {
                        next = -1;
                        break;
                    }
                }
            }
            if (next != -1) {
                int from = next;
                if (!matchesCount && this._hasFrom) {
                    while ((from = this._document.getParent(from)) > -1 && !this.matchesFrom(from)) {}
                }
                if (from != -1) {
                    this._countSiblings.setStartNode(next);
                    do {
                        if (this.matchesCount(next)) {
                            ++result;
                        }
                    } while ((next = this._countSiblings.next()) != -1);
                    return this.formatNumbers(result);
                }
            }
            return this.formatNumbers(SingleNodeCounter.EmptyArray);
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
        int result = (int)this._value;
        return this.formatNumbers(result);
    }
    
    public static NodeCounter getDefaultNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        return new DefaultSingleNodeCounter(translet, document, iterator);
    }
    
    static {
        EmptyArray = new int[0];
    }
    
    static class DefaultSingleNodeCounter extends SingleNodeCounter
    {
        public DefaultSingleNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
            super(translet, document, iterator);
        }
        
        @Override
        public NodeCounter setStartNode(final int node) {
            this._node = node;
            this._nodeType = this._document.getExpandedTypeID(node);
            this._countSiblings = this._document.getTypedAxisIterator(12, this._document.getExpandedTypeID(node));
            return this;
        }
        
        @Override
        public String getCounter() {
            int result;
            if (this._value != -2.147483648E9) {
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
                result = (int)this._value;
            }
            else {
                result = 1;
                this._countSiblings.setStartNode(this._node);
                int next;
                while ((next = this._countSiblings.next()) != -1) {
                    ++result;
                }
            }
            return this.formatNumbers(result);
        }
    }
}
