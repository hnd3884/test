package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class ForwardPositionIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    
    public ForwardPositionIterator(final DTMAxisIterator source) {
        this._source = source;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final ForwardPositionIterator clone = (ForwardPositionIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    @Override
    public int next() {
        return this.returnNode(this._source.next());
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        this._source.setStartNode(node);
        return this;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }
    
    @Override
    public void setMark() {
        this._source.setMark();
    }
    
    @Override
    public void gotoMark() {
        this._source.gotoMark();
    }
}
