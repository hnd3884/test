package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;

public class XSCMBinOp extends CMNode
{
    private CMNode fLeftChild;
    private CMNode fRightChild;
    
    public XSCMBinOp(final int type, final CMNode leftNode, final CMNode rightNode) {
        super(type);
        if (this.type() != 101 && this.type() != 102) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
        this.fLeftChild = leftNode;
        this.fRightChild = rightNode;
    }
    
    final CMNode getLeft() {
        return this.fLeftChild;
    }
    
    final CMNode getRight() {
        return this.fRightChild;
    }
    
    @Override
    public boolean isNullable() {
        if (this.type() == 101) {
            return this.fLeftChild.isNullable() || this.fRightChild.isNullable();
        }
        if (this.type() == 102) {
            return this.fLeftChild.isNullable() && this.fRightChild.isNullable();
        }
        throw new RuntimeException("ImplementationMessages.VAL_BST");
    }
    
    @Override
    protected void calcFirstPos(final CMStateSet toSet) {
        if (this.type() == 101) {
            toSet.setTo(this.fLeftChild.firstPos());
            toSet.union(this.fRightChild.firstPos());
        }
        else {
            if (this.type() != 102) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            toSet.setTo(this.fLeftChild.firstPos());
            if (this.fLeftChild.isNullable()) {
                toSet.union(this.fRightChild.firstPos());
            }
        }
    }
    
    @Override
    protected void calcLastPos(final CMStateSet toSet) {
        if (this.type() == 101) {
            toSet.setTo(this.fLeftChild.lastPos());
            toSet.union(this.fRightChild.lastPos());
        }
        else {
            if (this.type() != 102) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            toSet.setTo(this.fRightChild.lastPos());
            if (this.fRightChild.isNullable()) {
                toSet.union(this.fLeftChild.lastPos());
            }
        }
    }
}
