package com.sun.corba.se.impl.ior;

public class ObjectAdapterIdNumber extends ObjectAdapterIdArray
{
    private int poaid;
    
    public ObjectAdapterIdNumber(final int poaid) {
        super("OldRootPOA", Integer.toString(poaid));
        this.poaid = poaid;
    }
    
    public int getOldPOAId() {
        return this.poaid;
    }
}
