package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;

public class XSCMUniOp extends CMNode
{
    private CMNode fChild;
    
    public XSCMUniOp(final int type, final CMNode childNode) {
        super(type);
        if (this.type() != 5 && this.type() != 4 && this.type() != 6) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = childNode;
    }
    
    final CMNode getChild() {
        return this.fChild;
    }
    
    @Override
    public boolean isNullable() {
        return this.type() != 6 || this.fChild.isNullable();
    }
    
    @Override
    protected void calcFirstPos(final CMStateSet toSet) {
        toSet.setTo(this.fChild.firstPos());
    }
    
    @Override
    protected void calcLastPos(final CMStateSet toSet) {
        toSet.setTo(this.fChild.lastPos());
    }
    
    @Override
    public void setUserData(final Object userData) {
        super.setUserData(userData);
        this.fChild.setUserData(userData);
    }
}
