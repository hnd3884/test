package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;

final class SlotAllocator
{
    private int _firstAvailableSlot;
    private int _size;
    private int _free;
    private int[] _slotsTaken;
    
    SlotAllocator() {
        this._size = 8;
        this._free = 0;
        this._slotsTaken = new int[this._size];
    }
    
    public void initialize(final LocalVariableGen[] vars) {
        final int length = vars.length;
        int slot = 0;
        for (int i = 0; i < length; ++i) {
            final int size = vars[i].getType().getSize();
            final int index = vars[i].getIndex();
            slot = Math.max(slot, index + size);
        }
        this._firstAvailableSlot = slot;
    }
    
    public int allocateSlot(final Type type) {
        final int size = type.getSize();
        final int limit = this._free;
        int slot = this._firstAvailableSlot;
        int where = 0;
        if (this._free + size > this._size) {
            final int size2 = this._size * 2;
            this._size = size2;
            final int[] array = new int[size2];
            for (int j = 0; j < limit; ++j) {
                array[j] = this._slotsTaken[j];
            }
            this._slotsTaken = array;
        }
        while (where < limit) {
            if (slot + size <= this._slotsTaken[where]) {
                for (int i = limit - 1; i >= where; --i) {
                    this._slotsTaken[i + size] = this._slotsTaken[i];
                }
                break;
            }
            slot = this._slotsTaken[where++] + 1;
        }
        for (int i = 0; i < size; ++i) {
            this._slotsTaken[where + i] = slot + i;
        }
        this._free += size;
        return slot;
    }
    
    public void releaseSlot(final LocalVariableGen lvg) {
        final int size = lvg.getType().getSize();
        final int slot = lvg.getIndex();
        final int limit = this._free;
        for (int i = 0; i < limit; ++i) {
            if (this._slotsTaken[i] == slot) {
                for (int j = i + size; j < limit; this._slotsTaken[i++] = this._slotsTaken[j++]) {}
                this._free -= size;
                return;
            }
        }
        final String state = "Variable slot allocation error(size=" + size + ", slot=" + slot + ", limit=" + limit + ")";
        final ErrorMsg err = new ErrorMsg("INTERNAL_ERR", state);
        throw new Error(err.toString());
    }
}
