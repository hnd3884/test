package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class StepIterator extends DTMAxisIteratorBase
{
    protected DTMAxisIterator _source;
    protected DTMAxisIterator _iterator;
    private int _pos;
    
    public StepIterator(final DTMAxisIterator source, final DTMAxisIterator iterator) {
        this._pos = -1;
        this._source = source;
        this._iterator = iterator;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
        this._iterator.setRestartable(true);
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        this._isRestartable = false;
        try {
            final StepIterator clone = (StepIterator)super.clone();
            clone._source = this._source.cloneIterator();
            (clone._iterator = this._iterator.cloneIterator()).setRestartable(true);
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isRestartable) {
            this._source.setStartNode(this._startNode = node);
            this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
            return this.resetPosition();
        }
        return this;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        this._iterator.setStartNode(this._includeSelf ? this._startNode : this._source.next());
        return this.resetPosition();
    }
    
    @Override
    public int next() {
        int node;
        while ((node = this._iterator.next()) == -1) {
            if ((node = this._source.next()) == -1) {
                return -1;
            }
            this._iterator.setStartNode(node);
        }
        return this.returnNode(node);
    }
    
    @Override
    public void setMark() {
        this._source.setMark();
        this._iterator.setMark();
    }
    
    @Override
    public void gotoMark() {
        this._source.gotoMark();
        this._iterator.gotoMark();
    }
}
