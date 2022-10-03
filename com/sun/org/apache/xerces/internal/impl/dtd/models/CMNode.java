package com.sun.org.apache.xerces.internal.impl.dtd.models;

public abstract class CMNode
{
    private int fType;
    private CMStateSet fFirstPos;
    private CMStateSet fFollowPos;
    private CMStateSet fLastPos;
    private int fMaxStates;
    private Object fUserData;
    
    public CMNode(final int type) {
        this.fFirstPos = null;
        this.fFollowPos = null;
        this.fLastPos = null;
        this.fMaxStates = -1;
        this.fUserData = null;
        this.fType = type;
    }
    
    public abstract boolean isNullable();
    
    public final int type() {
        return this.fType;
    }
    
    public final CMStateSet firstPos() {
        if (this.fFirstPos == null) {
            this.calcFirstPos(this.fFirstPos = new CMStateSet(this.fMaxStates));
        }
        return this.fFirstPos;
    }
    
    public final CMStateSet lastPos() {
        if (this.fLastPos == null) {
            this.calcLastPos(this.fLastPos = new CMStateSet(this.fMaxStates));
        }
        return this.fLastPos;
    }
    
    final void setFollowPos(final CMStateSet setToAdopt) {
        this.fFollowPos = setToAdopt;
    }
    
    public final void setMaxStates(final int maxStates) {
        this.fMaxStates = maxStates;
    }
    
    public void setUserData(final Object userData) {
        this.fUserData = userData;
    }
    
    public Object getUserData() {
        return this.fUserData;
    }
    
    protected abstract void calcFirstPos(final CMStateSet p0);
    
    protected abstract void calcLastPos(final CMStateSet p0);
}
