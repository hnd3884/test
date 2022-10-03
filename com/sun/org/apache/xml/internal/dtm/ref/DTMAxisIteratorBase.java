package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class DTMAxisIteratorBase implements DTMAxisIterator
{
    protected int _last;
    protected int _position;
    protected int _markedNode;
    protected int _startNode;
    protected boolean _includeSelf;
    protected boolean _isRestartable;
    
    public DTMAxisIteratorBase() {
        this._last = -1;
        this._position = 0;
        this._startNode = -1;
        this._includeSelf = false;
        this._isRestartable = true;
    }
    
    @Override
    public int getStartNode() {
        return this._startNode;
    }
    
    @Override
    public DTMAxisIterator reset() {
        final boolean temp = this._isRestartable;
        this._isRestartable = true;
        this.setStartNode(this._startNode);
        this._isRestartable = temp;
        return this;
    }
    
    public DTMAxisIterator includeSelf() {
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
    public DTMAxisIterator cloneIterator() {
        try {
            final DTMAxisIteratorBase clone = (DTMAxisIteratorBase)super.clone();
            clone._isRestartable = false;
            return clone;
        }
        catch (final CloneNotSupportedException e) {
            throw new WrappedRuntimeException(e);
        }
    }
    
    protected final int returnNode(final int node) {
        ++this._position;
        return node;
    }
    
    protected final DTMAxisIterator resetPosition() {
        this._position = 0;
        return this;
    }
    
    public boolean isDocOrdered() {
        return true;
    }
    
    public int getAxis() {
        return -1;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
    }
    
    @Override
    public int getNodeByPosition(final int position) {
        if (position > 0) {
            final int pos = this.isReverse() ? (this.getLast() - position + 1) : position;
            int node;
            while ((node = this.next()) != -1) {
                if (pos == this.getPosition()) {
                    return node;
                }
            }
        }
        return -1;
    }
}
