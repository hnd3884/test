package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class DupFilterIterator extends DTMAxisIteratorBase
{
    private DTMAxisIterator _source;
    private IntegerArray _nodes;
    private int _current;
    private int _nodesSize;
    private int _lastNext;
    private int _markedLastNext;
    
    public DupFilterIterator(final DTMAxisIterator source) {
        this._nodes = new IntegerArray();
        this._current = 0;
        this._nodesSize = 0;
        this._lastNext = -1;
        this._markedLastNext = -1;
        this._source = source;
        if (source instanceof KeyIndex) {
            this.setStartNode(0);
        }
    }
    
    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            final boolean sourceIsKeyIndex = this._source instanceof KeyIndex;
            if (sourceIsKeyIndex && this._startNode == 0) {
                return this;
            }
            if (node != this._startNode) {
                this._source.setStartNode(this._startNode = node);
                this._nodes.clear();
                while ((node = this._source.next()) != -1) {
                    this._nodes.add(node);
                }
                if (!sourceIsKeyIndex) {
                    this._nodes.sort();
                }
                this._nodesSize = this._nodes.cardinality();
                this._current = 0;
                this._lastNext = -1;
                this.resetPosition();
            }
        }
        return this;
    }
    
    @Override
    public int next() {
        while (this._current < this._nodesSize) {
            final int next = this._nodes.at(this._current++);
            if (next != this._lastNext) {
                final int n = next;
                this._lastNext = n;
                return this.returnNode(n);
            }
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final DupFilterIterator clone = (DupFilterIterator)super.clone();
            clone._nodes = (IntegerArray)this._nodes.clone();
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
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }
    
    @Override
    public void setMark() {
        this._markedNode = this._current;
        this._markedLastNext = this._lastNext;
    }
    
    @Override
    public void gotoMark() {
        this._current = this._markedNode;
        this._lastNext = this._markedLastNext;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._current = 0;
        this._lastNext = -1;
        return this.resetPosition();
    }
}
