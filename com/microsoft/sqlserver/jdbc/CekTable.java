package com.microsoft.sqlserver.jdbc;

import java.io.Serializable;

class CekTable implements Serializable
{
    private static final long serialVersionUID = -4568542970907052239L;
    CekTableEntry[] keyList;
    
    CekTable(final int tableSize) {
        this.keyList = new CekTableEntry[tableSize];
    }
    
    int getSize() {
        return this.keyList.length;
    }
    
    CekTableEntry getCekTableEntry(final int index) {
        return this.keyList[index];
    }
    
    void setCekTableEntry(final int index, final CekTableEntry entry) {
        this.keyList[index] = entry;
    }
}
