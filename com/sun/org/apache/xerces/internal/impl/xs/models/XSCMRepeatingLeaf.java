package com.sun.org.apache.xerces.internal.impl.xs.models;

public final class XSCMRepeatingLeaf extends XSCMLeaf
{
    private final int fMinOccurs;
    private final int fMaxOccurs;
    
    public XSCMRepeatingLeaf(final int type, final Object leaf, final int minOccurs, final int maxOccurs, final int id, final int position) {
        super(type, leaf, id, position);
        this.fMinOccurs = minOccurs;
        this.fMaxOccurs = maxOccurs;
    }
    
    final int getMinOccurs() {
        return this.fMinOccurs;
    }
    
    final int getMaxOccurs() {
        return this.fMaxOccurs;
    }
}
