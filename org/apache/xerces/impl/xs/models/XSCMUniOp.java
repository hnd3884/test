package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.dtd.models.CMNode;

public class XSCMUniOp extends CMNode
{
    private CMNode fChild;
    
    public XSCMUniOp(final int n, final CMNode fChild) {
        super(n);
        if (this.type() != 5 && this.type() != 4 && this.type() != 6) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = fChild;
    }
    
    final CMNode getChild() {
        return this.fChild;
    }
    
    public boolean isNullable() {
        return this.type() != 6 || this.fChild.isNullable();
    }
    
    protected void calcFirstPos(final CMStateSet set) {
        set.setTo(this.fChild.firstPos());
    }
    
    protected void calcLastPos(final CMStateSet set) {
        set.setTo(this.fChild.lastPos());
    }
}
