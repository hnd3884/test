package com.sun.corba.se.impl.interceptors;

import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.PortableInterceptor.Current;
import org.omg.CORBA.LocalObject;

public class PICurrent extends LocalObject implements Current
{
    private int slotCounter;
    private ORB myORB;
    private OMGSystemException wrapper;
    private boolean orbInitializing;
    private ThreadLocal threadLocalSlotTable;
    
    PICurrent(final ORB myORB) {
        this.threadLocalSlotTable = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new SlotTableStack(PICurrent.this.myORB, new SlotTable(PICurrent.this.myORB, PICurrent.this.slotCounter));
            }
        };
        this.myORB = myORB;
        this.wrapper = OMGSystemException.get(myORB, "rpc.protocol");
        this.orbInitializing = true;
        this.slotCounter = 0;
    }
    
    int allocateSlotId() {
        final int slotCounter = this.slotCounter;
        ++this.slotCounter;
        return slotCounter;
    }
    
    SlotTable getSlotTable() {
        return this.threadLocalSlotTable.get().peekSlotTable();
    }
    
    void pushSlotTable() {
        this.threadLocalSlotTable.get().pushSlotTable();
    }
    
    void popSlotTable() {
        this.threadLocalSlotTable.get().popSlotTable();
    }
    
    @Override
    public void set_slot(final int n, final Any any) throws InvalidSlot {
        if (this.orbInitializing) {
            throw this.wrapper.invalidPiCall3();
        }
        this.getSlotTable().set_slot(n, any);
    }
    
    @Override
    public Any get_slot(final int n) throws InvalidSlot {
        if (this.orbInitializing) {
            throw this.wrapper.invalidPiCall4();
        }
        return this.getSlotTable().get_slot(n);
    }
    
    void resetSlotTable() {
        this.getSlotTable().resetSlots();
    }
    
    void setORBInitializing(final boolean orbInitializing) {
        this.orbInitializing = orbInitializing;
    }
}
