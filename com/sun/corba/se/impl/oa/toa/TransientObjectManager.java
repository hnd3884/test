package com.sun.corba.se.impl.oa.toa;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.orb.ORB;

public final class TransientObjectManager
{
    private ORB orb;
    private int maxSize;
    private Element[] elementArray;
    private Element freeList;
    
    void dprint(final String s) {
        ORBUtility.dprint(this, s);
    }
    
    public TransientObjectManager(final ORB orb) {
        this.maxSize = 128;
        this.orb = orb;
        (this.elementArray = new Element[this.maxSize])[this.maxSize - 1] = new Element(this.maxSize - 1, null);
        for (int i = this.maxSize - 2; i >= 0; --i) {
            this.elementArray[i] = new Element(i, this.elementArray[i + 1]);
        }
        this.freeList = this.elementArray[0];
    }
    
    public synchronized byte[] storeServant(final Object o, final Object o2) {
        if (this.freeList == null) {
            this.doubleSize();
        }
        final Element freeList = this.freeList;
        this.freeList = (Element)this.freeList.servant;
        final byte[] key = freeList.getKey(o, o2);
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("storeServant returns key for element " + freeList);
        }
        return key;
    }
    
    public synchronized Object lookupServant(final byte[] array) {
        final int bytesToInt = ORBUtility.bytesToInt(array, 0);
        final int bytesToInt2 = ORBUtility.bytesToInt(array, 4);
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("lookupServant called with index=" + bytesToInt + ", counter=" + bytesToInt2);
        }
        if (this.elementArray[bytesToInt].counter == bytesToInt2 && this.elementArray[bytesToInt].valid) {
            if (this.orb.transientObjectManagerDebugFlag) {
                this.dprint("\tcounter is valid");
            }
            return this.elementArray[bytesToInt].servant;
        }
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is invalid");
        }
        return null;
    }
    
    public synchronized Object lookupServantData(final byte[] array) {
        final int bytesToInt = ORBUtility.bytesToInt(array, 0);
        final int bytesToInt2 = ORBUtility.bytesToInt(array, 4);
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("lookupServantData called with index=" + bytesToInt + ", counter=" + bytesToInt2);
        }
        if (this.elementArray[bytesToInt].counter == bytesToInt2 && this.elementArray[bytesToInt].valid) {
            if (this.orb.transientObjectManagerDebugFlag) {
                this.dprint("\tcounter is valid");
            }
            return this.elementArray[bytesToInt].servantData;
        }
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("\tcounter is invalid");
        }
        return null;
    }
    
    public synchronized void deleteServant(final byte[] array) {
        final int bytesToInt = ORBUtility.bytesToInt(array, 0);
        if (this.orb.transientObjectManagerDebugFlag) {
            this.dprint("deleting servant at index=" + bytesToInt);
        }
        this.elementArray[bytesToInt].delete(this.freeList);
        this.freeList = this.elementArray[bytesToInt];
    }
    
    public synchronized byte[] getKey(final Object o) {
        for (int i = 0; i < this.maxSize; ++i) {
            if (this.elementArray[i].valid && this.elementArray[i].servant == o) {
                return this.elementArray[i].toBytes();
            }
        }
        return null;
    }
    
    private void doubleSize() {
        final Element[] elementArray = this.elementArray;
        final int maxSize = this.maxSize;
        this.maxSize *= 2;
        this.elementArray = new Element[this.maxSize];
        for (int i = 0; i < maxSize; ++i) {
            this.elementArray[i] = elementArray[i];
        }
        this.elementArray[this.maxSize - 1] = new Element(this.maxSize - 1, null);
        for (int j = this.maxSize - 2; j >= maxSize; --j) {
            this.elementArray[j] = new Element(j, this.elementArray[j + 1]);
        }
        this.freeList = this.elementArray[maxSize];
    }
}
