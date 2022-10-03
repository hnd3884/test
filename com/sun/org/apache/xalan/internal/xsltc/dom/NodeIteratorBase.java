package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.NodeIterator;

public abstract class NodeIteratorBase implements NodeIterator
{
    protected int _last;
    protected int _position;
    protected int _markedNode;
    protected int _startNode;
    protected boolean _includeSelf;
    protected boolean _isRestartable;
    
    public NodeIteratorBase() {
        this._last = -1;
        this._position = 0;
        this._startNode = -1;
        this._includeSelf = false;
        this._isRestartable = true;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
    }
    
    @Override
    public abstract NodeIterator setStartNode(final int p0);
    
    @Override
    public NodeIterator reset() {
        final boolean temp = this._isRestartable;
        this._isRestartable = true;
        this.setStartNode(this._includeSelf ? (this._startNode + 1) : this._startNode);
        this._isRestartable = temp;
        return this;
    }
    
    public NodeIterator includeSelf() {
        this._includeSelf = true;
        return this;
    }
    
    @Override
    public int getLast() {
        if (this._last == -1) {
            final int temp = this._position;
            this.setMark();
            this.reset();
            do {
                ++this._last;
            } while (this.next() != -1);
            this.gotoMark();
            this._position = temp;
        }
        return this._last;
    }
    
    @Override
    public int getPosition() {
        return (this._position == 0) ? 1 : this._position;
    }
    
    @Override
    public boolean isReverse() {
        return false;
    }
    
    @Override
    public NodeIterator cloneIterator() {
        try {
            final NodeIteratorBase clone = (NodeIteratorBase)super.clone();
            clone._isRestartable = false;
            return clone.reset();
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    protected final int returnNode(final int node) {
        ++this._position;
        return node;
    }
    
    protected final NodeIterator resetPosition() {
        this._position = 0;
        return this;
    }
}
