package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;

public abstract class AnyNodeCounter extends NodeCounter
{
    public AnyNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
        super(translet, document, iterator);
    }
    
    public AnyNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator, final boolean hasFrom) {
        super(translet, document, iterator, hasFrom);
    }
    
    @Override
    public NodeCounter setStartNode(final int node) {
        this._node = node;
        this._nodeType = this._document.getExpandedTypeID(node);
        return this;
    }
    
    @Override
    public String getCounter() {
        if (this._value == -2.147483648E9) {
            int next = this._node;
            final int root = this._document.getDocument();
            int result = 0;
            while (next >= root && !this.matchesFrom(next)) {
                if (this.matchesCount(next)) {
                    ++result;
                }
                --next;
            }
            return this.formatNumbers(result);
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
        return new DefaultAnyNodeCounter(translet, document, iterator);
    }
    
    static class DefaultAnyNodeCounter extends AnyNodeCounter
    {
        public DefaultAnyNodeCounter(final Translet translet, final DOM document, final DTMAxisIterator iterator) {
            super(translet, document, iterator);
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
                int next = this._node;
                result = 0;
                final int ntype = this._document.getExpandedTypeID(this._node);
                final int root = this._document.getDocument();
                while (next >= 0) {
                    if (ntype == this._document.getExpandedTypeID(next)) {
                        ++result;
                    }
                    if (next == root) {
                        break;
                    }
                    --next;
                }
            }
            return this.formatNumbers(result);
        }
    }
}
