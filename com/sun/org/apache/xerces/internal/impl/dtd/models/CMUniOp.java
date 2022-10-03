package com.sun.org.apache.xerces.internal.impl.dtd.models;

public class CMUniOp extends CMNode
{
    private CMNode fChild;
    
    public CMUniOp(final int type, final CMNode childNode) {
        super(type);
        if (this.type() != 1 && this.type() != 2 && this.type() != 3) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = childNode;
    }
    
    final CMNode getChild() {
        return this.fChild;
    }
    
    @Override
    public boolean isNullable() {
        return this.type() != 3 || this.fChild.isNullable();
    }
    
    @Override
    protected void calcFirstPos(final CMStateSet toSet) {
        toSet.setTo(this.fChild.firstPos());
    }
    
    @Override
    protected void calcLastPos(final CMStateSet toSet) {
        toSet.setTo(this.fChild.lastPos());
    }
}
