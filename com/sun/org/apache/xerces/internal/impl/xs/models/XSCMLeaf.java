package com.sun.org.apache.xerces.internal.impl.xs.models;

import com.sun.org.apache.xerces.internal.impl.dtd.models.CMStateSet;
import com.sun.org.apache.xerces.internal.impl.dtd.models.CMNode;

public class XSCMLeaf extends CMNode
{
    private Object fLeaf;
    private int fParticleId;
    private int fPosition;
    
    public XSCMLeaf(final int type, final Object leaf, final int id, final int position) {
        super(type);
        this.fLeaf = null;
        this.fParticleId = -1;
        this.fPosition = -1;
        this.fLeaf = leaf;
        this.fParticleId = id;
        this.fPosition = position;
    }
    
    final Object getLeaf() {
        return this.fLeaf;
    }
    
    final int getParticleId() {
        return this.fParticleId;
    }
    
    final int getPosition() {
        return this.fPosition;
    }
    
    final void setPosition(final int newPosition) {
        this.fPosition = newPosition;
    }
    
    @Override
    public boolean isNullable() {
        return this.fPosition == -1;
    }
    
    @Override
    public String toString() {
        final StringBuffer strRet = new StringBuffer(this.fLeaf.toString());
        if (this.fPosition >= 0) {
            strRet.append(" (Pos:" + Integer.toString(this.fPosition) + ")");
        }
        return strRet.toString();
    }
    
    @Override
    protected void calcFirstPos(final CMStateSet toSet) {
        if (this.fPosition == -1) {
            toSet.zeroBits();
        }
        else {
            toSet.setBit(this.fPosition);
        }
    }
    
    @Override
    protected void calcLastPos(final CMStateSet toSet) {
        if (this.fPosition == -1) {
            toSet.zeroBits();
        }
        else {
            toSet.setBit(this.fPosition);
        }
    }
}
