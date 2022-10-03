package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.xni.QName;

public class CMLeaf extends CMNode
{
    private final QName fElement;
    private int fPosition;
    
    public CMLeaf(final QName values, final int fPosition) {
        super(0);
        this.fElement = new QName();
        this.fPosition = -1;
        this.fElement.setValues(values);
        this.fPosition = fPosition;
    }
    
    public CMLeaf(final QName values) {
        super(0);
        this.fElement = new QName();
        this.fPosition = -1;
        this.fElement.setValues(values);
    }
    
    final QName getElement() {
        return this.fElement;
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
        final StringBuffer sb = new StringBuffer(this.fElement.toString());
        sb.append(" (");
        sb.append(this.fElement.uri);
        sb.append(',');
        sb.append(this.fElement.localpart);
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
