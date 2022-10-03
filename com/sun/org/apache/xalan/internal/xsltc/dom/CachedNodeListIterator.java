package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CachedNodeListIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    private IntegerArray _nodes;
    private int _numCachedNodes;
    private int _index;
    private boolean _isEnded;
    
    public CachedNodeListIterator(final DTMAxisIterator source) {
        this._nodes = new IntegerArray();
        this._numCachedNodes = 0;
        this._index = 0;
        this._isEnded = false;
        this._source = source;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isRestartable) {
            this._startNode = node;
            this._source.setStartNode(node);
            this.resetPosition();
            this._isRestartable = false;
        }
        return this;
    }
    
    @Override
    public int next() {
        return this.getNode(this._index++);
    }
    
    @Override
    public int getPosition() {
        return (this._index == 0) ? 1 : this._index;
    }
    
    @Override
    public int getNodeByPosition(final int pos) {
        return this.getNode(pos);
    }
    
    public int getNode(final int index) {
        if (index < this._numCachedNodes) {
            return this._nodes.at(index);
        }
        if (!this._isEnded) {
            final int node = this._source.next();
            if (node != -1) {
                this._nodes.add(node);
                ++this._numCachedNodes;
            }
            else {
                this._isEnded = true;
            }
            return node;
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        final ClonedNodeListIterator clone = new ClonedNodeListIterator(this);
        return clone;
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
