package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;

final class Element
{
    Object servant;
    Object servantData;
    int index;
    int counter;
    boolean valid;
    
    Element(final int index, final Object servant) {
        this.servant = null;
        this.servantData = null;
        this.index = -1;
        this.counter = 0;
        this.valid = false;
        this.servant = servant;
        this.index = index;
    }
    
    byte[] getKey(final Object servant, final Object servantData) {
        this.servant = servant;
        this.servantData = servantData;
        this.valid = true;
        return this.toBytes();
    }
    
    byte[] toBytes() {
        final byte[] array = new byte[8];
        ORBUtility.intToBytes(this.index, array, 0);
        ORBUtility.intToBytes(this.counter, array, 4);
        return array;
    }
    
    void delete(final Element servant) {
        if (!this.valid) {
            return;
        }
        ++this.counter;
        this.servantData = null;
        this.valid = false;
        this.servant = servant;
    }
    
    @Override
    public String toString() {
        return "Element[" + this.index + ", " + this.counter + "]";
    }
}
