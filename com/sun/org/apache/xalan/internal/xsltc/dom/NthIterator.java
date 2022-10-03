package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class NthIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    private final int _position;
    private boolean _ready;
    
    public NthIterator(final DTMAxisIterator source, final int n) {
        this._source = source;
        this._position = n;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final NthIterator clone = (NthIterator)super.clone();
            clone._source = this._source.cloneIterator();
            clone._isRestartable = false;
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    @Override
    public int next() {
        if (this._ready) {
            this._ready = false;
            return this._source.getNodeByPosition(this._position);
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isRestartable) {
            this._source.setStartNode(node);
            this._ready = true;
        }
        return this;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._source.reset();
        this._ready = true;
        return this;
    }
    
    @Override
    public int getLast() {
        return 1;
    }
    
    @Override
    public int getPosition() {
        return 1;
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
