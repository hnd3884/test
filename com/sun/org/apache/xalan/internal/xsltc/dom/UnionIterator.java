package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xalan.internal.xsltc.DOM;

public final class UnionIterator extends MultiValuedNodeHeapIterator
{
    private final DOM _dom;
    
    public UnionIterator(final DOM dom) {
        this._dom = dom;
    }
    
    public UnionIterator addIterator(final DTMAxisIterator iterator) {
        this.addHeapNode(new LookAheadIterator(iterator));
        return this;
    }
    
    private final class LookAheadIterator extends HeapNode
    {
        public DTMAxisIterator iterator;
        
        public LookAheadIterator(final DTMAxisIterator iterator) {
            this.iterator = iterator;
        }
        
        @Override
        public int step() {
            return this._node = this.iterator.next();
        }
        
        @Override
        public HeapNode cloneHeapNode() {
            final LookAheadIterator clone = (LookAheadIterator)super.cloneHeapNode();
            clone.iterator = this.iterator.cloneIterator();
            return clone;
        }
        
        @Override
        public void setMark() {
            super.setMark();
            this.iterator.setMark();
        }
        
        @Override
        public void gotoMark() {
            super.gotoMark();
            this.iterator.gotoMark();
        }
        
        @Override
        public boolean isLessThan(final HeapNode heapNode) {
            final LookAheadIterator comparand = (LookAheadIterator)heapNode;
            return UnionIterator.this._dom.lessThan(this._node, heapNode._node);
        }
        
        @Override
        public HeapNode setStartNode(final int node) {
            this.iterator.setStartNode(node);
            return this;
        }
        
        @Override
        public HeapNode reset() {
            this.iterator.reset();
            return this;
        }
    }
}
