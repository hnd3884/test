package com.sun.corba.se.impl.interceptors;

import java.util.ArrayList;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.util.List;

public class SlotTableStack
{
    private List tableContainer;
    private int currentIndex;
    private SlotTablePool tablePool;
    private ORB orb;
    private InterceptorsSystemException wrapper;
    
    SlotTableStack(final ORB orb, final SlotTable slotTable) {
        this.orb = orb;
        this.wrapper = InterceptorsSystemException.get(orb, "rpc.protocol");
        this.currentIndex = 0;
        this.tableContainer = new ArrayList();
        this.tablePool = new SlotTablePool();
        this.tableContainer.add(this.currentIndex, slotTable);
        ++this.currentIndex;
    }
    
    void pushSlotTable() {
        SlotTable slotTable = this.tablePool.getSlotTable();
        if (slotTable == null) {
            slotTable = new SlotTable(this.orb, this.peekSlotTable().getSize());
        }
        if (this.currentIndex == this.tableContainer.size()) {
            this.tableContainer.add(this.currentIndex, slotTable);
        }
        else {
            if (this.currentIndex > this.tableContainer.size()) {
                throw this.wrapper.slotTableInvariant(new Integer(this.currentIndex), new Integer(this.tableContainer.size()));
            }
            this.tableContainer.set(this.currentIndex, slotTable);
        }
        ++this.currentIndex;
    }
    
    void popSlotTable() {
        if (this.currentIndex <= 1) {
            throw this.wrapper.cantPopOnlyPicurrent();
        }
        --this.currentIndex;
        final SlotTable slotTable = this.tableContainer.get(this.currentIndex);
        this.tableContainer.set(this.currentIndex, null);
        slotTable.resetSlots();
        this.tablePool.putSlotTable(slotTable);
    }
    
    SlotTable peekSlotTable() {
        return this.tableContainer.get(this.currentIndex - 1);
    }
    
    private class SlotTablePool
    {
        private SlotTable[] pool;
        private final int HIGH_WATER_MARK = 5;
        private int currentIndex;
        
        SlotTablePool() {
            this.pool = new SlotTable[5];
            this.currentIndex = 0;
        }
        
        void putSlotTable(final SlotTable slotTable) {
            if (this.currentIndex >= 5) {
                return;
            }
            this.pool[this.currentIndex] = slotTable;
            ++this.currentIndex;
        }
        
        SlotTable getSlotTable() {
            if (this.currentIndex == 0) {
                return null;
            }
            --this.currentIndex;
            return this.pool[this.currentIndex];
        }
    }
}
