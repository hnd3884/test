package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public final class CurrentNodeListIterator extends DTMAxisIteratorBase
{
    private boolean _docOrder;
    private DTMAxisIterator _source;
    private final CurrentNodeListFilter _filter;
    private IntegerArray _nodes;
    private int _currentIndex;
    private final int _currentNode;
    private AbstractTranslet _translet;
    
    public CurrentNodeListIterator(final DTMAxisIterator source, final CurrentNodeListFilter filter, final int currentNode, final AbstractTranslet translet) {
        this(source, !source.isReverse(), filter, currentNode, translet);
    }
    
    public CurrentNodeListIterator(final DTMAxisIterator source, final boolean docOrder, final CurrentNodeListFilter filter, final int currentNode, final AbstractTranslet translet) {
        this._nodes = new IntegerArray();
        this._source = source;
        this._filter = filter;
        this._translet = translet;
        this._docOrder = docOrder;
        this._currentNode = currentNode;
    }
    
    public DTMAxisIterator forceNaturalOrder() {
        this._docOrder = true;
        return this;
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
        this._isRestartable = isRestartable;
        this._source.setRestartable(isRestartable);
    }
    
    @Override
    public boolean isReverse() {
        return !this._docOrder;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        try {
            final CurrentNodeListIterator clone = (CurrentNodeListIterator)super.clone();
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
    public DTMAxisIterator reset() {
        this._currentIndex = 0;
        return this.resetPosition();
    }
    
    @Override
    public int next() {
        final int last = this._nodes.cardinality();
        final int currentNode = this._currentNode;
        final AbstractTranslet translet = this._translet;
        int index = this._currentIndex;
        while (index < last) {
            final int position = this._docOrder ? (index + 1) : (last - index);
            final int node = this._nodes.at(index++);
            if (this._filter.test(node, position, last, currentNode, translet, this)) {
                this._currentIndex = index;
                return this.returnNode(node);
            }
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator setStartNode(int node) {
        if (this._isRestartable) {
            this._source.setStartNode(this._startNode = node);
            this._nodes.clear();
            while ((node = this._source.next()) != -1) {
                this._nodes.add(node);
            }
            this._currentIndex = 0;
            this.resetPosition();
        }
        return this;
    }
    
    @Override
    public int getLast() {
        if (this._last == -1) {
            this._last = this.computePositionOfLast();
        }
        return this._last;
    }
    
    @Override
    public void setMark() {
        this._markedNode = this._currentIndex;
    }
    
    @Override
    public void gotoMark() {
        this._currentIndex = this._markedNode;
    }
    
    private int computePositionOfLast() {
        final int last = this._nodes.cardinality();
        final int currNode = this._currentNode;
        final AbstractTranslet translet = this._translet;
        int lastPosition = this._position;
        int index = this._currentIndex;
        while (index < last) {
            final int position = this._docOrder ? (index + 1) : (last - index);
            final int nodeIndex = this._nodes.at(index++);
            if (this._filter.test(nodeIndex, position, last, currNode, translet, this)) {
                ++lastPosition;
            }
        }
        return lastPosition;
    }
}
