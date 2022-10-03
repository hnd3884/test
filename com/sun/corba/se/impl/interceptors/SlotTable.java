package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import org.omg.PortableInterceptor.InvalidSlot;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;

public class SlotTable
{
    private Any[] theSlotData;
    private ORB orb;
    private boolean dirtyFlag;
    
    SlotTable(final ORB orb, final int n) {
        this.dirtyFlag = false;
        this.orb = orb;
        this.theSlotData = new Any[n];
    }
    
    public void set_slot(final int n, final Any any) throws InvalidSlot {
        if (n >= this.theSlotData.length) {
            throw new InvalidSlot();
        }
        this.dirtyFlag = true;
        this.theSlotData[n] = any;
    }
    
    public Any get_slot(final int n) throws InvalidSlot {
        if (n >= this.theSlotData.length) {
            throw new InvalidSlot();
        }
        if (this.theSlotData[n] == null) {
            this.theSlotData[n] = new AnyImpl(this.orb);
        }
        return this.theSlotData[n];
    }
    
    void resetSlots() {
        if (this.dirtyFlag) {
            for (int i = 0; i < this.theSlotData.length; ++i) {
                this.theSlotData[i] = null;
            }
        }
    }
    
    int getSize() {
        return this.theSlotData.length;
    }
}
