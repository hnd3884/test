package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class FilterIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    private final DTMFilter _filter;
    private final boolean _isReverse;
    
    public FilterIterator(final DTMAxisIterator source, final DTMFilter filter) {
        this._source = source;
        this._filter = filter;
        this._isReverse = source.isReverse();
    }
    
    @Override
    public boolean isReverse() {
        return this._isReverse;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final FilterIterator clone = (FilterIterator)super.clone();
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
    public DTMAxisIterator reset() {
        this._source.reset();
        return this.resetPosition();
    }
    
    @Override
    public int next() {
        int node;
        while ((node = this._source.next()) != -1) {
            if (this._filter.acceptNode(node, -1) == 1) {
                return this.returnNode(node);
            }
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isRestartable) {
            this._source.setStartNode(this._startNode = node);
            return this.resetPosition();
        }
        return this;
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
