package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.dtd.models.CMNode;

public class XSCMLeaf extends CMNode
{
    private final Object fLeaf;
    private int fParticleId;
    private int fPosition;
    
    public XSCMLeaf(final int n, final Object fLeaf, final int fParticleId, final int fPosition) {
        super(n);
        this.fParticleId = -1;
        this.fPosition = -1;
        this.fLeaf = fLeaf;
        this.fParticleId = fParticleId;
        this.fPosition = fPosition;
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
    
    final void setPosition(final int fPosition) {
        this.fPosition = fPosition;
    }
    
    public boolean isNullable() {
        return this.fPosition == -1;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer(this.fLeaf.toString());
        if (this.fPosition >= 0) {
            sb.append(" (Pos:").append(Integer.toString(this.fPosition)).append(')');
        }
        return sb.toString();
    }
    
    protected void calcFirstPos(final CMStateSet set) {
        if (this.fPosition == -1) {
            set.zeroBits();
        }
        else {
            set.setBit(this.fPosition);
        }
    }
    
    protected void calcLastPos(final CMStateSet set) {
        if (this.fPosition == -1) {
            set.zeroBits();
        }
        else {
            set.setBit(this.fPosition);
        }
    }
}
