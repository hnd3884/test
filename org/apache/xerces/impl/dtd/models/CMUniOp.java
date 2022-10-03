package org.apache.xerces.impl.dtd.models;

public class CMUniOp extends CMNode
{
    private final CMNode fChild;
    
    public CMUniOp(final int n, final CMNode fChild) {
        super(n);
        if (this.type() != 1 && this.type() != 2 && this.type() != 3) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = fChild;
    }
    
    final CMNode getChild() {
        return this.fChild;
    }
    
    public boolean isNullable() {
        return this.type() != 3 || this.fChild.isNullable();
    }
    
    protected void calcFirstPos(final CMStateSet set) {
        set.setTo(this.fChild.firstPos());
    }
    
    protected void calcLastPos(final CMStateSet set) {
        set.setTo(this.fChild.lastPos());
    }
}
