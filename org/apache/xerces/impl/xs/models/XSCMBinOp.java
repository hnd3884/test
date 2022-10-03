package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.dtd.models.CMNode;

public class XSCMBinOp extends CMNode
{
    private CMNode fLeftChild;
    private CMNode fRightChild;
    
    public XSCMBinOp(final int n, final CMNode fLeftChild, final CMNode fRightChild) {
        super(n);
        if (this.type() != 101 && this.type() != 102) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
        this.fLeftChild = fLeftChild;
        this.fRightChild = fRightChild;
    }
    
    final CMNode getLeft() {
        return this.fLeftChild;
    }
    
    final CMNode getRight() {
        return this.fRightChild;
    }
    
    public boolean isNullable() {
        if (this.type() == 101) {
            return this.fLeftChild.isNullable() || this.fRightChild.isNullable();
        }
        if (this.type() == 102) {
            return this.fLeftChild.isNullable() && this.fRightChild.isNullable();
        }
        throw new RuntimeException("ImplementationMessages.VAL_BST");
    }
    
    protected void calcFirstPos(final CMStateSet set) {
        if (this.type() == 101) {
            set.setTo(this.fLeftChild.firstPos());
            set.union(this.fRightChild.firstPos());
        }
        else {
            if (this.type() != 102) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            set.setTo(this.fLeftChild.firstPos());
            if (this.fLeftChild.isNullable()) {
                set.union(this.fRightChild.firstPos());
            }
        }
    }
    
    protected void calcLastPos(final CMStateSet set) {
        if (this.type() == 101) {
            set.setTo(this.fLeftChild.lastPos());
            set.union(this.fRightChild.lastPos());
        }
        else {
            if (this.type() != 102) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            set.setTo(this.fRightChild.lastPos());
            if (this.fRightChild.isNullable()) {
                set.union(this.fLeftChild.lastPos());
            }
        }
    }
}
