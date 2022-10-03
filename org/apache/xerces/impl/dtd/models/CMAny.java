package org.apache.xerces.impl.dtd.models;

public class CMAny extends CMNode
{
    private final int fType;
    private final String fURI;
    private int fPosition;
    
    public CMAny(final int fType, final String furi, final int fPosition) {
        super(fType);
        this.fPosition = -1;
        this.fType = fType;
        this.fURI = furi;
        this.fPosition = fPosition;
    }
    
    final int getType() {
        return this.fType;
    }
    
    final String getURI() {
        return this.fURI;
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
        final StringBuffer sb = new StringBuffer();
        sb.append('(');
        sb.append("##any:uri=");
        sb.append(this.fURI);
        sb.append(')');
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
