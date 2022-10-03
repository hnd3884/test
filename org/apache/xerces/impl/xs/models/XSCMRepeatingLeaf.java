package org.apache.xerces.impl.xs.models;

public final class XSCMRepeatingLeaf extends XSCMLeaf
{
    private final int fMinOccurs;
    private final int fMaxOccurs;
    
    public XSCMRepeatingLeaf(final int n, final Object o, final int fMinOccurs, final int fMaxOccurs, final int n2, final int n3) {
        super(n, o, n2, n3);
        this.fMinOccurs = fMinOccurs;
        this.fMaxOccurs = fMaxOccurs;
    }
    
    final int getMinOccurs() {
        return this.fMinOccurs;
    }
    
    final int getMaxOccurs() {
        return this.fMaxOccurs;
    }
}
