package com.sun.org.apache.xerces.internal.impl.dtd.models;

import com.sun.org.apache.xerces.internal.xni.QName;

public class CMLeaf extends CMNode
{
    private QName fElement;
    private int fPosition;
    
    public CMLeaf(final QName element, final int position) {
        super(0);
        this.fElement = new QName();
        this.fPosition = -1;
        this.fElement.setValues(element);
        this.fPosition = position;
    }
    
    public CMLeaf(final QName element) {
        super(0);
        this.fElement = new QName();
        this.fPosition = -1;
        this.fElement.setValues(element);
    }
    
    final QName getElement() {
        return this.fElement;
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
        final StringBuffer strRet = new StringBuffer(this.fElement.toString());
        strRet.append(" (");
        strRet.append(this.fElement.uri);
        strRet.append(',');
        strRet.append(this.fElement.localpart);
        strRet.append(')');
        if (this.fPosition >= 0) {
            strRet.append(" (Pos:" + new Integer(this.fPosition).toString() + ")");
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
