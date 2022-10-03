package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class ClonedNodeListIterator extends DTMAxisIteratorBase
{
    private CachedNodeListIterator _source;
    private int _index;
    
    public ClonedNodeListIterator(final CachedNodeListIterator source) {
        this._index = 0;
        this._source = source;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        return this;
    }
    
    @Override
    public int next() {
        return this._source.getNode(this._index++);
    }
    
    @Override
    public int getPosition() {
        return (this._index == 0) ? 1 : this._index;
    }
    
    @Override
    public int getNodeByPosition(final int pos) {
        return this._source.getNode(pos);
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        return this._source.cloneIterator();
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._index = 0;
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
