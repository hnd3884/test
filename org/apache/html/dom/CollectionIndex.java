package org.apache.html.dom;

class CollectionIndex
{
    private int _index;
    
    int getIndex() {
        return this._index;
    }
    
    void decrement() {
        --this._index;
    }
    
    boolean isZero() {
        return this._index <= 0;
    }
    
    CollectionIndex(final int index) {
        this._index = index;
    }
}
