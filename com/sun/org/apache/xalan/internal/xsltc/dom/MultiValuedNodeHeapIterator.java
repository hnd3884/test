package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public abstract class MultiValuedNodeHeapIterator extends DTMAxisIteratorBase
{
    private static final int InitSize = 8;
    private int _heapSize;
    private int _size;
    private HeapNode[] _heap;
    private int _free;
    private int _returnedLast;
    private int _cachedReturnedLast;
    private int _cachedHeapSize;
    
    public MultiValuedNodeHeapIterator() {
        this._heapSize = 0;
        this._size = 8;
        this._heap = new HeapNode[8];
        this._free = 0;
        this._cachedReturnedLast = -1;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        this._isRestartable = false;
        final HeapNode[] heapCopy = new HeapNode[this._heap.length];
        try {
            final MultiValuedNodeHeapIterator clone = (MultiValuedNodeHeapIterator)super.clone();
            for (int i = 0; i < this._free; ++i) {
                heapCopy[i] = this._heap[i].cloneHeapNode();
            }
            clone.setRestartable(false);
            clone._heap = heapCopy;
            return clone.reset();
        }
        catch (final CloneNotSupportedException e) {
            BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
            return null;
        }
    }
    
    protected void addHeapNode(final HeapNode node) {
        if (this._free == this._size) {
            final int size = this._size * 2;
            this._size = size;
            final HeapNode[] newArray = new HeapNode[size];
            System.arraycopy(this._heap, 0, newArray, 0, this._free);
            this._heap = newArray;
        }
        ++this._heapSize;
        this._heap[this._free++] = node;
    }
    
    @Override
    public int next() {
        while (this._heapSize > 0) {
            final int smallest = this._heap[0]._node;
            if (smallest == -1) {
                if (this._heapSize <= 1) {
                    return -1;
                }
                final HeapNode temp = this._heap[0];
                final HeapNode[] heap = this._heap;
                final int n = 0;
                final HeapNode[] heap2 = this._heap;
                final int heapSize = this._heapSize - 1;
                this._heapSize = heapSize;
                heap[n] = heap2[heapSize];
                this._heap[this._heapSize] = temp;
            }
            else {
                if (smallest != this._returnedLast) {
                    this._heap[0].step();
                    this.heapify(0);
                    final int n2 = smallest;
                    this._returnedLast = n2;
                    return this.returnNode(n2);
                }
                this._heap[0].step();
            }
            this.heapify(0);
        }
        return -1;
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isRestartable) {
            this._startNode = node;
            for (int i = 0; i < this._free; ++i) {
                if (!this._heap[i]._isStartSet) {
                    this._heap[i].setStartNode(node);
                    this._heap[i].step();
                    this._heap[i]._isStartSet = true;
                }
            }
            final int free = this._free;
            this._heapSize = free;
            for (int i = free / 2; i >= 0; --i) {
                this.heapify(i);
            }
            this._returnedLast = -1;
            return this.resetPosition();
        }
        return this;
    }
    
    protected void init() {
        for (int i = 0; i < this._free; ++i) {
            this._heap[i] = null;
        }
        this._heapSize = 0;
        this._free = 0;
    }
    
    private void heapify(int i) {
        while (true) {
            final int r = i + 1 << 1;
            final int l = r - 1;
            int smallest = (l < this._heapSize && this._heap[l].isLessThan(this._heap[i])) ? l : i;
            if (r < this._heapSize && this._heap[r].isLessThan(this._heap[smallest])) {
                smallest = r;
            }
            if (smallest == i) {
                break;
            }
            final HeapNode temp = this._heap[smallest];
            this._heap[smallest] = this._heap[i];
            this._heap[i] = temp;
            i = smallest;
        }
    }
    
    @Override
    public void setMark() {
        for (int i = 0; i < this._free; ++i) {
            this._heap[i].setMark();
        }
        this._cachedReturnedLast = this._returnedLast;
        this._cachedHeapSize = this._heapSize;
    }
    
    @Override
    public void gotoMark() {
        for (int i = 0; i < this._free; ++i) {
            this._heap[i].gotoMark();
        }
        final int cachedHeapSize = this._cachedHeapSize;
        this._heapSize = cachedHeapSize;
        for (int i = cachedHeapSize / 2; i >= 0; --i) {
            this.heapify(i);
        }
        this._returnedLast = this._cachedReturnedLast;
    }
    
    @Override
    public DTMAxisIterator reset() {
        for (int i = 0; i < this._free; ++i) {
            this._heap[i].reset();
            this._heap[i].step();
        }
        final int free = this._free;
        this._heapSize = free;
        for (int i = free / 2; i >= 0; --i) {
            this.heapify(i);
        }
        this._returnedLast = -1;
        return this.resetPosition();
    }
    
    public abstract class HeapNode implements Cloneable
    {
        protected int _node;
        protected int _markedNode;
        protected boolean _isStartSet;
        
        public HeapNode() {
            this._isStartSet = false;
        }
        
        public abstract int step();
        
        public HeapNode cloneHeapNode() {
            HeapNode clone;
            try {
                clone = (HeapNode)super.clone();
            }
            catch (final CloneNotSupportedException e) {
                BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", e.toString());
                return null;
            }
            clone._node = this._node;
            clone._markedNode = this._node;
            return clone;
        }
        
        public void setMark() {
            this._markedNode = this._node;
        }
        
        public void gotoMark() {
            this._node = this._markedNode;
        }
        
        public abstract boolean isLessThan(final HeapNode p0);
        
        public abstract HeapNode setStartNode(final int p0);
        
        public abstract HeapNode reset();
    }
}
