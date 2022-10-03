package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;

public class SingletonIterator extends DTMAxisIteratorBase
{
    private int _node;
    private final boolean _isConstant;
    
    public SingletonIterator() {
        this(Integer.MIN_VALUE, false);
    }
    
    public SingletonIterator(final int node) {
        this(node, false);
    }
    
    public SingletonIterator(final int node, final boolean constant) {
        this._startNode = node;
        this._node = node;
        this._isConstant = constant;
    }
    
    @Override
    public DTMAxisIterator setStartNode(final int node) {
        if (this._isConstant) {
            this._node = this._startNode;
            return this.resetPosition();
        }
        if (this._isRestartable) {
            if (this._node <= 0) {
                this._startNode = node;
                this._node = node;
            }
            return this.resetPosition();
        }
        return this;
    }
    
    @Override
    public DTMAxisIterator reset() {
        if (this._isConstant) {
            this._node = this._startNode;
            return this.resetPosition();
        }
        final boolean temp = this._isRestartable;
        this._isRestartable = true;
        this.setStartNode(this._startNode);
        this._isRestartable = temp;
        return this;
    }
    
    @Override
    public int next() {
        final int result = this._node;
        this._node = -1;
        return this.returnNode(result);
    }
    
    @Override
    public void setMark() {
        this._markedNode = this._node;
    }
    
    @Override
    public void gotoMark() {
        this._node = this._markedNode;
    }
}
