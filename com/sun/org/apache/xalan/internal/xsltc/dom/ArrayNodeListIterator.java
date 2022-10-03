package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public class ArrayNodeListIterator implements DTMAxisIterator
{
    private int _pos;
    private int _mark;
    private int[] _nodes;
    private static final int[] EMPTY;
    
    public ArrayNodeListIterator(final int[] nodes) {
        this._pos = 0;
        this._mark = 0;
        this._nodes = nodes;
    }
    
    @Override
    public int next() {
        return (this._pos < this._nodes.length) ? this._nodes[this._pos++] : -1;
    }
    
    @Override
    public DTMAxisIterator reset() {
        this._pos = 0;
        return this;
    }
    
    @Override
    public int getLast() {
        return this._nodes.length;
    }
    
    @Override
    public int getPosition() {
        return this._pos;
    }
    
    @Override
    public void setMark() {
        this._mark = this._pos;
    }
    
    @Override
    public void gotoMark() {
        this._pos = this._mark;
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (node == -1) {
            this._nodes = ArrayNodeListIterator.EMPTY;
        }
        return this;
    }
    
    @Override
    public int getStartNode() {
        return -1;
    }
    
    @Override
    public boolean isReverse() {
        return false;
    }
    
    @Override
    public DTMAxisIterator cloneIterator() {
        return new ArrayNodeListIterator(this._nodes);
    }
    
    @Override
    public void setRestartable(final boolean isRestartable) {
    }
    
    @Override
    public int getNodeByPosition(final int position) {
        return this._nodes[position - 1];
    }
    
    static {
        EMPTY = new int[0];
    }
}
