package org.apache.xerces.impl.dtd.models;

public class CMBinOp extends CMNode
{
    private final CMNode fLeftChild;
    private final CMNode fRightChild;
    
    public CMBinOp(final int n, final CMNode fLeftChild, final CMNode fRightChild) {
        super(n);
        if (this.type() != 4 && this.type() != 5) {
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
        if (this.type() == 4) {
            return this.fLeftChild.isNullable() || this.fRightChild.isNullable();
        }
        if (this.type() == 5) {
            return this.fLeftChild.isNullable() && this.fRightChild.isNullable();
        }
        throw new RuntimeException("ImplementationMessages.VAL_BST");
    }
    
    protected void calcFirstPos(final CMStateSet set) {
        if (this.type() == 4) {
            set.setTo(this.fLeftChild.firstPos());
            set.union(this.fRightChild.firstPos());
        }
        else {
            if (this.type() != 5) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            set.setTo(this.fLeftChild.firstPos());
            if (this.fLeftChild.isNullable()) {
                set.union(this.fRightChild.firstPos());
            }
        }
    }
    
    protected void calcLastPos(final CMStateSet set) {
        if (this.type() == 4) {
            set.setTo(this.fLeftChild.lastPos());
            set.union(this.fRightChild.lastPos());
        }
        else {
            if (this.type() != 5) {
                throw new RuntimeException("ImplementationMessages.VAL_BST");
            }
            set.setTo(this.fRightChild.lastPos());
            if (this.fRightChild.isNullable()) {
                set.union(this.fLeftChild.lastPos());
            }
        }
    }
}
